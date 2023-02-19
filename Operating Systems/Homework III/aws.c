#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/epoll.h>
#include <sys/socket.h>
#include <sys/sendfile.h>
#include <sys/eventfd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <libaio.h>
#include <errno.h>

#include "util.h"
#include "debug.h"
#include "sock_util.h"
#include "w_epoll.h"
#include "aws.h"
#include "http_parser.h"

#define min(a, b) ((a) < (b) ? (a) : (b))

static int listenfd;
static int epollfd;
static http_parser request_parser;
static char request_path[BUFSIZ];	/* storage for request_path */

/**
 * Header for clients' answers when http request is invalid.
 *
 * @conn: connection handler.
 *
 * Return.
 *
 */
static void add_header_error(connection *conn)
{
	char buffer[BUFSIZ] = "HTTP/1.1 404 Not Found\r\n"
		"Date: Sun, 08 May 2011 09:26:16 GMT\r\n"
		"Server: Apache/2.2.9\r\n"
		"Last-Modified: Mon, 02 Aug 2010 17:55:28 GMT\r\n"
		"Accept-Ranges: bytes\r\n"
		"Content-Length: 153\r\n"
		"Vary: Accept-Encoding\r\n"
		"Connection: close\r\n"
		"Content-Type: text/html\r\n"
		"\r\n";
	memcpy(conn->send_buff, buffer, sizeof(buffer));
	conn->header_len = strlen(conn->send_buff);
}

/**
 * Header for clients' answers when http request is valid.
 *
 * @conn: connection handler.
 *
 * Return.
 *
 */
static void add_header_file(connection *conn)
{
	sprintf(conn->send_buff, "HTTP/1.1 200 OK\r\n"
		"Date: Sun, 08 May 2011 09:26:16 GMT\r\n"
		"Server: Apache/2.2.9\r\n"
		"Last-Modified: Mon, 02 Aug 2010 17:55:28 GMT\r\n"
		"Accept-Ranges: bytes\r\n"
		"Content-Length: %ld\r\n"
		"Vary: Accept-Encoding\r\n"
		"Connection: close\r\n"
		"Content-Type: text/html\r\n"
		"\r\n", conn->intern_file_len);

	conn->header_len = strlen(conn->send_buff);
}

/**
 * Callback for parsing http request;
 *
 * @p: parser;
 * @buf: parsed request;
 * @len: request's length.
 *
 * Return 0.
 *
 */
static int on_path_cb(http_parser *p, const char *buf, size_t len)
{
	memset(request_path, 0, sizeof(request_path));

	assert(p == &request_parser);
	memcpy(request_path, buf, len);

return 0;
}

/* Use mostly null settings except for on_path callback. */
static http_parser_settings settings_on_path = {
	/* on_message_begin */ 0,
	/* on_header_field */ 0,
	/* on_header_value */ 0,
	/* on_path */ on_path_cb,
	/* on_url */ 0,
	/* on_fragment */ 0,
	/* on_query_string */ 0,
	/* on_body */ 0,
	/* on_headers_complete */ 0,
	/* on_message_complete */ 0
};

/**
 * Once a new connection establishes with a client, create a handler.
 *
 * @client_fd: file descriptor for communication channel(socket).
 *
 * Return connection handler.
 *
 */
static connection *generate_connection_handler(int client_fd)
{
	connection *conn = malloc(sizeof(*conn));

	DIE(!conn, "malloc() failed!\n");

	memset(conn->recv_buff, 0, BUFSIZ);
	memset(conn->send_buff, 0, BUFSIZ);

	memset(&conn->context, 0, sizeof(io_context_t));
	int ret;

	ret = io_setup(1, &conn->context);
	DIE(ret < 0, "io_setup() failed!\n");

	conn->event_fd = eventfd(0, EFD_NONBLOCK);
	DIE(conn->event_fd < 0, "eventfd() failed!\n");

	conn->client_fd = client_fd;
	conn->intern_file_fd = 0;

	conn->intern_file_len = 0;
	conn->header_len = 0;
	conn->recv_len = 0;
	conn->send_len = 0;
	conn->file_offset = 0;

	conn->aio_actual_buff_len = 0;
	conn->aio_buff_count = 0;
	conn->aio_submit_count = 0;
	conn->aio_filled_buff_count = 0;
	conn->aio_sent_buff_count = 0;
	conn->aio_buff = NULL;
	conn->iocb = NULL;
	conn->piocb = NULL;
	conn->aio_mark_filled_buff = NULL;
	conn->state = PARTIAL_RECV_REQ;
	conn->file_type = UNKNOWN;

	return conn;
}

/**
 *
 * Accept connection request from client.
 *
 * Return.
 *
 */
static void handle_new_connection(void)
{
	/* Create new socket for communication with the client. */
	int client_fd, ret;

	struct sockaddr_in cl_addr;
	socklen_t cl_len = sizeof(cl_addr);

	client_fd = accept(listenfd, (struct sockaddr *)&cl_addr, &cl_len);

	DIE(client_fd < 0, "socket accept() failed!\n");

	/* Make IO operations nonblocking on socket and add it to epoll. */
	int flags = fcntl(client_fd, F_GETFL);

	flags |= O_NONBLOCK;
	fcntl(client_fd, F_SETFL, flags);

	connection *conn = generate_connection_handler(client_fd);

	ret = w_epoll_add_ptr_in(epollfd, client_fd, conn);
	DIE(ret < 0, "epoll_ctl() failed!\n");
}

/**
 *
 * Once client receives an answer, remove connection and free resources.
 *
 * @conn: connection handler.
 *
 * Return null connection.
 *
 */
static connection *remove_connection(connection *conn)
{
	int ret;

	if (conn->client_fd > 0) {
		ret = w_epoll_remove_ptr(epollfd, conn->client_fd, conn);
		DIE(ret < 0, "epoll_ctl() failed!\n");
	}
	if (conn->event_fd > 0 && conn->aio_buff_count) {
		ret = w_epoll_remove_ptr(epollfd, conn->event_fd, conn);
		DIE(ret < 0, "epoll_ctl() failed!\n");
	}
	if (conn->client_fd > 0) {
		ret = close(conn->client_fd);
		DIE(ret < 0, "close() failed!\n");
	}
	if (conn->intern_file_fd > 0) {
		ret = close(conn->intern_file_fd);
		DIE(ret < 0, "close() failed!\n");
	}
	if (conn->event_fd > 0) {
		ret = close(conn->event_fd);
		DIE(ret < 0, "close() failed!\n");
	}

	ret = io_destroy(conn->context);
	DIE(ret < 0, "io_destroy() failed!\n");

	for (int i = 0; i < conn->aio_buff_count; ++i)
		free(conn->aio_buff[i]);

	if (conn->aio_buff)
		free(conn->aio_buff);

	if (conn->piocb)
		free(conn->piocb);

	if (conn->iocb)
		free(conn->iocb);

	if (conn->aio_mark_filled_buff)
		free(conn->aio_mark_filled_buff);

	conn->state = PARTIAL_RECV_REQ;
	conn->file_type = UNKNOWN;

	free(conn);
	conn = NULL;

	return conn;
}

/**
 *
 * Parse http request from client.
 *
 * @conn: connection handler.
 *
 * Return.
 *
 */
static void parse_request(connection *conn)
{
	/* Http request read, parse it now. */
	http_parser_init(&request_parser, HTTP_REQUEST);

	ssize_t bytes_parsed = http_parser_execute(&request_parser,
	&settings_on_path, conn->recv_buff, conn->recv_len);

	if (bytes_parsed == 0) {
		conn = remove_connection(conn);

		return;
	}
	conn->state = FINISHED_RECV_REQ;
}

/**
 *
 * Reads http request from client, met in PARTIAL_RECV_REQ stage.
 * Check each time if reading finished by verifying last bytes of
 * the request.
 *
 * @conn: connection handler.
 *
 * Return connection state;
 *
 */
static connection_state read_message(connection *conn)
{
	int read_len = recv(conn->client_fd, conn->recv_buff + conn->recv_len,
	BUFSIZ - conn->recv_len, 0);

	if (read_len <= 0) {
		conn = remove_connection(conn);

		return CLOSED_CONNECTION;
	}

	conn->recv_len += read_len;

	/* Check if the entire http request was received. */
	if (conn->recv_len >= 4 &&
		!strcmp(conn->recv_buff + conn->recv_len - 4, END_OF_HEADER)) {
		parse_request(conn);
			if (!conn)
				return CLOSED_CONNECTION;
	}

	return conn->state;
}

/**
 *
 * Available bytes to read from client's socket.
 * -met in PARTIAL_RECV_REQ stage, receiving http request is in progress, when
 * it finishes open requested file and update stage based on the path;
 * -met in PARTIAL_SENT_DYNAMIC_ANS stage when AIO read did complete,
 * mark received segments from the disk for further sending.
 *
 * @conn: connection handler.
 *
 * Return.
 *
 */
static void handle_client_request(connection *conn)
{
	if (conn->state == PARTIAL_SENT_DYNAMIC_ANS) {
		/* Some buffers have been filled with data from disk. */
		struct io_event events[BUFSIZ];
		long long events_count;
		ssize_t ret;

		ret = read(conn->event_fd, &events_count, sizeof(events_count));
		DIE(ret < 0, "read() failed!\n");

		ret = io_getevents(conn->context, events_count,
		events_count, events, NULL);
		DIE(ret < 0, "io_getevents() failed!\n");
		conn->aio_filled_buff_count += ret;

		for (int i = 0; i < ret; ++i) {
			conn->aio_mark_filled_buff[*(int *)events[i].data] = 1;
			free(events[i].data);
		}

		/* Add client's socket to epoll for triggering when buffers can be written. */
		ret = w_epoll_add_ptr_out(epollfd, conn->client_fd, conn);
		DIE(ret < 0, "epoll_ctl() failed!\n");

		return;
	}
	/* Read http request, see if socket changes status. */
	connection_state ret;

	ret = read_message(conn);

	if (ret != FINISHED_RECV_REQ)
		return;

	/* Http request received and parsed, check answer type(static / dynamic). */
	conn->state = PARTIAL_SENT_HEADER_ANS;
	conn->intern_file_fd = open(request_path + 1, O_RDONLY);
	if (conn->intern_file_fd > 0) {
		struct stat file_details;

		fstat(conn->intern_file_fd, &file_details);
		conn->intern_file_len = file_details.st_size;
	} else {
		conn->file_type = ERROR;
		add_header_error(conn);
		goto end;
	}

	char staticc[BUFSIZ];

	memset(staticc, 0, BUFSIZ);
	strcat(staticc, AWS_REL_STATIC_FOLDER);

	if (strstr(conn->recv_buff, staticc)) {
		conn->file_type = STATIC;
		add_header_file(conn);
		goto end;
	}

	char dynamic[BUFSIZ];

	memset(dynamic, 0, BUFSIZ);
	strcat(dynamic, AWS_REL_DYNAMIC_FOLDER);

	if (strstr(conn->recv_buff, dynamic)) {
		conn->file_type = DYNAMIC;
		add_header_file(conn);
		goto end;
	}
	conn->file_type = ERROR;
	add_header_error(conn);

end:;
	/* Change socket for triggering output available. */
	int new_ret;

	new_ret = w_epoll_update_ptr_out(epollfd, conn->client_fd, conn);

	DIE(new_ret < 0, "epoll_ctl() failed!\n");
}

/**
 *
 * Set environment for AIO operations in PARTIAL_SENT_DYNAMIC_ANS stage:
 * -initialization of buffers for disk file's segments;
 * -initilization of iocb structures and add to eventfd and further to epoll;
 * -submit AIO operations.
 *
 * @conn: connection handler.
 *
 * Return.
 *
 */
static void set_aio_environment(connection *conn)
{
	int ret;

	/* Divide clients' answer in multiple segments of BUFSIZ size. */
	conn->aio_buff_count = (conn->intern_file_len + BUFSIZ - 1) / BUFSIZ;

	/* Allocate memory for segments and iocb structures. */
	conn->iocb = malloc(conn->aio_buff_count * sizeof(*conn->iocb));
	DIE(!conn->iocb, "malloc() failed!\n");

	conn->piocb = malloc(conn->aio_buff_count * sizeof(*conn->piocb));
	DIE(!conn->piocb, "malloc() failed!\n");

	conn->aio_buff = malloc(conn->aio_buff_count * sizeof(*conn->aio_buff));
	DIE(!conn->aio_buff, "malloc() failed!\n");

	conn->aio_mark_filled_buff = malloc(conn->aio_buff_count *
	sizeof(conn->aio_mark_filled_buff));
	DIE(!conn->aio_mark_filled_buff, "malloc() failed!\n");
	memset(conn->aio_mark_filled_buff, 0, conn->aio_buff_count);

	/* Initialize iocb structures for AIO read for segments with BUFIZ size. */
	for (int i = 0; i < conn->aio_buff_count - 1; ++i) {
		conn->aio_buff[i] = malloc(BUFSIZ * sizeof(char));
		DIE(!conn->aio_buff[i], "malloc() failed!\n");

		conn->piocb[i] = &conn->iocb[i];
		io_prep_pread(&conn->iocb[i], conn->intern_file_fd, conn->aio_buff[i],
			BUFSIZ, i * BUFSIZ);
		io_set_eventfd(&conn->iocb[i], conn->event_fd);

		conn->iocb[i].data = malloc(sizeof(int));
		DIE(!conn->iocb[i].data, "malloc() failed!\n");
		memcpy(conn->iocb[i].data, (void *)&i, sizeof(i));
	}

	/* Initialize iocb structure for AIO read last segment. */
	ssize_t reminder = conn->intern_file_len % BUFSIZ ? conn->intern_file_len % BUFSIZ : BUFSIZ;

	ssize_t last_pos = conn->aio_buff_count - 1;

	conn->aio_buff[last_pos] = malloc(BUFSIZ * sizeof(char));
	DIE(!conn->aio_buff[last_pos], "malloc() failed!\n");

	conn->piocb[last_pos] = &conn->iocb[last_pos];
	io_prep_pread(&conn->iocb[last_pos], conn->intern_file_fd,
	conn->aio_buff[last_pos], reminder, last_pos * BUFSIZ);
	io_set_eventfd(&conn->iocb[last_pos], conn->event_fd);

	conn->iocb[last_pos].data = malloc(sizeof(int));
	DIE(!conn->iocb[last_pos].data, "malloc() failed!\n");
	memcpy(conn->iocb[last_pos].data, (void *)&last_pos, sizeof(last_pos));

	/* Remove client's socket from epoll until any AIO read ends. */
	ret = w_epoll_remove_ptr(epollfd, conn->client_fd, conn);
	DIE(ret < 0, "epoll_ctl() failed!\n");

	ret = io_submit(conn->context, conn->aio_buff_count, conn->piocb);
	DIE(ret < 0, "io_submit() failled!\n");
	conn->aio_submit_count += ret;

	ret = w_epoll_add_ptr_in(epollfd, conn->event_fd, conn);
	DIE(ret < 0, "epoll_ctl() failed!\n");

}

/**
 *
 * Bytes can be written for clients' answers in PARTIAL_SENT_DYNAMIC_ANS stage.
 * Each time check if all answer's segments have been written and update
 * state in this case.
 *
 * @conn: connection handler.
 *
 * Return connection state.
 *
 */
static connection_state send_message(connection *conn)
{
	/* Send answer to client's request. */
	if (conn->state == PARTIAL_SENT_HEADER_ANS) {
		int send_len = send(conn->client_fd, conn->send_buff + conn->send_len,
		conn->header_len - conn->send_len, 0);

		if (send_len <= 0) {
			conn = remove_connection(conn);

			return CLOSED_CONNECTION;
		}
		conn->send_len += send_len;

		if (conn->send_len == conn->header_len) {
			conn->send_len = 0;
			conn->state = FINISHED_SENT_HEADER_ANS;
		}

		return conn->state;
	}
	if (conn->state == PARTIAL_SENT_STATIC_ANS)
		return conn->state;

	/* Send segment from dynamic file buffers. */
	if (conn->aio_sent_buff_count == conn->aio_buff_count - 1)
		conn->aio_actual_buff_len = conn->intern_file_len % BUFSIZ ?
			conn->intern_file_len % BUFSIZ : BUFSIZ;
	else
		conn->aio_actual_buff_len = BUFSIZ;

	if (conn->aio_mark_filled_buff[conn->aio_sent_buff_count] &&
															!conn->send_len)
		memcpy(conn->send_buff, conn->aio_buff[conn->aio_sent_buff_count],
													conn->aio_actual_buff_len);

	int send_len = send(conn->client_fd, conn->send_buff + conn->send_len,
								conn->aio_actual_buff_len - conn->send_len, 0);

	if (send_len <= 0) {
		conn = remove_connection(conn);

		return CLOSED_CONNECTION;
	}

	conn->send_len += send_len;
	if (conn->send_len == conn->aio_actual_buff_len) {
		conn->send_len = 0;
		++conn->aio_sent_buff_count;

		if (conn->aio_sent_buff_count == conn->aio_buff_count)
			conn->state = FINISHED_SENT_DYNAMIC_ANS;
	}

	return conn->state;
}


/**
 *
 * Bytes can be written for clients' answer, met in PARTIAL_SENT_STATIC_ANS,
 * and PARTIAL_SENT_DYNAMIC_ANS and PARTIAL_SENT_HEADER_ANS stages.
 * Update connection stage when needed.
 *
 * @conn: connection handler.
 *
 * Return.
 *
 */
static int handle_client_answer(connection *conn)
{
	connection_state ret;

	ret = send_message(conn);

	if (ret == PARTIAL_SENT_HEADER_ANS || !conn)
		return 0;

	if (ret == FINISHED_SENT_DYNAMIC_ANS || ret == FINISHED_SENT_STATIC_ANS
		|| (ret == FINISHED_SENT_HEADER_ANS && conn->file_type == ERROR)) {
		conn = remove_connection(conn);

		return 0;
	}

	if (ret == FINISHED_SENT_HEADER_ANS) {
		conn->state = PARTIAL_SENT_STATIC_ANS;
		/* Dynamic sending, use AIO beforehand. */
		if (conn->file_type == DYNAMIC) {
			conn->state = PARTIAL_SENT_DYNAMIC_ANS;
			set_aio_environment(conn);
		}

		return 0;
	}

	if (conn->state == PARTIAL_SENT_STATIC_ANS) {
		/* Send static file unsing sendfile. */
		ssize_t to_send = min(BUFSIZ,
		conn->intern_file_len - conn->file_offset);

		ssize_t send_len = sendfile(conn->client_fd, conn->intern_file_fd,
			&conn->file_offset, to_send);

		conn->send_len += send_len;
		if (conn->send_len == conn->intern_file_len) {
			conn->state = FINISHED_SENT_STATIC_ANS;
			conn = remove_connection(conn);

			return 0;
		}
	}

	if (conn->state == PARTIAL_SENT_DYNAMIC_ANS) {
		int rett;

		/* Close client socket when nothing is to be send at a certain moment. */
		if (conn->aio_sent_buff_count == conn->aio_filled_buff_count) {
		/* Check if there are more AIO operations to add to io_context. */
			if (conn->aio_submit_count != conn->aio_buff_count) {
				rett = io_submit(conn->context,
				conn->aio_buff_count - conn->aio_submit_count,
				conn->piocb + conn->aio_submit_count);
				DIE(rett < 0, "io_submit() failed!\n");

				conn->aio_submit_count += rett;
			}

			rett = w_epoll_remove_ptr(epollfd, conn->client_fd, conn);
			DIE(rett < 0, "epoll_ctl() failed!\n");
		}
	}

	return 0;
}

int main(void)
{
/* Create event manager (epoll) and listening socket. */
/* Add listening socket to the poll and wait for clients' connections. */
	int ret;

	epollfd = w_epoll_create();
	DIE(epollfd < 0, "epoll_create() failed!\n");

	listenfd = tcp_create_listener(AWS_LISTEN_PORT, DEFAULT_LISTEN_BACKLOG);
	DIE(listenfd < 0, "socket init(create, bind, listen) failed!\n");

	ret = w_epoll_add_fd_in(epollfd, listenfd);
	DIE(ret < 0, "epoll_ctl() failed!\n");

	for (;;) {
		struct epoll_event event;

		w_epoll_wait_infinite(epollfd, &event);

		connection *actual_conn = (connection *)event.data.ptr;

		if (event.data.fd == listenfd) {
			/* New connection established. */
			handle_new_connection();
			continue;
		}

		if (event.events & EPOLLIN) {
			/* Available input from a client's socket. */
			handle_client_request(actual_conn);
			continue;
		}

		if (event.events & EPOLLOUT) {
			/* Avaiable output to client's socket. */
			handle_client_answer(actual_conn);
			continue;
		}
	}

	return 0;
}

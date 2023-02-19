/*
 * Asynchronous Web Server - header file (macros and structures)
 *
 * 2011-2017, Operating Systems
 */

#ifndef AWS_H_
#define AWS_H_		1

#ifdef __cplusplus
extern "C" {
#endif

#define AWS_LISTEN_PORT		8888
#define AWS_DOCUMENT_ROOT	"./"
#define AWS_REL_STATIC_FOLDER	"static/"
#define AWS_REL_DYNAMIC_FOLDER	"dynamic/"
#define AWS_ABS_STATIC_FOLDER	(AWS_DOCUMENT_ROOT AWS_REL_STATIC_FOLDER)
#define AWS_ABS_DYNAMIC_FOLDER	(AWS_DOCUMENT_ROOT AWS_REL_DYNAMIC_FOLDER)
#define END_OF_HEADER "\r\n\r\n"

/**
 * Stages of connections between server and clients.
 *
 */
typedef enum connection_state {
	PARTIAL_RECV_REQ,          /* still receiving the http request;                */
	FINISHED_RECV_REQ,         /* http request received;                           */
	PARTIAL_SENT_STATIC_ANS,   /* requested file is static, sending in progress;   */
	FINISHED_SENT_STATIC_ANS,  /* static file was sent;                            */
	PARTIAL_SENT_DYNAMIC_ANS,  /* requested file is dynamic, sending in progress;  */
	FINISHED_SENT_DYNAMIC_ANS, /* dynamic file was sent;                           */
	PARTIAL_SENT_HEADER_ANS,   /* sending header before request file, in progress; */
	FINISHED_SENT_HEADER_ANS,  /* header was sent;                                 */
	CLOSED_CONNECTION          /* communication ended.                             */
} connection_state;

/**
 * Type of clients' requested file.
 *
 */
typedef enum file {
	ERROR,   /* requested file does not exist;     */
	STATIC,  /* requested file is static;          */
	DYNAMIC, /* requested file is dynamic;         */
	UNKNOWN  /* requested file was not parsed yet. */
} file;


/**
 * Connection handler for every established.
 *
 */
typedef struct connection {
	int intern_file_fd;           /* file descriptor to requested path;                       */
	int client_fd;                /* socket for accepting clients' requests;                  */
	int event_fd;                 /* file descriptor for waiting AIO completness;             */

	char recv_buff[BUFSIZ];       /* buffer for clients' requests;                            */
	char send_buff[BUFSIZ];       /* buffer for clients' answer;                              */
	char **aio_buff;              /* buffers for AIO operations;                              */
	char *aio_mark_filled_buff;   /* keeps track of AIO reads buffers;                        */

	size_t recv_len;              /* no. of received bytes in recv_buff;                      */
	size_t send_len;              /* no. of sent bytes from send_buff;                        */
	size_t header_len;            /* no. of bytes of answers' header;                         */
	size_t intern_file_len;       /* no. of bytes of clients' request file;                   */
	size_t aio_actual_buff_len;   /* no. of bytes of clients' answer buffer (divided);        */
	size_t aio_buff_count;        /* no. of segments of clients' answer;                      */
	size_t aio_submit_count;      /* no. of submitted(io_submit) segments of clients' answer; */
	size_t aio_filled_buff_count; /* no. of completed AIO reads from disk to buffers;         */
	size_t aio_sent_buff_count;   /* no. of sent segments of clients' answer;                 */
	off_t file_offset;            /* offset in clients' requested path.                       */

	io_context_t context;
	struct iocb *iocb;
	struct iocb **piocb;

	connection_state state;

	file file_type;



} connection;

#ifdef __cplusplus
}
#endif

#endif /* AWS_H_ */

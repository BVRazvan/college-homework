#include "so_scheduler_helper.h"
#include "util/so_scheduler.h"
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/**
 * Free queue when the scheduling ends, joining on terminated threads.
 *
 * @clean_queue : particular queue.
 *
 * Return : freed queue.
 */
t_queue *free_queue(t_queue *clean_queue)
{
	t_node *curr = clean_queue->head;
	t_node *next = NULL;

	while (curr) {
		if (!curr->t_details->joined)
			pthread_join(curr->t_details->t, NULL);

		next = curr->next;
		curr = next;
	}
	curr = clean_queue->head;

	while (curr) {
		next = curr->next;
		pthread_cond_destroy(&curr->t_details->cond);
		free(curr->t_details);
		free(curr);
		curr = next;
	}

	if (clean_queue) {
		free(clean_queue);
		clean_queue = NULL;
	}

	return clean_queue;
}


/**
 * Enque a thread having "READY" state, using round robin algorithm.
 *
 * @queue : queue having "READY" threads;
 * @t_new : new thread joining the queue.
 *
 * Return the queue of "READY" threads.
 */
t_queue *enq_robin(t_queue *queue, t_info *t_new)
{
	if (!queue) {
		queue = malloc(sizeof(*queue));

		if (!queue) {
			fprintf(stderr, "malloc() failed!\n");
			exit(-1);
		}
		queue->head = NULL;
	}

	t_node *to_add = malloc(sizeof(*to_add));

	if (!to_add) {
		fprintf(stderr, "malloc() failed!\n");
		exit(-1);
	}

	to_add->t_details = t_new;
	to_add->next = NULL;
	t_node *curr = queue->head;

	if (!queue->head) {
		queue->head = to_add;
		return queue;
	}

	if (to_add->t_details->prio > queue->head->t_details->prio) {
		to_add->next = queue->head;
		queue->head = to_add;
		return queue;
	}

	while (curr) {
		if (curr->next == NULL || curr->next->t_details->prio < to_add->t_details->prio) {
			t_node *t_aux = curr->next;

			curr->next = to_add;
			to_add->next = t_aux;
			break;
		}

		curr = curr->next;
	}

	return queue;
}


/**
 * Enque a thread having "WAITING"/"TERMINATED" state into certain queue.
 *
 * @queue : queue having "WAITING/READY" threads;
 * @t_new : new thread joining the queue.
 *
 * Return the certain queue.
 */
t_queue *enq_basic(t_queue *queue, t_info *t_new)
{
	if (!queue) {
		queue = malloc(sizeof(*queue));

		if (!queue) {
			fprintf(stderr, "malloc() failed!\n");
			exit(-1);
		}

		queue->head = NULL;
	}

	t_node *to_add = malloc(sizeof(*to_add));

	if (!to_add) {
		fprintf(stderr, "malloc() failed!\n");
		exit(-1);
	}

	to_add->t_details = t_new;
	to_add->next = NULL;

	if (!queue->head) {
		queue->head = to_add;
		return queue;
	}

	t_node *curr = queue->head;

	while (curr) {
		if (curr->next == NULL) {
			curr->next = to_add;
			break;
		}

		curr = curr->next;
	}

	return queue;
}


/**
 * Remove the "head" of a queue.
 *
 * @queue : certain queue whose "head" if removed.
 *
 * Return modified queue.
 */
t_queue *remove_head(t_queue *queue)
{
	t_node *queue_head = queue->head;

	queue->head = queue->head->next;
	free(queue_head);

	if (!queue->head) {
		free(queue);
		queue = NULL;
	}

	return queue;
}

/**
 * Wake threads waiting for a certain signal.
 *
 * @queue : queue containing "WAITING" threads;
 * @io    : certain raised signal.
 *
 * Returns the queue with woken up threads.
 *
 */
t_queue *wake_signaled(t_queue *queue, unsigned int io)
{
	t_node *curr = queue->head;
	t_queue *wake_threads = NULL;

	while (curr) {
		if (curr->t_details->wait == io) {
			curr->t_details->wait = -1;
			wake_threads = enq_robin(wake_threads, curr->t_details);
		}

		curr = curr->next;
	}

	return wake_threads;
}

/**
 * Remove woken up threads from the queue of "WAITING" threads.
 *
 * @queue : the queue of threads having "WAITING" state.
 *
 * Return the modified queue.
 *
 */
t_queue *remove_signaled(t_queue *queue)
{
	t_node *curr = queue->head, *prev = NULL;

	while (curr) {
		if (curr->t_details->wait == -1) {
			t_node *aux = curr;

			curr = curr->next;

			if (prev)
				prev->next = curr;

			free(aux);
		} else {
			if (!prev)
				queue->head = curr;
			prev = curr;
			curr = curr->next;
		}
	}

	if (!prev) {
		queue->head = NULL;
		free(queue);
		queue = NULL;
	}

	return queue;
}

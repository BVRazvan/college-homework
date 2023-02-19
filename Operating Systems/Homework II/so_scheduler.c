/*
 * Threads scheduler header
 *
 * 2022, Operating Systems
 */

#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include "util/so_scheduler.h"
#include "so_scheduler_helper.h"

static so_scheduler_t *so_sched;
static pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond_main;

/**
 * creates and initializes scheduler
 * time quantum for each thread
 * number of IO devices supported
 * returns: 0 on success or negative on error
 */

int so_init(unsigned int time_quantum, unsigned int io)
{
	if (!(io >= 0 && io <= SO_MAX_NUM_EVENTS))
		return -1;

	if (!(time_quantum > 0))
		return -2;

	if (so_sched != NULL)
		return -3;

	so_sched = malloc(sizeof(*so_sched));

	if (!so_sched) {
		fprintf(stderr, "malloc() failed!\n");
		exit(-1);
	}
	so_sched->q_t_ready = NULL;
	so_sched->q_t_done = NULL;
	so_sched->q_t_waiting = NULL;
	so_sched->io_devices = io;
	so_sched->time_quantum = time_quantum;
	so_sched->t_run = NULL;

	return 0;
}

/**
 * Function executed by every thread created with so_fork().
 *
 * @arg : arguments given to the function.
 *
 * Return.
 */
static void *start_thread(void *arg)
{
	pthread_mutex_lock(&mtx);

	t_start_thread *start_t_new = (t_start_thread *)arg;

	while (so_sched->t_run != NULL && so_sched->t_run->t != start_t_new->t->t)
		pthread_cond_wait(&start_t_new->t->cond, &mtx);

	start_t_new->t->t = pthread_self();
	start_t_new->func(start_t_new->t->prio);
	t_info *was = so_sched->t_run;

	so_sched->q_t_done = enq_basic(so_sched->q_t_done, was);

	if (so_sched->q_t_ready) {
		so_sched->t_run = so_sched->q_t_ready->head->t_details;
		so_sched->q_t_ready = remove_head(so_sched->q_t_ready);
		so_sched->t_run->time_quantum = so_sched->time_quantum;

		pthread_mutex_unlock(&mtx);

		pthread_cond_signal(&so_sched->t_run->cond);

	} else {
		so_sched->t_run = NULL;

		pthread_mutex_unlock(&mtx);

		pthread_cond_signal(&cond_main);

	}

	free(start_t_new);

	return 0;
}


/*
 * creates a new so_task_t and runs it according to the scheduler
 * + handler function
 * + priority
 * returns: tid of the new task if successful or INVALID_TID
 */

tid_t so_fork(so_handler *func, unsigned int priority)
{
	if (!(priority >= 0 && priority <= SO_MAX_PRIO))
		return INVALID_TID;

	if (!func)
		return INVALID_TID;


	t_info *t_new = malloc(sizeof(*t_new));

	if (!t_new) {
		fprintf(stderr, "malloc() failed!\n");
		exit(-1);
	}

	t_new->joined = 0;
	t_new->prio = priority;
	t_new->wait = -1;
	t_new->time_quantum = so_sched->time_quantum;
	pthread_cond_init(&t_new->cond, NULL);
	t_start_thread *t_start = malloc(sizeof(*t_start));

	if (!t_start) {
		fprintf(stderr, "malloc() failed!\n");
		exit(-1);
	}

	t_start->func = func;
	t_start->t = t_new;
	pthread_create(&t_new->t, NULL, start_thread, t_start);

	t_start->t = t_new;

	if (!so_sched->t_run) {
		pthread_mutex_lock(&mtx);

		pthread_cond_init(&cond_main, NULL);
		so_sched->t_run = t_new;
		so_sched->t_run->time_quantum = so_sched->time_quantum;

		while (so_sched->t_run != NULL)
			pthread_cond_wait(&cond_main, &mtx);

		pthread_mutex_unlock(&mtx);

	} else if (t_new->prio > so_sched->t_run->prio) {
		t_info *was = so_sched->t_run;

		so_sched->q_t_ready = enq_robin(so_sched->q_t_ready, was);
		so_sched->t_run = t_new;
		so_sched->t_run->time_quantum = so_sched->time_quantum;

		while (so_sched->t_run != NULL && so_sched->t_run->t != was->t)
			pthread_cond_wait(&was->cond, &mtx);

	} else {
		so_sched->q_t_ready = enq_robin(so_sched->q_t_ready, t_new);
		so_exec();
	}

	return t_new->t;
}

/*
 * waits for an IO device
 * + device index
 * returns: -1 if the device does not exist or 0 on success
 */
int so_wait(unsigned int io)
{
	if (!(io >= 0 && io < so_sched->io_devices))
		return -1;

	so_sched->t_run->wait = io;
	so_sched->q_t_waiting = enq_basic(so_sched->q_t_waiting, so_sched->t_run);
	t_info *was = so_sched->t_run;

	so_sched->t_run = so_sched->q_t_ready->head->t_details;
	so_sched->q_t_ready = remove_head(so_sched->q_t_ready);
	so_sched->t_run->time_quantum = so_sched->time_quantum;

	pthread_mutex_unlock(&mtx);

	pthread_cond_signal(&so_sched->t_run->cond);

	pthread_mutex_lock(&mtx);

	while (so_sched->t_run != NULL && so_sched->t_run->t != was->t)
		pthread_cond_wait(&was->cond, &mtx);

	return 0;
}

/*
 * signals an IO device
 * + device index
 * return the number of tasks woke or -1 on error
 */
int so_signal(unsigned int io)
{
	if (!(io >= 0 && io < so_sched->io_devices))
		return -1;

	if (!so_sched->q_t_waiting) {
		so_exec();

		return 0;
	}

	int woken = 0;
	t_queue *wake_threads = NULL;
	t_node *curr = so_sched->q_t_waiting->head, *prev = NULL;

	while (curr) {
		if (curr->t_details->wait == io) {
			++woken;
			wake_threads = enq_robin(wake_threads, curr->t_details);
			t_node *aux = curr;

			curr = curr->next;

			if (prev)
				prev->next = curr;

			free(aux);
		} else {
			if (!prev)
				so_sched->q_t_waiting->head = curr;

			prev = curr;
			curr = curr->next;
		}
	}

	if (!prev) {
		so_sched->q_t_waiting->head = NULL;
		free(so_sched->q_t_waiting);
		so_sched->q_t_waiting = NULL;
	}

	if (!wake_threads) {
		so_exec();

		return woken;
	}

	if (!(wake_threads->head->t_details->prio > so_sched->t_run->prio)) {
		while (wake_threads) {
			wake_threads->head->t_details->wait = -1;
			t_info *head_details = wake_threads->head->t_details;

			so_sched->q_t_ready = enq_robin(so_sched->q_t_ready, head_details);
			wake_threads = remove_head(wake_threads);
		}

		return woken;
	}

	so_sched->q_t_ready = enq_robin(so_sched->q_t_ready, so_sched->t_run);
	t_info *was = so_sched->t_run;

	wake_threads->head->t_details->wait = -1;
	so_sched->t_run = wake_threads->head->t_details;
	wake_threads = remove_head(wake_threads);

	while (wake_threads) {
		wake_threads->head->t_details->wait = -1;
		t_info *head_details = wake_threads->head->t_details;

		so_sched->q_t_ready = enq_robin(so_sched->q_t_ready, head_details);
		wake_threads = remove_head(wake_threads);
	}

	so_sched->t_run->time_quantum = so_sched->time_quantum;

	pthread_mutex_unlock(&mtx);

	pthread_cond_signal(&so_sched->t_run->cond);

	pthread_mutex_lock(&mtx);

	while (so_sched->t_run != NULL && so_sched->t_run->t != was->t)
		pthread_cond_wait(&was->cond, &mtx);

	return woken;
}

/*
 * does whatever operation
 */
void so_exec(void)
{
	--so_sched->t_run->time_quantum;

	if (so_sched->t_run->time_quantum)
		return;

	t_queue *q_t_ready = so_sched->q_t_ready;

	if (q_t_ready) {
		int q_t_ready_prio = q_t_ready->head->t_details->prio;
		int q_t_run_prio = so_sched->t_run->prio;

		if (q_t_ready_prio >= q_t_run_prio) {
			q_t_ready = enq_robin(q_t_ready, so_sched->t_run);
			t_info *was = so_sched->t_run;

			so_sched->t_run = q_t_ready->head->t_details;
			q_t_ready = remove_head(q_t_ready);
			so_sched->t_run->time_quantum = so_sched->time_quantum;

			pthread_mutex_unlock(&mtx);

			pthread_cond_signal(&so_sched->t_run->cond);

			pthread_mutex_lock(&mtx);

			while (so_sched->t_run != NULL && so_sched->t_run->t != was->t)
				pthread_cond_wait(&was->cond, &mtx);

		}

	} else {
		so_sched->t_run->time_quantum = so_sched->time_quantum;
	}

}

/*
 * destroys a scheduler
 */
void so_end(void)
{
	pthread_mutex_lock(&mtx);

	if (!so_sched) {
		pthread_mutex_unlock(&mtx);

		return;
	}

	if (so_sched->t_run) {
		pthread_join(so_sched->t_run->t, NULL);
		pthread_cond_destroy(&so_sched->t_run->cond);
		free(so_sched->t_run);
		so_sched->t_run = NULL;
	}

	if (so_sched->q_t_done) {
		free_queue(so_sched->q_t_done);
		so_sched->q_t_done = NULL;
	}

	if (so_sched->q_t_ready) {
		free_queue(so_sched->q_t_ready);
		so_sched->q_t_ready = NULL;
	}

	if (so_sched->q_t_waiting) {
		free_queue(so_sched->q_t_waiting);
		so_sched->q_t_waiting = NULL;
	}

	free(so_sched);
	so_sched = NULL;

	pthread_mutex_unlock(&mtx);

}

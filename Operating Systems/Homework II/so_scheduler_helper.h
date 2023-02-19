#ifndef SO_SCHEDULER_HELPER_H

#define SO_SCHEDULER_HELPER_H

#include <stdio.h>
#include <pthread.h>
#include "util/so_scheduler.h"

typedef struct t_info {
	tid_t t;
	int time_quantum;
	int run_time_left;
	int prio;
	int joined;
	int wait;
	pthread_cond_t cond;
} t_info;

typedef struct t_start_thread {
	t_info *t;
	so_handler *func;
} t_start_thread;

typedef struct t_node {
	struct t_node *next;
	t_info *t_details;
} t_node;

typedef struct t_queue {
	t_node *head;
} t_queue;

typedef struct {
	t_queue *q_t_ready, *q_t_waiting, *q_t_done;
	t_info *t_run;
	int time_quantum;
	int io_devices;
} so_scheduler_t;

/* Free a certain queue of threads including join. */
t_queue *free_queue(t_queue *clean_queue);

/* Enque a thread into the queue of READY threads based on round robin algorithm. */
t_queue *enq_robin(t_queue *queue, t_info *t_new);

/*Enque a thread into the queue of WAITING/TERMINATED threads based on ordinary cronological time. */
t_queue *enq_basic(t_queue *queue, t_info *t_new);

/* Deque the head of a certain queue*/
t_queue *remove_head(t_queue *queue);

/* Wake threads waiting for a certain signal. */
t_queue *wake_signaled(t_queue *queue, unsigned int io);

/* Remove woken threads from WAITING queue*/
t_queue *remove_signaled(t_queue *queue);

/* remove threads from a certain queue */
t_queue *remove_queue(t_queue *queue, t_queue *aux_queue);

#endif

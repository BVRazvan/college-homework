/*
 * Loader Implementation
 *
 * 2022, Operating Systems
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "exec_parser.h"
#include "segment_operations.h"

static so_exec_t *exec;
static void (*default_handler)(int, siginfo_t *, void *);
static int fd;

/**
 * SEG_FAULT handler which allows demand paging. Divided the handler in 5 steps:
 * 1. Check if the page causing SEG_FAULT lies in a segment;
 * 2. Check if page was already mapped. If not map it, otherwaise raise
 * SEG_FAULT;
 * 3. Use mmap for allocating memory in virtual space for the page fault;
 * 4. If the size of the segment is larger in virtual space than its actual
 * size there can be addresses to be filled with zero in fault page;
 * 5. In the end, data will be written from fileto the virtual memory.
 */
static void segv_handler(int signum, siginfo_t *info, void *context)
{
	/* TODO - actual loader implementation */

	/* 1 */
	/* Check if address causing SEG_FAULT lies inside a segment. */
	int found = 0;

	for (int i = 0; i < exec->segments_no; ++i) {
		uintptr_t sg_vaddr = exec->segments[i].vaddr;
		u_int sg_F_sz = exec->segments[i].file_size;
		u_int sg_M_sz = exec->segments[i].mem_size;
		u_int fault_addr = (u_int)info->si_addr;
		void *data = exec->segments[i].data;

		if (interv(fault_addr, sg_vaddr, sg_M_sz)) {
			u_int sg_page = (fault_addr - sg_vaddr) / page_sz;
			u_int fault_page = fault_addr / page_sz * page_sz;

			/* 2 */
			/*	Check if page was already mapped. If not, map it
			 *	otherwaise raise SEG_FAULT.
			 */
			if (unmapped(&data, (sg_M_sz + page_sz - 1) / page_sz, sg_page)) {
				free_segments(exec);
				default_handler(signum, info, context);
			} else {
				if (exec->segments[i].data != data)
					exec->segments[i].data = data;
			}

			/* 3 */
			/*  Use mmap for allocating memory in virtual space for the page.
			 *	fault.
			 *	File descriptor is opened initially with write permission,
			 *	then modified to segment's permissions.
			 */
			int flg = MAP_SHARED | MAP_FIXED | MAP_ANONYMOUS;
			void *addr = mmap((void *)fault_page, page_sz, PERM_W, flg, fd, 0);

			if (addr == MAP_FAILED) {
				write(STDERR_FILENO, "mmap error!\n", 13);
				free_segments(exec);
				default_handler(signum, info, context);
			}

			/* 4 */
			/**
			 *  If the size of the segment is larger in virtual space than its
			 *	actual size there can be addresses to be filled with zero in fault
			 *	page.
			 */
			if (sg_M_sz > sg_F_sz)
				do_zero(sg_vaddr, sg_F_sz, sg_M_sz, fault_page);

			/* 5 */
			/*	In the end, data will be written from file to the virtual memory */
			int to_read = get_data_sz(sg_vaddr, sg_F_sz, fault_page);

			if (add_sg(sg_page, fault_page,
									exec->segments[i].offset, to_read, fd)) {
				free_segments(exec);
				default_handler(signum, info, context);
			}

			mprotect((void *)fault_page, page_sz, exec->segments[i].perm);
			found = 1;
		}
	}
	if (found == 0) {
		free_segments(exec);
		default_handler(signum, info, context);
	}
}

int so_init_loader(void)
{
	/* TODO: initialize on-demand loader */
	struct sigaction sa;
	struct sigaction def;

	memset(&sa, 0, sizeof(sa));
	sa.sa_flags = SA_SIGINFO;
	sa.sa_handler = (void *)segv_handler;

	if (sigaction(SIGSEGV, &sa, &def) == -1) {
		write(STDERR_FILENO, "sigaction error!\n", 18);
		exit(-1);
	}
	default_handler = (void *)def.sa_handler;
	return 0;
}

int so_execute(char *path, char *argv[])
{
	exec = so_parse_exec(path);
	if (!exec)
		return -1;

	fd = open(path, O_RDONLY);

	so_start_exec(exec, argv);
	
	close(fd);
	free_segments(exec);

	return -1;
}

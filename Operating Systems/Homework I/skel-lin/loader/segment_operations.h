/*
 * Executable Parser Header
 *
 * 2022, Operating Systems
 */

#ifndef CHECK_INTERVAL_H

#include <stdint.h>
#include "exec_parser.h"

#define CHECK_INTERVAL_H
#define page_sz 4096
#define u_int uintptr_t

/* Check if an address lies inside a segment [x; x + add - 1]. */
int interv(u_int addr, u_int x, u_int add);

/* Check if a particular page is not already mapped. */
int unmapped(void **data, int nr_pages, int nth_page_local);

/* FIll with zero the memory of a page inside a segment, which lies between actual size and allocated size. */
int do_zero(u_int sg_vaddr, u_int sg_F_sz, u_int sg_M_sz, u_int fault_page);

/* Get the actual size of data which will be loaded in virtual memory. */
int get_data_sz(u_int sg_vaddr, u_int sg_F_sz, u_int fault_page);

/* Load executable's data in virtual memory. */
int add_sg(u_int sg_page, u_int fault_page, u_int offset, int to_read, int fd);

/* Free allocated data for each segment. */
void free_segments(so_exec_t *exec);

#endif

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include "segment_operations.h"

/**
 * Check if an address lies inside a segment [x; x + sz - 1].
 *
 * @addr : address;
 * @x    : left bound;
 * @add  : segment's length.
 *
 * Return 1 when address is inside the segment, 0 otherwise.
 */
int interv(u_int addr, u_int x, u_int sz)
{
	return (addr >= x && addr <= x + sz - 1);
}

/**
 * Check if a particular page inside a segment is not mapped and maps it.
 * First check implies allocating a char for each page.
 * 1 - mapped; 0 - not mapped
 *
 * @data           : vector of chars;
 * @nr_pages       : number of pages inside the segment;
 * @nth_page_local : the page which is to be check.
 *
 * Return 0 if page was not mapped already, -1/-2 otherwise.
 */
int unmapped(void **data, int nr_pages, int nth_page_local)
{
	if (!(*data)) {
		*data = calloc(nr_pages, sizeof(char));
		if (!(*data)) {
			write(STDERR_FILENO, "calloc() failed!\n", 17);
			return -1;
		}
	}
	char *parc = (char *)(*data);

	if (parc[nth_page_local] == 1)
		return -2;
	parc[nth_page_local] = 1;
	return 0;
}

/**
 * Having a segment with larger memory size in virtual space than actual size
 * it implies filling with zero the addresses between [file_size; mem_size].
 *
 * For the moment, a particular page is to be verified in 4 cases.
 * Before describing the cases, the following notations have to be introduced:
 *      -F_S = actual size of the segment;
 *      -M_S = allocated size for the segment;
 *      -S_ADDR = the address where actual segment starts.
 *
 * As said, being in the given page(fault_page), it must be filled with zero
 * in the region [file_size; mem_size]. Those 4 case are:
 *
 * I.
 *    [------------------S_ADDR+F_S---------------S_ADDR+M_S------------------]
 *    |-------------------------> fault page <--------------------------------|
 *
 * In this case, it is clear that there are S_ADDR+M_S - S_ADDR+F_S bytes to be
 * filled with zero, starting from S_ADDR+F_S.
 *
 * II.
 *    [-------S_ADDR+F_S--------|..|--------------S_ADDR+M_S-------------------]
 *    |-----> fault page <------|##|--> further page, does not matter which <--|
 *
 * In this case, all the bytes remaining from the page starting from S_ADDR+F_S
 * will be filled with zero.
 *
 * III.
 *    [----------------S_ADDR+F_S--------------- |..|--------S_ADDR+M_S-------]
 *    |-> previous page, does not matter which <-|##|-----> fault page <------|
 *
 * In this case, S_ADDR+F_S is in an previous page, so bytes from fault page
 * start to S_ADDR+M_S will be filled with zero.
 *
 * IV.
 *    [-----------S_ADDR+F_S-------------------- |..|----------------|..|-------------S_ADDR+M_S------------------]
 *    |-> previous page, does not matter which <-|##|-> fault page <-|##|-> further page, does not matter which <-|
 *
 * In this case, fault page lies between S_ADDR+F_S's page and S_ADDR+M_S's page
 * so the entire page will be filled with zero.
 *
 * @sg_vaddr  : segment's starting address;
 * @sg_F_sz   : segment's file size;
 * @sg_M_sz   : segment's allocated memory size;
 * fault_page : page in which error address lies.
 *
 * Return;
 */
int do_zero(u_int sg_vaddr, u_int sg_F_sz, u_int sg_M_sz, u_int fault_page)
{
	if (interv(sg_vaddr + sg_F_sz, fault_page, page_sz)) {
		if (interv(sg_vaddr + sg_M_sz - 1, fault_page, page_sz))
			memset((void *)(sg_vaddr + sg_F_sz), 0, sg_M_sz - sg_F_sz);
		else if (sg_vaddr + sg_M_sz >= fault_page + page_sz)
			memset((void *)(sg_vaddr + sg_F_sz), 0,
			fault_page + page_sz - (sg_vaddr + sg_F_sz));
	} else if (sg_vaddr + sg_F_sz < fault_page) {
		if (interv(sg_vaddr + sg_M_sz - 1, fault_page, page_sz))
			memset((void *)fault_page, 0, sg_vaddr + sg_M_sz - fault_page);
		else if (sg_vaddr + sg_M_sz >= fault_page + page_sz)
			memset((void *)fault_page, 0, page_sz);
	}
	return 0;
}

/**
 * Having the page which caused page fault, the data has to be loaded in
 * virtual space and this function gets the size of the data to be introduced.
 *
 * There are 3 cases which can appear.
 * Before describing the cases, the following notations have to be introduced:
 *      -F_S = actual size of the segment;
 *      -S_ADDR = the address where actual segment starts.
 *
 * I.
 *    [------------------S_ADDR+F_S-------------------------------------------]
 *    |-------------------------> fault page <--------------------------------|
 *
 * In this case, the bytes from the starting of the page to the S_ADDR+F_S
 * address have to be loaded into memory.
 *
 * II.
 *    [-----------S_ADDR+F_S-------------------- |..|-------------------------|
 *    |-> previous page, does not matter which <-|##|-----> fault page <------|
 *
 * In this case, S_ADDR+F_S address is somewhere in a previous page, so there
 * are no bytes to be written.
 *
 * III.
 *    |--------------------------|..|-------------S_ADDR+F_S------------------]
 *    |-------> fault page <-----|##|-> further page, does not matter which <-|
 *
 * In this case the fault page lies between the start of the segment and
 * the end of it, so the entire page will be written with data.
 *
 * @sg_vaddr   : the address at the start of the segment;
 * @sg_F_sz    : the file size of the segment;
 * @fault_page : the page in which page fault appeared.
 *
 * Return the size of the data to read into the buffer(virtual space).
 */
int get_data_sz(u_int sg_vaddr, u_int sg_F_sz, u_int fault_page)
{
int to_read;

	if (interv(sg_vaddr + sg_F_sz, fault_page, page_sz))
		to_read = sg_vaddr + sg_F_sz - fault_page;
	else if (sg_vaddr + sg_F_sz < fault_page)
		to_read = 0;
	else
		to_read = page_sz;
	return to_read;
}

/**
 * Read data from the segment into the page which caused page fault.
 *
 * @sg_page    : size of the page;
 * @fault_page : the page where data is placed;
 * @offset     : offset from the beginning of the executable where segments start;
 * @to_read    : number of bytes to read into memory;
 * @fd         : file descriptor of executable.
 *
 * Return 0 if all bytes were put in virtual space, -1/-2 otherwise.
 */
int add_sg(u_int sg_page, u_int fault_page, u_int offset, int to_read, int fd)
{
	u_int read_now = 0, did_read = 0;

	if (lseek(fd, offset + sg_page * page_sz, SEEK_SET) == -1)
		return -1;
	while (to_read) {
		read_now = read(fd, (void *)(fault_page + did_read), to_read);
		if (read_now == -1)
			return -2;
		to_read -= read_now;
		did_read += read_now;
	}
	return 0;
}

/**
 * Free data allocated for each segment
 *
 * @exec : executable file.
 *
 * Return;
 */
void free_segments(so_exec_t *exec)
{
	for (int i = 0; i < exec->segments_no; ++i) {
		if (exec->segments[i].data)
			free(exec->segments[i].data);
	}
}

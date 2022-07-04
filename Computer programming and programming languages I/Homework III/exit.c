// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "exit.h"
#include "mat_free.h"

// Freeing the memory in the end.

void prog_end(FILE *in, struct mat *A, struct mat *B, char *line)
{
	if (!in)
		printf("No image loaded\n");
	free_mat(A, 'c');
	free_mat(B, 'd');
	free(line);
}


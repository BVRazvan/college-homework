// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "mat_free.h"

// Freeing the memory of previous image.

void free_mat(struct mat *A, char type)
{
	if (type == 'c') {
		for (int i = 0; i < A->r; ++i)
			free(A->ch[i]);
		if (A->r > 0)
			free(A->ch);
		A->c = 0, A->r = 0, A->x1 = 0, A->x2 = 0, A->y1 = 0, A->y2 = 0;
	} else if (type == 'd') {
		for (int i = 0; i < A->r; ++i)
			free(A->d[i]);
		if (A->r > 0)
			free(A->d);
		A->c = 0, A->r = 0, A->x1 = 0, A->x2 = 0, A->y1 = 0, A->y2 = 0;
	}
}


// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "crop.h"
#include "mat_free.h"

struct mat crop(FILE **in, struct mat *A, int *ok)
{
	if (!*in) {
		printf("No image loaded\n");
		return *A;
	}
	// Cropping matrix A into matrix B.
	struct mat B;
	// Getting new dimensions of the matrix.
	int new_r = A->y2 - A->y1, new_c = A->x2 - A->x1;
	B.c = new_c, B.r = new_r;
	B.x1 = 0, B.y1 = 0, B.y2 = new_r, B.x2 = new_c;
	B.type = A->type, B.max_value = A->max_value;
	// Checking malloc results.
	B.d = (struct pixel_d **)malloc(new_r * sizeof(struct pixel_d *));
	if (!B.d) {
		*ok = 0;
		return *A;
	}
	for (int i = 0; i < new_r; ++i) {
		B.d[i] = (struct pixel_d *)malloc(new_c * sizeof(struct pixel_d));
		if (!B.d[i]) {
			*ok = 0;
			return *A;
		}
		// Applying crop action.
		for (int j = 0; j < new_c; ++j)
			B.d[i][j] = A->d[i + A->y1][j + A->x1];
	}
	free_mat(A, 'd');
	printf("Image cropped\n");
	return B;
}


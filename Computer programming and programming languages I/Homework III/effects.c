// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005
#define MAT_DIM 3
#define EFF_DIM 4

#include "effects.h"
#include "photo.h"

// Defining apply filters.

double **define_edge(int *ok)
{
	double **A;
	A = (double **)malloc(MAT_DIM * sizeof(double *));
	if (!A) {
		*ok = 0;
		return A;
	}
	for (int i = 0; i < MAT_DIM; ++i) {
		A[i] = (double *)malloc(MAT_DIM * sizeof(double));
		if (!A[i]) {
			*ok = 0;
			return A;
		}
	}
	A[0][0] = -1.0, A[0][1] = -1.0, A[0][2] = -1.0;
	A[1][0] = -1.0, A[1][1] = 8.0, A[1][2] = -1.0;
	A[2][0] = -1.0, A[2][1] = -1.0, A[2][2] = -1.0;
	return A;
}

double **define_sharpen(int *ok)
{
	double **A;
	A = (double **)malloc(MAT_DIM * sizeof(double *));
	if (!A) {
		*ok = 0;
		return A;
	}
	for (int i = 0; i < MAT_DIM; ++i) {
		A[i] = (double *)malloc(MAT_DIM * sizeof(double));
		if (!A[i]) {
			*ok = 0;
			return A;
		}
	}
	A[0][0] = 0.0, A[0][1] = -1.0, A[0][2] = 0.0;
	A[1][0] = -1.0, A[1][1] = 5.0, A[1][2] = -1.0;
	A[2][0] = 0.0, A[2][1] = -1.0, A[2][2] = 0.0;
	return A;
}

double **define_blur(int *ok)
{
	double **A;
	A = (double **)malloc(MAT_DIM * sizeof(double *));
	if (!A) {
		*ok = 0;
		return A;
	}
	for (int i = 0; i < MAT_DIM; ++i) {
		A[i] = (double *)malloc(MAT_DIM * sizeof(double));
		if (!A[i]) {
			*ok = 0;
			return A;
		}
	}
	A[0][0] = 0.1111, A[0][1] = 0.1111, A[0][2] = 0.1111;
	A[1][0] = 0.1111, A[1][1] = 0.1111, A[1][2] = 0.1111;
	A[2][0] = 0.1111, A[2][1] = 0.1111, A[2][2] = 0.1111;
	return A;
}

double **define_g_blur(int *ok)
{
	double **A;
	A = (double **)malloc(MAT_DIM * sizeof(double *));
	if (!A) {
		*ok = 0;
		return A;
	}
	for (int i = 0; i < MAT_DIM; ++i) {
		A[i] = (double *)malloc(MAT_DIM * sizeof(double));
		if (!A[i]) {
			*ok = 0;
			return A;
		}
	}
	A[0][0] = 0.0625, A[0][1] = 0.125, A[0][2] = 0.0625;
	A[1][0] = 0.125, A[1][1] = 0.25, A[1][2] = 0.125;
	A[2][0] = 0.0625, A[2][1] = 0.125, A[2][2] = 0.0625;
	return A;
}

// Freeing the memory in the end.

void free_filters(struct fil *filters)
{
	for (int i = 0; i < EFF_DIM; ++i) {
		for (int j = 0; j < MAT_DIM; ++j)
			free(filters[i].f[j]);
		free(filters[i].f);
	}
	free(filters);
}

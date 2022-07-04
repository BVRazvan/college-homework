// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define DIM 1005

#include "apply.h"
#include "photo.h"
#include "effects.h"
#include "mat_free.h"

double clamp(double x, int mn, int mx)
{
	if (x < mn)
		return 1.0 * mn;
	if (x > mx)
		return 1.0 * mx;
	return x;
}

void mult_mat(struct mat *A, struct mat *B, int x, int y, double **C)
{
	// Apllying filters.
	int ok = 1;
	double s_r = 0.0, s_g = 0.0, s_b = 0.0;
	for (int i = x - 1; i <= x + 1; ++i) {
		for (int j = y - 1; j <= y + 1; ++j) {
			if (i < 0 || i >= A->r || j < 0 || j >= A->c) {
				ok = 0;
				break;
			}
			s_r += 1.0 * A->d[i][j].r * C[i - x + 1][j - y + 1];
			s_g += 1.0 * A->d[i][j].g * C[i - x + 1][j - y + 1];
			s_b += 1.0 * A->d[i][j].b * C[i - x + 1][j - y + 1];
		}
	}
	if (ok == 1) {
		B->d[x][y].r = clamp(s_r, 0, B->max_value);
		B->d[x][y].g = clamp(s_g, 0, B->max_value);
		B->d[x][y].b = clamp(s_b, 0, B->max_value);
	} else {
		B->d[x][y] = A->d[x][y];
	}
}

void apply_eff(struct mat *A, struct mat *B, struct fil *eff, int idx)
{
	// Initially copying matrix A into B.
	for (int i = 0; i < A->r; ++i)
		for (int j = 0; j < A->c; ++j)
			B->d[i][j] = A->d[i][j];
	if (A->type == 1 || A->type == 3)
		return;
	// Applying the filters.
	for (int i = A->y1; i < A->y2; ++i)
		for (int j = A->x1; j < A->x2; ++j)
			mult_mat(A, B, i, j, eff[idx].f);
}

void output_type(int type, int val)
{
	// Different outputs.
	if (type == 1 || type == 3) {
		printf("Easy, Charlie Chaplin\n");
	} else {
		if (val == 0)
			printf("APPLY EDGE done\n");
		else if (val == 1)
			printf("APPLY SHARPEN done\n");
		else if (val == 2)
			printf("APPLY BLUR done\n");
		else
			printf("APPLY GAUSSIAN_BLUR done\n");
	}
}

struct mat apply(char *cmd, FILE *in, struct mat *A, struct fil *eff, int *ok)
{
	if (!in) {
		printf("No image loaded\n");
		return *A;
	}
	// I apply the filters in matrix B, then I return it as result.
	struct mat B;
	memset(&B, 0, sizeof(B));
	B.c = A->c, B.r = A->r;
	B.x1 = A->x1, B.y1 = A->y1;
	B.x2 = A->x2, B.y2 = A->y2;
	// Checking malloc results.
	B.d = (struct pixel_d **)malloc(B.r * sizeof(struct pixel_d *));
	if (!B.d) {
		*ok = 0;
		free_mat(&B, 'd');
		return *A;
	}
	B.type = A->type, B.max_value = A->max_value;
	for (int i = 0; i < B.r; ++i) {
		B.d[i] = (struct pixel_d *)malloc(B.c * sizeof(struct pixel_d));
		if (!B.d[i]) {
			*ok = 0;
			free_mat(&B, 'd');
			return *A;
		}
	}
	// Getting the filter name.
	cmd = strtok(NULL, " ");
	if (!cmd) {
		printf("Invalid command\n");
		free_mat(&B, 'd');
		return *A;
	}
	// Checking if the filter is in set.
	if (strcmp(cmd, "EDGE") == 0) {
		apply_eff(A, &B, eff, 0);
		output_type(A->type, 0);
	} else if (strcmp(cmd, "SHARPEN") == 0) {
		output_type(A->type, 1);
		apply_eff(A, &B, eff, 1);
	} else if (strcmp(cmd, "BLUR") == 0) {
		output_type(A->type, 2);
		apply_eff(A, &B, eff, 2);
	} else if (strcmp(cmd, "GAUSSIAN_BLUR") == 0) {
		output_type(A->type, 3);
		apply_eff(A, &B, eff, 3);
	} else {
		printf("APPLY parameter invalid\n");
		free_mat(&B, 'd');
		return *A;
	}
	free_mat(A, 'd');
	return B;
}

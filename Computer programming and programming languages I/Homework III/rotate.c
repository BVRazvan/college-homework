// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "mat_free.h"
#include "rotate.h"

// Copying matrix A into matrix B.

void copy_mat_all(struct mat *A, struct mat *B, int *ok)
{
	B->c = A->r, B->r = A->c;
	B->x1 = A->y1, B->y1 = A->x1;
	B->x2 = A->y2, B->y2 = A->x2;
	B->d = (struct pixel_d **)malloc(B->r * sizeof(struct pixel_d *));
	if (!B->d) {
		*ok = 0;
		return;
	}
	B->type = A->type, B->max_value = A->max_value;
	for (int i = 0; i < B->r; ++i) {
		B->d[i] = (struct pixel_d *)malloc(B->c * sizeof(struct pixel_d));
		if (!B->d[i]) {
			*ok = 0;
			return;
		}
	}
}

// Applying rotate on entire image.

void rotate_90_all(struct mat *A, struct mat *B)
{
	for (int i = A->y1; i < A->y2; ++i)
		for (int j = A->x2 - 1; j >= A->x1; --j)
			B->d[j][A->y2 - i - 1] = A->d[i][j];
}

void rotate_180_all(struct mat *A, struct mat *B)
{
	for (int j = A->x2 - 1; j >= 0; --j)
		for (int i = A->y2 - 1; i >= 0; --i)
			B->d[A->y2 - 1 - i][A->x2 - 1 - j] = A->d[i][j];
}

void rotate_270_all(struct mat *A, struct mat *B)
{
	for (int i = A->y1; i < A->y2; ++i)
		for (int j = A->x2 - 1; j >= A->x1; --j)
			B->d[A->x2 - j - 1][i] = A->d[i][j];
}

// Copying matrix A into matrix B.

void copy_mat_square(struct mat *A, struct mat *B, int *ok)
{
	B->c = A->c, B->r = A->r;
	B->x1 = A->x1, B->y1 = A->y1;
	B->x2 = A->x2, B->y2 = A->y2;
	B->d = (struct pixel_d **)malloc(B->r * sizeof(struct pixel_d *));
	if (!B->d) {
		*ok = 0;
		return;
	}
	B->type = A->type, B->max_value = A->max_value;
	for (int i = 0; i < B->r; ++i) {
		B->d[i] = (struct pixel_d *)malloc(B->c * sizeof(struct pixel_d));
		if (!B->d[i]) {
			*ok = 0;
			return;
		}
	}
}

// Applying rotate on square submatrix.

void rotate_90_square(struct mat *A, struct mat *B)
{
	for (int j = A->x1; j < A->x2; ++j)
		for (int i = A->y2 - 1; i >= A->y1; --i)
			B->d[A->y1 + j - A->x1][A->x1 + A->y2 - 1 - i] = A->d[i][j];
}

void rotate_180_square(struct mat *A, struct mat *B)
{
	for (int j = A->x2 - 1; j >= A->x1; --j)
		for (int i = A->y2 - 1; i >= A->y1; --i)
			B->d[A->y1 + A->y2 - 1 - i][A->x1 + A->x2 - 1 - j] = A->d[i][j];
}

void rotate_270_square(struct mat *A, struct mat *B)
{
	for (int j = A->x1; j < A->x2; ++j)
		for (int i = A->y2 - 1; i >= A->y1; --i)
			B->d[A->y1 + A->x2 - 1 - j][A->x1 + i - A->y1] = A->d[i][j];
}

struct mat rotate(char *cmd, FILE **in, struct mat *A, int *ok)
{
	struct mat B;
	if (!*in) {
		printf("No image loaded\n");
		return *A;
	}
	int sign = 1;
	cmd = strtok(NULL, " ");
	if (strchr(cmd, '-'))
		sign *= -1;
	int angle = 0, sz = strlen(cmd);
	// Getting the angle's value.
	for (int i = 0; i < sz; ++i)
		if (cmd[i] >= '0' && cmd[i] <= '9')
			angle = angle * 10 + (cmd[i] - '0');
	if (angle % 90 != 0) {
		printf("Unsupported rotation angle\n");
		return *A;
	}
	angle *= sign;
	printf("Rotated %d\n", angle);
	// We can only treat cases when angle is +0, +90, +180, +270 because
	// negative angles' value is the same as 360 + (negative angle).
	angle %= 360;
	if (angle < 0)
		angle += 360;
	// Checking if we rotate the entire image, so image's dimensions may change
	if ((A->x2 - A->x1) * (A->y2 - A->y1) == A->c * A->r) {
		if (angle == 0)
			return *A;
		if (angle == 90) {
			copy_mat_all(A, &B, ok);
			if (!*ok)
				return *A;
			rotate_90_all(A, &B), free_mat(A, 'd');
			return B;
		} else if (angle == 180) {
			copy_mat_square(A, &B, ok);
			if (!*ok)
				return *A;
			rotate_180_all(A, &B), free_mat(A, 'd');
			return B;
		} else if (angle == 270) {
			copy_mat_all(A, &B, ok);
			if (!*ok)
				return *A;
			rotate_270_all(A, &B), free_mat(A, 'd');
			return B;
		} else {
			return *A;
		}
		// Checking if we rotate just a square submatrix of image.
	} else if (A->x2 - A->x1 == A->y2 - A->y1) {
		copy_mat_square(A, &B, ok);
		if (!*ok)
			return *A;
		for (int i = 0; i < B.r; ++i)
			for (int j = 0; j < B.c; ++j)
				B.d[i][j] = A->d[i][j];
		if (angle == 0) {
			free_mat(A, 'd');
			return B;
		}
		if (angle == 90) {
			rotate_90_square(A, &B), free_mat(A, 'd');
			return B;
		} else if (angle == 180) {
			rotate_180_square(A, &B), free_mat(A, 'd');
			return B;
		} else if (angle == 270) {
			rotate_270_square(A, &B), free_mat(A, 'd');
			return B;
		} else {
			return *A;
		}
	} else {
		printf("Unsupported angle rotation\n");
		return *A;
	}
	return *A;
}


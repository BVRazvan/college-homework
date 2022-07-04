// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "read_mats.h"

// Reading image's values and checking malloc() result.

void read_gray_mat_txt(FILE **in, struct mat *A, int *ok)
{
	A->ch = NULL;
	A->ch = (struct pixel_ch **)malloc(A->r * sizeof(struct pixel_ch *));
	if (!A->ch) {
		*ok = 0;
		return;
	}
	for (int i = 0; i < A->r; ++i)
		A->ch[i] = NULL;
	for (int i = 0; i < A->r; ++i) {
		A->ch[i] = (struct pixel_ch *)malloc(A->c * sizeof(struct pixel_ch));
		if (!A->ch[i]) {
			*ok = 0;
			return;
		}
	}
	for (int i = 0; i < A->r; ++i) {
		for (int j = 0; j < A->c; ++j) {
			memset(&A->ch[i][j], 0, sizeof(A->ch[i][j]));
			fscanf(*in, "%hhu", &A->ch[i][j].gray);
		}
	}
	A->type = 1;
}

void read_color_mat_txt(FILE **in, struct mat *A, int *ok)
{
	A->ch = NULL;
	A->ch = (struct pixel_ch **)malloc(A->r * sizeof(struct pixel_ch *));
	if (!A->ch) {
		*ok = 0;
		return;
	}
	for (int i = 0; i < A->r; ++i)
		A->ch[i] = NULL;
	for (int i = 0; i < A->r; ++i) {
		A->ch[i] = (struct pixel_ch *)malloc(A->c * sizeof(struct pixel_ch));
		if (!A->ch[i]) {
			*ok = 0;
			return;
		}
	}
	for (int i = 0; i < A->r; ++i) {
		for (int j = 0; j < A->c; ++j) {
			memset(&A->ch[i][j], 0, sizeof(A->ch[i][j]));
			fscanf(*in, "%hhu", &A->ch[i][j].r);
			fscanf(*in, "%hhu", &A->ch[i][j].g);
			fscanf(*in, "%hhu", &A->ch[i][j].b);
		}
	}
	A->type = 2;
}

void read_gray_mat_bin(FILE **in, struct mat *A, int *ok)
{
	A->ch = NULL;
	A->ch = (struct pixel_ch **)malloc(A->r * sizeof(struct pixel_ch *));
	if (!A->ch) {
		*ok = 0;
		return;
	}
	for (int i = 0; i < A->r; ++i)
		A->ch[i] = NULL;
	for (int i = 0; i < A->r; ++i) {
		A->ch[i] = (struct pixel_ch *)malloc(A->c * sizeof(struct pixel_ch));
		if (!A->ch[i]) {
			*ok = 0;
			return;
		}
	}
	for (int i = 0; i < A->r; ++i) {
		for (int j = 0; j < A->c; ++j) {
			memset(&A->ch[i][j], 0, sizeof(A->ch[i][j]));
			fread(&A->ch[i][j].gray, sizeof(unsigned char), 1, *in);
		}
	}
	A->type = 3;
}

void read_color_mat_bin(FILE **in, struct mat *A, int *ok)
{
	A->ch = NULL;
	A->ch = (struct pixel_ch **)malloc(A->r * sizeof(struct pixel_ch *));
	if (!A->ch) {
		*ok = 0;
		return;
	}
	for (int i = 0; i < A->r; ++i)
		A->ch[i] = NULL;
	for (int i = 0; i < A->r; ++i) {
		A->ch[i] = (struct pixel_ch *)malloc(A->c * sizeof(struct pixel_ch));
		if (!A->ch[i]) {
			*ok = 0;
			return;
		}
	}
	for (int i = 0; i < A->r; ++i) {
		for (int j = 0; j < A->c; ++j) {
			memset(&A->ch[i][j], 0, sizeof(A->ch[i][j]));
			fread(&A->ch[i][j].r, sizeof(unsigned char), 1, *in);
			fread(&A->ch[i][j].g, sizeof(unsigned char), 1, *in);
			fread(&A->ch[i][j].b, sizeof(unsigned char), 1, *in);
		}
	}
	A->type = 4;
}

// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "parsing.h"
#include "read_mats.h"
#include "load.h"
#include "mat_free.h"

void load(char *filename, FILE **in, struct mat *A, struct mat *B, int *ok)
{
	filename = strtok(NULL, " ");
	filename[strcspn(filename, "\n")] = 0;
	if (*in) {
		free_mat(A, 'c');
		free_mat(B, 'd');
		fclose(*in);
	}
	*in = fopen(filename, "rt");
	if (!*in) {
		printf("Failed to load %s\n", filename);
	} else {
		printf("Loaded %s\n", filename);
		char *type;
		type = (char *)malloc(DIM * sizeof(char));
		if (!type) {
			*ok = 0;
			return;
		}
		// First reading image's dimensions.
		read_dim(in, A, &type);
		// Reading matrix's values depending on "magic word".
		int pos = ftell(*in);
		if (strcmp(type, "P2") == 0) {
			read_gray_mat_txt(in, A, ok);
		} else if (strcmp(type, "P3") == 0) {
			read_color_mat_txt(in, A, ok);
		} else if (strcmp(type, "P5") == 0) {
			fclose(*in);
			*in = fopen(filename, "rb");
			fseek(*in, pos, SEEK_CUR);
			read_gray_mat_bin(in, A, ok);
		} else if (strcmp(type, "P6") == 0) {
			fclose(*in);
			*in = fopen(filename, "rb");
			fseek(*in, pos, SEEK_CUR);
			read_color_mat_bin(in, A, ok);
		}
		B->x1 = A->x1, B->y1 = A->y1, B->x2 = A->x2, B->y2 = A->y2;
		B->r = A->r, B->c = A->c, B->type = A->type;
		B->max_value = A->max_value;
		B->d = NULL;
		B->d = (struct pixel_d **)malloc(B->r * sizeof(struct pixel_d *));
		if (!B->d) {
			*ok = 0;
			return;
		}
		for (int i = 0; i < B->r; ++i)
			B->d[i] = NULL;
		for (int i = 0; i < B->r; ++i) {
			B->d[i] = (struct pixel_d *)malloc(B->c * sizeof(struct pixel_d));
			if (!B->d[i]) {
				*ok = 0;
				return;
			}
			for (int j = 0; j < B->c; ++j) {
				B->d[i][j].r = (double)A->ch[i][j].r;
				B->d[i][j].g = (double)A->ch[i][j].g;
				B->d[i][j].b = (double)A->ch[i][j].b;
				B->d[i][j].gray = (double)A->ch[i][j].gray;
			}
		}
	free(type);
	}
}

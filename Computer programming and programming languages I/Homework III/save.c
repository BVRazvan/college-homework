// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#define DIM 1005

#include "photo.h"
#include "save.h"
#include "effects.h"
#include "apply.h"
#include "mat_free.h"

void copy_mat(struct mat *A, struct mat *B, int *ok)
{
	B->x1 = A->x1, B->x2 = A->x2, B->y1 = A->y1, B->y2 = A->y2;
	B->r = A->r, B->c = A->c, B->type = A->type;
	B->max_value = A->max_value;
	B->ch = NULL;
	B->ch = (struct pixel_ch **)malloc(B->r * sizeof(struct pixel_ch *));
	if (!B->ch) {
		*ok = 0;
		return;
	}
	for (int i = 0; i < B->r; ++i)
		B->ch[i] = NULL;
	for (int i = 0; i < B->r; ++i) {
		B->ch[i] = (struct pixel_ch *)malloc(B->c * sizeof(struct pixel_ch));
		if (!B->ch[i]) {
			*ok = 0;
			return;
		}
		for (int j = 0; j < B->c; ++j) {
			B->ch[i][j].r = clamp(round(A->d[i][j].r), 0, B->max_value);
			B->ch[i][j].g = clamp(round(A->d[i][j].g), 0, B->max_value);
			B->ch[i][j].b = clamp(round(A->d[i][j].b), 0, B->max_value);
			B->ch[i][j].gray = clamp(round(A->d[i][j].gray), 0, B->max_value);
		}
	}
}

void save(char *cmd, FILE **in, struct mat *A, struct mat *B, int *ok)
{
	if (!*in) {
		printf("No image loaded\n");
		return;
	}
	free_mat(B, 'c');
	copy_mat(A, B, ok);
	cmd = strtok(NULL, " ");
	char *file;
	file = (char *)malloc(DIM * sizeof(char));
	if (!file) {
		*ok = 0;
		return;
	}
	strcpy(file, cmd);
	cmd = strtok(NULL, " ");
	FILE *out = NULL;
	file[strcspn(file, "\n")] = 0;
	if (cmd && strstr(cmd, "ascii")) {
		out = fopen(file, "wt");
		if (B->type == 2 || B->type == 4) {
			fprintf(out, "P3\n"), fprintf(out, "%d %d\n", B->c, B->r);
			fprintf(out, "%d\n", B->max_value);
			for (int i = 0; i < B->r; ++i) {
				for (int j = 0; j < B->c; ++j) {
					fprintf(out, "%hhu ", (unsigned char)B->ch[i][j].r);
					fprintf(out, "%hhu ", (unsigned char)B->ch[i][j].g);
					fprintf(out, "%hhu ", (unsigned char)B->ch[i][j].b);
					if (j + 1 != B->c)
						fprintf(out, " ");
				}
				fprintf(out, "\n");
			}
		} else {
			fprintf(out, "P2\n"), fprintf(out, "%d %d\n", B->c, B->r);
			fprintf(out, "%d\n", B->max_value);
			for (int i = 0; i < B->r; ++i) {
				for (int j = 0; j < B->c; ++j) {
					fprintf(out, "%hhu ", (unsigned char)B->ch[i][j].gray);
					if (j + 1 != B->c)
						fprintf(out, " ");
				}
				fprintf(out, "\n");
			}
		}
		fclose(out);
	} else {
		out = fopen(file, "wt");
		if (B->type == 2 || B->type == 4) {
			fprintf(out, "P6\n"), fprintf(out, "%d %d\n", B->c, B->r);
			fprintf(out, "%d\n", B->max_value);
			fclose(out);
			out = fopen(file, "ab");
			for (int i = 0; i < B->r; ++i) {
				for (int j = 0; j < B->c; ++j) {
					fwrite(&B->ch[i][j].r, sizeof(unsigned char), 1, out);
					fwrite(&B->ch[i][j].g, sizeof(unsigned char), 1, out);
					fwrite(&B->ch[i][j].b, sizeof(unsigned char), 1, out);
				}
			}
		} else {
			fprintf(out, "P5\n"), fprintf(out, "%d %d\n", B->c, B->r);
			fprintf(out, "%d\n", B->max_value);
			fclose(out);
			out = fopen(file, "ab");
			for (int i = 0; i < B->r; ++i)
				for (int j = 0; j < B->c; ++j)
					fwrite(&B->ch[i][j].gray, sizeof(unsigned char), 1, out);
		}
		fclose(out);
	}
	printf("Saved %s\n", file);
	free(file);
}

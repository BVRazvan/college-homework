// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "effects.h"
#include "photo.h"
#include "parsing.h"

// Skipping characters till the end of the row.

void skip(FILE **in)
{
	char ch;
	do {
		fscanf(*in, "%c", &ch);
	} while (ch != '\n');
}

// Checking if current line is a commentary.

void check(FILE **in)
{
	char ch;
	fscanf(*in, "%c", &ch);
	if (ch == '#') {
		skip(in);
		check(in);
	} else {
		fseek(*in, -1, SEEK_CUR);
	}
}

// Reading image's dimensions

void read_dim(FILE **in, struct mat *A, char **type)
{
	check(in);
	fscanf(*in, "%s", *type);
	skip(in);
	check(in);
	fscanf(*in, "%d%d", &A->c, &A->r);
	A->x1 = 0, A->y1 = 0, A->x2 = A->c, A->y2 = A->r;
	skip(in);
	check(in);
	fscanf(*in, "%d", &A->max_value);
	skip(in);
	check(in);
}

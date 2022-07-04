// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "save.h"
#include "exit.h"
#include "effects.h"

// Freeing the memory if malloc() fails

void F(FILE *i, struct mat *A, struct mat *B, char *l, struct fil *f, int k)
{
	if (k == 0) {
		printf("%d\n", k);
		fprintf(stderr, "malloc() failed!\n");
		free_filters(f);
		prog_end(i, A, B, l);
		exit(-1);
	}
}

// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef EFFECTS_H
#define EFFECTS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

struct fil {
double **f;
};

double **define_edge(int *ok);

double **define_sharpen(int *ok);

double **define_blur(int *ok);

double **define_g_blur(int *ok);

void free_filters(struct fil *filters);

#endif


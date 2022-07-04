// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef APPLY_H
#define APPLY_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "effects.h"

double clamp(double x, int mn, int mx);

void mult_mat(struct mat *A, struct mat *B, int x, int y, double **C);

void apply_eff(struct mat *A, struct mat *B, struct fil *eff, int idx);

void output_type(int type, int val);

struct mat apply(char *cmd, FILE *in, struct mat *A, struct fil *eff, int *ok);

#endif


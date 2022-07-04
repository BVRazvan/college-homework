// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef ROTATE_H
#define ROTATE_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"

void copy_mat_all(struct mat *A, struct mat *B, int *ok);

void rotate_90_all(struct mat *A, struct mat *B);

void rotate_180_all(struct mat *A, struct mat *B);

void rotate_270_all(struct mat *A, struct mat *B);

void copy_mat_square(struct mat *A, struct mat *B, int *ok);

void rotate_90_square(struct mat *A, struct mat *B);

void rotate_180_square(struct mat *A, struct mat *B);

void rotate_270_square(struct mat *A, struct mat *B);

struct mat rotate(char *cmd, FILE **in, struct mat *A, int *ok);

#endif


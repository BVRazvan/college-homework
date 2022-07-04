// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef SAVE_H
#define SAVE_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "effects.h"
#include "apply.h"

void copy_mat(struct mat *A, struct mat *B, int *ok);

void save(char *cmd, FILE * *in, struct mat *A, struct mat *B, int *ok);

#endif

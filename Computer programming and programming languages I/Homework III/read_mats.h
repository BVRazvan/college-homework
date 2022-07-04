// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef READ_MATS_H
#define READ_MATS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"

void read_gray_mat_txt(FILE * *in, struct mat *A, int *ok);

void read_color_mat_txt(FILE **in, struct mat *A, int *ok);

void read_gray_mat_bin(FILE **in, struct mat *A, int *ok);

void read_color_mat_bin(FILE **in, struct mat *A, int *ok);

#endif

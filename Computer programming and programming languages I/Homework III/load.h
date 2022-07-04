// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef LOAD_H
#define LOAD_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "parsing.h"
#include "read_mats.h"
#include "mat_free.h"

void load(char *filename, FILE * *in, struct mat *A, struct mat *B, int *ok);

#endif

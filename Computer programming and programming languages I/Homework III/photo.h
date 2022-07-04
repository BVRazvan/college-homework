// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#ifndef PHOTO_H
#define PHOTO_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

struct pixel_ch {
	unsigned char r, g, b, gray;
};

struct pixel_d {
	double r, g, b, gray;
};

struct mat {
	int r, c, x1, x2, y1, y2, max_value;
	int type;
	struct pixel_ch **ch;
	struct pixel_d **d;
};

#endif


// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005

#include "photo.h"
#include "parsing.h"
#include "mat_free.h"
#include "select.h"
#include "swap.h"

void selected(char *cmd, FILE **in, struct mat *A)
{
	if (!*in) {
		printf("No image loaded\n");
		return;
	}
	// Checking if we select the entire image.
	if (strstr(cmd, "ALL")) {
		A->x1 = 0, A->y1 = 0, A->x2 = A->c, A->y2 = A->r;
		printf("Selected ALL\n");
		return;
	}
	int ok = 1;
	int limit_x1 = 0, limit_x2 = 0;
	int limit_y1 = 0, limit_y2 = 0;
	int cnt = 0;
	// Reading select action's dimensions.
	while (cmd) {
		++cnt;
		int sgn = 1;
		int sz = strlen(cmd), nr = 0;
		for (int i = 0; i < sz; ++i) {
			if (cmd[i] >= '0' && cmd[i] <= '9') {
				nr = nr * 10 + (cmd[i] - '0');
			} else if (cmd[i] == '-') {
				sgn *= -1;
			// Checking if there is an invaldi character.
			} else {
				printf("Invalid command\n");
				return;
			}
		}
		// "cnt" tells which coordinate is read.
		if (cnt == 1) {
			limit_x1 = nr * sgn;
		} else if (cnt == 2) {
			limit_y1 = nr * sgn;
		} else if (cnt == 3) {
			limit_x2 = nr * sgn;
		} else if (cnt == 4) {
			limit_y2 = nr * sgn;
		} else {
			ok = 0;
			printf("Invalid command\n");
			break;
		}
		cmd = strtok(NULL, " ");
	}
	if (cnt != 4 && ok == 1) {
		ok = 0;
		printf("Invalid command\n");
	}
	if (ok == 1) {
		// Checking if x1 > x2 or y1 > y2
		if (limit_x1 > limit_x2)
			swap(&limit_x1, &limit_x2);
		if (limit_y1 > limit_y2)
			swap(&limit_y1, &limit_y2);
		// Checking if the coordinates are included in the photo's dimensions.
		if (limit_x1 >= 0 && limit_x2 <= A->c &&
			limit_y1 >= 0 && limit_y2 <= A->r &&
				limit_x1 < limit_x2 && limit_y1 < limit_y2) {
			A->x1 = limit_x1, A->x2 = limit_x2;
			A->y1 = limit_y1, A->y2 = limit_y2;
			printf("Selected %d %d %d %d\n", A->x1, A->y1, A->x2, A->y2);
		} else {
			printf("Invalid set of coordinates\n");
		}
	}
}

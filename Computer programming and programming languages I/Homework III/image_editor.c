// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define DIM 1005
#define EFF_DIM 4

#include "effects.h"
#include "photo.h"
#include "parsing.h"
#include "read_mats.h"
#include "load.h"
#include "mat_free.h"
#include "select.h"
#include "swap.h"
#include "rotate.h"
#include "crop.h"
#include "apply.h"
#include "save.h"
#include "exit.h"
#include "fail.h"

int main(void)
{
	// Defining useful things.
	FILE *in;
	memset(&in, 0, sizeof(in));
	struct mat A, B;
	memset(&A, 0, sizeof(A));
	memset(&B, 0, sizeof(B));
	char *line, *command;
	memset(&line, 0, sizeof(line));
	memset(&command, 0, sizeof(command));
	int ok = 1;
	struct fil *filters;
	filters = (struct fil *)malloc(EFF_DIM * sizeof(struct fil));
	if (!filters) {
		ok = 0;
		F(in, &A, &B, line, filters, ok);
	}
	filters[0].f = define_edge(&ok);
	filters[1].f = define_sharpen(&ok);
	filters[2].f = define_blur(&ok);
	filters[3].f = define_g_blur(&ok);
	line = (char *)malloc(DIM * sizeof(char));
	// Reading each row.
	while (fgets(line, DIM * sizeof(char), stdin)) {
		// Getting the first word in this row.
		line[strcspn(line, "\n")] = '\0';
		command = strtok(line, " ");
		// Checking if this command is in set.
		if (strcmp(command, "LOAD") == 0) {
			load(command, &in, &A, &B, &ok);
			F(in, &A, &B, line, filters, ok);
		} else if (strcmp(command, "SELECT") == 0) {
			command = strtok(NULL, " ");
			selected(command, &in, &B);
		} else if (strcmp(command, "ROTATE") == 0) {
			B = rotate(command, &in, &B, &ok);
			F(in, &A, &B, line, filters, ok);
		} else if (strcmp(command, "CROP") == 0) {
			B = crop(&in, &B, &ok);
			F(in, &A, &B, line, filters, ok);
		} else if (strcmp(command, "APPLY") == 0) {
			B = apply(command, in, &B, filters, &ok);
			F(in, &A, &B, line, filters, ok);
		} else if (strcmp(command, "SAVE") == 0) {
			save(command, &in, &B, &A, &ok);
			F(in, &A, &B, line, filters, ok);
		} else if (strcmp(command, "EXIT") == 0) {
			prog_end(in, &A, &B, line);
			free_filters(filters);
			if (in)
				fclose(in);
		} else {
			printf("Invalid command\n");
		}
	}
	return 0;
}

// Copyright Valentin-Razvan Bogdan 311CAa 2021-2022 <bogdanvrazvan@gmail.com>

#include <stdio.h>
#include <stdlib.h>
#include "strassen.c"
#define mod 10007

void error_found(int type)
{
	// Different outputs for all types of errors.
	if (type == 1)
		printf("No matrix with the given index\n");
	else if (type == 2)
		printf("Cannot perform matrix multiplication\n");
	else if (type == 3)
		printf("Unrecognized command\n");
	else if (type == 4)
		fprintf(stderr, "Alloc failed!\n");
}

void free_space(int ****a, int **r, int pos)
{
	// Free each line for a matrix with given position.
	for (int i = 0; i < (*r)[pos]; ++i)
		free((*a)[pos][i]);
	// Lastly, I have to free the matrix from the vector.
	free((*a)[pos]);
}

void total_free(int ****a, int **r, int **c, int *nr_mats)
{
	// For each matrix in the vector free the memory for each line.
	for (int i = 0; i < *nr_mats; ++i)
		free_space(a, r, i);
	// In the end, I have to free the vector of matrices and the vectors
	// for number of rows and columns for each matrix.
	free(*a), free(*r), free(*c);
}

void read_matrix(int ****a, int rows, int cols, int pos)
{
	for (int i = 0; i < rows; ++i) {
		for (int j = 0; j < cols; ++j)
			scanf("%d", &(*a)[pos][i][j]);
	}
}

void add(int ****a, int **r, int **c, int pos, int rows, int cols, int *nr_mat)
{
	// Adding a matrix into the vector of matrices at a given position
	// with given dimensions.
	(*r)[pos] = rows;
	(*c)[pos] = cols;
	// Allocating memory for rows and columns.
	(*a)[pos] = (int **)malloc(rows * sizeof(int *));
	if (!(*a)[pos]) {
		error_found(4);
		total_free(a, r, c, nr_mat);
		exit(0);
	}
	for (int i = 0; i < rows; ++i) {
		(*a)[pos][i] = (int *)malloc(cols * sizeof(int));
		if (!(*a)[pos][i]) {
			error_found(4);
			total_free(a, r, c, nr_mat);
			exit(0);
		}
	}
}

int check_exist(int nr_mats, int pos)
{
	// Checking if the matrix at a given positions exists.
	if (nr_mats - 1 >= pos && nr_mats > 0 && pos >= 0)
		return 1;
	return 0;
}

void print(int ***a, int  *r, int *c, int nr_mats)
{
	int pos;

	scanf("%d", &pos);
	if (check_exist(nr_mats, pos) == 1) {
		for (int i = 0; i < r[pos]; ++i) {
			for (int j = 0; j < c[pos]; ++j)
				printf("%d ", a[pos][i][j]);
		printf("\n");
		}
	} else {
		error_found(1);
	}
}

void realloc_add(int ****a, int **r, int **c, int *alloc_mats, int nr_mats)
{
	// Reallocating the memory when the number of allocated matrices changes.
	*r = (int *)realloc(*r, *alloc_mats * sizeof(int));
	*c = (int *)realloc(*c, *alloc_mats * sizeof(int));
	*a = (int ***)realloc(*a, *alloc_mats * sizeof(int **));
	if (!(*a) || !(*r) || !(*c)) {
		error_found(4);
		total_free(a, r, c, &nr_mats);
		exit(0);
	}
	// Initializing new allocated memory with 0 when the number of
	// allocated matrices is doubled. It is a good practice for safety
	// because it happens to acces allocated memory which is not used
	// at a certain moment.
	for (int i = *alloc_mats >> 1; i < *alloc_mats; ++i)
		(*r)[i] = 0;
	for (int j = *alloc_mats >> 1; j < *alloc_mats; ++j)
		(*c)[j] = 0;
	for (int i = *alloc_mats >> 1; i < *alloc_mats; ++i)
		(*a)[i] = 0;
}

void realloc_remove(int ****a, int **r, int **c, int *alloc_mats, int nr_mats)
{
	// Reallocating the memory when the number of allocated matrices changes.
	*r = (int *)realloc(*r, *alloc_mats * sizeof(int));
	*c = (int *)realloc(*c, *alloc_mats * sizeof(int));
	*a = (int ***)realloc(*a, *alloc_mats * sizeof(int **));
	if (!(*a) || !(*r) || !(*c)) {
		error_found(4);
		total_free(a, r, c, &nr_mats);
		exit(0);
	}
}

void update(int ****a, int **r, int **c, int nr_mats, int *alloc_mat)
{
	// Checking if I have enough/too much allocated memory when I add/remove
	// a matrix; number of allocated matrices can be doubled/halved/not changed
	// at all.
	if (nr_mats >= *alloc_mat) {
		// Doubling the allocated memory when the vector of matrices ocupies
		// all of the memory.
		*alloc_mat <<= 1;
		realloc_add(a, r, c, alloc_mat, nr_mats);
	} else if (nr_mats < (*alloc_mat >> 1) && *alloc_mat > 1 && nr_mats >= 1) {
		// Halving the allocated memory when the second half is not used.
		*alloc_mat >>= 1;
		realloc_remove(a, r, c, alloc_mat, nr_mats);
	}
}

void read_dim_and_add(int ****a, int **r, int **c, int nr_mats)
{
	int rows, cols;

	scanf("%d%d", &rows, &cols);
	add(a, r, c, nr_mats - 1, rows, cols, &nr_mats);
	read_matrix(a, rows, cols, nr_mats - 1);
}

void copy(int ****a, int **r, int **c, int pos1, int pos2)
{
	// Copy the matrix from position pos1 to the matrix from position pos2.
	for (int i = 0; i < (*r)[pos1]; ++i) {
		for (int j = 0; j < (*c)[pos1]; ++j)
			(*a)[pos2][i][j] = (*a)[pos1][i][j];
	}
	(*r)[pos2] = (*r)[pos1];
	(*c)[pos2] = (*c)[pos1];
}

void cut(int ****a, int **r, int **c, int *nr_mats, int *alloc_mats)
{
	// Redimensioning a matrix at given position with given rows and columns.
	// new_r[i] - i-th row in the new matrix;
	// new_c[i] - i-th column in the new matrix.
	int pos, rows, cols;
	int *new_r, *new_c;

	scanf("%d", &pos);
	scanf("%d", &rows);
	new_r = (int *)malloc(rows * sizeof(int));
	if (!new_r) {
		error_found(4);
		total_free(a, r, c, nr_mats);
		exit(0);
	}
	for (int i = 0; i < rows; ++i)
		scanf("%d", &new_r[i]);
	scanf("%d", &cols);
	new_c = (int *)malloc(cols * sizeof(int));
	if (!new_c) {
		error_found(4);
		free(new_r);
		total_free(a, r, c, nr_mats);
		exit(0);
	}
	for (int j = 0; j < cols; ++j)
		scanf("%d", &new_c[j]);
	// Checking if the positions exists in the vector of matrices.
	if (check_exist(*nr_mats, pos) == 1) {
		// I will add a new matrix in the vector of matrices which will contain
		// the rows and columns given at the input and after that it will be
		// copied at the position of the matrix that needs redimensioning.
		// Lastly, I will delete the matrix added in the vector
		// and free the memory.
		// --
		// Increasing the number of matrices in the vector.
		++(*nr_mats);
		// Checking if I need to double/half/not change the memory
		update(a, r, c, *nr_mats, alloc_mats);
		// Adding a matrix in the vector with new dimensions;
		add(a, r, c, *nr_mats - 1, rows, cols, nr_mats);
		// Creating redimensioned matrix.
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j)
				(*a)[*nr_mats - 1][i][j] = (*a)[pos][new_r[i]][new_c[j]];
		}
		// Now I free the memory at the matrix which needs redimensioning
		// in order to recreate it with the new dimensions.
		free_space(a, r, pos);
		// Adding the matrix with given dimensions.
		add(a, r, c, pos, rows, cols, nr_mats);
		// Simply copying the redimensionated matrix(found at the end of
		// the vector of matrices) to the position given at input.
		copy(a, r, c, *nr_mats - 1, pos);
		// After the matrix at the given position is redimensioned I no longer
		// need the last matrix in the vector so I free it.
		free_space(a, r, *nr_mats - 1);
		(*r)[*nr_mats - 1] = 0;
		(*c)[*nr_mats - 1] = 0;
		// Decreasing the number of matrices.
		--(*nr_mats);
		// Checking once again if I need to double/half/not change the memory.
		update(a, r, c, *nr_mats, alloc_mats);
	} else {
		error_found(1);
	}
	free(new_r), free(new_c);
}

void find_dim(int *rows_a, int *cols_a, int nr_mats)
{
	int pos;

	scanf("%d", &pos);
	// Checking if the matrix at the given position exists.
	if (check_exist(nr_mats, pos) == 1)
		printf("%d %d\n", rows_a[pos], cols_a[pos]);
	else
		error_found(1);
}

void mult_elements(int ****a, int p1, int p2, int p3, int i, int k, int j)
{
	/// p1 - position of product matrix;
	/// p2, p3 - positions of factor matrixes.
	(*a)[p1][i][j] = (*a)[p1][i][j] + (*a)[p2][i][k] * (*a)[p3][k][j] % mod;
	(*a)[p1][i][j] %= mod;
	if ((*a)[p1][i][j] < 0)
		(*a)[p1][i][j] += mod;
}

void mult(int ****a, int **r, int **c, int *nr_mats, int *alloc_mats, int type)
{
	// Multiplying two matrices.
	int id1, id2;

	scanf("%d%d", &id1, &id2);
	//Checking if the positions do not exist in the vector of matrices.
	if (check_exist(*nr_mats, id1) + check_exist(*nr_mats, id2) != 2) {
		error_found(1);
		return;
	}
	// Checking if these matrices can be multiplied.
	if ((*c)[id1] != (*r)[id2]) {
		error_found(2);
		return;
	}
	// Adding the product matrix in the vector with coresponding
	// dimensions.
	++(*nr_mats);
	update(a, r, c, *nr_mats, alloc_mats);
	// Multiplying matrices.
	// Type 0 - usual multiplication;
	// Type 1 - strassen.
	if (type == 0) {
		add(a, r, c, *nr_mats - 1, (*r)[id1], (*c)[id2], nr_mats);
		for (int i = 0; i < (*r)[id1]; ++i) {
			for (int j = 0; j < (*c)[id2]; ++j) {
				(*a)[*nr_mats - 1][i][j] = 0;
				for (int k = 0; k < (*c)[id1]; ++k)
					mult_elements(a, *nr_mats - 1, id1, id2, i, k, j);
			}
		}
	} else {
		// ok checks if there are any allocating problems in Strassen's
		// algorithm. If so, free all memory and exit.
		int ok = 0;
		(*r)[*nr_mats - 1] = (*r)[id1];
		(*c)[*nr_mats - 1] = (*c)[id2];
		(*a)[*nr_mats - 1] = strassen((*a)[id1], (*a)[id2], (*r)[id1], &ok);
		if (ok == 1) {
			error_found(4);
			total_free(a, r, c, nr_mats);
			exit(0);
		}
	}
}

void swap_elements(int *ptr1, int *ptr2)
{
	int tmp = *ptr1;
	*ptr1 = *ptr2;
	*ptr2 = tmp;
}

void swap_matrix(int ***ptr1_matrix, int ***ptr2_matrix)
{
	int **tmp_matrix = *ptr1_matrix;
	*ptr1_matrix = *ptr2_matrix;
	*ptr2_matrix = tmp_matrix;
}

void sort(int ****a, int **r, int **c, int *nr_mats)
{
	// Sorting matrices based on the sum of their elements.
	// sum[i] - sum of elements in the i-th matrix.
	if (*nr_mats > 1) {
		int *sum;
		sum = (int *)malloc(*nr_mats * sizeof(int));
		for (int cnt = 0; cnt < *nr_mats; ++cnt) {
			sum[cnt] = 0;
			// Calculating sum in each matrix.
			for (int i = 0; i < (*r)[cnt]; ++i) {
				for (int j = 0; j < (*c)[cnt]; ++j)
					sum[cnt] = (sum[cnt] + (*a)[cnt][i][j]) % mod;
			}
			if (sum[cnt] < 0)
				sum[cnt] += mod;
		}
		// Sorting matrices(swapping the sum, the dimensions and pointers).
		for (int i = 0; i < *nr_mats - 1; ++i) {
			for (int j = i + 1; j < *nr_mats; ++j) {
				if (sum[i] > sum[j]) {
					swap_elements(&sum[i], &sum[j]);
					swap_elements(&(*r)[i], &(*r)[j]);
					swap_elements(&(*c)[i], &(*c)[j]);
					swap_matrix(&(*a)[i], &(*a)[j]);
				}
			}
		}
	// In the end free the memory for sum of elements.
	free(sum);
	}
}

void transpose(int ****a, int **r, int **c, int *nr_mats, int *alloc_mats)
{
	// Transposing a matrix.
	// Dimensions will be swapped i.e number of lines will be number of columns
	// and number of columns will be number of lines.
	// I will add a new matrix in the vector of matrices and create in it the
	// transpose matrix. Therefore i will copy the transpose at given position
	// and free the memory in the end.
	int pos;

	scanf("%d", &pos);
	// Checking if the matrix at given position exists.
	if (check_exist(*nr_mats, pos) == 1) {
		// Increasing the number of matrices.
		++(*nr_mats);
		// Checking if I need to double/half/not change the memory.
		update(a, r, c, *nr_mats, alloc_mats);
		// Adding the transpose matrix with swapped dimensions.
		// See that number of rows is c[pos](number of columns)
		// and number of columns is r[pos](number of rows).
		add(a, r, c, *nr_mats - 1, (*c)[pos], (*r)[pos], nr_mats);
		// Creating transpose
		for (int i = 0; i < (*c)[pos]; ++i) {
			for (int j = 0; j < (*r)[pos]; ++j)
				(*a)[*nr_mats - 1][i][j] = (*a)[pos][j][i];
		}
		// Free the memory at the given position in order to add the dimensions
		// of the transpose matrix.
		free_space(a, r, pos);
		// Adding the matrix with swapped dimensions.
		add(a, r, c, pos, (*c)[pos], (*r)[pos], nr_mats);
		// Copying the transpose at the given position.
		copy(a, r, c, *nr_mats - 1, pos);
		// Now free the memory because i no longer need the transpose at the
		// end of the vector.
		free_space(a, r, *nr_mats - 1);
		(*r)[*nr_mats - 1] = 0;
		(*c)[*nr_mats - 1] = 0;
		// Decreasing the number of matrices.
		--(*nr_mats);
		// Checking once again if I need to double/half/not change the memory.
		update(a, r, c, *nr_mats, alloc_mats);
	} else {
		error_found(1);
	}
}

void free_and_move(int ****a, int **r, int **c, int *nr_mats, int *alloc_mats)
{
	// Deleting a matrix from vector.
	int pos;

	scanf("%d", &pos);
	// Checking if the matrix at the given position exists.
	if (check_exist(*nr_mats, pos) == 1) {
		// For each matrix in the range[position to end of vector] I free
		// the memory and add the dimensions of the next matrix in order to
		// copy it in the previous matrix.
		// It can be seen that the matrix at position "pos" disappers because
		// there is no matrix which becomes a copy of it.
		// Simply saying, all matrices are shifted one position to the left
		// and the first one disappers.
		for (int cnt = pos; cnt < *nr_mats - 1; ++cnt) {
			free_space(a, r, cnt);
			add(a, r, c, cnt, (*r)[cnt + 1], (*c)[cnt + 1], nr_mats);
			copy(a, r, c, cnt + 1, cnt);
		}
		// In the end last matrix in vector becomes useless because it has
		// a copy in the previous matrix and it does not contain a copy of any
		// matrix.
		free_space(a, r, *nr_mats - 1);
		(*r)[*nr_mats - 1] = 0;
		(*c)[*nr_mats - 1] = 0;
		// Decreasing number of matrices in vector.
		--(*nr_mats);
		// Checking if I need to double/half/not change the memory.
		update(a, r, c, *nr_mats, alloc_mats);
	} else {
		error_found(1);
	}
}

int main(void)
{
	// Declaring vector of matrices and two vectors for matrices' dimensions
	// a - vector of matrices;
	// r[i] - number of rows of i-th matrix
	// c[i] - number of columns of i-th matrix
	// nr_mats - number of matrices in the vector
	// alloc_mats - number of matrices allocated in the memory
	// alloc_mats >= nr_mats at any time
	int ***a, *r, *c;
	int nr_mats = 0, alloc_mats = 1;
	char command;
	// Allocating memory for one matrix; "default allocated memory"
	a = (int ***)malloc(sizeof(int **));
	r = (int *)malloc(sizeof(int));
	c = (int *)malloc(sizeof(int));
	if (!a || !r || !c) {
		error_found(4);
		total_free(&a, &r, &c, &nr_mats);
		exit(0);
	}
	scanf(" %c", &command);
	while (command != 'Q') {
		if (command == 'L') {
			++nr_mats;
			update(&a, &r, &c, nr_mats, &alloc_mats);
			read_dim_and_add(&a, &r, &c, nr_mats);
		} else if (command == 'D') {
			find_dim(r, c, nr_mats);
		} else if (command == 'P') {
			print(a, r, c, nr_mats);
		} else if (command == 'C') {
			cut(&a, &r, &c, &nr_mats, &alloc_mats);
		} else if (command == 'M' || command == 'S') {
			mult(&a, &r, &c, &nr_mats, &alloc_mats, command == 'S');
		} else if (command == 'O') {
			sort(&a, &r, &c, &nr_mats);
		} else if (command == 'T') {
			transpose(&a, &r, &c, &nr_mats, &alloc_mats);
		} else if (command == 'F') {
			free_and_move(&a, &r, &c, &nr_mats, &alloc_mats);
		} else {
			error_found(3);
		}
		scanf(" %c", &command);
	}
	total_free(&a, &r, &c, &nr_mats);
	return 0;
}

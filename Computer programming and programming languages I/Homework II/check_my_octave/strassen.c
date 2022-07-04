// Copyright Valentin-Razvan Bogdan 311CAa 2021-2022 <bogdanvrazvan@gmail.com>

#include <stdio.h>
#include <stdlib.h>
#include "strassen.h"
#define mod 10007

int **sum(int **a, int **b, int n)
{
	// Sum of two matrices.
	int **c = (int **)malloc(n * sizeof(int *));
	for (int i = 0; i < n; ++i) {
		c[i] = (int *)malloc(n * sizeof(int));
		for (int j = 0; j < n; ++j)
			c[i][j] = ((a[i][j] + b[i][j]) % mod + mod) % mod;
	}
	return c;
}

int **dif(int **a, int **b, int n)
{
	// Substraction of two matrices.
	int **c = (int **)malloc(n * sizeof(int *));
	for (int i = 0; i < n; ++i) {
		c[i] = (int *)malloc(n * sizeof(int));
		for (int j = 0; j < n; ++j)
			c[i][j] = ((a[i][j] - b[i][j]) % mod + mod) % mod;
	}
	return c;
}

void create_part(int ****part, int n, int *ok)
{
	// Allocating memory for submatrices.
	*part = (int ***)malloc(8 * sizeof(int **));
	if (!(*part)) {
		*ok = 1;
		return;
	}
	for (int cnt = 0; cnt < 8; ++cnt) {
		(*part)[cnt] = (int **)malloc(n * sizeof(int *));
		if (!(*part)[cnt]) {
			*ok = 1;
			return;
		}
		for (int i = 0; i < n; ++i) {
			(*part)[cnt][i] = (int *)malloc(n * sizeof(int));
			if (!(*part)[cnt][i]) {
				*ok = 1;
				return;
			}
			for (int j = 0; j < n; ++j)
				(*part)[cnt][i][j] = 0;
		}
	}
}

void create_point(int ***point, int n, int *ok)
{
	// Allocating memory for important points in a matrix in order to bound
	// submatrices of a matrix
	*point = (int **)malloc(4 * sizeof(int *));
	if (!(*point)) {
		*ok = 1;
		return;
	}
	for (int i = 0; i < 4; ++i) {
		(*point)[i] = (int *)malloc(2 * sizeof(int));
		if (!(*point)[i]) {
			*ok = 1;
			return;
		}
	}
	(*point)[0][0] = 0, (*point)[0][1] = 0;
	(*point)[1][0] = 0, (*point)[1][1] = n;
	(*point)[2][0] = n, (*point)[2][1] = 0;
	(*point)[3][0] = n, (*point)[3][1] = n;
}

void divide(int ***a, int ****a_part, int pos, int x, int y, int n)
{
	// Creating submatrices
	for (int i1 = 0, i2 = x; i1 < n; ++i1, ++i2)
		for (int j1 = 0, j2 = y; j1 < n; ++j1, ++j2)
			(*a_part)[pos][i1][j1] = (*a)[i2][j2];
}

void divide_part(int ***a, int ***b, int ****part, int **point, int n)
{
	for (int i = 0; i < 8; ++i) {
		if (i < 4)
			divide(a, part, i, point[i % 4][0], point[i % 4][1], n);
		else
			divide(b, part, i, point[i % 4][0], point[i % 4][1], n);
	}
}

void combine(int ***part, int ***product, int ***point, int pos, int n)
{
	// Combining submatrices in order to get product matrix
	int x, y;
	if (pos == 0)
		x = (*point)[0][0], y = (*point)[0][1];
	else if (pos == 1)
		x = (*point)[1][0], y = (*point)[1][1];
	else if (pos == 2)
		x = (*point)[2][0], y = (*point)[2][1];
	else
		x = (*point)[3][0], y = (*point)[3][1];
	for (int i1 = 0, i2 = x; i1 < n; ++i1, ++i2)
		for (int j1 = 0, j2 = y; j1 < n; ++j1, ++j2)
			(*product)[i2][j2] = part[pos][i1][j1];
}

void free_strassen_1(int ****aux, int ***point, int m)
{
	// Free the memory after each function call
	for (int cnt = 0; cnt < 18; ++cnt) {
		for (int i = 0; i < m; ++i)
			free((*aux)[cnt][i]);
	free((*aux)[cnt]);
	}
	free(*aux);
	for (int i = 0; i < 4; ++i)
		free((*point)[i]);
	free(*point);
}

void free_strassen_2(int ****comb, int ****result, int ****part, int m)
{
	// Free the memory after each function call
	for (int cnt = 0; cnt < 7; ++cnt) {
		for (int i = 0; i < m; ++i)
			free((*comb)[cnt][i]);
	free((*comb)[cnt]);
	}
	free(*comb);
	for (int cnt = 0; cnt < 8; ++cnt) {
		for (int i = 0; i < m; ++i)
			free((*part)[cnt][i]);
	free((*part)[cnt]);
	}
	free(*part);
	free(*result);
}

void create_aux_1(int ****aux, int ***part, int m)
{
	// Create submatrices.
	(*aux)[0] = sum(part[0], part[3], m), (*aux)[1] = sum(part[4], part[7], m);
	(*aux)[2] = sum(part[2], part[3], m), (*aux)[3] = dif(part[5], part[7], m);
	(*aux)[4] = dif(part[6], part[4], m), (*aux)[5] = sum(part[0], part[1], m);
	(*aux)[6] = dif(part[2], part[0], m), (*aux)[7] = sum(part[4], part[5], m);
	(*aux)[8] = dif(part[1], part[3], m), (*aux)[9] = sum(part[6], part[7], m);
}

void create_aux_2(int ****aux, int ***comb, int m)
{
	// Create submatrices.
	(*aux)[10] = sum(comb[0], comb[3], m);
	(*aux)[11] = dif((*aux)[10], comb[4], m);
	(*aux)[12] = sum((*aux)[11], comb[6], m);
	(*aux)[13] = sum(comb[2], comb[4], m);
	(*aux)[14] = sum(comb[1], comb[3], m);
	(*aux)[15] = sum(comb[0], comb[2], m);
	(*aux)[16] = dif((*aux)[15], comb[1], m);
	(*aux)[17] = sum((*aux)[16], comb[5], m);
}

int **strassen(int **a, int **b, int n, int *ok)
{
	// Allocating memory for product matrix
	int **product = (int **)malloc(n * sizeof(int *));
	int ***comb, ***aux, ***result, **point, ***part;
	for (int i = 0; i < n; ++i) {
		product[i] = (int *)malloc(n * sizeof(int));
		for (int j = 0; j < n; ++j)
			product[i][j] = 0;
	}
	if (n == 1) {
		// Base case.
		product[0][0] = (a[0][0] * b[0][0] % mod + mod) % mod;
		return product;
	}
	// Being an recursive algorithm, I have to divide the matrix into
	// 4 submatrices and apply the same algorithm till base case and
	// then construct the result for bigger matrices.
	int m = n / 2;
	/// Creating 8 submatrices(parts) : 4 for matrix "a" and 4 for matrix "b"/
	create_part(&part, m, ok);
	if (*ok == 1) {
		free_strassen_1(&aux, &point, m);
		free_strassen_2(&comb, &result, &part, m);
		return 0;
	}
	// Creating a matrix of important points in a matrix i.e bottom left
	// coordinates for each submatrix (0,0) ; (0,n/2) ; (n/2,0) ; (n/2 , n/2).
	create_point(&point, m, ok);
	if (*ok == 1) {
		free_strassen_1(&aux, &point, m);
		free_strassen_2(&comb, &result, &part, m);
		return 0;
	}
	// Creating submatrices for matrix "a" and matrix "b".
	divide_part(&a, &b, &part, point, m);
	comb = (int ***)malloc(7 * sizeof(int **));
	aux = (int ***)malloc(18 * sizeof(int **));
	if (!comb || !aux)
		*ok = 1;
	if (*ok == 1) {
		free_strassen_1(&aux, &point, m);
		free_strassen_2(&comb, &result, &part, m);
		return 0;
	}
	create_aux_1(&aux, part, m);
	// Getting results from subproblems.
	comb[0] = strassen(aux[0], aux[1], m, ok);
	comb[1] = strassen(aux[2], part[4], m, ok);
	comb[2] = strassen(part[0], aux[3], m, ok);
	comb[3] = strassen(part[3], aux[4], m, ok);
	comb[4] = strassen(aux[5], part[7], m, ok);
	comb[5] = strassen(aux[6], aux[7], m, ok);
	comb[6] = strassen(aux[8], aux[9], m, ok);
	// Using results from subproblems in order to create product matrix.
	result = (int ***)malloc(4 * sizeof(int **));
	if (!result)
		*ok = 1;
	if (*ok == 1) {
		free_strassen_1(&aux, &point, m);
		free_strassen_2(&comb, &result, &part, m);
		return 0;
	}
	create_aux_2(&aux, comb, m);
	result[0] = aux[12], result[1] = aux[13];
	result[2] = aux[14], result[3] = aux[17];
	// Match submatrices like a puzzle to get product matrix.
	for (int i = 0; i < 4; ++i)
		combine(result, &product, &point, i, m);
	// Free the memory.
	free_strassen_1(&aux, &point, m);
	free_strassen_2(&comb, &result, &part, m);
	return product;
}

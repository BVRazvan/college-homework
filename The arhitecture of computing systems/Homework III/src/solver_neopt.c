/*
 * Tema 2 ASC
 * 2024 Spring
 */
#include "utils.h"

/*
 * Add your unoptimized implementation here
 */
double* my_solver(int N, double *A, double* B) {
	printf("NEOPT SOLVER\n");

	double *C = calloc(N * N, sizeof(*C));
	if (!C)
		return NULL;

	double *D = calloc(N * N, sizeof(*D));
	if (!D)
		return NULL;
	
	/**
	 * C = At * B;
	 */
	for (int i = 0; i < N; ++i)
        for (int j = 0; j < N; ++j)
            for (int k = 0; k <= i; ++k)
                C[i * N + j] += A[k * N + i] * B[k * N + j];

	/**
	 * C += B * A;
	*/
    for (int i = 0; i < N; ++i)
        for (int j = 0; j < N; ++j)
            for (int k = 0; k <= j; ++k)
                C[i * N + j] += B[i * N + k] * A[k * N + j];

	/**
	 * D = C * Bt
	*/
    for (int i = 0; i < N; ++i)
        for (int j = 0; j < N; ++j)
            for (int k = 0; k < N; ++k)
                D[i * N + j] += C[i * N + k] * B[j * N + k];
	
	free(C);

	return D;
}

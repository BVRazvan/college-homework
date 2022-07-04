// Copyright Bogdan Valentin-Razvan 311CA

#include <stdio.h>
#define NMAX 105

int main(void)
{
	int n, x[NMAX], c[NMAX], p_min, act_p = 0;
	int to_add = 0, add[NMAX], points = 0;

	scanf("%d", &n);
	for (int i = 0; i < n; ++i)
		scanf("%d", &x[i]);
	for (int i = 0; i < n; ++i)
		scanf("%d", &c[i]);
	scanf("%d", &p_min);
	for (int i = 0; i < n; ++i)
		act_p += x[i] * c[i];
	for (int i = 0; i < n; ++i) {
		if (x[i] != 10)
			add[points++] = (10 - x[i]) * c[i];
	}
	for (int i = 0; i < points - 1; ++i) {
		for (int j = i + 1; j < points; ++j) {
			if (add[i] < add[j]) {
				int aux = add[i];
				add[i] = add[j];
				add[j] = aux;
			}
		}
	}
	int ok = 0;
	if (act_p >= p_min) {
		ok = 1;
		to_add = points;
	}
	for (int i = 0; i < points && ok == 0; ++i) {
		act_p += add[i];
		if (act_p >= p_min) {
			ok = 1;
			to_add = i + 1;
		}
	}
	if (ok == 0)
		printf("-1\n");
	else
		printf("%d\n", to_add);
	return 0;
}

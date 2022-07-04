// Copyright Bogdan Valentin-Razvan 311CA

#include <stdio.h>
#define MAX 105

int main(void)
{
	int n;
	int dif[MAX], numbers = 0, digits[MAX], out_of_secv = 0;

	scanf("%d", &n);
	int ok = 0;
	do {
		for (int i = 0; i <= 9; ++i)
			digits[i] = 0;
		if (n == 0)
			++digits[0];
		while (n > 0) {
			++digits[n % 10];
			n /= 10;
		}
		int desc_ord = 0, asc_ord = 0;
		for (int i = 9; i >= 0; --i) {
			for (int j = 1; j <= digits[i]; ++j)
				desc_ord = desc_ord * 10 + i;
		}
		for (int i = 1; i <= 9; ++i) {
			for (int j = 1; j <= digits[i]; ++j)
				asc_ord = asc_ord * 10 + i;
		}
		for (int i = 0; i < numbers; ++i) {
			if (desc_ord - asc_ord == dif[i]) {
				ok = 1;
				out_of_secv = i;
			}
		}
		if (ok == 0)
			dif[numbers++] = desc_ord - asc_ord;
		n = dif[numbers - 1];
	} while (ok == 0);
	printf("%d\n", out_of_secv);
	for (int i = out_of_secv; i < numbers; ++i)
		printf("%d ", dif[i]);
	printf("\n");
	return 0;
}

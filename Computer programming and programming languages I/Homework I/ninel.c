// Copyright Bogdan Valentin-Razvan 311CA

#include <stdio.h>

int main(void)
{
	int n;
	scanf("%d", &n);
	int last = -1, before_last = -1, x;
	int found = 0, mn_even_idx = 0, mx_odd_idx = 0;
	long long sum = 0;
	if (n > 3) {
		mn_even_idx = -1;
		mx_odd_idx = -1;
	}
	for (int i = 0; i < n; ++i) {
		scanf("%d", &x);
		if (before_last != -1 && last > before_last && last > x) {
			++found;
			sum += 1LL * last;
			if ((i + 1) % 2 == 1) {
				if (last > mx_odd_idx)
					mx_odd_idx = last;
			} else {
				if (mn_even_idx == -1) {
					mn_even_idx = last;
				} else {
					if (last < mn_even_idx)
						mn_even_idx = last;
				}
			}
		}
		before_last = last, last = x;
	}
	printf("%lld\n", sum);
	if (found > 0)
		printf("%.7lf\n", 1.0 * sum / found);
	else
		printf("%.7f\n", 1.0 * sum);
	printf("%d\n%d\n", mx_odd_idx, mn_even_idx);
	return 0;
}


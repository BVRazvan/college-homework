// Copyright Bogdan Valentin-Razvan 311CA

#include <stdio.h>
#define NMAX 105

int main(void)
{
	int tt;
	int a[NMAX][NMAX], limits_l[NMAX][NMAX], limits_c[NMAX][NMAX];
	int restrictions_l[NMAX], restrictions_c[NMAX];

	scanf("%d", &tt);
	for (; tt; --tt) {
		int n, m;

		scanf("%d%d", &n, &m);
		for (int i = 0; i < n; ++i) {
			scanf("%d", &restrictions_l[i]);
			for (int j = 0; j < restrictions_l[i]; ++j)
				scanf("%d", &limits_l[i][j]);
		}
		for (int j = 0; j < m; ++j) {
			scanf("%d", &restrictions_c[j]);
			for (int i = 0; i < restrictions_c[j]; ++i)
				scanf("%d", &limits_c[j][i]);
		}
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < m; ++j)
				scanf("%d", &a[i][j]);
		}
		int ok = 1;
		int cnt = 0, found = 0;
		for (int i = 0; i < n && ok == 1; ++i) {
			cnt = 0, found = 0;
			for (int j = 0; j < m && ok == 1; ++j) {
				if (a[i][j] == 1) {
					cnt = 1;
					while (a[i][j] == a[i][j + 1] && j + 1 < m)
						++j, ++cnt;
					if (found + 1 > restrictions_l[i])
						ok = 0;
					if (cnt != limits_l[i][found])
						ok = 0;
					++found;
				}
			}
			if (found != restrictions_l[i])
				ok = 0;
		}
		for (int j = 0; j < m && ok == 1; ++j) {
			cnt = 0, found = 0;
			for (int i = 0; i < n && ok == 1; ++i) {
				if (a[i][j] == 1) {
					cnt = 1;
					while (a[i][j] == a[i + 1][j] && i + 1 < n)
						++i, ++cnt;
					if (found + 1 > restrictions_c[j])
						ok = 0;
					if (cnt != limits_c[j][found])
						ok = 0;
					++found;
				}
			}
			if (found != restrictions_c[j])
				ok = 0;
		}
		if (ok == 1)
			printf("Corect\n");
		else
			printf("Eroare\n");
	}
	return 0;
}

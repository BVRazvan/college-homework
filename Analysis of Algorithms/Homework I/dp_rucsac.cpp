#include <bits/stdc++.h>

using namespace std;


int main()
{
    ifstream fin;
    ofstream fout;

    fin.open("test.in");
    fout.open("test.out");

    int N;
    double G;

    fin >> N >> G;

    if (N * G > 8100000) {
        fout << "Limita de memorie depasita!\n";
        fin.close();
        fout.close();

        return 0;
    }

    vector < double > g(N);
    vector < double > v(N);
    vector < vector < double > > dp(N, vector < double > (G + 1, 0));

    for(int i = 0; i < N; ++i) {
        fin >> g[i] >> v[i];
    }

    for(int i = 0; i < N; ++i) {
        for(int j = 0; j <= G; ++j) {
            if (i > 0) {
                dp[i][j] = dp[i - 1][j];
            } else {
                dp[i][j] = 0;
            }

            if(j >= g[i]) {
                if (i > 0) {
                    dp[i][j] = max(dp[i][j], dp[i - 1][j - g[i]] + v[i]);
                } else {
                    dp[i][j] = v[i];
                }
            }
        }
    }

    fout << dp[N - 1][G] << "\n";

    long long valoare_ramasa = dp[N - 1][G];
    for (int i = N - 1; i >= 0; --i) {
        if (i != 0 && valoare_ramasa == dp[i - 1][G]) {
            continue;
        }

        fout << i << " ";
        valoare_ramasa -= v[i];
        G -= g[i];

        if (valoare_ramasa == 0) {
            break;
        }
    }
    fout << "\n";

    fin.close();
    fout.close();

    return 0;
}

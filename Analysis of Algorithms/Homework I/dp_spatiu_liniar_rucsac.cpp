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

    vector < double > g(N);
    vector < double > v(N);
    vector < vector < double > > dp(2, vector < double > (G + 1, 0));
    int phase = 0;

    for(int i = 0; i < N; ++i) {
        fin >> g[i] >> v[i];
    }

    for(int i = 0; i < N; ++i, phase ^= 1) {
        for(int j = 1; j <= G; ++j) {
            dp[phase ^ 1][j] = dp[phase][j];
            if(j >= g[i]) {
                dp[phase ^ 1][j] = max(dp[phase ^ 1][j], dp[phase][j - g[i]] + v[i]);
            }
        }
    }

    fout << dp[phase][G] << "\n";

    fin.close();
    fout.close();

    return 0;
}
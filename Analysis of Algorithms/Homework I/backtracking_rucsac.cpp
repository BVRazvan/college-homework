#include <bits/stdc++.h>

using namespace std;

int main() {
    ifstream fin;
    ofstream fout;

    fin.open("test.in");
    fout.open("test.out");
    
    int N;
    double G;
    
    fin >> N >> G;
    if (N * G > 5760) {
        fout << "Limita de timp depasita!\n";
        fin.close();
        fout.close();
        
        return 0;
    }

    vector < double > g(N);
    vector < double > v(N);
    vector < int > solutie(N);
    vector < int > solutie_posibila(N);
    double valoare_maxima = 0;
    int obiecte_luate = 0;

    for(int i = 0; i < N; ++i) {
        fin >> g[i] >> v[i];
    }

    for (long long i = 1; i < (1LL << N); ++i) {
        double greutate_posibila = 0, valoare_posibila = 0;
        int obiecte_potentiale = 0;

        for (int j = 0; j < N; ++j) {
            if (i & (1 << j)) {
                valoare_posibila += v[j];
                greutate_posibila += g[j];
                solutie_posibila[obiecte_potentiale++] = j;
            }
            if (greutate_posibila > G) {
                break;
            }
        }

        if (greutate_posibila <= G && valoare_posibila > valoare_maxima) {
            valoare_maxima = valoare_posibila;
            obiecte_luate = obiecte_potentiale;
            for (int j = 0; j < obiecte_luate; ++j) {
                solutie[j] = solutie_posibila[j];
            }
        }
    }

    fout << valoare_maxima << "\n";
    for (int i = 0; i < obiecte_luate; ++i) {
        fout << solutie[i] << " ";
    }
    fout << "\n";

    fin.close();
    fout.close();

    return 0;
}

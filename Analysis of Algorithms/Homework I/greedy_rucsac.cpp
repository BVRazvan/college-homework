#include <bits/stdc++.h>

using namespace std;

struct obiect {
    double g, v;
    int ind;

    bool operator < (const obiect & next_obiect) const
    {
        if (g == 0 || next_obiect.g == 0) {
            return true;
        }

        double raport_1 = 1.0 * v / g;
        double raport_2 = 1.0 * next_obiect.v / next_obiect.g;
        return ((raport_1 > raport_2) || (raport_1 == raport_2 && g > next_obiect.g));
    }
};

int main()
{
    ifstream fin;
    ofstream fout;

    fin.open("test.in");
    fout.open("test.out");

    int N;
    double G;

    fin >> N >> G;

    double valoare_maxima = 0;
    int obiecte_luate = 0;
    vector < obiect > obiecte(N);
    vector < int > solutie(N);

    for(int i = 0; i < N; ++i) {
        fin >> obiecte[i].g >> obiecte[i].v;
        obiecte[i].ind = i;
    }

    sort(obiecte.begin(), obiecte.end());

    for (int i = 0; i < N; ++i) {
        if (G >= obiecte[i].g) {
            solutie[obiecte_luate++] = obiecte[i].ind;
            G -= obiecte[i].g;
            valoare_maxima += obiecte[i].v;
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

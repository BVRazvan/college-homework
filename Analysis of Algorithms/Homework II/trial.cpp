#include <iostream>
#include <vector>
#include <cstring>

#include "task.h"

#define N 505

std::ifstream fin("sat.sol");
std::ofstream fout("sat.cnf");


class Task1 : public Task {
public:
    int n, m, k, size, x, removed_subsets = 0, partial_sums[N] = {0};
    std::vector < int > a[N];
    std::map < int, int> oracle_ans;
    std::map < int, bool > exists;
    char ans[N];
    long long hash_subsets[N] = {0};
    long long mx = 0;

    void read_problem_data() {
        for (int i = 1; i <= m; ++i) {
            std::cin >> size;

            for (int j = 1; j <= size; ++j) {
                std::cin >> x;

                a[x].push_back(i);
                hash_subsets[i] |= (1LL << x);
            }
            mx = std::max(mx, hash_subsets[i]);
        }

        for (int i = 1; i <= n; ++i) {
            if (!a[i].size()) {
                std::cout << "False\n";
                fout.close();
                fin.close();
                exit(0);
            }
        }
    }

    void init_fields() {
        std::cin >> n >> m >> k;

        for (int i = 1; i <= n; ++i) {
            a[i].clear();
        }
        oracle_ans.clear();
        exists.clear();
    }

    void delete_redundant_subsets() {
        for (int i = 1; i <= m; ++i) {
            bool ok = false;
            for (int j = 1; j <= m && ok == false; ++j) {
                if (i == j) {
                    continue;
                }
                if ((hash_subsets[i] & hash_subsets[j]) == hash_subsets[i]) {
                    if (j < i && exists[j]) {
                        continue;
                    }
                    ++partial_sums[i];
                    ++removed_subsets;
                    exists[i] = true;
                    ok = true;
                    break;
                }
            }
            if (ok == false) {
                exists[i] = false;
            }
        }

        for (int i = 2; i <= m; ++i) {
            partial_sums[i] += partial_sums[i - 1];
        }
    }

    void solve() {
        init_fields();
        read_problem_data();
        delete_redundant_subsets();
        formulate_oracle_question();
        ask_oracle();
        decipher_oracle_answer();
        write_answer();
    }

    void formulate_oracle_question() {
        fout << "p cnf " << m - partial_sums[m] << " " << n + 1 << "\n";

        int cnt = 0;
        for (int i = 1; i <= n; ++i) {
            for (int j = 0; j < a[i].size(); ++j) {
                if (!exists[a[i][j]]) {
                    fout << a[i][j] - partial_sums[a[i][j] - 1] << " ";
                }
            }
            fout << "0\n";
        }
        for (int i = 1; i <= n; ++i) {
            fout << i << " ";
        }
        fout << "0\n";
        fout.close();
    }

    void decipher_oracle_answer() {
        int vars, ans_vars, x;

        fin >> ans >> vars;

        for (int i = 1; i <= vars; ++i) {
            fin >> x;
            
            oracle_ans[abs(x)] = x;
        }
    }

    void write_answer() {
        int ans_vars = 0, freq[N] = {0};

        if (!strcmp(ans, "False")) {
            std::cout << ans << "\n";
            fin.close();
            exit(0);
        }

        for (int i = 1; i <= m; ++i) {
            if (!exists[i] && oracle_ans[i- partial_sums[i - 1]] > 0) {
                ++ans_vars;
            }
        }

        if (ans_vars > k) {
            std::cout << "False\n";
            fin.close();
            exit(0);
        }

        std::cout << ans << "\n" << k << "\n";

        for (int i = 1; i <= m; ++i) {
            if (!exists[i] && oracle_ans[i - partial_sums[i - 1]] > 0) {
                std::cout << i << " ";
            } else if (ans_vars < k) {
                std::cout << i << " ";
                ++ans_vars;
            }
        }
        std::cout << "\n";

        fin.close();
    }
};

int main()
{
    Task1 trial;
    trial.solve();

    return 0;
}
#include <iostream>
#include <vector>
#include <cstring>

#include "task.h"

#define N 505
#define M 45

std::ifstream fin("sat.sol");
std::ofstream fout("sat.cnf");


class Task2 : public Task {
public:
    int n, m, p, size, x, removed_subsets = 0, wanted_cards = 0, partial_sums[N] = {0};
    std::vector < int > a[M];
    std::map < std::string, int > card_to_nr;
    std::map < int, std::string > nr_to_card;
    std::map < int, int> oracle_ans;
    std::map < int, bool > exists;
    char ans[M];
    long long hash_subsets[N] = {0};
    long long mx = 0;

    void read_problem_data() {
        std::string s;

        std::cin.get();
        for (int i = 1; i <= n; ++i) {
            std::getline(std::cin, s);

            auto idx = card_to_nr.find(s);
            if (idx == card_to_nr.end()) {
                card_to_nr[s] = 0;
            }
        }
        for (int i = 1; i <= m; ++i) {
            std::getline(std::cin, s);

            auto idx = card_to_nr.find(s);
            if (idx == card_to_nr.end()) {
                card_to_nr[s] = ++wanted_cards;
                nr_to_card[wanted_cards] = s;
            }
        }
        for (int i = 1; i <= p; ++i) {
            std::cin >> size;

            std::cin.get();
            for (int j = 1; j <= size; ++j) {
                std::getline(std::cin, s);

                auto idx = card_to_nr.find(s);
                if (idx != card_to_nr.end() && idx->second > 0) {
                    a[idx->second].push_back(i);
                    hash_subsets[i] |= (1LL << (idx->second));
                }
            }
            mx = std::max(mx, hash_subsets[i]);
        }
    }

    void init_fields() {
        std::cin >> n >> m >> p;

        for (int i = 1; i <= wanted_cards; ++i) {
            a[i].clear();
        }
        oracle_ans.clear();
        card_to_nr.clear();
        nr_to_card.clear();
        exists.clear();
    }

    void delete_redundant_subsets() {
        for (int i = 1; i <= p; ++i) {
            bool ok = false;
            for (int j = 1; j <= p && ok == false; ++j) {
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

        for (int i = 2; i <= p; ++i) {
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
        fout << "p cnf " << p - partial_sums[p] << " " << wanted_cards << "\n";

        int cnt = 0;
        for (int i = 1; i <= wanted_cards; ++i) {
            for (int j = 0; j < a[i].size(); ++j) {
                if (!exists[a[i][j]]) {
                    fout << a[i][j] - partial_sums[a[i][j] - 1] << " ";
                }
            }
            fout << "0\n";
        }
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

        for (int i = 1; i <= p; ++i) {
            if (!exists[i] && oracle_ans[i- partial_sums[i - 1]] > 0) {
                ++ans_vars;
            }
        }

        std::cout << ans_vars << "\n";

        for (int i = 1; i <= p; ++i) {
            if (!exists[i] && oracle_ans[i - partial_sums[i - 1]] > 0) {
                std::cout << i << " ";
            }
        }
        std::cout << "\n";

        fin.close();
    }
};

int main()
{
    Task2 trial;
    trial.solve();

    return 0;
}
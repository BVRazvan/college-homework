#include <iostream>
#include <vector>
#include <queue>
#include <cstring>

#include "task.h"

#define N 705
#define M 105
std::ifstream fin("sat.sol");
std::ofstream fout("sat.cnf");


class Task3 {
public:
    int n, m, p, size, x, wanted_cards = 0;
    int packets[N][M], sol[N];
    std::vector < int > a[N];
    std::priority_queue < std::pair < int, int > > pq;
    std::map < std::string, int > card_to_nr;
    std::map < int, bool > already_found;
    std::map < int, std::string > nr_to_card;
    std::map < int, bool > exists;

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
            already_found.clear();
            for (int j = 1; j <= size; ++j) {
                std::getline(std::cin, s);

                auto idx = card_to_nr.find(s);
                if (idx != card_to_nr.end() && idx->second > 0) {
                    auto idx_2 = already_found.find(idx->second);
                    if (idx_2 != already_found.end()) {
                        continue;
                    }
                    a[idx->second].push_back(i);
                    packets[i][++packets[i][0]] = idx->second;
                    already_found[idx->second] = true;
                }
            }
        }
    }

    void init_fields() {
        std::cin >> n >> m >> p;

        for (int i = 1; i <= p; ++i) {
            packets[i][0] = 0;
        }
        for (int i = 1; i <= m; ++i) {
            a[i].clear();
        }
        card_to_nr.clear();
        nr_to_card.clear();
    }

    void refresh_packets(int idx) {

        for (int j = 1; j <= packets[idx][0]; ++j) {
            exists[packets[idx][j]] = true;
        }
        packets[idx][0] = 0;
        for (int i = 1; i <= p; ++i) {
            if (i == idx) {
                continue;
            }
            int new_size = 0;
            for (int j = 1; j <= packets[i][0]; ++j) {
                if (!exists[packets[i][j]]) {
                    packets[i][++new_size] = packets[i][j];
                }
            }
            packets[i][0] = new_size;
        }
    }

    void choose_packets() {
        pq = std::priority_queue < std::pair < int, int > > ();

        for (int i = 1; i <= m; ++i) {
            if (a[i].size() == 1 && !exists[packets[a[i][0]][0]]) {
                wanted_cards -= packets[a[i][0]][0];
                refresh_packets(a[i][0]);
                sol[++sol[0]] = a[i][0];
            }
        }
        for (int i = 1; i <= p; ++i) {
            if (packets[i][0] > 0) {
                pq.push(std::make_pair(packets[i][0], i));
            }
        }
        while (wanted_cards > 0 && !pq.empty()) {
            auto top = pq.top();
            pq.pop();

            if (packets[top.second][0] != top.first) {
                pq.push(std::make_pair(packets[top.second][0], top.second));
                continue;
            }

            wanted_cards -= top.first;
            sol[++sol[0]] = top.second;
            if (wanted_cards > 0) {
                refresh_packets(top.second);
            }
        }
    }

    void solve() {
        init_fields();
        read_problem_data();
        choose_packets();
        write_answer();
    }

    void write_answer() {
        std::cout << sol[0] << "\n";
        for (int i = 1; i <= sol[0]; ++i) {
            std::cout << sol[i] << " ";
        }
        std::cout << "\n";
    }
};

int main()
{
    Task3 trial;
    trial.solve();

    return 0;
}
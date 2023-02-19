#include <bits/stdc++.h>

using namespace std;

char base[] = "test";

int main()
{
    mt19937 rng(chrono::steady_clock::now().time_since_epoch().count());

    ofstream file;

    int cnt = 0;
    srand(time(NULL));

    
    for (int i = 3; i <= 24; i += 3) {
        ++cnt;

        string to_add = to_string(cnt);
        char const *to_add_char_array = to_add.c_str();
        char output[20] = "in/test";
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        int G = i * 10;
        vector < int > g_1(i, 0);
        vector < int > v_1(i, 0);

        double max_dif = 0, min_dif = 0, abatere = 0.0;
        double average = 0.0, m = 0.0;
        do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2 = rand_1 - 1;
                g_1[j] = rand_1;
                v_1[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_1[j] / g_1[j]) * (m - v_1[j] / g_1[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.66));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_1[j] << " " << v_1[j] << "\n";
        }

        file.close();
        ++cnt;

        //fout << "----------------------------Testul " << ++cnt << " -------------------------- \n";

        to_add = to_string(cnt);
        to_add_char_array = to_add.c_str();
        strcpy(output, "in/test");
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        G = i * 10;
        vector < int > g_2(i, 0);
        vector < int > v_2(i, 0);

        do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;      
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2;
                if (has == 0) {
                    has = 1;
                    rand_2 = rand_1 = i * 10;
                } else {
                    rand_2 = 1 + rand_1 - rng() % (i);
                    if (rand_2 < 0) {
                        rand_2 = 1;
                    }
                }
                g_2[j] = rand_1;
                v_2[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_2[j] / g_2[j]) * (m - v_2[j] / g_2[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.8 + 0.00001 * i));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_2[j] << " " << v_2[j] << "\n";
        }

        file.close();

        ++cnt;

        to_add = to_string(cnt);
        to_add_char_array = to_add.c_str();
        strcpy(output, "in/test");
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        G = i * 10;
        vector < int > g_3(i, 0);
        vector < int > v_3(i, 0);

       do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;      
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2;
                if (has == 0) {
                    has = 1;
                    rand_2 = rand_1 = i * 10;
                } else {
                    rand_2 = 1 + rand_1 - rng() % rand_1;
                    if (rand_2 < 0) {
                        rand_2 = 1;
                    }
                }
                g_3[j] = rand_1;
                v_3[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_3[j] / g_3[j]) * (m - v_3[j] / g_3[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 1 - 0.00001 * i));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_3[j] << " " << v_3[j] << "\n";
        }

        file.close();

    }


    for (int i = 100; i < 1000; i += 100) {
        ++cnt;

        string to_add = to_string(cnt);
        char const *to_add_char_array = to_add.c_str();
        char output[20] = "in/test";
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        int G = i * 10;
        vector < int > g_1(i, 0);
        vector < int > v_1(i, 0);

        double max_dif = 0, min_dif = 0, abatere = 0.0;
        double average = 0.0, m = 0.0;
        do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2 = rand_1 - 1;
                g_1[j] = rand_1;
                v_1[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_1[j] / g_1[j]) * (m - v_1[j] / g_1[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.55));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_1[j] << " " << v_1[j] << "\n";
        }

        file.close();
        ++cnt;

        to_add = to_string(cnt);
        to_add_char_array = to_add.c_str();
        strcpy(output, "in/test");
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        G = i * 10;
        vector < int > g_2(i, 0);
        vector < int > v_2(i, 0);

        do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;      
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2;
                if (has == 0) {
                    has = 1;
                    rand_2 = rand_1 = i * 10;
                } else {
                    rand_2 = 1 + rand_1 - rng() % (i);
                    if (rand_2 < 0) {
                        rand_2 = 1;
                    }
                }
                g_2[j] = rand_1;
                v_2[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_2[j] / g_2[j]) * (m - v_2[j] / g_2[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.25 + 0.00001 * i));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_2[j] << " " << v_2[j] << "\n";
        }

        file.close();

        ++cnt;

        to_add = to_string(cnt);
        to_add_char_array = to_add.c_str();
        strcpy(output, "in/test");
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        G = i * 10;
        vector < int > g_3(i, 0);
        vector < int > v_3(i, 0);

       do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;      
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2;
                if (has == 0) {
                    has = 1;
                    rand_2 = rand_1 = i * 10;
                } else {
                    rand_2 = 1 + rand_1 - rng() % rand_1;
                    if (rand_2 < 0) {
                        rand_2 = 1;
                    }
                }
                g_3[j] = rand_1;
                v_3[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_3[j] / g_3[j]) * (m - v_3[j] / g_3[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.55 - 0.00001 * i));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_3[j] << " " << v_3[j] << "\n";
        }

        file.close();
    }

    for (int i = 1000; i <= 10000; i += 1000) {
        ++cnt;

        string to_add = to_string(cnt);
        char const *to_add_char_array = to_add.c_str();
        char output[20] = "in/test";
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        int G = i * 10;
        vector < int > g_1(i, 0);
        vector < int > v_1(i, 0);

        double max_dif = 0, min_dif = 0, abatere = 0.0;
        double average = 0.0, m = 0.0;
        do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2 = rand_1 - 1;
                g_1[j] = rand_1;
                v_1[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_1[j] / g_1[j]) * (m - v_1[j] / g_1[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.66));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_1[j] << " " << v_1[j] << "\n";
        }

        file.close();
        ++cnt;

        to_add = to_string(cnt);
        to_add_char_array = to_add.c_str();
        strcpy(output, "in/test");
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        G = i * 10;
        vector < int > g_2(i, 0);
        vector < int > v_2(i, 0);

        do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;      
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2;
                if (has == 0) {
                    has = 1;
                    rand_2 = rand_1 = i * 10;
                } else {
                    rand_2 = 1 + rand_1 - rng() % (i);
                    if (rand_2 < 0) {
                        rand_2 = 1;
                    }
                }
                g_2[j] = rand_1;
                v_2[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_2[j] / g_2[j]) * (m - v_2[j] / g_2[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.25 + 0.000001 * i));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_2[j] << " " << v_2[j] << "\n";
        }

        file.close();

        ++cnt;

        to_add = to_string(cnt);
        to_add_char_array = to_add.c_str();
        strcpy(output, "in/test");
        strcat(output, to_add_char_array);
        strcat(output, ".in");
        file.open(output);

        G = i * 10;
        vector < int > g_3(i, 0);
        vector < int > v_3(i, 0);

       do {
            m = 0;
            double mp = 0;
            max_dif = 0.0, min_dif = 99999999.0, average = 0.0, abatere = 0.0;
            int has = 0;      
            for(int j = 0; j < i; ++j) {
                int rand_1 = (1 + rng() % (i));
                int rand_2;
                if (has == 0) {
                    has = 1;
                    rand_2 = rand_1 = i * 10;
                } else {
                    rand_2 = 1 + rand_1 - rng() % rand_1;
                    if (rand_2 < 0) {
                        rand_2 = 1;
                    }
                }
                g_3[j] = rand_1;
                v_3[j] = rand_2;

                m += 1.0 * rand_2 / rand_1;
            }
            m = 1.0 * m / i;
            for(int j = 0; j < i; ++j) {
                abatere += (m - v_3[j] / g_3[j]) * (m - v_3[j] / g_3[j]);
            }
            abatere = 1.0 * abatere / (i - 1);
            abatere = sqrt(abatere);
        } while (!(m > 0.55 - 0.00001 * i));

        file << i << " " << G << "\n";
        for (int j = 0; j < i; ++j) {
            file << g_3[j] << " " << v_3[j] << "\n";
        }

        file.close();
    }
    return 0;
}

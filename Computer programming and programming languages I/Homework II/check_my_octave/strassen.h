// Copyright Valentin-Razvan Bogdan 311CAa 2021-2022 <bogdanvrazvan@gmail.com>

int **sum(int **a, int **b, int n);
int **dif(int **a, int **b, int n);
void create_part(int ****part, int n, int *ok);
void create_point(int ***point, int n, int *ok);
void divide(int ***a, int ****a_part, int pos, int x, int y, int n);
void divide_part(int ***a, int ***b, int ****part, int **point, int n);
void combine(int ***part, int ***product, int ***point, int pos, int n);
void free_strassen_1(int ****aux, int ***point, int m);
void free_strassen_2(int ****comb, int ****result, int ****part, int m);
void create_aux_1(int ****aux, int ***part, int m);
void create_aux_2(int ****aux, int ***comb, int m);
int **strassen(int **a, int **b, int n, int *ok);

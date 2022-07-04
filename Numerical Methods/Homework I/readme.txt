Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

	O sa vorbesc despre fiecare functie in parte
	
---------------------------------------------------------------------

Citire_graf

	Am creat aceasta functie pentru a face citirea grafului, in ideea
de a nu scrie acelasi cod la toate functiile.

---------------------------------------------------------------------

Iterative

	Functia consta in implementarea algoritmului iterativ descris pe
link-ul de pe wikipedia. Initial am construit matricea de pondere
a muchiilor, iar apoi am calculat matricea R pana cand suma
diferentelor in modul  a elementelor [ R(t + 1) - R(t) ] devine mai
mica decat EPS.

---------------------------------------------------------------------

Algebraic

	Functia rezolva ecuatia matriceala:
R = [coloana de (1 - d) / N] + d * M * R.
	Dupa cateva modificari asupra ecuatiei, matricea R este egala
cu [(I - d * T) ^ -1] * [coloana de (1 - d) / N] si ramane doar de
calculat acea matrice inversa, procedeu detaliat in functia PR_Inv.

---------------------------------------------------------------------

PR_Inv

	Functia calculeaza inversa unei matrici folosind factorizarea
Gram-Schmidt modificata. Asadar, A = Q * R, unde Q ortogonala, iar
R superior triunghiulara. A ^ -1 va fi R ^ -1 * Q ^ -1, dar stiind ca
Q ^ -1 = Q transpus, ramane de calculat R ^ -1. Pentru asta, ma
folosesc de forma matricei R (superior triunghiulara), prin urmare
voi rezolva N sisteme, unul pentru fiecare coloana.

---------------------------------------------------------------------

Apartenenta

	Functia consta in calcularea constantelor a si b, prin
rezolvarea unui sistem cu doua ecuatii si doua necunoscute, iar apoi
calculez valoarea functiei membru.

---------------------------------------------------------------------

PageRank

	Functia combina toate functii implementate anterior, iar la
final se face scrierea in fisier dupa conditiile impuse.

---------------------------------------------------------------------

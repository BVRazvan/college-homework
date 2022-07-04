function [R1 R2] = PageRank(nume, d, eps)
	% Calculeaza indicii PageRank pentru cele 3 cerinte
	% Scrie fisierul de iesire nume.out
  
  # Calculez matricele R cu cei doi algoritmi
  R1 = Iterative(nume, d, eps);
  R2 = Algebraic(nume, d);
  
  # Afisarea matricelor
  [A, N, L, val1, val2] = Citire_graf(nume);
  fout = fopen(strcat(nume, ".out"), "w");
  
  fprintf(fout, "%d\n", N);
  for i = 1 : N
    fprintf(fout, "%f\n", R1(i));
  endfor
  fprintf(fout, "\n");
  for i = 1 : N
    fprintf(fout, "%f\n", R2(i));
  endfor
  fprintf(fout, "\n");
  
  # Sortarea vectorului coloana; vectorul pos retine pozitia initiala
  # a fiecarui element 
  PR_1 = R2;
  pos = zeros(1, N);
  for i = 1 : N
    pos(i) = i;
  endfor
  for i = 1 : N - 1
    for j = i + 1 : N
      if PR_1(i) < PR_1(j)
        aux = PR_1(i);
        PR_1(i) = PR_1(j);
        PR_1(j) = aux;
        aux = pos(i);
        pos(i) = pos(j);
        pos(j) = aux;
      endif
    endfor
  endfor
  
  for i = 1 : N
    fprintf(fout, "%d %d %f\n", i, pos(i), Apartenenta(PR_1(i), val1, val2));
  endfor
  
  fclose(fout);
endfunction


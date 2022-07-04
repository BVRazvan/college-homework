function [A, N, L, val1, val2] = Citire_graf(nume)
  # Am decis sa fac o functie de citire pentru
  # a nu copia codul in fiecare program
  # N = numarul de noduri
  # A = matricea de adiacenta
  # L(i) = numarul de muchii care ies din nodul i
  in = fopen(nume, 'r');
  N = fscanf(in, "%d", 1);
  A = zeros(N);
  L = zeros(1, N);
  
  for i = 1 : N
    nod = fscanf(in, "%d", 1);
    nr_noduri = fscanf(in, "%d", 1);
    L(nod) = nr_noduri;
    for j = 1 : nr_noduri
      vecin = fscanf(in, "%d", 1);
      A(nod, vecin) = 1;
      # Am grija sa decrementez L(i) daca am muchie (i, i);
      if nod == vecin
        --L(nod);
        A(nod, nod) = 0;
      endif
    endfor
  endfor
  
  val1 = fscanf(in, "%f", 1);
  val2 = fscanf(in, "%f", 1);
  
  fclose(in);
  
endfunction

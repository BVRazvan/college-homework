function R = Algebraic(nume, d)
	% Functia care calculeaza vectorul PageRank folosind varianta algebrica de calcul.
	% Intrari: 
	%	-> nume: numele fisierului in care se scrie;
	%	-> d: probabilitatea ca un anumit utilizator sa continue navigarea la o pagina urmatoare.
	% Iesiri:
	%	-> R: vectorul de PageRank-uri acordat pentru fiecare pagina.
  
  # Scot R din ecuatia de link-ul de wikipedia:
  # R = [coloana de (1 - d) / N] + d * M * R
  # => R = [(I - d * T) ^ -1] * [coloana de (1 - d) / N]
  
  [A, N, L, val1, val2] = Citire_graf(nume);
  M = zeros(N);
  
  # Construiesc matricea de pondere a muchiilor
  for i = 1 : N
    for j = 1 : N
      if A(j, i) == 1
        M(i, j) = 1.0 / L(j);
      endif
    endfor
  endfor
  
  B = zeros(N, 1);
  
  # Matricea coloana de (1 - d) / N
  for i = 1 : N
    B(i) =  1.0 * (1 - d) / N;
  endfor
  
  # Calculez inversa folosind factorizarea G-S
  R = zeros(N, 1);
  I = eye(N);
  INV = PR_Inv(I - d * M);
  R = INV * B;
  
endfunction

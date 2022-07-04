function R = Iterative(nume, d, eps)
	% Functia care calculeaza matricea R folosind algoritmul iterativ.
	% Intrari:
	%	-> nume: numele fisierului din care se citeste;
	%	-> d: coeficentul d, adica probabilitatea ca un anumit navigator sa continue navigarea (0.85 in cele mai multe cazuri)
	%	-> eps: eruarea care apare in algoritm.
	% Iesiri:
	%	-> R: vectorul de PageRank-uri acordat pentru fiecare pagina.
  
  # Implementarea algoritmului iterativ descris pe wikipedia
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
  
  # Calculez R(t) pana cand suma diferentelor in modul 
  # a elementelor [ R(t + 1) - R(t) ] devine mai mica decat EPS
  # R(t + 1) = d * M * R(t) + (1 - d) / N * [coloana 1]
  R = zeros(N, 1);
  while 1
    dif = 0;
    old_R = R;
    R = d * M * R + (1 - d) / N * ones(N, 1);
    
    for i = 1 : N
      dif += abs(old_R(i) - R(i));
    endfor
    
    if dif < eps
      break
    endif
  endwhile
  
endfunction
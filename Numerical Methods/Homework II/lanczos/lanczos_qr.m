function [Q R] = lanczos_qr(A)
	% Functia care calculeaza inversa matricii A folosind factorizari Gram-Schmidt
	% Folosind G-S se poate scrie A = Q * R
  % unde Q ortogonala (Q ^ -1 = Q transpus), iar R e superior triunghiulara
  % Inversand, A ^ -1 = R ^ -1 * Q ^ -1
  % Cum Q ^ -1 = Q transpus, ramane doar sa inversam R ^ -1
  % Desigur, ma folosesc de forma matricei R
  # Prin urmare, rezolv cele N sisteme folosind substitutia inapoi.
  
  [N N] = size(A);
  B = zeros(N);
  Q = zeros(N);
  R = zeros(N);
  
  # G-S modificat
  for i = 1 : N
    R(i, i) = norm(A(:, i), 2);
    Q(:, i) = A(:, i) / R(i, i);
    
    for j = i + 1 : N
      R(i,j) = Q(:,i)' * A(:,j);
      A(:, j) = A(:, j) - Q(:, i) * R(i, j);
    endfor
    
  endfor
  
endfunction

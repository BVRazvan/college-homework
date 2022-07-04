function [A B C] = lanczos_adaptiv_rational(A, B, C, sigma, tol)
  eps = 1;
  m = 1;
  [n n] = size(A);
  H_old = eye(n);
  while eps > tol
    [V_krylov W_krylov H_new] = lanczos_rational(A, B, C, sigma);
    A = W_krylov(:, :, m)' * A * V_krylov(:, :, m);
    B = W_krylov(:, :, m)' * B;
    C = C * V_krylov(:, :, m);
    eps = max(abs(H_new - H_old));
    H_old = H_new;
    m = m + 1;
  endwhile
endfunction

function [V_krylov W_krylov H_f] = lanczos_rational(A, B, C, sigma)
  [n n] = size(A);
  m = size(sigma);
  S = zeros(n, n, 100);
  R = zeros(n, n, 100);
  V = zeros(n, n, 100);
  W = zeros(n, n, 100);
  Q = zeros(n, n, 100);
  S(:, :, 1) = inv(A - sigma(1) * eye(n)) * B;
  R(:, :, 1) = (inv(A - sigma(1)))' * C';
  [V(:, :, 1) H(:, :, 1)] = lanczos_qr(S(:, :, 1));
  new = eye(n) / V(:, :, 1);
  W(:, :, 1) = new';
  G(:, :, 1) = R(:, :, 1) / W(:, :, 1);
  V_krylov(:, :, :) = V(:, :, 1);
  W_krylov(:, :, :) = W(:, :, 1);
  for k = 1 : m
    if k < m
      if sigma(k + 1) == inf
        S(:, :, k + 1) = A * V(:, :, k);
        R(:, :, k + 1) = A' * W(:, :, k);
      else
        S(:, :, k + 1) = inv(A - sigma(k + 1) * eye(n));
        R(:, :, k + 1) = (inv(A - sigma(k + 1) * eye(n)))' * W(:, :, k);
      endif
      H(:, :, k + 1) = W_krylov(:, :, k) * S(:, :, k + 1);
      G(:, :, k + 1) = V_krylov(:, :, k) * R(:, :, k + 1);
      [V(:, :, k + 1) H(:, :, k + 1)] = lanczos_qr(S(:, :, k + 1));
      [W(:, :, k + 1) G(:, :, k + 1)] = lanczos_qr(R(:, :, k + 1));
      [P(:, :, k) D(:, :, k) new] = svd(W(:, :, k + 1)' * V(:, :, k + 1));
      Q(:, :, k) = new';
      V(:, :, k + 1) = V(:, :, k + 1) * Q(:, :, k) * D(:, :, k)^(-1/2);
      W(:, :, k + 1) = W(:, :, k + 1) * P(:, :, k) * D(:, :, k)^(-1/2);
      H(:, :, k + 1) = D(:, :, k)^(1/2) * Q(:, :, k)' * H(:, :, k + 1);
      G(:, :, k + 1) = D(:, :, k)^(1/2) * P(:, :, k)' * G(:, :, k + 1);
      V_krylov(:, :, k + 1) = V(:, :, k + 1);
      W_krylov(:, :, k + 1) = W(:, :, k + 1);
    else
      if sigma(k + 1) == inf
        S(:, :, k + 1) = A * B;
        R(:, :, k + 1) = A' * C;
      else
        S(:, :, k + 1) = A' * B;
        R(:, :, k + 1) = (inv(A))' * C';
        endif
      H(:, :, k) = W_krylov(:, :, k)' * S(:, :, k + 1);
      G(:, :, k) = V_krylov(:, :, k)' * R(:, :, k + 1);
      S(:, :, k + 1) = S(:, :, k + 1) - V_krylov(:, :, k) * H(:, :, k);
      R(:, :, k + 1) = R(:, :, k + 1) - W_krylov(:, :, k) * G(:, :, k);
      [V(:, :, k + 1) H(:, :, k + 1)] = lanczos_qr(S(:, :, k + 1));
      [W(:, :, k + 1) G(:, :, k + 1)] = lanczos_qr(R(:, :, k + 1));
      [P(:, :, k) D(:, :, k) new] = svd(W(:, :, k + 1)' * V(:, :, k + 1));
      V(:, :, k + 1) = V(:, :, k + 1) * Q(:, :, k) * D(:, :, k)^(-1/2);
      W(:, :, k + 1) = W(:, :, k + 1) * P(:, :, k) * D(:, :, k)^(-1/2);
      H_f = H(:, :, k + 1);
    endif
  endfor
endfunction

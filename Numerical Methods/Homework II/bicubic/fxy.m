function r = fxy(f, x, y)
    % =========================================================================
    % Aproximeaza derivata fata de x si y a lui f in punctul (x, y).
    % =========================================================================
    
    % TODO: Calculeaza derivata.
    [m n] = size(f);
    
    if (x + 1 > m) || (x - 1 < 1) || (y + 1 > n) || (y - 1 < 1)
      r = 0;
      return;
    endif
    r = (f(x - 1, y - 1) + f(x + 1, y + 1) - f(x + 1, y - 1) - f(x - 1, y + 1)) / 4;
    
endfunction
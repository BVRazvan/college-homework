function r = fx(f, x, y)
    % =========================================================================
    % Aproximeaza derivata fata de x a lui f in punctul (x, y).
    % =========================================================================
    
    % TODO: Calculeaza derivata.
    [m n] = size(f);
    
    if (x + 1 > m) || (x - 1 < 1)
      r = 0;
      return;
    endif
    r = (f(x + 1, y) - f(x - 1, y)) / 2;
endfunction
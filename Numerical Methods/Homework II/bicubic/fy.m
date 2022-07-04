function r = fy(f, x, y)
    % =========================================================================
    % Aproximeaza derivata fata de y a lui f in punctul (x, y).
    % =========================================================================
    
    % TODO: Calculeaza derivata.
    [m n] = size(f);
    
    if (y + 1 > n) || (y - 1 < 1)
      r = 0;
      return;
    endif
    r = (f(x, y + 1) - f(x, y - 1)) / 2;
endfunction
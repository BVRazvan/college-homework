function out = proximal_2x2(f, STEP = 0.1)
    % ===================================================================================
    % Aplica Interpolare Proximala pe imaginea 2 x 2 f cu puncte intermediare echidistante.
    % f are valori cunoscute �n punctele (1, 1), (1, 2), (2, 1) ?i (2, 2).
    % Parametrii:
    % - f = imaginea ce se va interpola;
    % - STEP = distan?a dintre dou? puncte succesive.
    % ===================================================================================
    
    % TODO: Defineste coordonatele x si y ale punctelor intermediare.
    x_int = 1 : STEP : 2;
    y_int = x_int;
    % Se afl? num?rul de puncte.
    n = length(x_int);

    % TODO: Cele 4 puncte �ncadratoare vor fi aceleasi pentru toate punctele din interior.
    x_start = y_start = 1;
    x_finish = y_finish = 2;
    % TODO: Initializeaza rezultatul cu o matrice nula n x n.
    out = zeros(n);
    % Se parcurge fiecare pixel din imaginea finala.
    for i = 1 : n
        for j = 1 : n
            % TODO: Afla cel mai apropiat pixel din imaginea initiala.
            % TODO: Calculeaza pixelul din imaginea finala.
            if (i * STEP + 1 <= 1.5 && j * STEP + 1 <= 1.5)
              out(i, j) = f(x_start, y_start);
            elseif (i * STEP + 1 > 1.5 && j * STEP + 1 <= 1.5)
              out(i, j) = f(x_finish, y_start);
            elseif (i * STEP + 1 <= 1.5 && j * STEP + 1 > 1.5)
              out(i, j) = f(x_start, y_finish);
            else
              out(i, j) = f(x_finish, y_finish);
            endif
            
        endfor
    endfor

endfunction
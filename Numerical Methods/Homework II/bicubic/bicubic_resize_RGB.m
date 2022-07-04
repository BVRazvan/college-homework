function out = bicubic_resize_RGB(img, p, q)
    % =========================================================================
    % Redimensioneaza imaginea img astfel �nc�t aceasta save fie de dimensiune p x q.
    % Imaginea img este colorata.
    % =========================================================================

    [m n] = size(img);
    
    % TODO: Extrage canalul rosu al imaginii.
    R = img(:, :, 1);
    
    % TODO: Extrage canalul verde al imaginii.
    G = img(:, :, 2);
    
    % TODO: Extrage canalul albastru al imaginii.
    B = img(:, :, 3);
    
    % TODO: Aplica functia bicubic pe cele 3 canale ale imaginii.
    R = bicubic_resize(R, p, q);
    G = bicubic_resize(G, p, q);
    B = bicubic_resize(B, p, q);
    
    % TODO: Formeaza imaginea finala concatenand cele 3 canale de culori.
    out = cat(m * n, R, G, B);
    
endfunction

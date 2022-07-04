function out = proximal_resize_RGB(img, p, q)
    % =========================================================================
    % Redimensioneaza imaginea img astfel �nc�t aceasta save fie de dimensiune p x q.
    % Imaginea img este colorata.
    % =========================================================================
    out = zeros(p, q);
    % TODO: Extrage canalul rosu al imaginii.
    R = img(:, :, 1);
    % TODO: Extrage canalul verde al imaginii.
    G = img(:, :, 2);
    % TODO: Extrage canalul albastru al imaginii.
    B = img(:, :, 3);
    % TODO: Aplica functia proximal pe cele 3 canale ale imaginii.
    R = proximal_resize(R, p, q);
    G = proximal_resize(G, p, q);
    B = proximal_resize(B, p, q);
    % TODO: Formeaza imaginea finala concaten�nd cele 3 canale de culori.
    out = cat(p * q, R, G, B);
endfunction

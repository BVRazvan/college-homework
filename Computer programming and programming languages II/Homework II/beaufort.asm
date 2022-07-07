;Copyright Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

%include "../include/io.mac"

section .bss
    len_plain resd 1
    len_key resd 1
    i resd 1
    j resd 1
section .text
    global beaufort
    extern printf

; void beaufort(int len_plain, char *plain, int len_key, char *key, char tabula_recta[26][26], char *enc) ;
beaufort:
    ;; DO NOT MODIFY
    push ebp
    mov ebp, esp
    pusha

    mov eax, [ebp + 8]  ; len_plain
    mov ebx, [ebp + 12] ; plain (address of first element in string)
    mov ecx, [ebp + 16] ; len_key
    mov edx, [ebp + 20] ; key (address of first element in matrix)
    mov edi, [ebp + 24] ; tabula_recta
    mov esi, [ebp + 28] ; enc
    ;; DO NOT MODIFY
    ;; TODO: Implement spiral encryption
    ;; FREESTYLE STARTS HERE

; [ initializez variabilele globale
    mov [len_plain], dword eax
    mov [len_key], dword ecx
    mov edi, dword 0
    mov [i], dword edi
    mov [j], dword edi
; ]

    jmp solve

; [ cazul special cand diferenta dintre cele doua caractere e negativa
overflow:
    add ecx, dword 26
    jmp continue_1
; ]

; [ verific daca am ajuns la finalul cheii pentru a reseta indicele j
restore:
    mov edi, dword 0
    jmp continue_2
; ]

; [ pun indicii si caracterele corespondente in registre si
;    calculez diferenta dintre caractere
solve:
    xor eax, eax
    xor ecx, ecx
    xor edi, edi
    mov edi, dword [i]
    mov al, byte [ebx + edi]
    mov edi, dword [j]
    mov cl, byte [edx + edi]
    sub ecx, eax
    cmp ecx, 0
    jl overflow
; ]

; [ adun la diferenta 65 pentru a ajunge la litera respectiv
;    si pun rezultatul in string-ul final + verificare a j-ului
continue_1:
    add ecx, 65
    mov edi, dword [i]
    mov [esi + edi], cl
    mov edi, dword [j]
    mov eax, dword [len_key]
    inc edi
    cmp edi, eax
    jge restore
; ]

; [ verific daca am ajuns la finalul string-ului de criptat,
;    daca nu incrementam i-ul
continue_2:
    mov [j], edi
    mov edi, dword [i]
    mov eax, dword [len_plain]
    inc edi
    cmp edi, eax
    jge end
    mov [i], edi
    mov eax, dword [i]
    jmp solve
; ]

end:
    ;; FREESTYLE ENDS HERE
    ;; DO NOT MODIFY
    popa
    leave
    ret
    ;; DO NOT MODIFY

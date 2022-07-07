; Copyright Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

%include "../include/io.mac"

section .text
    global simple
    extern printf

simple:
    ;; DO NOT MODIFY
    push    ebp
    mov     ebp, esp
    pusha

    mov     ecx, [ebp + 8]  ; len
    mov     esi, [ebp + 12] ; plain
    mov     edi, [ebp + 16] ; enc_string
    mov     edx, [ebp + 20] ; step

    ;; DO NOT MODIFY
    ;; Your code starts here
    xor ebx, ebx

; [ copiez string-ul initial in cel final

copy:
    xor eax, eax
    mov eax, [esi + ebx]
    mov [edi + ebx], eax
    inc ebx
    cmp ebx, ecx
    jl copy
; ]
    jmp start

; [ aflu restul impartirii numarului rest la 26

modulo_26:
    sub edx, 26
    cmp edx, 26
    jge modulo_26
; ]
    jmp solve

start:
    cmp edx, 26
    jg modulo_26

    jmp solve

; [ am cazul cand step presupune trecerea de litera 'Z'    
overfl:
    sub byte [edi + ecx - 1], 91
    add byte [edi + ecx - 1], 65
    dec ecx
    cmp ecx, 0
    jle end
; ]

solve:     
    add [edi + ecx - 1], edx
    cmp byte [edi + ecx - 1], 90
    jg overfl
    loop solve

end:
    xor eax, eax
    ;; Your code ends here
    ;; DO NOT MODIFY
    popa
    leave
    ret
    
    ;; DO NOT MODIFY

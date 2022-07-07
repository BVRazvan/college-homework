;Copyright Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

%include "../include/io.mac"

section .data
    d_x db 0, 1, 0, -1
    d_y db 1, 0, -1, 0
    frecv times 1000 db 0
    type db 0
section .bss
    N resb 1
    plain_begin resd 1
    key_begin resd 1
    enc_begin resd 1
section .rodata
SHIFT_DIM EQU 24
VECT_DIM EQU 999
section .text
    global spiral
    extern printf

; void spiral(int N, char *plain, int key[N][N], char *enc_string);
spiral:
    ;; DO NOT MODIFY
    push ebp
    mov ebp, esp
    pusha

    mov eax, [ebp + 8]  ; N (size of key line)
    mov ebx, [ebp + 12] ; plain (address of first element in string)
    mov ecx, [ebp + 16] ; key (address of first element in matrix)
    mov edx, [ebp + 20] ; enc_string (address of first element in string)
    ;; DO NOT MODIFY
    ;; TODO: Implement spiral encryption
    ;; FREESTYLE STARTS HERE
; [ copiez parametrii in variabile globale
    mov [N], al
    mov [plain_begin], ebx
    mov [key_begin], ecx
    mov [enc_begin], edx
; ]

    xor eax, eax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    xor esi, esi
    xor edi, edi

; [ initializez vectorul de aparitii cu 0, deoarece am avut probleme
;   cu testele
    mov ecx, VECT_DIM

free:
    mov [frecv + ecx], byte 0
    loop free

    mov [frecv], byte 1
    mov [type], byte 0
; ]
    jmp solve

; [ resetez contorul vectorului de pozitie
restore:
    xor eax, eax
    mov [type], al
    jmp continue_1
; ]

recovery:
; [ ma intorc in ultima pozitie, cea care era valida
;   si ma intorc la 90 de grade
    xor eax, eax
    mov al, [type]
    xor ebx, ebx
    mov bl, [d_x + eax]
    sub esi, ebx
    mov bl, [d_y + eax]
    sub edi, ebx

    shl esi, SHIFT_DIM
    shr esi, SHIFT_DIM
    shl edi, SHIFT_DIM
    shr edi, SHIFT_DIM

    inc al
    cmp al, 4
    jge restore
    mov [type], al
    jmp continue_1
; ]

solve:
    shl esi, SHIFT_DIM
    shr esi, SHIFT_DIM
    shl edi, SHIFT_DIM
    shr edi, SHIFT_DIM

    xor eax, eax
    xor ebx, ebx
    xor edx, edx

; [ sunt la o pozitie valida, scriu in string-ul criptat
;   la pozitia curenta
    mov eax, [plain_begin]
    add bl, [eax + ecx]

    xor eax, eax
    mov eax, esi
    mov dl, [N]
    mul dl
    add eax, edi

    xor edx, edx
    mov edx, [key_begin]
    add ebx, [edx + eax * 4]
    xor eax, eax
    mov eax, [enc_begin]
    add eax, ecx
    mov [eax], ebx
; ]
    xor eax, eax
    xor ebx, ebx

; [ verific daca am parcurs toata matricea
    mov bl, [N]
    mov al, [N]
    mul bl
    dec eax
    cmp ecx, eax
    jge end
; ]

; [ avansez in matrice
    xor eax, eax
    mov al, [type]
    xor ebx, ebx
    mov bl, [d_x + eax]
    add esi, ebx
    mov bl, [d_y + eax]
    add edi, ebx
; ]

    shl esi, SHIFT_DIM
    shr esi, SHIFT_DIM
    shl edi, SHIFT_DIM
    shr edi, SHIFT_DIM

; [ verific daca pozitia este valida
    xor eax, eax
    xor edx, edx
    mov eax, esi
    mov dl, [N]
    mul dl
    add eax, edi 
    mov bl, [frecv + eax]

; [ verific daca am mai vizitat pozitia
    test bl, 1
    jnz recovery
; ]

; [ verific daca 0 <= i < N si 0 <= j < N
    xor edx, edx
    mov dl, [N]

    cmp esi, edx
    jge recovery

    cmp edi, edx
    jge recovery

    cmp esi, 0
    jl recovery

    cmp edi, 0
    jl recovery

; ]
    mov [frecv + eax], byte 1
    inc ecx
    jmp solve
; ]

; [ am revenit in pozitia anterioara, ma intorc la 90 de grade
;   si avansez, marcand in vectorul de aparitii pozitia
continue_1:
    xor ebx, ebx
    xor eax, eax

    mov al, [type]
    mov bl, [d_x + eax]
    add esi, ebx
    mov bl, [d_y + eax]
    add edi, ebx

    shl esi, SHIFT_DIM
    shr esi, SHIFT_DIM
    shl edi, SHIFT_DIM
    shr edi, SHIFT_DIM

    xor eax, eax
    xor ebx, ebx
    mov eax, esi
    mov bl, [N]
    mul bl
    add eax, edi

    mov [frecv + eax], byte 1

    inc ecx
    jmp solve
; ]

end:
    ;; FREESTYLE ENDS HERE
    ;; DO NOT MODIFY
    popa
    leave
    ret
    ;; DO NOT MODIFY

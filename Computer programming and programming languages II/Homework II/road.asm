;Copyright Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

%include "../include/io.mac"

struc point
    .x: resw 1
    .y: resw 1
endstruc

section .text
    global road
    extern printf

road:
    ;; DO NOT MODIFY
    push ebp
    mov ebp, esp
    pusha

    mov eax, [ebp + 8]      ; points
    mov ecx, [ebp + 12]     ; len
    mov ebx, [ebp + 16]     ; distances
    ;; DO NOT MODIFY
   
    ;; Your code starts here
    dec ecx
solve:
    mov [ebx + (ecx - 1) * 4], dword 0
    jmp phase_1

; [ verific daca numarul e negativ si calculez opusul la nevoie]
abs_value_1:
    cmp dx, 0
    je phase_2
    not dx
    add dx, 1
    jmp phase_2
; ]

; [ dx += |x1 - x2|
phase_1:
    xor dx, dx
    add dx, [eax + ecx * point_size + point.x]
    sub dx, [eax + (ecx - 1) * point_size + point.x]
    js abs_value_1
    jmp phase_2
; ]

; [ verific daca numarul e negativ si calculez opusul la nevoie]
abs_value_2:
    cmp dx, 0
    jz end
    not dx
    add dx, 1
    jmp end
; ]

; [ dx += |y1 - y2|
phase_2:
    ror edx, 16
    shr edx, 16
    add [ebx + (ecx - 1) * 4], edx
    xor dx, dx
    add dx, [eax + ecx * point_size + point.y]
    sub dx, [eax + (ecx - 1) * point_size + point.y]
    js abs_value_2
    jmp end
; ]

end:
    add [ebx + (ecx - 1) * 4], dx

loop solve
    ;; Your code ends here
    
    ;; DO NOT MODIFY
    popa
    leave
    ret
    ;; DO NOT MODIFY
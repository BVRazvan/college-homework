;Copyright Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

%include "../include/io.mac"

struc point
    .x: resw 1
    .y: resw 1
endstruc

section .text
    global points_distance
    extern printf

points_distance:
    ;; DO NOT MODIFY
    
    push ebp
    mov ebp, esp
    pusha

    mov ebx, [ebp + 8]      ; points
    mov eax, [ebp + 12]     ; distance
    ;; DO NOT MODIFY

    ;; Your code starts here
    mov [eax], dword 0
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
    add dx, [ebx + point.x]
    sub dx, [ebx + point_size + point.x]
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
    add [eax], dx
    xor dx, dx
    add dx, [ebx + point.y]
    sub dx, [ebx + point_size + point.y]
    js abs_value_2
    jmp end
; ]

end:
    add [eax], dx
    ;; Your code ends here
    
    ;; DO NOT MODIFY
    popa
    leave
    ret

    ;; DO NOT MODIFY
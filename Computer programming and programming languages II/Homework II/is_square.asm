;Copyright Bogdan Valentin-Razvan 311CA 2021-2022 <bogdanvrazvan@gmail.com>

%include "../include/io.mac"

section .text
    global is_square
    extern printf

is_square:
    ;; DO NOT MODIFY
    push ebp
    mov ebp, esp
    pusha

    mov ebx, [ebp + 8]      ; dist
    mov eax, [ebp + 12]     ; nr
    mov ecx, [ebp + 16]     ; sq
    ;; DO NOT MODIFY
   
    ;; Your code starts here
    jmp solve

; [dupa un numar de scaderi valoarea devine 0, deci e patrat perfect
found:
    mov [ecx + eax * 4], dword 1
    cmp eax, 0
    jnz solve
    jmp end
; ]

; [dupa un numar de scaderi valoarea devine negativa, deci nu e patrat perfect
not_found:
    mov [ecx + eax * 4], dword 0
    cmp eax, 0
    jnz solve
    jmp end
; ]

; [scaderi repetate de 1, 3, 5, ... . EDX este contorul scazut
check:
    cmp [ebx + eax * 4], dword 0
    jz found
    sub [ebx + eax * 4], edx
    jz found
    cmp [ebx + eax * 4], dword 0
    jl not_found
    add edx, 2
    jmp check
; ]

solve:
    dec eax
    xor edx, edx
    mov edx, 1
    jmp check

end:
    ;; Your code ends here
    
    ;; DO NOT MODIFY
    popa
    leave
    ret
    ;; DO NOT MODIFY
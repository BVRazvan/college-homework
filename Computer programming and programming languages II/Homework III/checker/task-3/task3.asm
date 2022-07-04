section .data
    format db "%d", 10, 0
    delim db " ,.", 10, 0

section .bss
    len_1 resd 1
    len_2 resd 1
    ans resd 1

extern strcmp
extern strtok
extern printf
extern qsort
extern strlen
global get_words
global compare_func
global sort

section .text

; compare_func (char *a, char *b)
compare_func:
    enter 0, 0

    pushad
    
    xor eax, eax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    xor esi, esi
    xor edi, edi

; [ calculez lungimea primului cuvant
    mov ebx, [ebp + 8]
    push dword [ebx]
    call strlen
    add esp, 4
 ; ]
    mov [len_1], eax

    xor eax, eax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    xor esi, esi
    xor edi, edi

; [ calculez lungimea celui de-al doilea cuvant
    mov ebx, [ebp + 12]
    push dword [ebx]
    call strlen
    add esp, 4
; ]

    mov [len_2], eax

    mov eax, [len_1]
    sub eax, [len_2]
    cmp eax, 0
    jnz end_it

    xor eax, eax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    xor esi, esi
    xor edi, edi
    
; [ daca au aceeasi lungime, apelez strcmp
    mov ebx, [ebp + 12]
    push dword [ebx]
    mov ebx, [ebp + 8]
    push dword [ebx]
    call strcmp
    add esp, 8
; ]

end_it:
    mov [ans], eax
    popad
    mov eax, [ans]
    leave
    ret

;; sort(char **words, int number_of_words, int size)
;  functia va trebui sa apeleze qsort pentru soratrea cuvintelor 
;  dupa lungime si apoi lexicografix

sort:
    enter 0, 0

    xor eax, eax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    xor esi, esi
    xor edi, edi

; [ pun parametrii pe stiva si apelez functia de sortare
    push dword compare_func
    push dword [ebp + 16]
    push dword [ebp + 12]
    push dword [ebp + 8]
    call qsort
    add esp, 16
    xor eax, eax
    leave
    ret
; ]

;; get_words(char *s, char **words, int number_of_words)
;  separa stringul s in cuvinte si salveaza cuvintele in words
;  number_of_words reprezinta numarul de cuvinte
get_words:
    enter 0, 0

    xor eax, eax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    xor esi, esi
    xor edi, edi

    mov edx, dword [ebp + 8]
    mov esi, dword [ebp + 12]

    push ebx
    push ecx
    push edx
    push esi
    push edi

; [ pun parametrii si apelez strtok
    push delim
    push edx
    call strtok
    add esp, 8
; ]

    pop edi
    pop esi
    pop edx
    pop ecx
    pop ebx

loop:
    mov [esi + ecx], eax

    push ebx
    push ecx
    push edx
    push esi
    push edi

; [ pun parametrii si apelez strok
    push delim
    push 0
    call strtok
    add esp, 8
; ]

    pop edi
    pop esi
    pop edx
    pop ecx
    pop ebx

    cmp eax, 0
    je end
    add ecx, 4
    jmp loop

end:
    xor eax, eax
    leave
    ret

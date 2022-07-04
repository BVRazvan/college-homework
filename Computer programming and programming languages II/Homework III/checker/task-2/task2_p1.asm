section .text
	global cmmmc

;; int cmmmc(int a, int b)
;
;; calculate least common multiple fow 2 numbers, a and b
cmmmc:
	xor ecx, ecx
	xor edx, edx
	xor esi, esi

	pop esi
	pop ecx
	pop edx
	pushad
	push edx
	push ecx
	jmp loop
; [ primul numar este mai mic, adaug "a"-ul initial
add_eax:
	add eax, ecx
	jmp continue
; ]

; [ al doilea numar este mai mic, adaug "b"-ul initial
add_edi:
	add edi, edx
	jmp continue
; ]

loop:
	xor eax, eax
	xor edi, edi

; [ scot numerele de pe stiva si le compar
	pop eax
	pop edi
	cmp eax, edi
	js add_eax
	cmp eax, edi
	jz end
	jmp add_edi
; ]

continue:
; [ readaug numerele pe stiva si repet procesul
	push edi
	push eax
	jmp loop
; ]

end:
	push esi
	ret

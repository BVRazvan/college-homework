section .data
	ans db 0

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

add_eax:
	add eax, ecx
	jmp continue
add_edi:
	add edi, edx
	jmp continue
loop:
	xor eax, eax
	xor edi, edi

	pop eax
	pop edi
	cmp eax, edi
	js add_eax
	cmp eax, edi
	jz end
	jmp add_edi
continue:
	push edi
	push eax
	jmp loop
end:
	push eax
	pop dword [ans]
	popad
	push dword [ans]
	pop eax
	push esi
	ret

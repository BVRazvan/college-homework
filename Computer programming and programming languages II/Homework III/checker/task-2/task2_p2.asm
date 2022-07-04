section .text
	global par

;; int par(int str_length, char* str)
;
; check for balanced brackets in an expression
par:
	xor eax, eax
	xor ecx, ecx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	push 1
	pop eax

	pop esi
	pop ecx
	pop edi
	push 1
	jmp start

; [ parantezarea e gresita
fail:
	xor eax, eax
	jmp end_2_fail
; ]

loop_2:
	xor eax, eax
	xor edi, edi
	pop eax
	push eax
	pop edi
	shl eax, 24
	shr eax, 24
	cmp eax, 40
	jz loop_2
	jmp end_2

start:
	jmp loop_1

add_1:
	push edx
	push edx
	jmp continue

add_2:
	push 1
	push eax
	jmp continue

; [ parcurg string-ul 
loop_1:
	xor eax, eax
	xor edx, edx
	push dword [edi]
	pop eax
; [ manipulez octetii 
	shl eax, 24
	shr eax, 24
	pop edx
	shl edx, 24
	shr edx, 24
; ]

; [ verific cazurile 
	cmp edx, eax
	jz add_1
	cmp edx, 40
	jz continue
	cmp eax, 40
	jz add_2
	jmp fail
; ]

continue:
	inc edi
	loop loop_1
; ]

; [ am parcurs stringul, verific daca stiva este goala
end_1:
	xor eax, eax
	xor edx, edx
	pop eax
	push eax
	pop edx
	shl eax, 24
	shr eax, 24
	cmp eax, 40
	jz fail
; ]

end_2:
	xor eax, eax
	push 1
	pop eax
	push esi
	ret

end_2_fail:
	push esi
	ret

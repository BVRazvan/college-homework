section .bss
	ans resd 1

section .text
	global sort

; struct node {
;     	int val;
;    	struct node* next;
; };

;; struct node* sort(int n, struct node* node);
; 	The function will link the nodes in the array
;	in ascending order and will return the address
;	of the new found head of the list
; @params:
;	n -> the number of nodes in the array
;	node -> a pointer to the beginning in the array
; @returns:
;	the address of the head of the sorted list
sort:
	enter 0, 0

	pushad
	xor ecx, ecx
	mov ecx, 0
	jmp loop_1

; [am gasit perechea de valori (i, i + 1) si le "leg"
found_all:
	mov [esi + 4], edi
; ]

loop_1:
	xor eax, eax
	xor ebx, ebx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	inc ecx
	cmp ecx, [ebp + 8]
	jz end
	mov edx, [ebp + 12]
	jmp loop_2

; [ am gasit nodul i din perechea(i, i + 1)
found_1:
	mov esi, edx
	jmp continue_1
; ]

; [ am gasit nodul i + 1 din perechea (i, i + 1)
found_2:
	mov edi, edx
	jmp continue_2
; ]


loop_2:
	inc ebx
; [ caut nodul i din perechea(i, i + 1
	cmp [edx], ecx
	jz found_1
; ]

continue_1:
; [ caut nodu i + 1 din perechea (i, i + 1)
	inc ecx
	cmp [edx], ecx
	jz found_2
; ]

continue_2:
	dec ecx
	cmp ebx, [ebp + 8]
	jz found_all
	add edx, 8
	jmp loop_2

; [ am gasit toate perechile, caut nodul 1, primul element din lista sortata
end:
	xor ecx, ecx
	xor edx, edx
	xor ebx, ebx
	xor eax, eax
	mov ebx, 1
	mov ecx, [ebp + 8]
	mov edx, [ebp + 12]
	jmp loop_3

found_3:
	mov eax, edx
	jmp continue_3

loop_3:
	cmp [edx], ebx
	jz found_3
	
continue_3:
	add edx, 8
	loop loop_3
	mov [ans], eax
	popad
	mov eax, [ans]
	leave
	ret
; ]
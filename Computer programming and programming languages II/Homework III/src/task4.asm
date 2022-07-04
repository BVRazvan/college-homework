section .text
	global cpu_manufact_id
	global features
	global l2_cache_info

;; void cpu_manufact_id(char *id_string);
;
;  reads the manufacturer id string from cpuid and stores it in id_string
cpu_manufact_id:
	enter 	0, 0
	
	pushad

	xor eax, eax
	xor ebx, ebx
	xor ecx, ecx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	mov edi, [ebp + 8]

	cpuid

	mov [edi], ebx
	mov [edi + 4], edx
	mov [edi + 8], ecx


	popad

	leave
	ret

;; void features(int *apic, int *rdrand, int *sgx, int *svm)
;
;  checks whether apic, rdrand and mpx / svm are supported by the CPU
;  MPX should be checked only for Intel CPUs; otherwise, the mpx variable
;  should have the value -1
;  SVM should be checked only for AMD CPUs; otherwise, the svm variable
;  should have the value -1
features:
	enter 	0, 0

	pushad

	xor eax, eax
	xor ebx, ebx
	xor ecx, ecx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	mov eax, dword 1
	mov edi, [ebp + 8]

	cpuid

	shl edx, 22
	shr edx, 31
	mov [edi], edx

	shl ecx, 1
	shr ecx, 31

	xor edi, edi

	mov edi, [ebp + 12]

	mov [edi], ecx

	xor eax, eax
	xor ebx, ebx
	xor ecx, ecx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	mov eax, dword 7
	cpuid

	mov edi, [ebp + 16]

	shl ebx, 17
	shr ebx, 31
	mov [edi], ebx

	xor eax, eax
	xor ebx, ebx
	xor ecx, ecx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	popad

	leave
	ret

;; void l2_cache_info(int *line_size, int *cache_size)
;
;  reads from cpuid the cache line size, and total cache size for the current
;  cpu, and stores them in the corresponding parameters
l2_cache_info:
	enter 	0, 0

	pushad

	xor eax, eax
	xor ebx, ebx
	xor ecx, ecx
	xor edx, edx
	xor esi, esi
	xor edi, edi

	mov eax, dword 0x80000006

	cpuid
	
	mov edi, [ebp + 8]

	xor edx, edx
	mov edx, ecx

	shl ecx, 24
	shr ecx, 24

	mov [edi], ecx

	xor edi, edi
	mov edi, [ebp + 12]

	shr edx, 16
	mov [edi], edx

	popad

	leave
	ret

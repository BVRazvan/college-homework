// Copyright Bogdan Valentin-Razvan 311CA <bogdanvrazvan@gmail.com> 2021-2022

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "structs.h"
#include <inttypes.h>
#define DIM 256
#define U_C sizeof(unsigned char)
#define U_I sizeof(unsigned int)

void check_alloc(void *p)
{
	// verific daca alocarea a avut succes
	if (!p) {
		fprintf(stderr, "alloc() failed!");
		exit(-1);
	}
}

data_structure *combine_details(char *word)
{
	// crearea structurii cu datele citite
	int sz = 0, sz_1 = 0, sz_2 = 0;
	// alocare campuri [
	data_structure *to_add = NULL;
	to_add = malloc(sizeof(*to_add));
	check_alloc(to_add);
	to_add->header = malloc(sizeof(head));
	check_alloc(to_add->header);
	// ]
	// adaug primul char* in void *data [
	word = strtok(NULL, " ");
	int type = (int) atoi(word);
	word = strtok(NULL, " ");
	to_add->data = malloc(strlen(word) + 1);
	sz += strlen(word) + 1;
	char *add_word = strdup(word);
	check_alloc(add_word);
	memmove(to_add->data, add_word, sz);
	free(add_word);
	// ]
	// adaug cele 2 numere in void *data [
	void *nr_1 = NULL, *nr_2 = NULL;
	int nr = 0;
	if (type == 1) {
		sz_1 = sizeof(int8_t);
		sz_2 = sizeof(int8_t);
		word = strtok(NULL, " ");
		nr_1 = malloc(sizeof(int8_t));
		check_alloc(nr_1);
		nr_2 = malloc(sizeof(int8_t));
		check_alloc(nr_2);
		nr = atoi(word);
		memmove((int8_t*)nr_1, &nr, sizeof(int8_t));
		word = strtok(NULL, " ");
		nr = atoi(word);
		memmove(nr_2, &nr, sizeof(int8_t));
	} else if (type == 2) {
		sz_1 = sizeof(int16_t);
		sz_2 = sizeof(int32_t);
		word = strtok(NULL, " ");
		nr_1 = malloc(sizeof(int16_t));
		check_alloc(nr_1);
		nr = atoi(word);
		memmove(nr_1, &nr, sizeof(int16_t));
		word = strtok(NULL, " ");
		nr_2 = malloc(sizeof(int32_t));
		check_alloc(nr_2);
		nr = atoi(word);
		memmove(nr_2, &nr, sizeof(int32_t));
	} else {
		sz_1 = sizeof(int32_t);
		sz_2 = sizeof(int32_t);
		word = strtok(NULL, " ");
		nr_1 = malloc(sizeof(int32_t));
		check_alloc(nr_1);
		nr = atoi(word);
		memmove(nr_1, &nr, sizeof(int32_t));
		word = strtok(NULL, " ");
		nr_2 = malloc(sizeof(int32_t));
		check_alloc(nr_2);
		nr = atoi(word);
		memmove(nr_2, &nr, sizeof(int32_t));
	}
	to_add->data = realloc(to_add->data, sz + sz_1);
	memmove(to_add->data + sz, nr_1, sz_1);
	sz += sz_1;
	to_add->data = realloc(to_add->data, sz + sz_2);
	memmove(to_add->data + sz, nr_2, sz_2);
	sz += sz_2;
	free(nr_1), free(nr_2);
	// ]
	// adaug al 2-lea char* in void *data [
	word = strtok(NULL, " ");
	to_add->data = realloc(to_add->data, sz + strlen(word) + 1);
	add_word = strdup(word);
	check_alloc(add_word);
	memmove(to_add->data + sz, add_word, strlen(word) + 1);
	free(add_word);
	sz += strlen(word) + 1;
	// ]
	// adaug type si len in structura [
	to_add->header->type = type + 48;
	to_add->header->len = (unsigned int) sz;
	// ]
	return to_add;
}
int add_last(void **arr, int *len, data_structure *data)
{
	// sz retine numarul de octeti parcursi, sz_data - lunginea void *data
	int sz = 0, sz_data = 0;
	sz_data = data->header->len;
	unsigned int *act_len = NULL;
	// parcurg pana la finalul vectorului [
	for (int i = 0; i < *len; ++i) {
		sz += U_C;
		act_len = (unsigned int *)(*arr + sz);
		sz += U_I;
		sz += *act_len;
	}
	// ]
	if (!(*arr))
		*arr = malloc(sz + U_C + U_I + sz_data);
	else
		*arr = realloc(*arr, sz + U_C + U_I + sz_data);
	check_alloc(*arr);
	// adaug "partile" structurii separat [
	memmove(*arr + sz, &(data->header->type), U_C);
	sz += U_C;
	memmove(*arr + sz, &(data->header->len), U_I);
	sz += U_I;
	memmove(*arr + sz, (data->data), sz_data);
	// ]
	sz += sz_data;
	++(*len);
	return 0;
}

int add_at(void **arr, int *len, data_structure *data, int index)
{
	// sz retine numarul de octeti parcursi
	// sz_2 retine numarul de octeti parcursi pana la pozitia unde adaug
	int sz = 0, sz_data = 0, sz_2 = 0;
	sz_data = data->header->len;
	if (index < 0)
		return -1;
	if (index > *len)
		index = *len;
	unsigned int *act_len = NULL;
	// parcurg pana la pozitia unde adaug [
	for (int i = 0; i < *len; ++i) {
		sz += U_C;
		act_len = (unsigned int *)(*arr + sz);
		sz += U_I;
		sz += *act_len;
		if (i < index)
			sz_2 += U_I + U_C + *act_len;
	}
	// ]
	// folosesc un vector auxiliar pentru a stoca octetii ce vor fi shiftati
	void *aux = NULL;
	aux = malloc(sz - sz_2);
	check_alloc(aux);
	memmove(aux, *arr + sz_2, sz - sz_2);
	// ]
	// maresc dimensiunea vectorului generic [
	if (arr)
		*arr = realloc(*arr, sz + sz_data + U_I + U_C);
	else
		*arr = malloc(sz + sz_data + U_I + U_C);
	check_alloc(*arr);
	// ]
	// adaug "partile" noii structuri [
	memmove(*arr + sz_2, &(data->header->type), U_C);
	sz_2 += U_C;
	memmove(*arr + sz_2, &(data->header->len), U_I);
	sz_2 += U_I;
	memmove(*arr + sz_2, data->data, sz_data);
	sz_2 += sz_data;
	// ]
	// shiftarea [
	memmove(*arr + sz_2, aux, sz - sz_2 + U_I + U_C + sz_data);
	// ]
	free(aux);
	++(*len);
	return 0;
}

void print_details(unsigned char *act_type, void *arr, int sz)
{
	// afisez fiecare tip de structura [
	printf("Tipul %c\n", *act_type);
	if (*act_type == '1') {
		char *word_1 = (char *)(arr + sz);
		int delay = strlen(word_1) + sizeof(int8_t) + sizeof(int8_t) + 1;
		char *word_2 = (char *)(arr + sz + delay);
		int8_t nr_1 = *(int8_t *) (arr + sz + strlen(word_1) + 1);
		int8_t nr_2 = *(int8_t *) (arr + sz + strlen(word_1) + sizeof(int8_t) + 1);
		printf("%s pentru %s\n", word_1, word_2);
		printf("%"PRId8"\n", nr_1);
		printf("%"PRId8"\n", nr_2);
	} else if (*act_type == '2') {
		char *word_1 = (char *)(arr+ sz);
		int delay = strlen(word_1) + sizeof(int16_t) + sizeof(int32_t) + 1;
		char *word_2 = (char *)(arr + sz + delay);
		int16_t nr_1 = *(int16_t *) (arr + sz + strlen(word_1) + 1);
		int32_t nr_2 = *(int32_t *) (arr + sz + strlen(word_1) + sizeof(int16_t) + 1);
		printf("%s pentru %s\n", word_1, word_2);
		printf("%"PRId16"\n", nr_1);
		printf("%"PRId32"\n", nr_2);
	} else if (*act_type == '3') {
		char *word_1 = (char *)(arr + sz);
		int delay = strlen(word_1) + sizeof(int32_t) + sizeof(int32_t) + 1;
		char *word_2 = (char *)(arr + sz + delay);
		int32_t nr_1 = *(int32_t *) (arr + sz + strlen(word_1) + 1);
		int32_t nr_2 = *(int32_t *) (arr + sz + strlen(word_1) + sizeof(int32_t) + 1);
		printf("%s pentru %s\n", word_1, word_2);
		printf("%"PRId32"\n", nr_1);
		printf("%"PRId32"\n", nr_2);
	}
	printf("\n");
	// ]
}

void find(void *data_block, int len, int index) 
{
	if (index < 0 || index >= len)
		return;
	int sz = 0;
	unsigned char *act_type = NULL;
	unsigned int *act_len = NULL;
	// parcurg vectorul pana la pozitia respectiva [
	for (int i = 0; i < index; ++i) {
		act_type = (unsigned char *)(data_block + sz);
		sz += U_C;
		act_len = (unsigned int *)(data_block + sz);
		sz += U_I;
		sz += *act_len;
	}
	// ]
	act_type = (unsigned char *)(data_block + sz);
	sz += U_C;
	act_len = (unsigned int *)(data_block + sz);
	sz += U_I;
	print_details(act_type, data_block, sz);
}

int delete_at(void **arr, int *len, int index)
{
	// sz retine numarul de octeti parcursi
	// sz_2 retine numarul de octeti parcursi pana la pozitia unde adaug
	if (index < 0 || index >= *len)
		return 0;
	int sz = 0;
	int sz_2 = 0;
	unsigned int *act_len = NULL;
	if (index < 0)
		return -1;
	if (index > *len)
		index = *len;
	// parcurg vectorul [
	for (int i = 0; i < *len; ++i) {
		sz += U_C;
		act_len = (unsigned int *)(*arr + sz);
		sz += U_I;
		sz += *act_len;
		if (i < index)
			sz_2 += U_I + U_C + *act_len;
	}
	// ]
	// lungimea void *data ce va fi sters [
	unsigned int nr = *(unsigned int *)(*arr + sz_2 + U_C);
	// ]
	// creez vector auxiliar ce retine octetii ce vor fi shiftati la stanga [
	void *aux = NULL;
	aux = malloc(sz - sz_2 - U_I - U_C - nr);
	check_alloc(aux);
	memmove(aux, *arr + sz_2 + U_I + U_C + nr, sz - sz_2 - U_C - U_I - nr);
	// ]
	// shiftare si redimensionare a vectorului generic [
	memmove(*arr + sz_2, aux, sz - sz_2 - U_I - U_C - nr);
	*arr = realloc(*arr, sz - nr - U_I - U_C);
	// ]
	free(aux);
	--(*len);
	return 0;
}

void print(void *arr, int len) {
	int sz = 0;
	unsigned char *act_type = NULL;
	unsigned int *act_len = NULL;
	// parcurgerea vectorului generic [
	for (int i = 0; i < len; ++i) {
		act_type = (unsigned char *)(arr + sz);
		sz += U_C;
		act_len = (unsigned int *)(arr + sz);
		sz += U_I;
		print_details(act_type, arr, sz);
		sz += *act_len;
	}
	// ]
}

int main() {
	void *arr = NULL;
	int len = 0;
	char *line = NULL;
	line = malloc(DIM * sizeof(char));
	while(fgets(line, DIM, stdin)) {
		line[strcspn(line, "\n")] = '\0';
		char *word = strtok(line, " ");
		if (!strncmp(word, "insert", 6)) {
			int index = 0, ok = 0;
			if (!strcmp(word, "insert_at")) {
				ok = 1;
				word = strtok(NULL, " ");
				index = atoi(word);
			}
			data_structure *to_add = combine_details(word);
			if (ok)
				add_at(&arr, &len, to_add, index);
			else
				add_last(&arr, &len, to_add);
			free(to_add->header);
			free(to_add->data);
			free(to_add);
		} else if (!strcmp(word, "print")) {
			print(arr, len);
		} else if (!strcmp(word, "find")) {
			word = strtok(NULL, " ");
			find(arr, len, atoi(word));
		} else if (!strcmp(word, "delete_at")) {
			word = strtok(NULL, " ");
			delete_at(&arr, &len, atoi(word));
		} else if (!strcmp(word, "exit")) {
			if(arr)
				free(arr);
			free(line);
			return 0;
		}
	}
	return 0;
}

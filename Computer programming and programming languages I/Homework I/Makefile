# Copyright Bogdan Valentin-Razvan 311CA
# compiler setup
CSETT=-Wall -Wextra

build: ninel codeinvim vectsecv nomogram

ninel: ninel.c
	gcc $(CSETT) ninel.c -o ninel

codeinvim: codeinvim.c
	gcc $(CSETT) codeinvim.c -o codeinvim

vectsecv: vectsecv.c
	gcc $(CSETT) vectsecv.c -o vectsecv

nomogram: nomogram.c
	gcc $(CSETT) nomogram.c -o nomogram
	
clean:
	rm -f ninel codeinvim vectsecv nomogram

.PHONY: clean


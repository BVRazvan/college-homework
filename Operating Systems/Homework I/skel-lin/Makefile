CC = gcc
CFLAGS = -fPIC -m32 -Wall
LDFLAGS = -m32

.PHONY: build
build: libso_loader.so

libso_loader.so: loader.o exec_parser.o segment_operations.o
	$(CC) $(LDFLAGS) -shared -o $@ $^

exec_parser.o: loader/exec_parser.c loader/exec_parser.h
	$(CC) $(CFLAGS) -o $@ -c $<

loader.o: loader/loader.c
	$(CC) $(CFLAGS) -o $@ -c $<

segment_operations.o: loader/segment_operations.c loader/segment_operations.h
	$(CC) $(CFLAGS) -o $@ -c $<

.PHONY: clean
clean:
	-rm -f exec_parser.o loader.o segment_operations.o libso_loader.so

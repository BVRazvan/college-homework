CC=gcc
CFLAGS=-Wall
AIO=-laio

.PHONY: build

build: aws

aws: aws.o http_parser.o sock_util.o
	$(CC) $(CFLAGS) -o $@ $^ $(AIO)

aws.o: aws.c
	$(CC) $(CFLAGS) -o $@ -c $<

http_parser.o: http_parser.c
	$(CC) $(CFLAGS) -o $@ -c $<

sock_util.o: sock_util.c
	$(CC) $(CFLAGS) -o $@ -c $<

.PHONY: clean

clean:
	rm -rf aws *.o

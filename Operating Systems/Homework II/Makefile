CC = gcc
CFLAGS = -fPIC -Wall

.PHONY: build

build: libscheduler.so so_scheduler.o

libscheduler.so: so_scheduler.o so_scheduler_helper.o
	$(CC) $(LDFLAGS) -shared -o $@ $^

so_scheduler.o: so_scheduler.c util/so_scheduler.h
	$(CC) $(CFLAGS) -o $@ -c $<

so_scheduler_helper.o: so_scheduler_helper.c so_scheduler_helper.h
	$(CC) $(CFLAGS) -o $@ -c $<

.PHONY: clean

clean:
	-rm -f libscheduler.so *.o

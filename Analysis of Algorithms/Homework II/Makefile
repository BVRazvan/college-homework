CC = g++
CFLAGS = -Wall -Wextra -std=c++17
LDFLAGS = -lm

build: trial rise redemption

run_trial:
	./trial

run_rise:
	./rise

run_redemption:
	./redemption

trial: trial.cpp
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

rise: rise.cpp
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

redemption: redemption.cpp
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

pack:
	zip -FSr 321CA_BanigreiOctavian.zip *.cpp *.hpp *.h Makefile README

clean:
	rm -f trial rise redemption

.PHONY: pack build clean

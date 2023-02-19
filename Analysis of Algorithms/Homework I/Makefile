
.PHONY: build

build: backtracking_rucsac dp_rucsac greedy_rucsac greedy_rucsac dp_spatiu_liniar_rucsac

backtracking_rucsac: backtracking_rucsac.cpp
	g++ $< -o $@

dp_rucsac: dp_rucsac.cpp
	g++ $< -o $@

greedy_rucsac: greedy_rucsac.cpp
	g++ $< -o $@

dp_spatiu_liniar_rucsac: dp_spatiu_liniar_rucsac.cpp
	g++ $< -o $@

run-p1: backtracking_rucsac
	./backtracking_rucsac

run-p2: dp_rucsac
	./dp_rucsac

run-p3: greedy_rucsac
	./greedy_rucsac

run-p4: dp_spatiu_liniar_rucsac
	./dp_spatiu_liniar_rucsac

run-best: dp_spatiu_liniar_rucsac
	./dp_spatiu_liniar_rucsac

.PHONY: clean

clean:
	rm -rf *_rucsac

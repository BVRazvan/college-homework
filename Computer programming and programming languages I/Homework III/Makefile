
# Copyright Valentin-Razvan Bogdan 311CAa 2021-2022 <bogdanvrazvan@gmail.com>

build: image_editor

image_editor: image_editor.c 
	gcc -Wall -Wextra -std=c99  -o image_editor image_editor.c effects.c crop.c rotate.c parsing.c read_mats.c load.c mat_free.c select.c swap.c apply.c save.c exit.c fail.c -lm
clean:
	rm -rf image_editor

.PHONY: clean

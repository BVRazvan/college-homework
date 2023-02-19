#!/bin/bash

make clean && make && cd checker-lin && make -f Makefile.checker clean && rm libscheduler.so && cp ../libscheduler.so . && make -f Makefile.checker
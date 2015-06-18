all: lrustack freqsize

freqsize: freqsize.c
	gcc freqsize.c -o freqsize
lrustack: lrustack.h availdocs.h lrustack.cpp availdocs.cpp main.cpp
	g++ -c availdocs.cpp
	g++ -c lrustack.cpp
	g++ -c main.cpp 
	g++ -o lrustack lrustack.o availdocs.o main.o ../prowgen/distributions.o -lm 

clean:
	rm *~ *.o lrustack freqsize

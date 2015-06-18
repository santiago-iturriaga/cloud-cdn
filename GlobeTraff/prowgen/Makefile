ProWGen: request.o distributions.o stream.o main.o stack.o 
	g++ -o ProWGen main.o request.o  stack.o stream.o distributions.o -lm

request.o: request.cc request.h 
	g++ -c -g request.cc

stack.o: stack.cc stack.h
	g++ -c -g stack.cc

distributions.o: distributions.cc distributions.h
	g++ -c -g distributions.cc

stream.o: stream.cc stream.h  stack.h 
	g++ -c -g stream.cc

main.o: main.cc stream.h
	g++  -c -g main.cc


clean:
	rm -f ProWGen main.o request.o stack.o stream.o distributions.o





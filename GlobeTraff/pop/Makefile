CC=gcc
CFLAGS1=
EXTRA_CFLAGS=-g

CFLAGS=$(CFLAGS1) $(EXTRA_CFLAGS)
LIBS=-lm
INCLUDES=

.c.o: 
	$(CC) -c $(CFLAGS) $(INCLUDES) $<

OBJS=main.o getch.o gettoken.o hash.o


hash: $(OBJS)
	$(CC) -o popularity $(OBJS) $(LIBS) 


clean:
	rm -f $(OBJS) popularity


CC = gcc
CFLAGS = -mwindows
RC = windres
OBJS = thir.o res.o

thir.exe: $(OBJS)
	$(CC) -o $@ $^ $(FLAGS)

thir.o: thir.c include/thir.h
	$(CC) -c $@ $<

res.o: res.rc manifest.xml
	$(RC) -o $@ $<

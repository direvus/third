CC = gcc
CFLAGS = -O2 -mwindows
RC = windres
RT = "/c/Program Files/POV-Ray/bin/pvengine.exe"
RTFLAGS = +FS +W800 +H600 -EXIT
OBJS = thir.o res.o

thir.exe: $(OBJS)
	$(CC) -o $@ $^ $(CFLAGS)

thir.o: thir.c include/thir.h
	$(CC) -c $<

res.o: res.rc manifest.xml include/tetra.bmp
	$(RC) -o $@ $<

images/tetra.bmp: images/tetra.pov
	$(RT) $(RTFLAGS) $<

clean:
	rm $(OBJS)
	rm images/*.bmp

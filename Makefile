CC = gcc
CFLAGS = -O2 -mwindows
RC = windres
RT = "/c/Program Files/POV-Ray/bin/pvengine.exe"
RTFLAGS = +FS +W800 +H600 -EXIT
OBJS = thir.o res.o
IMGS = include/coin.bmp include/tetra.bmp include/hex.bmp include/oct.bmp include/dec.bmp include/dodec.bmp include/icos.bmp include/2dec.bmp

thir.exe: $(OBJS)
	$(CC) -o $@ $^ $(CFLAGS)

thir.o: thir.c include/thir.h
	$(CC) -c $<

res.o: res.rc include/manifest.xml $(IMGS)
	$(RC) -o $@ $<

images/%.bmp: images/%.pov
	$(RT) $(RTFLAGS) $<

clean:
	rm $(OBJS)
	rm images/*.bmp

CC = gcc
CFLAGS = -O2
LFLAGS = -mwindows
RC = windres
RT = "/c/Program Files/POV-Ray/bin/pvengine.exe"
RTFLAGS = +FS +W24 +H18 +A -EXIT
ZIP = "/d/programs/7-Zip/7z.exe"
ZIPFLAGS = a

OBJS = thir.o res.o mt19937ar.o
IMGS = include/coin.bmp include/tetra.bmp include/hex.bmp include/oct.bmp include/dec.bmp include/dodec.bmp include/icos.bmp include/2dec.bmp include/dodec_32x32.ico include/oct_16x16.ico include/var.bmp

thir.exe: $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LFLAGS)

thir.o: thir.c include/thir.h
	$(CC) $(CFLAGS) -c $<

res.o: res.rc include/manifest.xml $(IMGS)
	$(RC) -o $@ $<

mt19937ar.o: mt19937ar.c include/mt19937ar.h
	$(CC) $(CFLAGS) -c $<

images/%.bmp: images/%.pov
	$(RT) $(RTFLAGS) $<

clean:
	-rm thir.exe $(OBJS) images/*.bmp

dist/thir.7z: thir.exe README README.html
	$(ZIP) $(ZIPFLAGS) -t7z $@ $^

dist/thir.zip: thir.exe README README.html
	$(ZIP) $(ZIPFLAGS) -tzip $@ $^

dist:	dist/thir.7z dist/thir.zip

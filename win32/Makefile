CC = gcc
CFLAGS = -O2 -Wall
LFLAGS = -mwindows
RC = windres
ZIP = "/d/programs/7-Zip/7z.exe"
ZIPFLAGS = a

OBJS = third.o res.o mt19937ar.o
IMGS = include/coin.bmp include/tetra.bmp include/hex.bmp include/oct.bmp include/dec.bmp include/dodec.bmp include/icos.bmp include/2dec.bmp include/dodec_32x32.ico include/oct_16x16.ico include/var.bmp

third.exe: $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LFLAGS)

third.o: third.c include/third.h
	$(CC) $(CFLAGS) -c $<

res.o: res.rc include/manifest.xml $(IMGS)
	$(RC) -o $@ $<

mt19937ar.o: mt19937ar.c include/mt19937ar.h
	$(CC) $(CFLAGS) -c $<

clean:
	-rm third.exe $(OBJS)

../dist/third-w32.7z: third.exe README README.html
	$(ZIP) $(ZIPFLAGS) -t7z $@ $^

../dist/third-w32.zip: third.exe README README.html
	$(ZIP) $(ZIPFLAGS) -tzip $@ $^

dist:	../dist/third-w32.7z ../dist/third-w32.zip

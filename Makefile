RT = "povray"
RTFLAGS = -D +W32 +H24 +A

PNGS = coin.png tetra.png hex.png oct.png dec.png dodec.png icos.png 2dec.png
BMPS = coin.bmp tetra.bmp hex.bmp oct.bmp dec.bmp dodec.bmp icos.bmp 2dec.bmp

%.bmp: %.pov
	$(RT) $(RTFLAGS) +FS +O- $< > $@

%.png: %.pov
	$(RT) $(RTFLAGS) +FN +O- $< > $@

bmps: $(BMPS)

pngs: $(PNGS)

gtk: $(PNGS)
	cp -f $^ ../gtk/include/

clean:
	rm -f $(BMPS)
	rm -f $(PNGS)

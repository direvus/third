RT = "povray"
RTFLAGS = -D +W32 +H24 +A +Q11

BMPS = coin.bmp tetra.bmp hex.bmp oct.bmp dec.bmp dodec.bmp icos.bmp 2dec.bmp
RTPNGS = coin.png tetra.png hex.png oct.png dec.png dodec.png icos.png 2dec.png

SVG = "inkscape"
SVGFLAGS = -z

SVGPNGS = app.png d2.png d4.png d6.png d8.png d10.png d12.png d20.png d100.png mul.png mod.png

$(BMPS): %.bmp: %.pov
	$(RT) $(RTFLAGS) +FS +O- $< > $@

$(RTPNGS): %.png: %.pov
	$(RT) $(RTFLAGS) +FN +UA +O- $< > $@

$(SVGPNGS): %.png: %.svg
	$(SVG) $(SVGFLAGS) -e $@ $<

bmps: $(BMPS)

rtpngs: $(RTPNGS)

svgpngs: $(SVGPNGS)

gtk: $(RTPNGS) $(SVGPNGS) app.svg
	cp -f $^ ../gtk/include/

clean:
	rm -f $(BMPS) $(RTPNGS) $(SVGPNGS)

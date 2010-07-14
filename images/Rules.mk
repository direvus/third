RT = "povray"
RTFLAGS = -D +W32 +H24 +A +Q11
SVG = "inkscape"
SVGFLAGS = -z

POVS = $(wildcard images/*.pov)
SVGS = $(wildcard images/*.svg)
ICOS = images/app.ico
POVBMPS = $(POVS:.pov=.bmp)
POVPNGS = $(POVS:.pov=.png)
SVGPNGS = $(SVGS:.svg=.png)

SHARE += $(SVGPNGS)
FILES += $(SVGPNGS)
CLEAN += $(POVBMPS) $(POVPNGS) $(SVGPNGS)

$(POVBMPS): %.bmp: %.pov
	$(RT) $(RTFLAGS) +FS +O- $< > $@

$(POVPNGS): %.png: %.pov
	$(RT) $(RTFLAGS) +FN +UA +O- $< > $@

$(SVGPNGS): %.png: %.svg
	$(SVG) $(SVGFLAGS) -e $@ $<

$(ICOS): %.ico: %.png
	icotool -c -o $@ $<

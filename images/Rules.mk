SVG = "inkscape"
SVGFLAGS = -z

SVGS = $(wildcard images/*.svg)
ICOS = images/app.ico
SVGPNGS = $(SVGS:.svg=.png)

SHARE += $(SVGPNGS)
FILES += $(SVGPNGS)
CLEAN += $(SVGPNGS)

$(SVGPNGS): %.png: %.svg
	$(SVG) $(SVGFLAGS) -e $@ $<

$(ICOS): %.ico: %.png
	icotool -c -o $@ $<

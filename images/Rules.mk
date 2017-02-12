SVG = "inkscape"
SVGFLAGS = -z

SVGS = $(wildcard images/*.svg)
ICOS = images/app.ico
SVGPNGS = $(SVGS:.svg=.png)
SVGPNGS_48 = $(SVGS:.svg=_48.png)

SHARE += $(SVGPNGS)
FILES += $(SVGPNGS)
DROIDFILES += $(SVGPNGS) images/app_48.png
CLEAN += $(SVGPNGS)

$(SVGPNGS): %.png: %.svg
	$(SVG) $(SVGFLAGS) -e $@ $<

$(SVGPNGS_48): %_48.png: %.svg
	$(SVG) $(SVGFLAGS) -w 48 -h 48 -e $@ $<

$(ICOS): %.ico: %.png
	icotool -c -o $@ $<

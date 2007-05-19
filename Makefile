RT = "povray"
RTFLAGS = -D +W24 +H18 +A

images/%.bmp: images/%.pov
	$(RT) $(RTFLAGS) +FS +O- $< > $@

images/%.png: images/%.pov
	$(RT) $(RTFLAGS) +FN +O- $< > $@

clean:
	rm -f images/*.bmp
	rm -f images/*.png

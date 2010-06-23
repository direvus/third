DISTDIR = ./dist
NAME = third
FILES = *.py include/* Makefile.dist

dist: third.tar.bz2 third-win32.zip

third.tar.bz2: $(FILES)
	mkdir -p $(DISTDIR)/$(NAME)/include
	install third.py $(DISTDIR)/$(NAME)
	install Makefile.dist $(DISTDIR)/$(NAME)/Makefile
	install include/* $(DISTDIR)/$(NAME)/include
	tar -cjvpf $@ -C $(DISTDIR) $(NAME)/

third-win32.zip: $(FILES)
	mkdir -p $(DISTDIR)/$(NAME)
	install third.py $(DISTDIR)/$(NAME)
	install include/* $(DISTDIR)/$(NAME)
	(cd $(DISTDIR) && zip -r ../$@ $(NAME))

clean:
	rm -rf $(DISTDIR)

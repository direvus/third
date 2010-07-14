DISTDIR = dist
NAME = third
SHARE = images/app.svg
FILES = *.py Makefile.dist
CLEAN = $(DISTDIR)

all: third.tar.bz2 third-win32.zip

dir = images
include images/Rules.mk

third.tar.bz2: $(FILES) Makefile
	rm -rf $(DISTDIR)
	mkdir -p $(DISTDIR)/$(NAME)/share
	install third.py $(DISTDIR)/$(NAME)
	install Makefile.dist $(DISTDIR)/$(NAME)/Makefile
	install $(SHARE) $(DISTDIR)/$(NAME)/share
	tar -cjvpf $@ -C $(DISTDIR) $(NAME)/

third-win32.zip: $(FILES) Makefile
	rm -rf $(DISTDIR)
	mkdir -p $(DISTDIR)/$(NAME)
	install third.py $(SHARE) $(DISTDIR)/$(NAME)
	(cd $(DISTDIR) && zip -r ../$@ $(NAME))

clean:
	rm -rf $(CLEAN)

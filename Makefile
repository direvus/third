DISTDIR = dist-linux
NAME = third
SHARE = images/app.svg
FILES = *.py Makefile.dist
CLEAN = $(DISTDIR)

all: third.tar.bz2

dir = images
include images/Rules.mk

third.tar.bz2: $(FILES) Makefile
	rm -rf $(DISTDIR)
	mkdir -p $(DISTDIR)/$(NAME)/share
	install third.py $(DISTDIR)/$(NAME)
	install Makefile.dist $(DISTDIR)/$(NAME)/Makefile
	install $(SHARE) $(DISTDIR)/$(NAME)/share
	tar -cjvpf $@ -C $(DISTDIR) $(NAME)/

clean:
	rm -rf $(CLEAN)

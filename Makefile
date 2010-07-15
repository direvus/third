DISTDIR = dist-linux
NAME = third
SHARE = images/app.svg
DOCS = README
FILES = *.py Makefile.dist $(DOCS)
CLEAN = $(DISTDIR)

all: third.tar.bz2

dir = images
include images/Rules.mk

third.tar.bz2: $(FILES) Makefile
	rm -rf $(DISTDIR)
	mkdir -p $(DISTDIR)/$(NAME)
	install third.py $(DISTDIR)/$(NAME)
	install Makefile.dist $(DISTDIR)/$(NAME)/Makefile
	mkdir -p $(DISTDIR)/$(NAME)/share
	install $(SHARE) $(DISTDIR)/$(NAME)/share
	mkdir -p $(DISTDIR)/$(NAME)/doc
	install $(DOCS) $(DISTDIR)/$(NAME)/doc
	tar -cjvpf $@ -C $(DISTDIR) $(NAME)/

clean:
	rm -rf $(CLEAN)

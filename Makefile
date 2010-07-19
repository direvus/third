LINUX = dist-linux
WIN32 = dist-win32
NAME = third
SHARE = images/app.svg
DOCS = README
FILES = *.py Makefile.dist $(DOCS)
WIN32FILES = *.py $(DOCS) images/*.png images/*.ico *.iss *.bat *.exe
CLEAN = $(LINUX) $(WIN32)

all: linux win32

linux: third.tar.bz2

win32: third-win32-source.zip

dir = images
include images/Rules.mk

third.tar.bz2: $(FILES) Makefile
	rm -rf $(LINUX)
	mkdir -p $(LINUX)/$(NAME)
	install third.py $(LINUX)/$(NAME)
	install Makefile.dist $(LINUX)/$(NAME)/Makefile
	mkdir -p $(LINUX)/$(NAME)/share
	install $(SHARE) $(LINUX)/$(NAME)/share
	mkdir -p $(LINUX)/$(NAME)/doc
	install $(DOCS) $(LINUX)/$(NAME)/doc
	tar -cjvpf $@ -C $(LINUX) $(NAME)/

third-win32-source.zip: $(WIN32FILES) Makefile
	rm -rf $(WIN32)
	mkdir -p $(WIN32)/$(NAME)
	install $(WIN32FILES) $(WIN32)/$(NAME)
	(cd $(WIN32) && zip -r ../$@ $(NAME))

clean:
	rm -rf $(CLEAN)

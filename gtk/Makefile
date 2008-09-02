DESTDIR = 
BINDIR = $(DESTDIR)/usr/local/bin
SHAREDIR = $(DESTDIR)/usr/local/share/third

install:
	mkdir -p $(BINDIR)
	install third.py $(BINDIR)/
	mkdir -p $(SHAREDIR)
	install include/* $(SHAREDIR)/

package: third.tar.bz2

third.tar.bz2: DESTDIR = ./package
third.tar.bz2: install
	tar -cjvpf $@ -C $(DESTDIR) $(DESTDIR)/


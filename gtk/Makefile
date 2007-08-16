PY = python
PYFLAGS = -OO

third.pyo: third.py
	$(PY) $(PYFLAGS) -c "import py_compile; py_compile.compile(\"$^\");"

clean:
	-rm third.py?

@echo off
REM Call py2exe, copy select files from the GTK+ installation into the dist
REM directory, and then call InnoSetup to build the installer.

set dist="dist"
set py="C:\Program Files (x86)\Python26\python.exe"
set gtk="C:\Program Files (x86)\GTK+"
set inno="C:\Program Files (x86)\Inno Setup 5\ISCC.exe"

call %py% setup.py py2exe
for %%t in ("etc" "lib" "share\locale\en_GB" "share\themes") do ^
xcopy %gtk%\%%t %dist%\%%t\ /s /y
copy %gtk%\bin\intl.dll %dist%\ /y
call %inno% install.iss

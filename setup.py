"""
third: That's How I Roll Dice
    A dice roller for roleplaying nerds.
        http://swords.id.au/third/

Copyright (c) 2010, Brendan Jurd <bj@swords.id.au>
All rights reserved.

third is open-source, licensed under the Simplified BSD License, a copy of
which can be found in the file LICENSE at the top level of the source code.

"""
from distutils.core import setup
from glob import glob
import py2exe

setup(
    name = "third",
    description = "That's How I Roll Dice",
    version = "1.0",

    windows = [{
        "script": "third.py",
        "icon_resources": [(1, "app.ico")]
    }],

    options = {
        "py2exe": {
            "packages": ["encodings", "gtk"],
            "includes": "cairo, pango, pangocairo, atk, gobject, gio, glib",
            "dll_excludes": ["iconv.dll","intl.dll","DNSAPI.dll"]
        }
    },

    data_files = glob("*.png") + ["README"],
)

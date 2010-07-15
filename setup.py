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
            "packages": "encodings",
            "includes": "cairo, pango, pangocairo, atk, gobject, gio"
        }
    },

    data_files = glob("*.png") + ["README"],
)


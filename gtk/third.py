#!/usr/bin/env python
"""THIR: That's How I Roll

A dice roller for roleplaying nerds.

THIR provides all of the common dice used in roleplaying: d2, d4, d6, d8, d10,
d12, d20 and d100.  It also lets you roll dice with an arbitrary number of
sides.

You can also add modifiers and multipliers to your dice roll, which greatly
simplifies arithmetically tedious rolls like (5d6 * 2) - 8.

The goal is to take the process of rolling dice and adding numbers together out
of the equation, so we can get on with the having of fun.

"""

import pygtk
pygtk.require('2.0')
import gtk
import pango
from random import randint

class Die(gtk.Button):
    """A button representing a particular die."""

    def __init__(self, sides, icon):
        gtk.Button.__init__(self)

        self.set_border_width(0)
        self.set_focus_on_click(False)
        self.set_relief(gtk.RELIEF_NONE)

        self.image = gtk.Image()
        self.image.set_from_file(''.join(["include/", icon, ".png"]))
        self.image.show()
        self.add(self.image)

        self.show()

class Counter(gtk.SpinButton):
    """A spinner widget which contains the quantity of each die to roll."""

    def __init__(self, sides):
        gtk.SpinButton.__init__(self, gtk.Adjustment(0, 0, 65535, 1, 0))

        self.set_numeric(True)
        self.modify_font(pango.FontDescription("sans,monospace normal 9"))
        self.show()

class DieBox(gtk.VBox):
    """The box containing the die buttons."""

    def __init__(self, spacing=2):
        gtk.VBox.__init__(self, True, spacing)
        self.counters = {}
        self.show()

    def add_die(self, sides, icon):
        button = Die(sides, icon)
        button.connect("button_press_event", self.press, sides)

        counter = Counter(sides)
        self.counters[sides] = counter

        hb = gtk.HBox(False)

        hb.pack_start(button, False, False)
        hb.pack_end(counter, False, False)

        self.pack_start(hb, False, False)
        hb.show()

    def press(self, widget, event, data=None):

        counter = self.counters[data]

        if event.button == 1:
            step = gtk.SPIN_STEP_FORWARD

            if event.state & gtk.gdk.SHIFT_MASK:
                step = gtk.SPIN_PAGE_FORWARD

            counter.spin(step, 10)

        elif event.button == 3:
            step = gtk.SPIN_STEP_BACKWARD

            if event.state & gtk.gdk.SHIFT_MASK:
                step = gtk.SPIN_PAGE_BACKWARD

            counter.spin(step, 10)

        elif event.button == 2:
            counter.set_value(0)

        return True

class THIRD(gtk.Window):
    """The main THIRD application window."""

    def __init__(self):
        gtk.Window.__init__(self, gtk.WINDOW_TOPLEVEL)
        self.connect("delete_event", self.delete_event)

        self.set_border_width(5)

        self.dbox = DieBox()
        self.add(self.dbox)

        self.dbox.add_die(2, "coin")
        self.dbox.add_die(4, "tetra")
        self.dbox.add_die(6, "hex")
        self.dbox.add_die(8, "oct")
        self.dbox.add_die(10, "dec")
        self.dbox.add_die(12, "dodec")
        self.dbox.add_die(20, "icos")
        self.dbox.add_die(100, "2dec")

        self.show()

    def delete_event(self, widget, event, data=None):
        gtk.main_quit()
        return False

    def main(self):
        gtk.main()

if __name__ == '__main__':
    third = THIRD()
    third.main()

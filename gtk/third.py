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
import gobject
import pango
from random import randint


_dice_set = (2, 4, 6, 8, 10, 12, 20, 100)


def _roll(sides):
    return randint(1, sides)


def _validate_int(input):
    """Validate a signed integer.
    
    The allowed range is +/-32767.
    Illegal values will result in a ValueError being raised.

    """
    limit = 32767
    output = int(input)

    if abs(output) > limit:
        raise ValueError("Value %s is out of range.\n"
                         "Allowed values are %d through %d" 
                         % (input, -limit, limit))
    return output


def _validate_uint(input):
    """Validate an unsigned integer.

    Allowed range is zero to 65535.
    Illegal values will result in a ValueError being raised.

    """
    limit = 65535
    output = int(input)

    if not 0 <= output <= limit:
        raise ValueError("Value %s is out of range.\n"
                         "Allowed values are 0 through %d"
                         % (input, limit))
    return output


class THIRDError(Exception):
    """Crudmonkeys!  Something has gone wrong with THIRD.
    
    It's this class' job to explain the problem to the user.
    """
    def display(self):
        print "Crudmonkeys!  %s" % self


class Config:
    """A configuration of dice and modifiers to roll."""

    def __init__(self, name="", dice={}, 
                 dx_size=0, dx_count=0, modifier=0, multiplier=1):

        self.name = name.strip()
        self.dice = {}
        self.dx_size = 0
        self.dx_count = 0
        self.modifier = 0
        self.multiplier = 1

        for die in _dice_set:
            if die in dice:
                try:
                    self.dice[die] = _validate_int(dice[die])
                except ValueError, e:
                    te = THIRDError("Invalid number of d%d:\n%s" 
                                    % (die, e))
                    te.display()

        try:
            self.dx_size = _validate_uint(dx_size)
        except ValueError, e:
            te = THIRDError("Invalid number of sides for custom die:\n%s"
                            % e)
            te.display()

        try:
            self.dx_count = _validate_int(dx_count)
        except ValueError, e:
            te = THIRDError("Invalid number of custom dice:\n%s" % e)
            te.display()

        try:
            self.modifier = _validate_int(modifier)
        except ValueError, e:
            te = THIRDError("Invalid modifier:\n%s" % e)
            te.display()

        try:
            self.multiplier = _validate_uint(multiplier)
        except ValueError, e:
            te = THIRDError("Invalid multiplier:\n%s" % e)
            te.display()

            
    def roll(self, log=None):
        """Evaluate the configuration.

        This is done by rolling each of the dice, adding the results together
        and then applying the modifier and multiplier, if they are present.

        A negative die count means we subtract each of the rolls from the total
        instead of adding them.

        """
        total = 0
        number = 0

        for die, n in self.dice.iteritems():
            for i in range(0, abs(n)):
                result = _roll(die)
                if n < 0: result *= -1
                total += result
                number += 1
                log.append_result(number, "d%d" % die, result)

        if self.dx_size > 0:
            for i in range(0, abs(self.dx_count)):
                result = _roll(self.dx_size)
                if self.dx_count < 0: result *= -1
                total += result
                number += 1
                log.append_result(number, "d%d" % self.dx_size, result)

        if self.modifier != 0:
            total += self.modifier
            label = self.modifier > 0 and "+" or "-"
            number += 1
            log.append_result(number, label, abs(self.modifier))

        if self.multiplier != 1:
            total *= self.multiplier
            number += 1
            log.append_result(number, "x", self.multiplier)

        return total

    def describe(self):
        """Return a string representation of this configuration."""

        str = ""
        pos = []
        neg = []

        for die, n in self.dice.iteritems():
            if n == 0:
                continue
            elif abs(n) > 1:
                term = "%dd%d" % (abs(n), die)
            else:
                term = "d%d" % die

            if n > 0:
                pos.append(term)
            else:
                neg.append(term)
        if self.dx_size > 0 and self.dx_count != 0:
            if abs(self.dx_count) > 1:
                term = "%dd%d" % (abs(self.dx_count), self.dx_size)
            else:
                term = "d%d" % self.dx_size

            if self.dx_count > 0:
                pos.append(term)
            else:
                neg.append(term)
        if self.modifier > 0:
            pos.append(str(self.modifier))
        elif self.modifier < 0:
            neg.append(str(self.modifier))

        str = ' + '.join(pos)
        if neg:
            negterms = ' - '.join(neg)
            str = ' - '.join([str, negterms])

        if self.multiplier > 1:
            str = ' * '.join([str, str(self.multiplier)])
            
        return str


class Die(gtk.Button):
    """A button representing a particular die."""

    def __init__(self, sides, icon):
        gtk.Button.__init__(self)

        self.set_border_width(0)
        self.set_focus_on_click(False)
        self.set_relief(gtk.RELIEF_NONE)

        self.image = gtk.Image()
        self.image.set_from_file(''.join(["include/", icon, ".png"]))
        self.add(self.image)


class Counter(gtk.SpinButton):
    """A spinner widget which contains the quantity of each die to roll."""

    def __init__(self, sides):
        gtk.SpinButton.__init__(self, gtk.Adjustment(0, -32767, 32767, 1, 5))

        self.set_numeric(True)
        self.modify_font(pango.FontDescription("sans,monospace normal 9"))

    def value(self):
        return self.get_value_as_int()


class DieBox(gtk.VBox):
    """The box containing the die buttons."""

    def __init__(self, spacing=2):
        gtk.VBox.__init__(self, True, spacing)
        self.counters = {}

        self.add_die(2, "coin")
        self.add_die(4, "tetra")
        self.add_die(6, "hex")
        self.add_die(8, "oct")
        self.add_die(10, "dec")
        self.add_die(12, "dodec")
        self.add_die(20, "icos")
        self.add_die(100, "2dec")

    def add_die(self, sides, icon):
        button = Die(sides, icon)
        button.connect("button_press_event", self.press, sides)

        counter = Counter(sides)
        self.counters[sides] = counter

        hb = gtk.HBox(False)

        hb.pack_start(button, False, False)
        hb.pack_end(counter, False, False)

        self.pack_start(hb, False, False)

    def get_counter(self, sides):
        """Return a reference to the counter for a particular die."""
        return self.counters[sides]

    def get_counters(self):
        """Return a dictionary of all non-zero die counts.

        The dictionary form is "sides": "count"

        """
        result = {}
        for die, counter in self.counters.iteritems():
            n = counter.value()
            if n != 0:
                result[die] = n
        return result

    def press(self, widget, event, data=None):
        """One of the die buttons has received a mouse click.
        
        The data argument should contain the number of sides on the target die.

        Double and triple clicks are ignored because normal button press events
        are sent during a double or triple click anyway.
        
        """
        if event.type != gtk.gdk.BUTTON_PRESS:
            return True

        counter = self.counters[data]

        if event.button == 1:
            step = gtk.SPIN_STEP_FORWARD

            if event.state & gtk.gdk.SHIFT_MASK:
                step = gtk.SPIN_PAGE_FORWARD

            counter.spin(step)

        elif event.button == 3:
            step = gtk.SPIN_STEP_BACKWARD

            if event.state & gtk.gdk.SHIFT_MASK:
                step = gtk.SPIN_PAGE_BACKWARD

            counter.spin(step)

        elif event.button == 2:
            counter.set_value(0)

        return True


class THIRDLog(gtk.ListStore):
    """The columns of the log are, in order:

    The sequence number of the row
    The description of the item (roll or modifier)
    The numeric result of the item.

    """
    def append_result(self, count, label, amount):
        iter = self.append()
        self.set(iter, 0, count, 1, label, 2, amount)


class THIRDLogView(gtk.TreeView):

    def __init__(self, model):
        gtk.TreeView.__init__(self, model)


        cells = []
        for i in range(3): cells.append(gtk.CellRendererText())

        cells[0].xalign = 1.0
        cells[2].xalign = 1.0

        for i in range(3):
            col = gtk.TreeViewColumn("", cells[i], text=i)
            self.append_column(col)


class THIRD(gtk.Window):
    """The main THIRD application window."""

    def __init__(self):
        gtk.Window.__init__(self, gtk.WINDOW_TOPLEVEL)
        self.connect("delete_event", self.delete_event)

        self.set_border_width(5)
        self.set_title("third")
        
        self.bold = pango.FontDescription("sans bold 10")

        self.dbox = DieBox()

        self.rollbutton = gtk.Button(stock="gtk-ok")
        self.rollbutton.connect("clicked", self.roll)
        self.resetbutton = gtk.Button(stock="gtk-cancel")
        self.total = gtk.Label()
        self.total.modify_font(self.bold)
        self.total.set_alignment(1.0, 0.5)

        bb = gtk.HButtonBox()
        bb.set_layout(gtk.BUTTONBOX_EDGE)
        bb.add(self.rollbutton)
        bb.add(self.resetbutton)

        self.log = THIRDLog(gobject.TYPE_UINT, 
                            gobject.TYPE_STRING,
                            gobject.TYPE_INT)

        self.logview = THIRDLogView(self.log)

        self.logscroll = gtk.ScrolledWindow()
        self.logscroll.set_policy(gtk.POLICY_AUTOMATIC,
                                  gtk.POLICY_ALWAYS)
        self.logscroll.add_with_viewport(self.logview)

        self.label = gtk.Label()
        self.label.modify_font(self.bold)
        self.label.set_alignment(0.0, 0.5)
        self.label.set_text(self.get_config().describe())

        self.resultbox = gtk.VBox(False)
        self.resultbox.pack_start(self.label, False, False)
        self.resultbox.pack_start(self.logscroll, True, True)
        self.resultbox.pack_start(bb, True, False)
        self.resultbox.pack_start(self.total, True, False)

        self.mainbox = gtk.HBox(False, 5)
        self.mainbox.pack_start(self.dbox, False, False)
        self.mainbox.pack_start(self.resultbox, True, True)
        self.add(self.mainbox)

        self.show_all()

    def get_config(self):
        """Return a Config object based on the current widget settings."""

        # TODO: Implement dx, mod and mult here
        return Config("", self.dbox.get_counters())

    def set_config(self, config):
        """Populate the widgets based on values in a Config object."""

        # TODO

    def roll(self, widget, data=None):
        """Roll the current widget configuration.

        Results will be posted to the total label.

        """
        config = self.get_config()
        self.label.set_text(config.describe())
        self.log.clear()
        result = config.roll(self.log)
        self.total.set_text(str(result))
        return result

    def delete_event(self, widget, event, data=None):
        gtk.main_quit()
        return False

    def main(self):
        gtk.main()


if __name__ == '__main__':
    third = THIRD()
    third.main()

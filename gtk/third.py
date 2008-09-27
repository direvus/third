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

import os
import pygtk
pygtk.require('2.0')
import gtk
import gobject
import pango
from random import randint
import pickle


_dice_set = (2, 4, 6, 8, 10, 12, 20, 100)
_app_dir = os.getenv("HOME") + "/.third/"
_presets_file = _app_dir + "presets"
_share_dir = "/usr/local/share/third/"


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

    def __init__(self, name="", counters={}, dx_size=0, dx_count=0):
        self.set_name(name)
        self.dice = {}
        self.dx_size = 0
        self.dx_count = 0
        self.multiplier = 1
        self.modifier = 0

        for die in _dice_set:
            if die in counters:
                try:
                    self.dice[die] = _validate_int(counters[die])
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

        if counters.has_key('mod'):
            try:
                self.modifier = _validate_int(counters['mod'])
            except ValueError, e:
                te = THIRDError("Invalid modifier:\n%s" % e)
                te.display()

        if counters.has_key('mul'):
            try:
                self.multiplier = _validate_uint(counters['mul'])
            except ValueError, e:
                te = THIRDError("Invalid multiplier:\n%s" % e)
                te.display()

    def get_name(self):
        return self.name

    def set_name(self, name):
        self.name = name.strip()

    def has_dice(self):
        for die in self.dice:
            if self.dice[die] != 0:
                return True

        if self.dx_count != 0:
            return True

        return False
        
    def get_dice(self):
        return self.dice

    def get_die(self, sides):
        if sides in self.dice:
            return self.dice[sides]
        else:
            return 0

    def get_dx_size(self):
        return self.dx_size

    def get_dx_count(self):
        return self.dx_count

    def get_modifier(self):
        return self.modifier

    def get_multiplier(self):
        return self.multiplier

    def min(self):
        """Return the minimum possible outcome of this configuration."""
        total = 0

        for die, n in self.dice.iteritems():
            total += n

        if self.dx_size > 0:
            total += self.dx_count

        total *= self.multiplier
        total += self.modifier
        return total

    def max(self):
        """Return the maximum possible outcome of this configuration."""
        total = 0

        for die, n in self.dice.iteritems():
            total += (die * n)

        total += (self.dx_size * self.dx_count)
        total *= self.multiplier
        total += self.modifier
        return total

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

        if self.multiplier != 1:
            total *= self.multiplier
            number += 1
            log.append_result(number, "x", self.multiplier)

        if self.modifier != 0:
            total += self.modifier
            label = self.modifier > 0 and "+" or "-"
            number += 1
            log.append_result(number, label, abs(self.modifier))

        log.append_total(total)
        return total

    def describe(self):
        """Return a string representation of this configuration."""

        result = ""
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
            neg.append(str(abs(self.modifier)))

        result = ' + '.join(pos)
        if neg:
            negterms = ' - '.join(neg)
            result = ' - '.join([result, negterms])

        if self.multiplier > 1:
            result = ' * '.join([result, str(self.multiplier)])
            
        return result


class Die(gtk.Button):
    """A button representing a particular die or mod."""

    def __init__(self, icon):
        gtk.Button.__init__(self)

        self.set_relief(gtk.RELIEF_NONE)
        self.set_border_width(0)
        self.set_focus_on_click(False)

        self.image = gtk.Image()
        self.image.set_from_file(''.join([_share_dir, icon, ".png"]))
        self.add(self.image)


class Counter(gtk.SpinButton):
    """A spinner widget which contains the quantity of each die to roll."""

    def __init__(self, value=0):
        gtk.SpinButton.__init__(self, gtk.Adjustment(value, -32767, 32767, 
                                                     1, 5))

        self.set_numeric(True)
        self.modify_font(pango.FontDescription("sans,monospace normal 9"))
        self.set_width_chars(2)

    def value(self):
        return self.get_value_as_int()

    def set_value(self, value):
        value = _validate_int(value)
        return gtk.SpinButton.set_value(self, value)


class CounterControl(gtk.HBox):
    """A control consisting of a button and a spinner.  
    
    The button can be used to modify the spinner's value.
    
    """
    def __init__(self, button, counter):
        gtk.HBox.__init__(self, False, 1)

        self.pack_start(button, False, False)
        self.pack_end(counter, False, False)


class DieBox(gtk.VBox):
    """The box containing the die buttons."""

    def __init__(self, spacing=2):
        gtk.VBox.__init__(self, True, spacing)
        self.counters = {}
        self.buttons = {}

        self.add_die(2, "d2")
        self.add_die(4, "d4")
        self.add_die(6, "d6")
        self.add_die(8, "d8")
        self.add_die(10, "d10")
        self.add_die(12, "d12")
        self.add_die(20, "d20")
        self.add_die(100, "d100")

        self.dx_size = gtk.SpinButton(gtk.Adjustment(3, 3, 16383, 1, 5))
        self.dx_size.set_width_chars(2)
        self.dx_count = Counter()
        self.add_control(self.dx_size, self.dx_count)

        self.add_die("mul", "mul", 1);
        self.add_die("mod", "mod");

    def add_control(self, button, counter):
        control = CounterControl(button, counter)
        self.pack_start(control, False, False)

    def add_die(self, name, icon, count=0):
        button = Die(icon)
        button.connect("button_press_event", self.press, name)
        self.buttons[name] = button

        counter = Counter(count)
        counter.connect("value-changed", self.update, name)
        self.counters[name] = counter
        self.add_control(button, counter)

    def connect_updates(self, callback):
        """
        Connect all signals which update the configuration with the given
        handler.

        """ 
        for sides in self.counters:
            self.counters[sides].connect("value-changed", callback)
        self.dx_size.connect("value-changed", callback)
        self.dx_count.connect("value-changed", callback)

    def get_counter(self, sides):
        """Return a reference to the counter for a particular die."""
        return self.counters[sides]

    def get_dx_size(self):
        return self.dx_size.get_value_as_int()

    def get_dx_count(self):
        return self.dx_count.get_value_as_int()

    def get_counters(self):
        """Return a dictionary of all counter values.

        The dictionary form is "name": "count"

        """
        result = {}
        for name, counter in self.counters.iteritems():
            result[name] = counter.value()
        return result

    def set_counter(self, sides, count):
        if sides not in self.counters:
            raise ValueError("No counter for %d-sided dice present." % sides)
        self.counters[sides].set_value(count)

    def set_dx_size(self, sides):
        self.dx_size.set_value(_validate_uint(sides))

    def set_dx_count(self, count):
        self.dx_count.set_value(count)

    def press(self, widget, event, data=None):
        """One of the die or mod buttons has received a mouse click.
        
        The data argument should contain the identifier of the button.  This is
        the number of sides for a die button, or the name for a mod button.

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

    def update(self, widget, data=None):
        """One of the counters has had its value updated.

        The data argument should contain the counter's identifier, as for the
        press() method.

        If the counter is considered "live" (it has been set to a non-default
        value), then we update the style of the corresponding button.

        """
        button = self.buttons[data]

        if data == 'mul':
            default = 1
        else:
            default = 0

        if widget.get_value() == default:
            button.set_relief(gtk.RELIEF_NONE)
        else:
            button.set_relief(gtk.RELIEF_NORMAL)

        return True


class THIRDLog(gtk.ListStore):
    """The columns of the log are, in order:

    The sequence number of the row
    The description of the item (roll or modifier)
    The numeric result of the item.

    """
    def append_result(self, count, label, amount):
        self.append([count, label, amount])

    def append_total(self, total):
        self.append([None, "=", total])


class THIRDLogView(gtk.TreeView):
    def __init__(self, model):
        gtk.TreeView.__init__(self, model)

        self.set_headers_visible(False)
        cells = []
        for i in range(3): cells.append(gtk.CellRendererText())

        cells[0].set_property("foreground", "#999999")
        cells[0].set_property("foreground-set", True)
        cells[0].set_property("xalign", 1.0)
        cells[2].set_property("xalign", 1.0)
        cells[2].set_property("weight", 700)
        cells[2].set_property("weight-set", True)

        for i in range(3):
            col = gtk.TreeViewColumn("", cells[i], text=i)
            self.append_column(col)


class THIRDPresets(gtk.ListStore):
    def add_preset(self, config):
        self.append([config.get_name(), config.describe()])


class THIRDPresetView(gtk.TreeView):
    def __init__(self, model):
        gtk.TreeView.__init__(self, model)

        self.set_headers_visible(False)

        cell = gtk.CellRendererText()
        col = gtk.TreeViewColumn("", cell, text=0)
        self.append_column(col)

        cell = gtk.CellRendererText()
        col = gtk.TreeViewColumn("", cell, text=1)
        self.append_column(col)

    def has_selection(self):
        (path, column) = self.get_cursor()
        return (path != None)


class THIRDNewPreset(gtk.Dialog):
    def __init__(self, parent, config):
        gtk.Dialog.__init__(self, "Add a preset", parent,
                            gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT)

        self.vbox.set_spacing(5)
        self.vbox.pack_start(gtk.Label("Enter a name for the following "
                                       "configuration:"))
        conflabel = gtk.Label()
        conflabel.set_markup("<b>" + config.describe() + "</b>")
        self.vbox.pack_start(conflabel)

        self.input = gtk.Entry()
        self.input.set_activates_default(True)
        self.vbox.pack_start(self.input)
        self.input.grab_focus()

        self.add_button(gtk.STOCK_OK, gtk.RESPONSE_OK)
        self.add_button(gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL)
        self.set_default_response(gtk.RESPONSE_OK)

        self.show_all()

    def get_text(self):
        return self.input.get_text()


class THIRDEditPreset(gtk.Dialog):
    def __init__(self, parent, config):
        gtk.Dialog.__init__(self, "Rename a preset", parent,
                            gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT)

        self.vbox.set_spacing(5)
        self.vbox.pack_start(gtk.Label("Enter a new name for the following "
                                       "configuration:"))
        conflabel = gtk.Label()
        conflabel.set_markup("<b>" + config.describe() + "</b>")
        self.vbox.pack_start(conflabel)

        self.input = gtk.Entry()
        self.input.set_text(config.get_name())
        self.input.set_activates_default(True)
        self.vbox.pack_start(self.input)
        self.input.grab_focus()

        self.add_button(gtk.STOCK_OK, gtk.RESPONSE_OK)
        self.add_button(gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL)
        self.set_default_response(gtk.RESPONSE_OK)

        self.show_all()

    def get_text(self):
        return self.input.get_text()


class THIRD(gtk.Window):
    """The main THIRD application window."""

    def __init__(self):
        gtk.Window.__init__(self, gtk.WINDOW_TOPLEVEL)
        self.connect("delete_event", self.delete_event)

        self.set_border_width(5)
        self.set_title("third")
        self.set_icon_from_file(_share_dir + "app.png")
        
        self.bold = pango.FontDescription("sans bold 10")

        self.dbox = DieBox()
        self.dbox.connect_updates(self.update_config)

        self.rollbutton = gtk.Button(stock="gtk-ok")
        self.rollbutton.connect("clicked", self.roll)
        self.resetbutton = gtk.Button(stock="gtk-cancel")
        self.resetbutton.connect("clicked", self.reset)

        bb = gtk.VButtonBox()
        bb.set_layout(gtk.BUTTONBOX_EDGE)
        bb.add(self.rollbutton)
        bb.add(self.resetbutton)

        self.log = THIRDLog(str, str, int)
        self.logview = THIRDLogView(self.log)

        self.logscroll = gtk.ScrolledWindow()
        self.logscroll.set_policy(gtk.POLICY_AUTOMATIC,
                                  gtk.POLICY_ALWAYS)
        self.logscroll.set_shadow_type(gtk.SHADOW_IN)
        self.logscroll.add(self.logview)

        self.label = gtk.Label()
        self.label.modify_font(self.bold)
        self.label.set_alignment(0.0, 0.5)

        self.range = gtk.Label()
        self.range.set_alignment(1.0, 0.0)

        labelbox = gtk.HBox()
        labelbox.pack_start(gtk.Label("Range: "), False, False)
        labelbox.pack_end(self.range, True, True)

        self.slider = gtk.HScale()
        self.slider.set_increments(1, 1)
        self.slider.set_draw_value(False)

        self.resultbox = gtk.VBox(False, 5)
        self.resultbox.pack_start(self.label, False, False)
        self.resultbox.pack_start(labelbox, False, False)
        self.resultbox.pack_start(self.slider, False, False)
        self.resultbox.pack_start(self.logscroll, True, True)
        self.resultbox.pack_start(bb, False, False)

        self.mainbox = gtk.HBox(False, 10)
        self.mainbox.pack_start(gtk.VSeparator(), False, False)
        self.mainbox.pack_start(self.dbox, False, False)
        self.mainbox.pack_start(gtk.VSeparator(), False, False)
        self.mainbox.pack_end(self.resultbox, True, True)

        panes = gtk.HPaned()
        self.add(panes)

        self.presets = []
        self.presetmodel = THIRDPresets(str, str)
        self.presetview = THIRDPresetView(self.presetmodel)
        self.presetview.connect("cursor-changed", self.select_preset)
        self.presetview.connect("row-activated", self.activate_preset)
        self.presetscroll = gtk.ScrolledWindow()
        self.presetscroll.set_policy(gtk.POLICY_NEVER,
                                     gtk.POLICY_AUTOMATIC)
        self.presetscroll.set_shadow_type(gtk.SHADOW_IN)
        self.presetscroll.add(self.presetview)
        self.load_presets()

        bb = gtk.VButtonBox()
        bb.set_layout(gtk.BUTTONBOX_EDGE)

        self.addbutton = gtk.Button(stock="gtk-add")
        self.addbutton.connect("clicked", self.add_preset)
        bb.add(self.addbutton)

        self.editbutton = gtk.Button(stock="gtk-edit")
        self.editbutton.set_sensitive(False)
        self.editbutton.connect("clicked", self.edit_preset)
        bb.add(self.editbutton)

        self.savebutton = gtk.Button(stock="gtk-save")
        self.savebutton.set_sensitive(False)
        self.savebutton.connect("clicked", self.save_preset)
        bb.add(self.savebutton)

        self.removebutton = gtk.Button(stock="gtk-remove")
        self.removebutton.set_sensitive(False)
        self.removebutton.connect("clicked", self.remove_preset)
        bb.add(self.removebutton)

        box = gtk.VBox(False, 5)
        box.pack_start(self.presetscroll, True, True)
        box.pack_end(bb, False, False)

        panes.add1(box)
        panes.add2(self.mainbox)

        self.update_config()

        self.show_all()

    def get_config(self):
        """Return a Config object based on the current widget settings."""

        return Config("", self.dbox.get_counters(), 
                      self.dbox.get_dx_size(), self.dbox.get_dx_count())

    def set_config(self, config):
        """Populate the widgets based on values in a Config object."""

        for die in _dice_set:
            self.dbox.set_counter(die, config.get_die(die))

        self.dbox.set_dx_size(config.get_dx_size())
        self.dbox.set_dx_count(config.get_dx_count())

        self.dbox.set_counter('mul', config.get_multiplier())
        self.dbox.set_counter('mod', config.get_modifier())

    def set_label(self, config):
        """Set the label to show the description of a Config."""

        self.label.set_text(config.describe())

    def set_range(self, config):
        """Show the min and max values of a Config."""
        self.range.set_text("%d - %d" % (config.min(), config.max()))

    def set_slider(self, config):
        """Set up the slider to the config's range."""
        min = config.min()
        max = config.max()

        if min < max:
            self.slider.set_range(min, max)
            self.slider.set_value(min)

    def update_config(self, widget=None, data=None):
        """The active configuration has changed, so update the UI accordingly.

        """
        config = self.get_config()
        self.set_label(config)
        self.set_range(config)
        self.set_slider(config)

        self.addbutton.set_sensitive(config.has_dice())
        self.savebutton.set_sensitive(config.has_dice() and
                                      self.presetview.has_selection())

    def roll(self, widget, data=None):
        """Roll the current widget configuration."""

        config = self.get_config()
        self.log.clear()
        result = config.roll(self.log)
        self.slider.set_value(result)
        return result

    def reset(self, widget, data=None):
        """Set all widgets back to their default state."""

        zero = Config("Zero", {}, 0, 0)
        self.set_config(zero)

    def load_presets(self):
        if not os.path.exists(_presets_file):
            return

        f = open(_presets_file, 'r')
        self.presets = pickle.load(f)
        f.close()

        for config in self.presets:
            self.presetmodel.add_preset(config)

    def save_presets(self):
        if not os.path.exists(_app_dir):
            os.mkdir(_app_dir)

        f = open(_presets_file, 'w')
        pickle.dump(self.presets, f)
        f.close()

    def add_preset(self, widget, data=None):
        config = self.get_config()
        dialog = THIRDNewPreset(self, config)
        response = dialog.run()
        name = dialog.get_text().strip()
        dialog.destroy()

        if response == gtk.RESPONSE_OK and name != '':
            config.set_name(name)
            self.presets.append(config)
            self.presetmodel.add_preset(config)
            self.save_presets()

    def edit_preset(self, widget, data=None):
        view = self.presetview
        (path, column) = view.get_cursor()
        if path == None:
            return

        index = path[0]
        config = self.presets[index]
        dialog = THIRDEditPreset(self, config)
        response = dialog.run()
        name = dialog.get_text().strip()
        dialog.destroy()

        if response == gtk.RESPONSE_OK and name != config.get_name():
            config.set_name(name)
            store = view.get_model()
            iter = store.get_iter(path)
            store.set_value(iter, 0, name)
            self.save_presets()

    def save_preset(self, widget, data=None):
        view = self.presetview
        (path, column) = view.get_cursor()
        if path == None:
            return

        index = path[0]
        config = self.get_config()
        preset = self.presets[index]

        config.set_name(preset.get_name())
        self.presets[index] = config
        store = view.get_model()
        iter = store.get_iter(path)
        store.set_value(iter, 1, config.describe())
        self.save_presets()

    def remove_preset(self, widget, data=None):
        view = self.presetview
        (path, column) = view.get_cursor()
        if path == None:
            return

        store = view.get_model()
        iter = store.get_iter(path)
        iter = view.get_model().get_iter(path)
        store.remove(iter)

        index = path[0]
        self.presets.pop(index)
        self.save_presets()

    def select_preset(self, widget, data=None):
        (path, column) = widget.get_cursor()
        selected = (path != None)
        if selected:
            index = path[0]
            self.set_config(self.presets[index])
            self.update_config()

        self.editbutton.set_sensitive(selected)
        self.removebutton.set_sensitive(selected)

        config = self.get_config()
        self.savebutton.set_sensitive(selected and config.has_dice())

    def activate_preset(self, widget, path, column, data=None):
        self.roll(widget)

    def delete_event(self, widget, event, data=None):
        gtk.main_quit()
        return False

    def main(self):
        gtk.main()


if __name__ == '__main__':
    third = THIRD()
    third.main()

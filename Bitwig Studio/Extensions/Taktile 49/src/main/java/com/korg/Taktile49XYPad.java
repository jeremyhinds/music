package com.korg;

import com.bitwig.extension.controller.api.ControllerHost;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.callback.StringValueChangedCallback;

import java.util.Arrays;

class Taktile49XYPad implements ControlChangeHandler {
    private CursorTrack cursorTrack;
    private boolean isXYInstrument = false;
    private Device device;
    private CursorRemoteControlsPage controlsPage;

    Taktile49XYPad(CursorTrack cursorTrack) {
        this.cursorTrack = cursorTrack;
        device = cursorTrack.createCursorDevice(
            "Primary", "Primary", 8, CursorDeviceFollowMode.FIRST_INSTRUMENT);
        device.name().addValueObserver(
            (StringValueChangedCallback) name -> deviceNameCallback(name));
        controlsPage = device.createCursorRemoteControlsPage("XY", 8, "");
        controlsPage.pageNames().markInterested();
        controlsPage.getParameter(0).name().markInterested();
        controlsPage.getParameter(1).name().markInterested();
    }

    private void deviceNameCallback(String name) {
        if (name.equals("XY Instrument")) {
            isXYInstrument = true;
        } else {
            isXYInstrument = false;
        }
    }

    public boolean handlesMessage(ShortMidiMessage msg) {
        // Let the default handler take care of it if it's not an XY instrument.
        if (!isXYInstrument) return false;

        final int data1 = msg.getData1();
        return data1 == Taktile49CC.XY_TOUCH.cc() ||
               data1 == Taktile49CC.XY_X.cc() ||
               data1 == Taktile49CC.XY_Y.cc();
    }

    public void handleMessage(ShortMidiMessage msg) {
        final int cc = msg.getData1();
        if (cc == Taktile49CC.XY_TOUCH.cc()) return;

        controlsPage.selectFirst();
        RemoteControl param = null;
        if (cc == Taktile49CC.XY_X.cc()) {
            param = controlsPage.getParameter(0);
            if (!param.name().get().equals("X")) return;
        } else if (cc == Taktile49CC.XY_Y.cc()) {
            param = controlsPage.getParameter(1);
            if (!param.name().get().equals("Y")) return;
        }
        if (param != null)
            param.set(msg.getData2(), 128);
    }
}

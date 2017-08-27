package com.korg;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.UserControlBank;

class Taktile49UserControl implements ControlChangeHandler {
    private UserControlBank userControls;

    Taktile49UserControl(UserControlBank userControls) {
        this.userControls = userControls;
    }

    public boolean handlesMessage(ShortMidiMessage msg) {
        final int data1 = msg.getData1();
        return data1 >= Taktile49CC.LOWEST_CC && data1 <= Taktile49CC.HIGHEST_CC;
    }

    public void handleMessage(ShortMidiMessage msg) {
        final int index = msg.getData1() - Taktile49CC.LOWEST_CC;
        Parameter userControl = userControls.getControl(index);
        if (userControl != null) {
            userControl.set(msg.getData2(), 128);
            return;
        }
    }
}

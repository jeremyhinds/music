package com.korg;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.Transport;

class Taktile49Transport implements ControlChangeHandler {
    Taktile49Transport(Transport transport) {
        this.transport = transport;
    }

    private final Transport transport;

    public boolean handlesMessage(ShortMidiMessage msg) {
        final int cc = msg.getData1();
        return cc == Taktile49CC.PLAY.cc() ||
               cc == Taktile49CC.STOP.cc() ||
               cc == Taktile49CC.REC.cc() ||
               cc == Taktile49CC.REW.cc() ||
               cc == Taktile49CC.FF.cc() ||
               cc == Taktile49CC.LOOP.cc();
    }

    public void handleMessage(ShortMidiMessage msg) {
        // Don't do anything when the button is released.
        if (msg.getData2() == 0) return;
       
        if (msg.getData1() == Taktile49CC.PLAY.cc())
            transport.isPlaying().toggle();
        else if (msg.getData1() == Taktile49CC.STOP.cc())
            transport.stop();
        else if (msg.getData1() == Taktile49CC.REC.cc())
            transport.isArrangerRecordEnabled().toggle();
        else if (msg.getData1() == Taktile49CC.REW.cc())
            transport.rewind();
        else if (msg.getData1() == Taktile49CC.FF.cc())
            transport.fastForward();
        else if (msg.getData1() == Taktile49CC.LOOP.cc())
            transport.isArrangerLoopEnabled().toggle();
   }
}

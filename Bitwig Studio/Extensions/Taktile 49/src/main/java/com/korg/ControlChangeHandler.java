package com.korg;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;

interface ControlChangeHandler {
    public boolean handlesMessage(ShortMidiMessage msg);
    public void handleMessage(ShortMidiMessage msg);
}

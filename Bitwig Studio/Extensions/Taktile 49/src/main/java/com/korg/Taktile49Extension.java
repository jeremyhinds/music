package com.korg;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.NoteInput;
import com.bitwig.extension.controller.ControllerExtension;

import java.util.ArrayList;
import java.util.List;

public class Taktile49Extension extends ControllerExtension {

    private List<ControlChangeHandler> ccHandlers = new ArrayList<ControlChangeHandler>();
    private NoteInput noteInput0;
    private NoteInput ccPassthrough0;

    protected Taktile49Extension(
    final Taktile49ExtensionDefinition definition, final ControllerHost host)
    {
       super(definition, host);
    }

    @Override
    public void init() {
        final ControllerHost host = getHost();      
       
        noteInput0 = host.getMidiInPort(0).createNoteInput(
            "taktile-49-notes", "80????", "90????");
        noteInput0.setShouldConsumeEvents(true);

        // Pass pitch and mod wheels through. Without this, they don't work in
        // VSTs. But for in-BWS mappings, onMidi0 needs to see the messages.
        ccPassthrough0 = host.getMidiInPort(0).createNoteInput(
            "taktile-49-cc-passthrough", "??00??", "??01??");
        ccPassthrough0.setShouldConsumeEvents(false);
       
        ccHandlers.add(new Taktile49Transport(host.createTransport()));
        ccHandlers.add(new Taktile49Mixer(host.createMainTrackBank(8, 0, 0)));
        ccHandlers.add(new Taktile49UserControl(
                host.createUserControls(Taktile49CC.CONTROL_COUNT)));
       
        host.getMidiInPort(0).setMidiCallback(
            (ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
        /*
        host.getMidiInPort(0).setSysexCallback((String data) -> onSysex0(data));
        host.getMidiInPort(1).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi1(msg));
        host.getMidiInPort(1).setSysexCallback((String data) -> onSysex1(data));
        host.getMidiInPort(2).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi2(msg));
        host.getMidiInPort(2).setSysexCallback((String data) -> onSysex2(data));
        */
       
        // TODO: Perform your driver initialization here.
        // For now just show a popup notification for verification that it is running.
        host.showPopupNotification("Taktile 49 Initialized");
    }

    @Override
    public void exit() {
       // TODO: Perform any cleanup once the driver exits
       // For now just show a popup notification for verification that it is no longer running.
       getHost().showPopupNotification("Taktile 49 Exited");
    }

    @Override
    public void flush() {
       // TODO Send any updates you need here.
    }

    /** Called when we receive short MIDI message on port 0. */
    private void onMidi0(ShortMidiMessage msg) {
        final int data1 = msg.getData1();
        final int data2 = msg.getData2();
        final ControllerHost host = getHost();      
        host.println(
            String.format("cc: %d %d %d",
                          msg.getStatusByte(), data1, data2)
        );

        for (ControlChangeHandler handler : ccHandlers) {
            if (handler.handlesMessage(msg)) {
                handler.handleMessage(msg);
                return;
            }
        }
    }
}

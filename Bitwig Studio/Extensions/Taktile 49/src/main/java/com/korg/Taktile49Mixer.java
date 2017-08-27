package com.korg;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.lang.Math;
import java.util.Arrays;

class Taktile49Mixer implements ControlChangeHandler {
    static final int TRACKS_PER_BANK = 8;

    /** The maximim percentage (between 0 and 1) of difference between a
    * previous and new value before beginning to modify the value.
    *
    * This is to prevent volume and pan from jumping wildly when the controller
    * and the track are at very different values.
    */
    private static final double LATCH_THRESHOLD = 0.05;

    private TrackBank trackBank;

    Taktile49Mixer(TrackBank trackBank) {
        this.trackBank = trackBank;
        makeTracksObservable();
    }

    private void makeTracksObservable() {
        for (int i = 0; i < TRACKS_PER_BANK; i++) {
            Track track = trackBank.getChannel(i);
            track.getVolume().markInterested();
            track.getPan().markInterested();
        }
    }

    // This must be defined in ascending-sort order.
    private static final int[] handledCCs = {
        Taktile49CC.F1_KNOB.cc(),
        Taktile49CC.F2_KNOB.cc(),
        Taktile49CC.F3_KNOB.cc(),
        Taktile49CC.F4_KNOB.cc(),
        Taktile49CC.F5_KNOB.cc(),
        Taktile49CC.F6_KNOB.cc(),
        Taktile49CC.F7_KNOB.cc(),
        Taktile49CC.F8_KNOB.cc(),

        Taktile49CC.F1_SLIDER.cc(),
        Taktile49CC.F2_SLIDER.cc(),
        Taktile49CC.F3_SLIDER.cc(),
        Taktile49CC.F4_SLIDER.cc(),
        Taktile49CC.F5_SLIDER.cc(),
        Taktile49CC.F6_SLIDER.cc(),
        Taktile49CC.F7_SLIDER.cc(),
        Taktile49CC.F8_SLIDER.cc(),

        Taktile49CC.F1_SOLO.cc(),
        Taktile49CC.F2_SOLO.cc(),
        Taktile49CC.F3_SOLO.cc(),
        Taktile49CC.F4_SOLO.cc(),
        Taktile49CC.F5_SOLO.cc(),
        Taktile49CC.F6_SOLO.cc(),
        Taktile49CC.F7_SOLO.cc(),
        Taktile49CC.F8_SOLO.cc(),

        Taktile49CC.F1_MUTE.cc(),
        Taktile49CC.F2_MUTE.cc(),
        Taktile49CC.F3_MUTE.cc(),
        Taktile49CC.F4_MUTE.cc(),
        Taktile49CC.F5_MUTE.cc(),
        Taktile49CC.F6_MUTE.cc(),
        Taktile49CC.F7_MUTE.cc(),
        Taktile49CC.F8_MUTE.cc(),

        Taktile49CC.TRACK_NEXT.cc(),
        Taktile49CC.TRACK_PREV.cc(),

        Taktile49CC.F1_REC.cc(),
        Taktile49CC.F2_REC.cc(),
        Taktile49CC.F3_REC.cc(),
        Taktile49CC.F4_REC.cc(),
        Taktile49CC.F5_REC.cc(),
        Taktile49CC.F6_REC.cc(),
        Taktile49CC.F7_REC.cc(),
        Taktile49CC.F8_REC.cc(),
    };

    public boolean handlesMessage(ShortMidiMessage msg) {
        return Arrays.binarySearch(handledCCs, msg.getData1()) > -1;
    }

    public void handleMessage(ShortMidiMessage msg) {
        final int cc = msg.getData1();
        final int value = msg.getData2();

        // The volume and pan controls are the only ones that might pay
        // attention to a value of 0.
        if (inRange(cc, Taktile49CC.F1_SLIDER.cc(), Taktile49CC.F8_SLIDER.cc())) {
            final int i = cc - Taktile49CC.F1_SLIDER.cc();
            final Parameter volume = trackBank.getChannel(i).getVolume();
            // Latch when the new value is close to the old one.
            // TODO: This logic is aweful and only works for slow changes.
            if (Math.abs(volume.get() - (value / 128.0)) < LATCH_THRESHOLD)
                volume.set(value, 128);
        }
        else if (inRange(cc, Taktile49CC.F1_KNOB.cc(), Taktile49CC.F8_KNOB.cc())) {
            final int i = cc - Taktile49CC.F1_KNOB.cc();
            final Parameter pan = trackBank.getChannel(i).getPan();
            // Latch when the new value is close to the old one.
            // TODO: This logic is aweful and only works for slow changes.
            if (Math.abs(pan.get() - (value / 128.0)) < LATCH_THRESHOLD)
                pan.set(value, 128);
        }

        // Ignore button release messages for everything else.
        if (value == 0)
            return;

        if (cc == Taktile49CC.TRACK_NEXT.cc()) {
            trackBank.scrollChannelsPageDown();
            makeTracksObservable();
        }
        else if (cc == Taktile49CC.TRACK_PREV.cc()) {
            trackBank.scrollChannelsPageUp();
            makeTracksObservable();
        }
        else if (inRange(cc, Taktile49CC.F1_SOLO.cc(), Taktile49CC.F8_SOLO.cc())) {
            final int i = cc - Taktile49CC.F1_SOLO.cc();
            trackBank.getChannel(i).getSolo().toggle();
        }
        else if (inRange(cc, Taktile49CC.F1_MUTE.cc(), Taktile49CC.F8_MUTE.cc())) {
            final int i = cc - Taktile49CC.F1_MUTE.cc();
            trackBank.getChannel(i).getMute().toggle();
        }
        else if (inRange(cc, Taktile49CC.F1_REC.cc(), Taktile49CC.F8_REC.cc())) {
            final int i = cc - Taktile49CC.F1_REC.cc();
            trackBank.getChannel(i).getArm().toggle();
        }
    }

    private boolean inRange(int n, int low, int high) {
        return n >= low && n <= high;
    }
}

loadAPI(1);

host.defineController("Korg", "Taktile 49", "1.0", "0a2ca477-b02e-4522-ab27-40ba89d16462");
host.addDeviceNameBasedDiscoveryPair(["taktile-49 MIDI 1", "taktile-49 MIDI 3"], ["taktile-49 MIDI 2"]);
host.defineMidiPorts(1, 1);

// Maybe specific to Scene 9 mode
var CC =
{
	REW : 43,
	FF : 44,
	STOP : 42,
	PLAY : 41,
	REC : 45,
	LOOP : 46,

	// Hold down "MARKER" and press transport buttons.
	MARK_LEFT: 61,
	MARK_RIGHT: 62,
	MARK_SET: 60,

	F1_SOLO : 32,
	F2_SOLO : 33,
	F3_SOLO : 34,
	F4_SOLO : 35,
	F5_SOLO : 36,
	F6_SOLO : 37,
	F7_SOLO : 38,
	F8_SOLO : 39,

	F1_MUTE : 48,
	F2_MUTE : 49,
	F3_MUTE : 50,
	F4_MUTE : 51,
	F5_MUTE : 52,
	F6_MUTE : 53,
	F7_MUTE : 54,
	F8_MUTE : 55,

	F1_REC : 83,
	F2_REC : 84,
	F3_REC : 85,
	F4_REC : 86,
	F5_REC : 87,
	F6_REC : 88,
	F7_REC : 89,
	F8_REC : 90,

	S1 : 24,
	S2 : 25,
	S3 : 26,
	S4 : 27,
	S5 : 28,
	S6 : 29,
	S7 : 30,
	S8 : 31,

	K1 : 16,
	K2 : 17,
	K3 : 18,
	K4 : 19,
	K5 : 20,
	K6 : 21,
	K7 : 22,
	K8 : 23,

	TRACK_NEXT : 59,
	TRACK_PREV : 58,

	MOD : 1,
	XY_X : 12,
	XY_Y : 13,

	TOUCH_SCALE_Y: 3,
	PEDAL: 11,
	SWITCH: 64,
}

var transport, trackbank, primaryInstrument, userControls;

var LOWEST_CC = 1;
var HIGHEST_CC = 119;

var isShift = false;
var isPlay = false;

// Don't know if this is needed, but these are the modes
// the Assign button cycles through. It determines which
// CCs are emitted by the F* buttons.
/*
var FMode = Object.freeze({
	SOLO: 0,
	MUTE: 1,
	REC:  2,
})
*/

function inRange(v, l, h) {
	return v >= l && v <= h;
}

var isXY = false;
function isXYParam(name) {
	println(name)
	if (name == "Mix X" || name == "Mix Y") {
		isXY = true;
	} else {
		isXY = false;
	}
}

function init()
{
	host.getMidiInPort(0).setMidiCallback(onMidi);
	host.getMidiInPort(0).setSysexCallback(onSysex);
	noteInput = host.getMidiInPort(0).createNoteInput(
		"taktile-49-notes",
		"80????",     // key note on
                "90????"      // key note off
        );
	// Don't send these events to the midi callback.
	noteInput.setShouldConsumeEvents(true);

	transportInput = host.getMidiInPort(0).createNoteInput(
		"taktile-49-transport",
		"??2???");
	transportInput.setShouldConsumeEvents(false);

	// Pass pitch and mod wheels through. Without this, they don't
	// work in VSTs.
	taktile = host.getMidiInPort(0).createNoteInput(
		"taktile-49-ccs",
		"??00??",
		"??01??");
	taktile.setShouldConsumeEvents(false);

	// Transport Controls
	transport = host.createTransport();
	transport.addIsPlayingObserver(function(on)
		{
			isPlay = on;
		});

	// (tracks, sends, scenes)
	trackbank = host.createMainTrackBank(8, 0, 0);
	// trackbank.setChannelScrollStepSize(8);

	// Map the X/Y pad to the first two common parameters.
	cursorDevice = host.createCursorDeviceSection(8);
	cursorTrack = host.createCursorTrackSection(3, 0);
	primaryInstrument = cursorTrack.getPrimaryInstrument();
	primaryInstrument.getCommonParameter(0).addNameObserver(128, "", isXYParam);

	// Make the rest freely mappable
	userControls = host.createUserControlsSection(HIGHEST_CC - LOWEST_CC + 1 - 8);
}

function onMidi(status, data1, data2)
{
	// printMidi(status, data1, data2);

	// Note input should already be consumed, but just in case...
	if (!isChannelController(status)) { return; }

	/*
	// The X/Y pad emits CC 0x5c when touched, which breaks
	// midi learn for the actual x/y value CCs. But it might be
	// useful to leave it as user-assignable.
	if (data1 == 0x5c) { return; }
	*/

	// It is basically impossible to manually assign the XY pad to the
	// analogous Instrument/Effect, but the Mix X/Y values are the first
	// two items in the panel mappings by default. This may not make much
	// sense for other device types, but it's what we have to work with.
	if (data1 == CC.XY_X) {
		if (isXY) {
			primaryInstrument.getCommonParameter(0).set(data2, 128);
		} else {
			// TODO: For Polysynth, make this Filter Freq
		}
		return;
	}
	if (data1 == CC.XY_Y) {
		if (isXY) {
			primaryInstrument.getCommonParameter(1).set(data2, 128);
		} else {
			// TODO: For Polysynth, make this Filter Res
		}
		return;
	}

	// The semantics of Page{Up,Down} seems to be directional in the UI
	// rather than WRT the channel index.
	if (data1 == CC.TRACK_NEXT) {
		if (data2 > 0) {
			trackbank.scrollChannelsPageDown();
		}
		return;
	} else if (data1 == CC.TRACK_PREV) {
		if (data2 > 0) {
			trackbank.scrollChannelsPageUp();
		}
		return;
	}

	if (inRange(data1, CC.S1, CC.S8)) {
		var i = data1 - CC.S1;
		trackbank.getChannel(i).getVolume().set(data2, 128);
		return;
	}
	if (inRange(data1, CC.K1, CC.K8)) {
		var i = data1 - CC.K1;
		trackbank.getChannel(i).getPan().set(data2, 128);
		return;
	}
	if (inRange(data1, CC.F1_SOLO, CC.F8_SOLO)) {
		if (data2 > 0) {
			var i = data1 - CC.F1_SOLO;
			trackbank.getChannel(i).getSolo().toggle(false);
		}
		return;
	}

	if (inRange(data1, CC.F1_MUTE, CC.F8_MUTE)) {
		if (data2 > 0) {
			var i = data1 - CC.F1_MUTE;
			trackbank.getChannel(i).getMute().toggle();
		}
		return;
	}

	if (inRange(data1, CC.F1_REC, CC.F8_REC)) {
		if (data2 > 0) {
			var i = data1 - CC.F1_REC;
			trackbank.getChannel(i).getArm().toggle();
		}
		return;
	}

	// Use the switch pedal to toggle recording in the launcher view.
	if (data1 == CC.SWITCH) {
		// ClipLauncherSlots.record(i)
	}

	// Transport controls
	var cc = data1;
	var val = data2;
	var pressed = val > 0;	// Ignore key release
	if (pressed)
	{
		switch (cc)
		{
			case CC.PLAY:
				transport.play();
				break;
			case CC.STOP:
				transport.stop();
				break;
			case CC.REC:
				transport.record();
				break;
			case CC.REW:
				transport.rewind();
				break;
			case CC.FF:
				transport.fastForward();
				break;
			case CC.LOOP:
				transport.toggleLoop();
				break;
		}
	}

	if (data1 >= LOWEST_CC && data1 <= HIGHEST_CC)
	{
		var index = data1 - LOWEST_CC;
		control = userControls.getControl(index)
		if (control != null) {
			control.set(data2, 128);
			return;
		}
	}

}

function onSysex(data)
{
}

function flush() {
}

function exit()
{
}

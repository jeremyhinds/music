loadAPI(1);

host.defineController("Alesis", "QX25", "1.0", "E3776016-0AE0-4D01-B47C-BE2F6A8A67DE");
host.addDeviceNameBasedDiscoveryPair(["QX25 Midi 1"], ["QX25 Midi 1"]);
host.defineMidiPorts(1, 0);

var CC = 
{
	REW : 118,
	FF : 117,
	STOP : 114,
	PLAY : 115,
	REC : 116,
	LOOP : 119,
	K1 : 14,
	K2 : 15,
	K3 : 16,
	K4 : 17,
	K5 : 18,
	K6 : 19,
	K7 : 20,
	K8 : 21,
	S1 : 22,  // Slider is unassigned here.
}

var LOWEST_CC = 1;
var HIGHEST_CC = 119;

var DEVICE_START_CC = CC.K1;
var DEVICE_END_CC = CC.K8;

var isShift = false;
var isPlay = false;

function init()
{
	host.getMidiInPort(0).setMidiCallback(onMidi);
	qx25 = host.getMidiInPort(0).createNoteInput(
		"QX25",
		"80????",     // key note on
                "90????",     // key note off
                // "B0????",  // mod wheel, left out so it can be assignable.
                "E0????",     // pitch wheel
                // "D0????",  // aftertouch, left out so it can be assignable, but it's not working either way.
                "92????",     // pad note on
                "82????"      // pad note off
        );
	qx25.setShouldConsumeEvents(true);  // Don't send these events to the midi callback.

	// Transport Controls
	transport = host.createTransport();
	transport.addIsPlayingObserver(function(on)
		{
			isPlay = on;
		});

	// Knobs to device parameters

	cursorDevice = host.createCursorDeviceSection(8);
	cursorTrack = host.createCursorTrackSection(3, 0);
	primaryInstrument = cursorTrack.getPrimaryInstrument();

	for ( var i = 0; i < 8; i++)
	{
		var p = primaryInstrument.getMacro(i).getAmount();
		p.setIndication(true);
	}

	// Make the rest freely mappable
	userControls = host.createUserControlsSection(HIGHEST_CC - LOWEST_CC + 1 - 8);

	for ( var i = LOWEST_CC; i < HIGHEST_CC; i++)
	{
		if (!isInDeviceParametersRange(i))
		{
			var index = userIndexFromCC(i);
			userControls.getControl(index).setLabel("CC" + i);
		}
	}
}

function isInDeviceParametersRange(cc)
{
	return cc >= DEVICE_START_CC && cc <= DEVICE_END_CC;
}

function userIndexFromCC(cc)
{
	if (cc > DEVICE_END_CC)
	{
		return cc - LOWEST_CC - 8;
	}

	return cc - LOWEST_CC;
}

function onMidi(status, data1, data2)
{
	// printMidi(status, data1, data2);
	if (isChannelController(status))
	{
		if (isInDeviceParametersRange(data1))
		{
			var index = data1 - DEVICE_START_CC;
			primaryInstrument.getMacro(index).getAmount().set(data2, 128);
		}
		else if (data1 >= LOWEST_CC && data1 <= HIGHEST_CC)
		{
			var index = data1 - LOWEST_CC;
			control = userControls.getControl(index)
			if (control != null) {
				control.set(data2, 128);
			}
		}
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
}

function exit()
{
}

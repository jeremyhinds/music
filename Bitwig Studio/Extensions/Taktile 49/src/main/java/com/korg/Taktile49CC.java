package com.korg;

public enum Taktile49CC {

    REW(43),
    FF(44),
    STOP(42),
    PLAY(41),
    REC(45),
    LOOP(46),

    // Hold down "MARKER" and press transport buttons.
    MARK_LEFT(61),
    MARK_RIGHT(62),
    MARK_SET(60),

    F1_SOLO(32),
    F2_SOLO(33),
    F3_SOLO(34),
    F4_SOLO(35),
    F5_SOLO(36),
    F6_SOLO(37),
    F7_SOLO(38),
    F8_SOLO(39),

    F1_MUTE(48),
    F2_MUTE(49),
    F3_MUTE(50),
    F4_MUTE(51),
    F5_MUTE(52),
    F6_MUTE(53),
    F7_MUTE(54),
    F8_MUTE(55),

    F1_REC(83),
    F2_REC(84),
    F3_REC(85),
    F4_REC(86),
    F5_REC(87),
    F6_REC(88),
    F7_REC(89),
    F8_REC(90),

    F1_SLIDER(24),
    F2_SLIDER(25),
    F3_SLIDER(26),
    F4_SLIDER(27),
    F5_SLIDER(28),
    F6_SLIDER(29),
    F7_SLIDER(30),
    F8_SLIDER(31),

    F1_KNOB(16),
    F2_KNOB(17),
    F3_KNOB(18),
    F4_KNOB(19),
    F5_KNOB(20),
    F6_KNOB(21),
    F7_KNOB(22),
    F8_KNOB(23),

    TRACK_NEXT(59),
    TRACK_PREV(58),

    MOD(1),
    XY_X(12),
    XY_Y(13),

    TOUCH_SCALE_Y(3),
    PEDAL(11),
    SWITCH(64);

    public static final int LOWEST_CC = 1;
    public static final int HIGHEST_CC = 119;

    public static final int CONTROL_COUNT = HIGHEST_CC - LOWEST_CC + 1 - 8;
    private final int cc;

    public int cc() { return cc; }

    Taktile49CC(int cc) {
        this.cc = cc;
    }
}

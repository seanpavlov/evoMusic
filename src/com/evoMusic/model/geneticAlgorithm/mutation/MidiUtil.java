package com.evoMusic.model.geneticAlgorithm.mutation;

public class MidiUtil {
    public final static int NBR_OF_NOTES = 12;
    public final static int MIDI_NOTE_MIN = 0;
    public final static int MIDI_NOTE_MAX = 127;
    private String[] noteTranslation = { "C", "Cm", "D", "Dm", "E", "F", "Fm",
            "G", "Gm", "A", "Am", "B" };

    /**
     * Easy to use class for calculating different kind of thing that has to do
     * with the MIDI pitch. With this, there is no need to think of the
     * calculations, which will probably save some time. Every note is defined
     * by a MIDI number in the range of 0-127.
     */
    public MidiUtil() {
    }

    /**
     * Calculate what pitch the note has. Example: 24 is C1 so it returns 1, 36
     * is C2 so it returns 2 and so on.
     * 
     * @param midiNbr
     *            is the MIDI number in range of 0-127.
     * @return the pitch as an integer and therefore without the note name.
     */
    public int getNotePitch(int midiNbr) {
        return ((midiNbr / 12) - 1);
    }

    /**
     * Reads the note translation and give the note name back. Example: 24 is C1
     * so it returns C.
     * 
     * @param midiNbr
     *            is the MIDI number in range of 0-127.
     * @return the note name as a string.
     */
    public String getNoteName(int midiNbr) {
        return noteTranslation[(midiNbr % 12)];
    }

    /**
     * Check if the note can be increased by N number of half steps. This has to
     * be done since MIDI has an upper limit.
     * 
     * @param midiNbr
     *            is the MIDI number in range of 0-127.
     * @param halfSteps
     *            is number of half steps to be increased.
     * @return true if it can be raised and otherwise false.
     */
    public boolean canRaiseNote(int midiNbr, int halfSteps) {
        return !((midiNbr + halfSteps) > MIDI_NOTE_MAX);
    }

    /**
     * Check if the note can be lowered by N number of half steps. This has to
     * be done since MIDI has a lower limit.
     * 
     * @param midiNbr
     *            is the MIDI number in range of 0-127.
     * @param halfSteps
     *            is number of half steps to be lowered.
     * @return true if it can be lowered and otherwise false.
     */
    public boolean canLowerNote(int midiNbr, int halfSteps) {
        return !((midiNbr - halfSteps) < MIDI_NOTE_MIN);
    }

    /**
     * When iterating throughout the notes, some of them can be blank. These
     * notes are showing as -2147483648 and therefore need to be handled.
     * 
     * @param midiNbr
     *            is the MIDI number in range of 0-127.
     * @return true if it is blank and false otherwise.
     */
    public boolean isBlank(int midiNbr) {
        return (midiNbr > 127 || midiNbr < 0);
    }
}

package com.evoMusic.util;

/**
 * Describes different patterns of musical scales
 */
public enum ScalePattern {

    PENTATONIC("100101010010"), DIATONIC("101011010101");

    private int[] notePitches = new int[12];

    private ScalePattern(String pattern) {
        char[] charArr = pattern.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            notePitches[i] = charArr[i] == '1' ? 1 : 0;
        }
    }

    /**
     * 
     * @return an array consisting of 1s where a pitch is included and 0s where
     *         it's not. The array index represents the note interval
     */
    public int[] getPitches() {
        return notePitches;
    }
}

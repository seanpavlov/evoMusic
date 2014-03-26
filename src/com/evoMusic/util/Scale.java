package com.evoMusic.util;

public enum Scale {

    PENTA("100101010010"),
    DIATONIC("101011010101");
    
    int[] notePitches = new int[12];
    
    private Scale(String pattern) {
        char[] charArr = pattern.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            notePitches[i] = charArr[i] == '1' ? 1 : 0;
        }
    }
    
    public int[] getPítches() {
        return notePitches;
    }
}

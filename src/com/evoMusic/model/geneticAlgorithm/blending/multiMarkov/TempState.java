package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import jm.music.data.Note;

public class TempState<E> {

    
    public static final double TIME_PRECISION = 0.000001;

    private E value;
    private int trackIndex;

    public TempState(E value, int trackIndex) {
        this.trackIndex = trackIndex;
        this.value = value;
    }
    
    public int trackIndex() {
        return this.trackIndex;
    }
    
    public E getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TempState)) {
            return false;
        } else if (o == this) {
            return true;
        } else {
            TempState other = (TempState) o;
            return this.trackIndex == other.trackIndex
                    && this.value.equals(other.value);
        }
    }
}

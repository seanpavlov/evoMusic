package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jm.music.data.Note;

public class ChordState<E> {

    // indices of returned list in createStates method.
    private static final int INTERVALS = 0;
    private static final int RHYTHM_VALUES = 1;
    private static final int DURATIONS = 2;
    private static final int DYNAMICS = 3;

    private List<E> stateList;

    public ChordState(List<E> state) {
        this.stateList = state;
    }
    
    public List<E> getState() {
        return stateList;
    }

    public List<ChordState> createStates(List<Note> notes, int previousPitch) {

        // maps interval to its original note index.
        Map<Integer, Integer> intervalMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < notes.size(); i++) {
            intervalMap.put(i, notes.get(i).getPitch() - previousPitch);
        }
        Iterator<Integer> iter = intervalMap.keySet().iterator();
        List<Integer> intervalList = new ArrayList<Integer>();
        while (iter.hasNext()) {
            intervalList.add(iter.next());
        }
        Collections.sort(intervalList);
        // by now duplicate intervals are gone and list are sorted with highest
        // note first
        
        List<Double> rhythmValues = new ArrayList<Double>();
        List<Double> durations = new ArrayList<Double>();
        List<Integer> dynamics = new ArrayList<Integer>();
        Note rightNote;
        for (Integer interval : intervalList) {
            rightNote = notes.get(intervalMap.get(interval));
            rhythmValues.add(rightNote.getRhythmValue());
            durations.add(rightNote.getDuration());
            dynamics.add(rightNote.getDynamic());
        }

        List<ChordState> stateList = new ArrayList<ChordState>();
        stateList.add(new ChordState<>(intervalList));
        stateList.add(new ChordState<>(rhythmValues));
        stateList.add(new ChordState<>(durations));
        stateList.add(new ChordState<>(dynamics));
        
        return stateList;
    }
}

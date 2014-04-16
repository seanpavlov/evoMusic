package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.*;

import jm.music.data.Note;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.Sort;
import com.evoMusic.util.TrackTag;

public class ZipfsLawRater extends SubRater {

    /**
     * Constructor for ZipfsLawRater
     * @param weight
     */
    public ZipfsLawRater(double weight){
        super.setWeight(weight);
    }
    
    
    @Override
    public double rate(Song song) {
       // Track t = song.getTaggedTracks(TrackTag.MELODY).get(0);

        List<List<Note>> sortedNoteList = Sort.getSortedNoteList(song);
        Map<Integer, Integer> pitchOccurances = new HashMap<Integer, Integer>(); 
        
        for (List<Note> notes : sortedNoteList){
            for (Note n : notes){
                int pitchModOctave = n.getPitch() % 12;
                if (pitchOccurances.containsKey(pitchModOctave)){
                    pitchOccurances.put(pitchModOctave, pitchOccurances.get(pitchModOctave)+1);
                } else {
                    pitchOccurances.put(pitchModOctave, 1);
                }
            }
        }
        
        Collection<Integer> values = pitchOccurances.values();
        List<Integer> valuesAsList = new ArrayList<Integer>(values);
        Collections.sort(valuesAsList);
        System.out.println(valuesAsList);
        plot(valuesAsList.get(valuesAsList.size()-1), valuesAsList.size());
        return 0;
    }
    
    private void plot(int largest, int length){
        List<Integer> values = new ArrayList<Integer>();
        for (int x = length; x >= 1; x--){
            values.add(largest / x);
        }
        System.out.println(values);
    }

}

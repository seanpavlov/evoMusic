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
                if (pitchOccurances.containsKey(n.getPitch())){
                    pitchOccurances.put(n.getPitch(), pitchOccurances.get(n.getPitch())+1);
                } else {
                    pitchOccurances.put(n.getPitch(), 1);
                }
            }
        }
        Collection<Integer> values = pitchOccurances.values();
        List<Integer> valuesAsList = new ArrayList<Integer>(values);
        Collections.sort(valuesAsList);
        System.out.println(valuesAsList);
        
        return 0;
    }

}

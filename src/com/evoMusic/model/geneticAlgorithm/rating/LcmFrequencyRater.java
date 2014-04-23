package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.Sort;
import com.evoMusic.util.TrackTag;

public class LcmFrequencyRater extends SubRater{
    
    public LcmFrequencyRater(double weight){
        super.setWeight(weight);
    }

    @Override
    public double rate(Song song) {
        double partRating = 0;
        List<Track> tracks = new ArrayList<Track>();
        tracks.addAll(song.getTaggedTracks(TrackTag.CHORDS));
        tracks.addAll(song.getTaggedTracks(TrackTag.MELODY));
        tracks.addAll(song.getTaggedTracks(TrackTag.BASELINE));
        for(Track track : tracks){
           partRating += ratePart(track.getPart());
        }
        //System.out.println("part rating: " + partRating);
        //System.out.println("nbr of track: " + tracks.size());
        if(tracks.size() == 0)
            return 0;
        return partRating / tracks.size();
    }
    
    private double ratePart(Part part){
        List<List<Note>> sortedNotes = Sort.getSortedNoteList(part);
        double nbrOfNotes = 0;
        double disonance = 1.0;
        
        for(List<Note> notes : sortedNotes){
            List<Integer> frequencies= new ArrayList<Integer>();
            for(Note note : notes){
                if(note.getPitch() != Note.REST){
                    int frequency =  ( note.getPitch() % 12 ) + 1; //(Math.round(note.getFrequency() * 100.0));
                    frequencies.add(frequency);                   
                }
            }
            
            nbrOfNotes++;
            if (frequencies.size() > 1){
                int gcd = gcdMultiple(frequencies);
                List<Integer> ratios = new ArrayList<Integer>();
                for(Integer d : frequencies){
                    ratios.add(d/gcd);
                }
                int lcm = lcmMultiple(ratios);
                double log = Math.log(lcm)/ Math.log(2);
                disonance += log;
            } else {
                disonance += 0;
            }
        }
        if(nbrOfNotes == 0)
            return 0;

        double averageDis = disonance / nbrOfNotes;
        if (averageDis > 1.0){
            return 1 / averageDis;
        } else {
            return 1 - averageDis;
        }
    }
    
    private Integer gcdMultiple(List<Integer> values){
        
        if(values.size() > 0){
            int gcd = values.get(0);
            for(int i = 1; i < values.size(); i++){
                gcd = gcd(gcd, values.get(i));
            }
            return gcd;
        }
        return 0;
        
    }
    
    private Integer gcd(Integer first, Integer second){
        int temp;  
        if(first==second){
          return first;
        }
        if(first < second){ 
          temp = first;
          first = second;
          second = temp;
        }
        return gcd(first - second, second); 
      }
    
    
    private Integer lcmMultiple(List<Integer> values){
        
        if(values.size() > 0){
            int lcm = values.get(0);
            for(int i = 1; i < values.size(); i++){
                lcm = lcm(lcm, values.get(i));
            }
            return lcm;
        }
        return 0;
    }
        
    private Integer lcm(Integer first, Integer second){ 
        return(first * second / (gcd(first, second)));
    }
}

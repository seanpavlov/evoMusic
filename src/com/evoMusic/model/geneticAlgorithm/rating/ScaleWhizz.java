package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.constants.Instruments;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.util.Scale;
import com.evoMusic.util.TrackTag;

public class ScaleWhizz extends SubRater {
    private List<Part> targeted;

    //  0 = C
    //  1 = C#
    //  2 = D
    // 11 = B
    int[] notePitch;

    private int numberOfNotes;
    
    public ScaleWhizz(double weight) {
        setWeight(weight);
    }
    
    private void notePitchCheck(Part track) {
        for (Phrase phrase : track.getPhraseArray()) {
            countNotes(phrase);
        }
    }
    
    private void countNotes(Phrase phrase) {
        for(Note note : phrase.getNoteArray()) {
            if(!note.isRest()) {
                notePitch[note.getPitch()%12]++;
                numberOfNotes++;
            }
        }
    }

    @Override
    public double rate(Song song) {
        targeted = song.getTaggedTracks(TrackTag.MELODY);
        notePitch = new int[12];;
        numberOfNotes = 0;
        for (Part track : targeted) {
            if(track.getInstrument() != Instruments.DRUM) {
                notePitchCheck(track);
            }
        }
        double hits = maximumScaleHits(notePitch, Scale.DIATONIC);
        System.out.println("hits: "+hits );
        System.out.println("numberOfNotes: "+numberOfNotes);
        System.out.println("rating: " + (numberOfNotes == 0 ? 0 : hits / numberOfNotes + "\n---"));
        return numberOfNotes == 0 ? 0 : hits / numberOfNotes;
    }
    
    private int maximumScaleHits(int[] pitches, Scale scale) { 
        int[] scaleArr = scale.getPítches();
        int hits = 0;
        int currentMaxHits = 0;
        
        for (int i = 0; i < scaleArr.length; i++) {
            if( scaleArr[i] != 0 ) {
                hits = 0;
                
                System.out.print("current scale: \t");
                for (int x = 0; x < scaleArr.length; x++) {
                    System.out.print(scaleArr[(x+i)%scaleArr.length]);
                }
                System.out.println();
                System.out.print("pitches: \t");
                for (int x = 0; x < pitches.length; x++) {
                    System.out.print(pitches[x]);
                }
                
                for (int j = 0; j < pitches.length; j++) {
                    hits += pitches[j] * scaleArr[(j+i)%scaleArr.length];
                }
                System.out.println("  hits: " + hits);
                if (hits > currentMaxHits) {
                    currentMaxHits = hits;
                }
            }
        }
        
        return currentMaxHits;
    }
}
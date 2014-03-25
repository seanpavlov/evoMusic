package com.evoMusic;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.evoMusic.controller.InputController;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRater;
import com.evoMusic.util.TrackTag;

public class Main {
    
    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        //new InputController(args);
        
       Score rythmScore = new Score("Rhythm");
        Part inst = new Part("s", 0, 9);
        Phrase phr = new Phrase(0.0);
        int x ;
        double[] pattern = new double[5];
        double[] pattern0 = {1.0, 0.5, 0.5, 1.5, 0.5};
        double[] pattern1 = {0.5, 0.5, 1.5, 0.5, 1.0};
        double[] pattern2 = {2.0, 0.5, 0.5, 0.5, 0.5};
        double[] pattern3 = {1.5, 0.5, 1.0, 0.5, 0.5};
        
        for(short i=0;i<8;i++){                        
            // choose one of the patterns at random                        
            x = (int)(Math.random()*4);
                        System.out.println("x = " + x);
                
                        switch (x) {
                
                        case 0:
                                pattern = pattern0;
                                break;
                        case 1:
                                pattern = pattern1;
                                break;
                        case 2:
                                pattern = pattern2;
                                break;
                        case 3:
                                pattern = pattern3;
                                break;
                        default:
                                System.out.println("Random number out of range");
                        }
               for (short j=0; j<pattern.length; j++) {
                   Note note = new Note((int)(38), pattern[j]);
                   phr.addNote(note);
               }
               Note note = new Note((int)(49), 4.0);
               phr.addNote(note);
        
               
        }
        inst.addPhrase(phr);
        rythmScore.addPart(inst);
        Song song = new Song(rythmScore);
        song.addTagToTrack(inst, TrackTag.BEAT);
        BeatRater r = new BeatRater(1.0);
        double rating = r.rate(song);
        System.out.println("rating: " + rating);
    }
}

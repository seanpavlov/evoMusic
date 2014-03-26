package com.evoMusic;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Part;
import jm.util.View;

import com.evoMusic.controller.InputController;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater.Chord;
import com.evoMusic.util.Translator;

public class Main {
    
    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        //new InputController(args);
        
        Song song = Translator.INSTANCE.loadMidiToSong("midifiles/Elton_John-Candle_in_the_Wind.mid");
        ChordRepetitionRater rater = new ChordRepetitionRater();
        
        List<Chord> list = new ArrayList<Chord>();
        List<Chord> list1 = new ArrayList<Chord>();
        List<Chord> list2 = new ArrayList<Chord>();
        List<List<Chord>> chords;// = new ArrayList<List<Chord>>();
        List<Integer> p0 = new ArrayList<Integer>();
        List<Integer> p1 = new ArrayList<Integer>();
        List<Integer> p2 = new ArrayList<Integer>();
        List<Integer> p3 = new ArrayList<Integer>();
        List<Integer> p4 = new ArrayList<Integer>();
        List<Integer> p5 = new ArrayList<Integer>();
        List<Integer> p6 = new ArrayList<Integer>();
        List<Integer> p7 = new ArrayList<Integer>();
        List<Integer> p8 = new ArrayList<Integer>();
        p0.add(14);

        p1.add(34);
        p1.add(42);
        p1.add(8);
        p1.add(45);
       
        p2.add(15);
        
        p3.add(14);
        
        p4.add(34);
        p4.add(42);
        p4.add(8);
        p4.add(45);
        
        p5.add(15);
     
        p6.add(35);
        p6.add(22);
        p6.add(28);
        
        p7.add(51);
        p7.add(42);
        p7.add(28);
        p7.add(36);
        
        p8.add(11);
        p8.add(32);
        
        list.add(rater.new Chord(p0, 0.7666));
        list.add(rater.new Chord(p1, 0.7666));
        list.add(rater.new Chord(p2, 0.7666));
        
        list.add(rater.new Chord(p3, 0.7666));
        list.add(rater.new Chord(p4, 0.7666));
        list.add(rater.new Chord(p5, 0.7666));
        list.add(rater.new Chord(p6, 0.7666));
        
        list.add(rater.new Chord(p7, 0.7666));
        list.add(rater.new Chord(p8, 0.7666));
        
        //chords.add(list);
        //chords.add(list1);
        //chords.add(list2);

        //rater.sortByValues(chords);
        chords = rater.findChordPatterns(list, 1);
        
        for(List<Chord> cl : chords){
            System.out.println("next pattern: ");
            for(Chord c : cl){
                System.out.println(c);
            }
        }
        System.out.println("Length:" + chords.size());

        
        /*Part part = song.getScore().getPart(0);
        rater.ratePart(part);*/
    }
}

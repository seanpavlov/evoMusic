package com.evoMusic;

import java.util.*;

import com.evoMusic.controller.GAHandler;
import com.evoMusic.controller.InputController;
import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.Individual;

public class Main {

    public static void main(String[] args) {
        Song s = Translator.INSTANCE.loadMidiToSong("./midifiles/super_mario_bros_theme.mid");
        Song n = Translator.INSTANCE.loadMidiToSong("./midifiles/Sweden.mid");
        Song m = Translator.INSTANCE.loadMidiToSong("./midifiles/m83.mid");
      
//        MongoDatabase.getInstance().insertSong(s);
        List<Individual> inds = new ArrayList<Individual>();
//        for (Song s : MongoDatabase.getInstance().retrieveSongs()){
           // if (s.hasTrackTags()){
//            MongoDatabase.getInstance().removeSong(s);
           //     inds.add(new Individual(s, 0.0));            
           // }
//        }

        inds.add(new Individual(s, 0.0));
        inds.add(new Individual(n, 0.0));
        inds.add(new Individual(m, 0.0));
        
        System.out.println(s.getScore().getPartArray().length);
        System.out.println(s.getScore().getPartArray()[0].getPhraseArray().length);

        Crossover cross = new Crossover(40);
        Song crossed = cross.makeCrossover(inds);
        Translator.INSTANCE.playSong(m);
    }
}

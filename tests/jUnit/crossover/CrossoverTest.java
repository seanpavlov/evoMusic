package jUnit.crossover;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.Individual;
import com.evoMusic.util.TrackTag;
import com.evoMusic.util.Translator;

    public class CrossoverTest {
        
        @Test
        public void testSaveAndLoadEq(){
            String name1 = "Sweden";
            String path1 = "./midifiles/" + name1 + ".mid";
            
            String name2 = "mm2wily1";
            String path2 = "./midifiles/" + name2 + ".mid";
            
            Individual norway = new Individual(Translator.INSTANCE.loadMidiToSong(path1),0.0);
            Individual sweden = new Individual(Translator.INSTANCE.loadMidiToSong(path2),0.0);

            Random randomGen = new Random();

            
            for (int i = 0; i < norway.getSong().getNbrOfTracks(); i++){
                if (i % 2 == 0)
                    norway.getSong().addTagToTrack(i, TrackTag.BEAT);
                else
                    norway.getSong().addTagToTrack(i, TrackTag.MELODY);
            }
            
            for (int i = 0; i < sweden.getSong().getNbrOfTracks(); i++){
                if (i % 2 == 0)
                    sweden.getSong().addTagToTrack(i, TrackTag.BEAT);
                else
                    sweden.getSong().addTagToTrack(i, TrackTag.MELODY);
            }
            
            List<Individual> parents = new ArrayList<Individual>();
            
            parents.add(sweden);
            parents.add(norway);
            Crossover crossover = new Crossover(4);
            Song child = crossover.makeCrossover(parents);
            
            System.out.println(child.getNbrOfTracks());
            Translator.INSTANCE.showSong(child);
            Translator.INSTANCE.playSong(child);
      }
}


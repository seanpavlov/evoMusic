package jUnit.crossover;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.Individual;
import com.evoMusic.util.TrackTag;
import com.evoMusic.util.Translator;

    public class CrossoverTest {
        
        @Test
        public void testSaveAndLoadEq(){
            String name1 = "Norway";
            String path1 = "./midifiles/" + name1 + ".mid";
            
            String name2 = "Sweden";
            String path2 = "./midifiles/" + name2 + ".mid";
            
            Individual norway = new Individual(Translator.INSTANCE.loadMidiToSong(path1),0.0);
            Individual sweden = new Individual(Translator.INSTANCE.loadMidiToSong(path2),0.0);
            
            for (int i = 0; i < norway.getSong().getNbrOfTracks(); i++){
                norway.getSong().addTagToTrack(i, TrackTag.BEAT);
            }
            
            for (int i = 0; i < sweden.getSong().getNbrOfTracks(); i++){
                sweden.getSong().addTagToTrack(i, TrackTag.BEAT);
            }
            
            System.out.println(norway.getSong().getScore().getEndTime());
            System.out.println(sweden.getSong().getScore().getEndTime());
            
            List<Individual> parents = new ArrayList<Individual>();
            
            parents.add(sweden);
            parents.add(norway);
            
            
            Crossover crossover = new Crossover(10);
            Song child = crossover.makeCrossover(parents);
            
            System.out.println(child.getScore().getEndTime());
      }
}


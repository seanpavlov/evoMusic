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
            String name1 = "hyrule_temple_theme";
            String path1 = "./midifiles/Games/" + name1 + ".mid";
            
            String name2 = "pokemon_stadium_theme";
            String path2 = "./midifiles/Games/" + name2 + ".mid";
            
            String name3 = "dream_land";
            String path3 = "./midifiles/Games/" + name3 + ".mid";
            
            Individual norway = new Individual(Translator.INSTANCE.loadMidiToSong(path1),0.0);
            Individual sweden = new Individual(Translator.INSTANCE.loadMidiToSong(path2),0.0);
            Individual m83 = new Individual(Translator.INSTANCE.loadMidiToSong(path3),0.0);

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

            for (int i = 0; i < m83.getSong().getNbrOfTracks(); i++){
                if (i % 2 == 0)
                    m83.getSong().addTagToTrack(i, TrackTag.BEAT);
                else
                    m83.getSong().addTagToTrack(i, TrackTag.MELODY);
            }
            List<Individual> parents = new ArrayList<Individual>();
            
            parents.add(sweden);
            parents.add(norway);
           // parents.add(m83);

            Translator.INSTANCE.showSong(sweden.getSong());
            Translator.INSTANCE.showSong(norway.getSong());
            Translator.INSTANCE.showSong(m83.getSong());
            
            Song child = null;
            //for (int i = 1; i < 30; i++){                
                Crossover crossover = new Crossover(8);
                crossover.setSimplicityLevel(130);
                child = crossover.makeCrossover(parents);
                
                Translator.INSTANCE.showSong(child);
            //}
            Translator.INSTANCE.playSong(child);
      }
}


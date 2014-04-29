package jUnit.mutator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SwapSegmentMutator;


public class SwapSegmentMutatorTest {
    
    private static Song testSong;
    private static ISubMutator mutator;
    
    @BeforeClass
    public static void setUp(){
        mutator = new SwapSegmentMutator(1);
        
        Phrase phrase = new Phrase(0.0);
        
        for(int i = 0; i < 30; i++){
            phrase.addNote(i, 0.10);
        }
        testSong = new Song(new Score(new Part(phrase)));
        //testSong = Translator.INSTANCE.loadMidiToSong("midifiles/m83.mid");
    }
    
    @Test
    public void testSwapSegment(){
        for(int k = 0; k < 5; k++){
            System.out.println("lap: " + k);
        int noteCountBefore = 0;
        List<Integer> beforeValues = new ArrayList<Integer>();
        for(Track track : testSong.getTracks()){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                for(int v : phrase.getPitchArray()){
                    if(v != Note.REST){
                        System.out.print(v + " ");
                        beforeValues.add(v);
                        noteCountBefore++;
                    }
                }
            }
        }    
        System.out.println();
        mutator.mutate(testSong);
        boolean same = false;
        int noteCountAfter = 0;
        for(Track track : testSong.getTracks()){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                int[] values = phrase.getPitchArray();
                for(int i = 0; i < values.length; i++){ 
                    if(values[i] != Note.REST){
                        noteCountAfter++;
                        System.out.print(values[i] + " ");
                        if(!same && values[i] != beforeValues.get(i)){
                            same = true;
                        }
                    }
                }
            }
        }
        System.out.println();
        assertTrue("Same nbr of non Rest notes before and after mutation", noteCountBefore == noteCountAfter);
        assertTrue("Every note should not be in same place after mutation",same);  
        }
    }

}

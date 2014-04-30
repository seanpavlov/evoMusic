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
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SwapSegmentMutator;


public class SwapSegmentMutatorTest {
    
    private static Song testSong;
    private static ISubMutator mutator;
    
    @BeforeClass
    public static void setUp(){
        mutator = new SwapSegmentMutator(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(1.0);
        
        for(int i = 0; i < 30; i++){
            if(i % 5 == 0)
                phrase.addNote(Note.REST, 0.10);
            else
                phrase.addNote(i, 0.10);
            if(i % 7 == 0)
                phrase2.addNote(Note.REST, 0.2);
            else
                phrase2.addNote(i+30, 0.2);
        }
        Part part = new Part();
        part.add(phrase);
        part.add(phrase2);
        testSong = new Song(new Score(part));
    }
    
    @Test
    public void testSwapSegment(){
        for(int k = 0; k < 200; k++){
        List<Integer> beforeValues = new ArrayList<Integer>();
        List<Integer> beforeValuesNonRest = new ArrayList<Integer>();
        for(Track track : testSong.getTracks()){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                for(Note n : phrase.getNoteArray()){
                    int pitch = n.getPitch();
                    if(pitch != Note.REST){
                        beforeValuesNonRest.add(pitch);
                    }
                    beforeValues.add(pitch);
                }
            }
        }    
        mutator.mutate(testSong);
        List<Integer> afterValues = new ArrayList<Integer>();
        List<Integer> afterValuesNonRest = new ArrayList<Integer>();
        for(Track track : testSong.getTracks()){
            Phrase[] phrases = track.getPart().getPhraseArray();
            for(Phrase phrase : phrases){
                for(Note n : phrase.getNoteArray()){ 
                    int pitch = n.getPitch();
                    if(pitch != Note.REST){
                        afterValuesNonRest.add(pitch);
                    }
                    afterValues.add(pitch);
                }
            }
        }

        assertTrue("Same or less nbr of non rest notes before and after mutation", beforeValuesNonRest.size() >= afterValuesNonRest.size());
        assertTrue("Every non rest note should not be in same place after mutation" , !beforeValuesNonRest.equals(afterValuesNonRest));
        assertTrue("Every note should not be in same place after mutation", !beforeValues.equals(afterValues));  
        }
    }

}

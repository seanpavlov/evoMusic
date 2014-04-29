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
        
        for(int i = 0; i < 30; i++){
            phrase.addNote(i, 0.10);
        }
        testSong = new Song(new Score(new Part(phrase)));
    }
    
    @Test
    public void testSwapSegment(){
        for(int k = 0; k < 200; k++){
        List<Integer> beforeValues = new ArrayList<Integer>();
        for(Track track : testSong.getTracks()){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                for(Note n : phrase.getNoteArray()){
                    beforeValues.add(n.getPitch());
                }
            }
        }    
        mutator.mutate(testSong);
        List<Integer> afterValues = new ArrayList<Integer>();
        for(Track track : testSong.getTracks()){
            Phrase[] phrases = track.getPart().getPhraseArray();
            for(Phrase phrase : phrases){
                for(Note n : phrase.getNoteArray()){ 
                    afterValues.add(n.getPitch());
                }
            }
        }
        
        assertTrue("Same nbr of non Rest notes before and after mutation", beforeValues.size() == afterValues.size());
        assertTrue("Every note should not be in same place after mutation", !beforeValues.equals(afterValues));  
        }
    }

}

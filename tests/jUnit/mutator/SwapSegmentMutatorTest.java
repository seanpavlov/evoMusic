package jUnit.mutator;

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
            phrase.addNote(i, 0.25);
        }
        testSong = new Song(new Score(new Part(phrase)));
    }
    
    @Test
    public void test(){
       // int[] values1 = testSong.getTrack(0).getPart().getPhrase(0).getPitchArray();
        System.out.println("Before: ");
        for(Track track : testSong.getTracks()){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                for(int v : phrase.getPitchArray()){
                    if(v != Note.REST)
                    System.out.print(v + " ");
                }
            }
        }
        System.out.println();
        
        mutator.mutate(testSong);
        //int[] values2 = testSong.getTrack(0).getPart().getPhrase(0).getPitchArray();
        
        System.out.println("After: ");
        for(Track track : testSong.getTracks()){
            for(Phrase phrase : track.getPart().getPhraseArray()){
                for(int v : phrase.getPitchArray()){
                    //if(v != Note.REST)
                    System.out.print(v + " ");
                }
            }
        }
    }

}

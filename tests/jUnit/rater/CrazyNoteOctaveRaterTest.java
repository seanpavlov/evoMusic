package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.CrazyNoteOctaveRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class CrazyNoteOctaveRaterTest {
    private static Song testSong;
    
    private static SubRater rater;
    
    @BeforeClass
    public static void setUpSongs(){
        rater = new CrazyNoteOctaveRater(1);
        
        Phrase phrase = new Phrase(0.0);
        
        for(int i = 0; i < 20; i++){
            int j;
            if(i % 4 == 0)
                j = i + ((int)(Math.random() * 104) + 24);
            else
                j  = i;
            System.out.println("Pitch: " + j);
            phrase.addNote(j, 1.0);               
        }
        testSong  = new Song(new Score(new Part(phrase)));       
    }
    
    @Test
    public void shouldNotGetPerfectRating(){
        double rating = rater.rate(testSong);
        assertFalse("Should not ger perfect rating", rating == 1.0);
    }
    
}

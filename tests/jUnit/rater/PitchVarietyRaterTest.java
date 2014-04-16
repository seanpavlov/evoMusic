package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.PitchVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class PitchVarietyRaterTest {
    private static Song testSongPerfectRating, testSongMiddleRating, testSongWorstRating;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new PitchVarietyRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        
        for(int i = 1; i < 21 ; i++){
            phrase.addNote(i, 1.0);
            if(i % 2 == 0)
                phrase2.addNote(i-1, 1.0);
            else
                phrase2.addNote(i, 1.0);
            phrase3.addNote(1, 1.0);
        }

        testSongPerfectRating = new Song(new Score(new Part(phrase)));
        testSongMiddleRating = new Song(new Score(new Part(phrase2)));
        testSongWorstRating = new Song(new Score(new Part(phrase3)));
    }
    
    /**Test with song whose pitch values are all unique and
     * should get perfect rating*/
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(testSongPerfectRating);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    /**Test with song whose nbr of unique pitch values
     * is half of the nbr of total notes and should 
     * get rating 0.5 */
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("Song should rate 0.5", rating == 0.5);
    }
    
    /**Test with song which only have 1 unique pitch value
     * and nbr of notes is more than 1 and should get
     * rating 0.0*/
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(testSongWorstRating);
        assertTrue("Song should get rating 0.0", rating == 0.0);
    }
    
}

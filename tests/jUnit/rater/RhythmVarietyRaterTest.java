package jUnit.rater;

import static org.junit.Assert.assertTrue;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import org.junit.Before;
import org.junit.Test;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.RhythmicVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class RhythmVarietyRaterTest {
 private Song badSong, goodSong;
    
    private SubRater rater;
    
    @Before
    public void setUpSong(){
        rater = new RhythmicVarietyRater(1);
        
        Score goodScore = new Score();
        Score badScore = new Score();
        
        Part badPart = new Part();
        Part goodPart = new Part();
        
        for (int i = 1 ; i < 3; i++){
            Phrase p = new Phrase();
            for (int j = 1 ; i < 10; i++){
                Note n = new Note(10, j*i);
                p.add(n);
            }
            goodPart.add(p);
        }

        for (int i = 1 ; i < 3; i++){
            Phrase p = new Phrase();
            for (int j = 1 ; i < 10; i++){
                Note n = new Note(10, j);
                p.add(n);
            }
            badPart.add(p);
        }
        
        goodScore.add(goodPart);
        badScore.add(badPart);

        goodSong = new Song(goodScore);
        badSong = new Song(badScore);
    }
    
    /**
     * Test that the same song always gets the same rating
     * */
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(goodSong);
        assertTrue("Rating should be same for same song twice", rating1 == rating2);
    }
    
    /**
     * Test that song containing track, tagged with Beat tag, which contains patterns, gets rating higher than 0
     * */
    @Test 
    public void testShouldNotRateZero(){
        double rating = rater.rate(goodSong);
        assertTrue("Song known to be good should get higher rating than 0", rating > 0);
    }
    
    /**
     * Test that random valued beat track should rate less that song with known patterns in its beat track
     * */
    @Test
    public void testShouldRateLess(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(badSong);
        assertTrue("Good song should be better than bad song :)", rating1 > rating2);
    }
}

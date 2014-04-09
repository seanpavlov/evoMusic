package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyPitchRangeRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyPitchRangeRaterTest {
    private static Song testSongPerfect, testSongMiddleRating, testSongLowRating;
    private static SubRater rater;

    @BeforeClass
    public static void setUp(){
        rater = new MelodyPitchRangeRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        
        for(int i = 0; i < 20; i++){
            phrase.addNote(1, 1.0); 
            if(i < 10)
                phrase2.addNote(10, 1.0);
            else
                phrase2.addNote(20, 1.0);
            phrase3.addNote(i, 1.0);
        }
        testSongPerfect = new Song(new Score(new Part(phrase)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        testSongMiddleRating = new Song(new Score(new Part(phrase2)));
        testSongMiddleRating.addTagToTrack(0, TrackTag.MELODY);
        testSongLowRating = new Song(new Score(new Part(phrase3)));
        testSongLowRating.addTagToTrack(0, TrackTag.MELODY);
    }
    
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("should get rating 0.5", rating == 0.5);
    }
    
    @Test
    public void shouldGetLowRating(){
        double rating = rater.rate(testSongLowRating);
        assertTrue("should ger low rating", rating < 0.2);
    }
}

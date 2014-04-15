package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.RepeatedPitchDensityRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class RepeatedPitchDensityRaterTest {
    private static Song testSongPerfect, testSongMiddleRating;
    private static SubRater rater;

    @BeforeClass
    public static void setUp(){
        rater = new RepeatedPitchDensityRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        
        for(int i = 1; i < 21; i++){
            phrase.addNote(1, 1.0);
            if(i % 3 == 0)
                phrase2.addNote(2, 1.0);
            else
                phrase2.addNote(1, 1.0);
        }
        
        testSongPerfect = new Song(new Score(new Part(phrase)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        
        testSongMiddleRating = new Song(new Score(new Part(phrase2)));
        testSongMiddleRating.addTagToTrack(0, TrackTag.MELODY);
    }
    
    
    @Test
    public void shouldRaterPerfect(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("song with only same pitch values should rate perfect", rating == 1.0);
    }
    
    @Test
    public void shouldRateMiddle(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("should rate 0.35", rating == 0.35);
    }
    
}

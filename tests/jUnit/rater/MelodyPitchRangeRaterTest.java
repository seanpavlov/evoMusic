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
    private static Song testSongPerfect, testSongMiddleRating, testSongWorstRating;
    private static SubRater rater;

    @BeforeClass
    public static void setUp(){
        rater = new MelodyPitchRangeRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        
        for(int i = 0; i < 20; i++){
            phrase.addNote(1, 1.0); 
            if(i < 10){
                phrase2.addNote(10, 1.0);
                phrase3.addNote(i, 1.0);
            }else{
                phrase2.addNote(20, 1.0);
                phrase3.addNote(127, 1.0);
            }
            
        }
        testSongPerfect = new Song(new Score(new Part(phrase)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        testSongMiddleRating = new Song(new Score(new Part(phrase2)));
        testSongMiddleRating.addTagToTrack(0, TrackTag.MELODY);
        testSongWorstRating = new Song(new Score(new Part(phrase3)));
        testSongWorstRating.addTagToTrack(0, TrackTag.MELODY);
    }
    
    /**Test with song whose highest and lowest pitch
     * is the same value and it should get rating 1.0*/
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    /**Test with song whose highest pitch is twice as high as
     * it's lowest and it should get rating 0.5*/
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("should get rating 0.5", rating == 0.5);
    }
    
    /**Test with song whose different between higest and lowest
     * pitch is 127 and should get rating 0.0*/
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(testSongWorstRating);
        assertTrue("should ger low rating", rating == 0.0);
    }
}

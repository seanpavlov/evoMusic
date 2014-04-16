package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyNoteDensityRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyNoteDensityRaterTest {
    private static Song testSongMiddle, testSongPerfect, testSongLowRating, testSongRandom;
    private static SubRater rater;

    @BeforeClass
    public static void setUp(){
        rater = new MelodyNoteDensityRater(1);
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);    
        Phrase phrase4 = new Phrase(0.0);
        
        for(int i = 0; i < 20; i++){
            if(i < 3)
                phrase.addNote(1, 0.5);
            else
                phrase.addNote(1, 0.2);
            
            phrase2.addNote(1, 0.5);
            if(i == 0){
                phrase3.addNote(1, 1.0);
            }else{
                phrase3.addNote(1, 0.05);
            }
            
            phrase4.addNote(1, Math.random());
        }
        
        testSongMiddle = new Song(new Score(new Part(phrase)));
        testSongMiddle.addTagToTrack(0, TrackTag.MELODY);
        
        testSongPerfect = new Song(new Score(new Part(phrase2)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        
        testSongLowRating = new Song(new Score(new Part(phrase3)));
        testSongLowRating.addTagToTrack(0, TrackTag.MELODY);
        
        testSongRandom = new Song(new Score(new Part(phrase4)));
        testSongRandom.addTagToTrack(0, TrackTag.MELODY);
    }
    
    /**Song should get rating around 0.5*/
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddle);
        assertTrue("Song should get rating around 0.5",rating < 0.6 && rating > 0.4);
    }
    
    /**Song whose nbr of notes/beat is the same throughout the whole
     * song should get rating 1.0*/
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Same nbr of note/beat throughout song should give rating 1.0", rating == 1.0);
    }
    
    @Test
    public void shouldGetLowRating(){
        double rating = rater.rate(testSongLowRating);
        assertTrue("Song should get rating around 0.1", rating < 0.2 && rating > 0.0);
    }
    
    @Test
    public void shouldGetSameRating(){
        double rating1 = rater.rate(testSongRandom);
        double rating2 = rater.rate(testSongRandom);
        assertTrue("same song should get same rating", rating1 == rating2);
    }
}

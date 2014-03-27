package jUnit.rater;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyRepetitionRaterTest {
    private Song badSong, goodSong, testSong;
    
    private SubRater rater;
    
    /**
     * Create songs instances with known rhythm values to be able to test rater
     */
    @Before
    public void setUpSong(){
       rater = new MelodyRepetionRater(1);
       
       Phrase test = new Phrase();
       Phrase good = new Phrase();
       Phrase bad = new Phrase();
       
       int[] testNotes = {1,2,3,4,5,6,7,2,3,4,3,7};
       int[] goodNotes = {1,2,3,1,2,3,1,2,3,1,2,3};
       int[] badNotes  = {1,3,5,2,5,2,7,7,9,4,2,6};
       
       for (int i = 0; i < testNotes.length; i++){
           Note testN = new Note();
           testN.setPitch(testNotes[i]);
           test.addNote(testN);
           
           Note goodN = new Note();
           goodN.setPitch(goodNotes[i]);
           good.addNote(goodN);
           
           Note badN = new Note();
           badN.setPitch(badNotes[i]);
           bad.addNote(badN);
       }
       
       Part testP = new Part();
       testP.add(test);
       Score testS = new Score(testP);
       testSong = new Song(testS);
       testSong.addTagToTrack(0, TrackTag.MELODY);

       Part goodP = new Part();
       goodP.add(good);
       Score goodS = new Score(goodP);
       goodSong = new Song(goodS);
       goodSong.addTagToTrack(0, TrackTag.MELODY);

       Part badP = new Part();
       badP.add(bad);
       Score badS = new Score(badP);
       badSong = new Song(badS);
       badSong.addTagToTrack(0, TrackTag.MELODY);
    }    
    
    /**
     * Test that the same song always gets the same rating
     * */
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSong);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    }
    
    /**
     * Test that song containing track, tagged with Beat tag, which contains patterns, gets rating higher than 0
     * */
    @Test 
    public void testShouldNotRateZero(){
        double rating = rater.rate(testSong);
        assertTrue("Song with beat tags at known beat parts should get higher rating than 0", rating > 0);
    }
    
    /**
     * Test that random valued beat track should rate less that song with known patterns in its beat track
     * */
    @Test
    public void testShouldRateLess(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(badSong);
        System.out.println("rate good " + rating1);
        System.out.println("rate bad " + rating2);
        
        assertTrue("Random tagged beat parts should rate less than song with known beat parts", rating1 > rating2);
    }
}

package jUnit.raters;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Random;

import jm.music.data.Part;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.enumerators.TrackTag;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class BeatRaterTest {
    
    private Song testSongBeatTags, testSongNoBeatTags, testSong ;
    
    private SubRater rater;
    
    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     * @throws IOException
     *             if any problems with loading the MIDI occurrs.
     */
    @Before
    public void setUpSong() throws IOException {
        rater = new BeatRater();
        testSongBeatTags = Translator.INSTANCE.loadMidiToSong("midifiles/m83-midnight_city.mid");
        int count = 0;
        
        /**Add beat tags to known beat parts*/
        for(Part part : testSongBeatTags.getScore().getPartArray()){
            if(count == 7 || count == 8 || count == 9)
                testSongBeatTags.addTagToTrack(part, TrackTag.BEAT);
            count++;
        }
        
        testSongNoBeatTags = Translator.INSTANCE.loadMidiToSong("midifiles/Sweden.mid");
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_bros_theme.mid");
    }
    
    @Test
    public void testSameRating(){
        SubRater rater = new BeatRater();
        double rating1 = rater.rate(testSongBeatTags);
        double rating2 = rater.rate(testSongBeatTags);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    }
    
    @Test
    public void testShouldRateZero() throws IOException{
        double rating = rater.rate(testSongNoBeatTags);
        assertTrue("Song with no Rythm or Beat tag should rate 0", rating == 0.0);
    }
    
    @Test 
    public void testShouldNotRateZero(){
        
        double rating = rater.rate(testSongBeatTags);
        assertTrue("Song with beat tags at known beat parts should get higher rating than 0", rating > 0);
    }
    
    
    @Test
    public void testShouldRateLess(){
        int l = 0;
        Random rand = new Random();
        int[] randoms = new int[3];
        for(int i = 0 ; i < 3 ; i++){
            randoms[i] = rand.nextInt(19);
        }
        
        /**Add beat tags to random selected parts*/
        for(Part part : testSong.getScore().getPartArray()){
            if(l == randoms[0] || l == randoms[1] || l == randoms[2])
                testSong.addTagToTrack(part, TrackTag.BEAT);
            ++l;
        }
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSongBeatTags);
        assertTrue("Random tagged beat parts should rate less than song with known beat parts", rating1 < rating2);
    }
    
    

}

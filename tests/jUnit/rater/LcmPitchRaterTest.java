package jUnit.rater;

import static org.junit.Assert.assertTrue;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.geneticAlgorithm.rating.LcmPitchRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class LcmPitchRaterTest {
    private static Song goodSong, badSong;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new LcmPitchRater(1.0);
        
        Phrase good1 = new Phrase(0);
        Phrase bad1 = new Phrase(0);
        
        Phrase good2 = new Phrase(0);
        Phrase bad2 = new Phrase(0);
        
        Phrase good3 = new Phrase(0);
        Phrase bad3 = new Phrase(0);
        
        for (int i = 0; i < 16; i++){
            Note g1 = new Note();
            g1.setDuration(1.0);
            g1.setPitch(i);
            good1.add(g1);
        
            Note g2 = new Note();
            g2.setDuration(1.0);
            g2.setPitch(i);
            good2.add(g2);
        
            Note g3 = new Note();
            g3.setDuration(1.0);
            g3.setPitch(i);
            good3.add(g3);
            
            Note b1 = new Note();
            b1.setDuration(1.0);
            b1.setPitch((int) Math.floor(Math.random()*128));
            bad1.add(b1);
        
            Note b2 = new Note();
            b2.setDuration(1.0);
            b2.setPitch((int) Math.floor(Math.random()*128));
            bad2.add(b2);
            
            Note b3 = new Note();
            b3.setDuration(1.0);
            b3.setPitch((int) Math.floor(Math.random()*128));
            bad3.add(b3);
        }

        Part goodPart = new Part();
        goodPart.add(good1);
        goodPart.add(good2);
        goodPart.add(good3);
        
        Part badPart = new Part();
        badPart.add(bad1);
        badPart.add(bad2);
        badPart.add(bad3);
        
        goodSong = new Song(new Score());
        goodSong.addTrack(new Track(goodPart));
        goodSong.addTagToTrack(0, TrackTag.MELODY);
        
        badSong = new Song(new Score());
        badSong.addTrack(new Track(badPart));
        badSong.addTagToTrack(0, TrackTag.MELODY);
        
    }
    
    @Test
    public void testAllSongs(){
        for (Song s : MongoDatabase.getInstance().retrieveSongs()){
            double rating = rater.rate(s);
            assertTrue("same song should get same rating",rating <= 1 && rating >= 0);
        }
    }
    
    /**
     * Test that the same song always gets the same rating
     * */
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(goodSong);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    }
    
    /**
     * Test that good song is better than bad song
     * */
    @Test
    public void testBetterRating(){
        double rating1 = rater.rate(goodSong);
        double rating2 = rater.rate(badSong);
        assertTrue("Rating value should be same for same song twice", rating1 > rating2);
    }
}

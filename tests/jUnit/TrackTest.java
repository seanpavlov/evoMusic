package jUnit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import jm.music.data.Part;
import org.junit.Before;
import org.junit.Test;

import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.DrCross;
import com.evoMusic.util.TrackTag;

public class TrackTest {
    Song testSong;
    Track testTrack;
    
    @Before
    public void setUp(){
        testSong = Helpers.createTestSong();
        testTrack = testSong.getTrack(0);
    }
    

    @Test
    public void testGetSegment() {
        Track t1 = testTrack.getSegment(0, 4);
        Track t2 = testTrack.getSegment(4, 8);

        assertTrue("Track segment must be of length 4", t1.getPart().getEndTime()  == 4.0);
        assertTrue("Track segment must be of length 4", t2.getPart().getEndTime()  == 8.0);
    }

    @Test
    public void testAddTag() {
        testTrack.addTag(TrackTag.BASELINE);
        assertTrue("Track must have tracktag", testTrack.hasTag(TrackTag.BASELINE));
    }

    @Test
    public void testInsertLength() {
        Track t1 = testTrack.getSegment(0, 4);
        Track t2 = testTrack.getSegment(4, 8);
        t2.insert(t1, 4);

        assertEquals(12.0, t2.getPart().getEndTime(), 0.00001);
    }

    @Test
    public void testGetSegmentsLength() {
        List<Track> segments = testTrack.getSegments(4);
        
        for (Track track : segments){
            assertEquals(4.0, track.getPart().getEndTime(), 0.00001);
        }
    }
}

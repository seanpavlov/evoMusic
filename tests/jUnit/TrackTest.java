package jUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
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
        testTrack.setTag(TrackTag.BASELINE);
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
    
    @Test
    public void testEquals() {
        Track t1 = testTrack.getSegment(0, 4);
        Track t2 = testTrack.getSegment(4, 8);
        assertTrue(!t1.equals(t2));
        t2 = testTrack.getSegment(0, 8);
        assertTrue(t1.equals(t2.getSegment(0, 4)));
    }
}

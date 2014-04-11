package jUnit;

import static org.junit.Assert.*;
import java.util.List;
import jm.music.data.Part;
import org.junit.Before;
import org.junit.Test;
import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;

public class TrackTest {
    Song testSong;
    Track testTrack;
    
    @Before
    public void setUp(){
        testSong = Helpers.createTestSong();
        testTrack = testSong.getTrack(0);
    }
    
//    @Test
    public void testGetSegment() {
        Track t = testTrack.getSegment(12, 4);
        Translator.INSTANCE.showPart(t.getPart());
        Translator.INSTANCE.playPart(t.getPart());
        System.out.println(t.getPart().getEndTime());
        fail("Not yet implemented");
    }
    
//    @Test
    public void testGetSegments() {
        List<Track> t = testTrack.getSegments(20);
        
        for (Track tr : t){
            Translator.INSTANCE.showPart(tr.getPart());
        }
       
        int i = 16;
        Translator.INSTANCE.showPart(t.get(8).getPart());
        Translator.INSTANCE.playPart(t.get(8).getPart());
        
        fail("Not yet implemented");
    }
    
    @Test
    public void test() {
        Track trackOriginal = Helpers.createSongWithMelody(
                new int[] { 8 + 12 * 4, 8 + 12 * 4, 8 + 12 * 4, 8 + 12 * 4 })
                .getTrack(0);
        Track trackInsert = Helpers.createSongWithMelody(
                new int[] { 0 + 12 * 4, 1 + 12 * 4, 2 + 12 * 4, 3 + 12 * 4 })
                .getTrack(0);

        Track newTrack = new Track(new Part());
        
        newTrack.merge(trackOriginal, 4);
        newTrack.merge(trackInsert, 0);
        newTrack.insert(trackInsert, 4);
        newTrack.printRoll();
        newTrack.getSegment(2, 4).printRoll();;
    }
}

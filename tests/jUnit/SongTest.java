package jUnit;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import jm.music.data.Part;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.util.TrackTag;

public class SongTest {

    private static Song testSong;
    
    @BeforeClass
    public static void setUpTestSong() {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
    }
    
    @Test
    public void testAddTagToTrack() {
        Track track = testSong.getTrack(1);

        testSong.addTagToTrack(1, TrackTag.MELODY);
        testSong.addTagToTrack(1, TrackTag.BASELINE);
        testSong.addTagToTrack(1, TrackTag.MELODY);
        
        Set<TrackTag> ttags = testSong.getTrackTags(1);
        
        assertTrue("Only two tags should exist", ttags.size() == 2);
        assertTrue(ttags.contains(TrackTag.MELODY));
        assertTrue(ttags.contains(TrackTag.BASELINE));
    }

    @Test
    public void testGetTrackTagsInt() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetTrackTagsPart() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetTaggedTracks() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetTrackTags() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetUserTags() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddUserTag() {
        fail("Not yet implemented");
    }

}

package jUnit;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
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

        testSong.addTagToTrack(1, TrackTag.MELODY);
        testSong.addTagToTrack(1, TrackTag.BASELINE);
        testSong.addTagToTrack(1, TrackTag.MELODY);

        Set<TrackTag> ttags = testSong.getTrackTags(1);

        assertTrue("Only two tags should exist", ttags.size() == 2);
        assertTrue(ttags.contains(TrackTag.MELODY));
        assertTrue(ttags.contains(TrackTag.BASELINE));
    }

    @Test
    public void testGetTaggedTracks() {
        testSong.addTagToTrack(0, TrackTag.BASELINE);
        testSong.addTagToTrack(1, TrackTag.BASELINE);

        assertTrue("Two tracks should have the tag BASSLINE", testSong
                .getTaggedTracks(TrackTag.BASELINE).size() == 2);

        assertTrue("No track should have the tag BEAT", testSong
                .getTaggedTracks(TrackTag.BEAT).size() == 0);
    }

    @Test
    public void testAddUserTag() {
        assertTrue(testSong.getUserTags().size() == 0);
        testSong.addUserTag("Sexy disco");
        testSong.addUserTag("Sexy disco");
        assertTrue("Don't allow dupes!", testSong.getUserTags().size() == 1);
    }

}

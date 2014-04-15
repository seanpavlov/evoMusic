package jUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        TrackTag tag = testSong.getTrackTag(1);

        assertTrue("Tag should not be null", tag != null);
        assertTrue(tag.equals(TrackTag.MELODY));
        assertFalse(tag.equals(TrackTag.BASELINE));
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

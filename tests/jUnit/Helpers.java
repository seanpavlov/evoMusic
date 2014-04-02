package jUnit;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.util.TrackTag;

public abstract class Helpers {

    public static Song createTestSong() {
        Song testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        for(Track track : testSong.getTracks()){
            track.addTag(TrackTag.MELODY);
        }
        return testSong;
    }
}

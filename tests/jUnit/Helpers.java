package jUnit;

import jm.JMC;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.util.TrackTag;

public abstract class Helpers {

    /**
     * Creates a dummy song with all tracks given the tracktag melody
     */
    public static Song createTestSong() {
        Song testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        for(Track track : testSong.getTracks()){
            track.setTag(TrackTag.MELODY);
        }
        return testSong;
    }
    
    /**
     * Creates a song with a melody given by integer pitches. Every pitch is
     * played in the same order as the array. Every note is a quarter length
     */
    public static Song createSongWithMelody(int[] pitches) {

        Note[] notes = new Note[pitches.length];
        for (int i = 0; i < pitches.length; i++) {
            notes[i] = new Note(5*12 + pitches[i], JMC.QUARTER_NOTE);
        }
        
        return new Song(new Score(new Part(new Phrase(notes))));
        
    }
}

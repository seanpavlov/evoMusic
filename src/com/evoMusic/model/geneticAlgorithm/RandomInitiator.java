package com.evoMusic.model.geneticAlgorithm;

import jUnit.SongTest;

import java.util.Random;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import jm.music.data.Score;

public class RandomInitiator {

    // 4th, 8th, 16th note etc.
    private static final int SMALLEST_NOTE_TYPE = 32;
    private static final double REST_PROBABILITY = 0.8;
    private static final int NOTE_DYNAMICS = 64;

    private Song song;
    private double maxLength;
    private Random random = new Random();

    public RandomInitiator(double maxLength) {
        this.song = new Song(new Score());
        this.song.getScore().setTempo(120);
        this.maxLength = maxLength;
    }

    /**
     * Generates a MelodyTrack and returns the song in which this track is added
     * to.
     * 
     * @return
     */
    public Song generateMelody() {
        Part melodyPart = new Part();
        Track melodyTrack = new Track(melodyPart);
        int nrOfPhrases = (int) Math.random() * 6 + 1;

        // randomize number of phrases
        for (int i = 0; i < nrOfPhrases; i++) {
            Phrase phrase = new Phrase();
            phrase.setStartTime(0);

            // add notes while length is less than maxlength
            do {
                Note n = new Note();
                // double noteLength = (random.nextInt(8) + 1) / 4;
                double noteLength = getRandomNoteLength(true);
                n.setDuration(noteLength);
                n.setRhythmValue(noteLength);

                if (random.nextDouble() < REST_PROBABILITY) {
                    n.setPitch(Note.REST);
                } else {
                    n.setPitch(getRandomPitch(true));
//                    n.setPitch(random.nextInt(80) + 22);
                }
                n.setDynamic(NOTE_DYNAMICS);
                phrase.add(n);
            } while (phrase.getEndTime() < maxLength);

            melodyPart.add(phrase);
        }

        melodyTrack.setTag(TrackTag.MELODY);
        song.addTrack(melodyTrack);
        return song;
    }

    private double getRandomNoteLength(boolean curveControlled) {
        int noteLength;
        
        if (curveControlled) {
            double x = random.nextDouble() * 2;
    
            /*
             * A 3rd grade function distributing values between 1 and
             * smallestNoteType, with values closer to the middle having higher
             * probability.
             */
            noteLength = (int) ((Math.pow((x - 1), 3) + 1) * 0.5
                    * SMALLEST_NOTE_TYPE + 1);
        } else {
            noteLength = (int)((random.nextDouble() + 1) * SMALLEST_NOTE_TYPE);
        }

        return 4.0 / noteLength;
    }

    private int getRandomPitch(boolean curveControlled) {
        int notePitch;
        
        if (curveControlled) {
          double x = random.nextDouble() * 2;

          /*
           * A 3rd grade function distributing values between 1 and
           * including 127, with values closer to the middle having higher
           * probability.
           */
          notePitch = (int) ((Math.pow((x - 1), 3) + 1) * 0.5 * 128);
            
        } else {
            notePitch = (int)(random.nextDouble() * 128);
        }

        return notePitch;
    }
}

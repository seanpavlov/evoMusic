package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.List;

import jm.constants.Instruments;
import jm.music.data.Note;
import jm.music.data.Phrase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.ScalePattern;
import com.evoMusic.util.TrackTag;

/**
 * ScaleWhizz analyzes notes and tries to fit them into a scale pattern. The
 * more notes that can be fitted into a scale pattern, the better is the result.
 */
public class ScaleWhizz extends SubRater {

    private ScalePattern scalePattern;

    /**
     * Creates the sub rater. The scale pattern is by default set to diatonic as
     * it is the most common pattern. Applies for instance to aeolian and ionian
     * scales (standard major and minor).
     * 
     * @param weight
     *            weight of this rater.
     */
    public ScaleWhizz(double weight) {
        setWeight(weight);
        scalePattern = ScalePattern.DIATONIC;
    }

    /**
     * The function returns 1 given 1 and 0 given 7/12 as its the worst possible
     * outcome from the ScaleWizz
     */
    private double ratingLinear(double x) {
        return 12 * x / 5.0 - 7 / 5.0;
    }

    @Override
    public double rate(Song song) {
        return rateSegment(song, 0, song.getScore().getEndTime());
    }
    
    public double rateSegment(Song segment, double from, double to) {
        List<Track> targeted = segment.getTracks();
        
        PitchCounter pc = new PitchCounter(targeted, from, to);
        int hits = maximumScaleHits(pc.notePitches);
        
        return pc.numberOfNotes == 0 ? 0 : ratingLinear(1.0 * hits
                / pc.numberOfNotes);
    }

    /**
     * Given an array of pitches, returns highest number of pitches that could be
     * fitted into the scale pattern
     * 
     * @param pitches
     *            note pitches to look at
     * @return maximum number of hits
     */
    private int maximumScaleHits(int[] pitches) {
        int[] scaleArr = scalePattern.getPitches();
        int hits = 0;
        int currentMaxHits = 0;

        // rotate the scale pattern its whole length
        for (int i = 0; i < scaleArr.length; i++) {
            hits = 0;

            // try to fit the pitch
            for (int j = 0; j < pitches.length; j++) {
                hits += pitches[j] * scaleArr[(j + i) % scaleArr.length];
            }

            // save result if it improved
            if (hits > currentMaxHits) {
                currentMaxHits = hits;
            }
        }

        return currentMaxHits;
    }

    /**
     * Helper class for counting notes of different pitch
     */
    private final class PitchCounter {

        private final int[] notePitches = new int[12];
        private int numberOfNotes = 0;

        /**
         * Instantiates and starts counting notes. Drum instruments are skipped
         * for obvious reasons. 
         * @param tracks tracks to be counted. 
         */
        public PitchCounter(List<Track> tracks, double from, double to) {
            for (Track track : tracks) {
                if (track.getPart().getInstrument() != Instruments.DRUM) {
                    countNotes(track, from, to);
                }
            }
        }

        private void countNotes(Track track, double from, double to) {
            if (track.hasTag(TrackTag.BEAT)) {
                return;
            } 
            
            for (Phrase phrase : track.getPart().getPhraseArray()) {
                if (to < phrase.getStartTime()) {
                    return;
                }
                Phrase cpPhrase = phrase.copy(Math.max(from, phrase.getStartTime()), to, false, false, false);
                for (Note note : cpPhrase.getNoteArray()) {
                    if (!note.isRest()) {
                        notePitches[note.getPitch() % 12]++;
                        numberOfNotes++;
                    }
                }
            }
        }
    }
}
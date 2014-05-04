package com.evoMusic.model.geneticAlgorithm.blending.multiMarkov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class StateTrack {

    private int         firstNote;
    private int         instrument;
    private int         channel;
    private TrackTag    tag;
    private int         largestChord;

    private List<State> stateList;

    /**
     * Creates a new interval track from a given track, maintaining its
     * properties.
     * 
     * @param track
     *            The track from which this instance will be based on.
     */
    public StateTrack(Track track) {
        Part part = track.getPart();
        instrument = part.getInstrument();
        channel = part.getChannel();
        largestChord = 0;
        stateList = new ArrayList<State>();
        if (part.size() == 0) {
            firstNote = 60;
            tag = TrackTag.NONE;
            return;
        }
        tag = track.getTag();

        // sort by start time and group notes at the same start time.
        double partLength;
        State currentState;
        for (Phrase phrase : part.getPhraseArray()) {
            partLength = phrase.getStartTime();
            for (Note note : phrase.getNoteArray()) {
                currentState = null;
                for (State foundState : stateList) {
                    if (foundState.startsAtTime(partLength)) {
                        currentState = foundState;
                        break;
                    }
                }
                if (currentState == null) {
                    currentState = new State(partLength);
                }
                currentState.addNote(note);
                stateList.add(currentState);
                partLength += note.getRhythmValue();
            }
        }
        Collections.sort(stateList);

        // Initialize all states and find the largest state size.
        this.firstNote = stateList.get(0).getHighestPitch();
        int previousPitch = firstNote;
        
        // find all start times.
        Double[] nextStartTimes = new Double[stateList.size()];
        for (int i = 0; i < nextStartTimes.length-1; i++) {
            nextStartTimes[i] = stateList.get(i+1).getStartTime();
        }
        nextStartTimes[nextStartTimes.length-1] = null;
        // Initiate all states.
        State state;
        for (int stateIndex = 0; stateIndex < stateList.size(); stateIndex++) {
            state = stateList.get(stateIndex);
            state.initializeIntervalForm(previousPitch, nextStartTimes[stateIndex]);
            previousPitch += state.getHighestInterval();
            if (state.size() > largestChord) {
                largestChord = state.size();
            }
        }
    }

    /**
     * Creates a new interval track from the given properties.
     * 
     * @param firstNote
     *            The note that will come first when converting this interval
     *            track into a real track.
     * @param instrument
     *            The instrument of this track.
     * @param channel
     *            The channel of this track.
     * @param stateList
     *            A list of states that will define this track.
     * @param tag
     *            The track tag for this track.
     */
    public StateTrack(int firstNote, int instrument, int channel,
            List<State> stateList, TrackTag tag) {

        this.firstNote = firstNote;
        this.instrument = instrument;
        this.channel = channel;
        this.stateList = stateList;
        this.tag = tag;
    }

    /**
     * Creates Track object converted from this interval track.
     * 
     * @return The track made from this interval track's properties.
     */
    public Track toTrack() {
        List<Phrase> phrases = new ArrayList<Phrase>();
        for (int i = 0; i < largestChord; i++) {
            phrases.add(new Phrase(0.0));
        }

        List<Note> generatedNotes;
        State currentState;
        int chordSize;
        int previousPitch = getFirstNote();

        // looping through each chord.
        for (int stateIndex = 0; stateIndex < stateList.size(); stateIndex++) {
            currentState = stateList.get(stateIndex);
            chordSize = currentState.size();
            System.out.println(chordSize);
            generatedNotes = currentState.toNotes(previousPitch);
            previousPitch += currentState.getHighestInterval();

            for (int noteIndex = 0; noteIndex < chordSize; noteIndex++) {
                phrases.get(noteIndex).add(generatedNotes.get(noteIndex));
            }
            // Make sure all phrases are the same length
            for (int i = chordSize; i < largestChord; i++) {
                phrases.get(i).add(
                        new Note(Note.REST, currentState.getRhythmValue()));
            }
        }
        Part newPart = new Part();
        for (Phrase phrase : phrases) {
            newPart.add(phrase);
        }
        newPart.setChannel(getChannel());
        newPart.setInstrument(getInstrument());
        return new Track(newPart, tag);
    }

    /**
     * Gets the first note of this track.
     * 
     * @return The first note of this track.
     */
    public int getFirstNote() {
        return firstNote;
    }

    /**
     * Gets the instrument of this track.
     * 
     * @return The instrument of this track.
     */
    public int getInstrument() {
        return instrument;
    }

    /**
     * Gets the channel of this track.
     * 
     * @return The channel of this track.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Gets the list of states of this track.
     * 
     * @return The list of states of this track.
     */
    public List<State> getStates() {
        return new ArrayList<State>(this.stateList);
    }

    /**
     * Gets the tag of this track.
     * 
     * @return The tag of this track.
     */
    public TrackTag getTag() {
        return tag;
    }
    
}

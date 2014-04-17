package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class Crossover {
    private int intersections;
    private Integer maxDuration = null;
    private Integer minDuration = 0;

    private int silenceLength = 40;
    private int simplicity = 5;
    private Random randomGen;    

    /**
     * Constructor for crossover
     * 
     * @param intersections
     */
    public Crossover(int intersections){
        randomGen = new Random();
        this.intersections = intersections;
    }

    /**
     * Sets the maximum length of the results, defaults to no restriction if 
     * not set specifically with this method.
     * 
     * @param maxDuration
     */
    public void setMaxDuration(int maxDuration){
        this.maxDuration = maxDuration;
    }
    
    /**
     * Sets the minimum length of the results, defaults to 10
     * high values increases time consumption drastically
     * 
     * @param maxDuration
     */
    public void setMinDuration(int minDuration){
        this.minDuration = minDuration;
    }
    
    /**
     * Set simplicity level, the higher the value the less complex the results will be,
     * defaults to 1 (complex) and should not exceed 100.
     * 
     * @param simplicity
     */
    private void setSimplicityLevel(int simplicity){
        this.simplicity = simplicity;
    }
    
    /**
     * Set maximum silence duration in a song, defaults to 20.
     * 
     * @param silenceLength
     */
    private void setMaxSilenceLength(int silenceLength){
        this.silenceLength = silenceLength;
    }
    
    /**
     * Called to generate one new offspring from a list of parents
     * 
     * @param parents
     * @return child
     */
    public Song makeCrossover(List<Individual> parents){
        this.setMaxSilenceLength(randomGen.nextInt(20)+1);
        this.setSimplicityLevel(randomGen.nextInt(100)+2);
        
        Score finalScore = new Score();
        Song child = new Song(finalScore);
        
        try {
            parents.get(1);
        } catch (Exception e) {
            System.err.println("Not enough parents in crossover");
            return child;
        }
        
        this.setMaxSilenceLength(randomGen.nextInt(10)+1);
        this.setSimplicityLevel(randomGen.nextInt(50)+2);
        
        List<List<Track>> tracksWithTag = new ArrayList<List<Track>>();
        double averageTempo = 0;
        
        List<TrackTag> tags = getCommonTracks(parents);
        tags.remove(TrackTag.NONE);
        try {
            tags.get(0);
        } catch (Exception e) {
            System.out.println("No common tracktags, parents could not be crossed");
            e.printStackTrace();
        }
        for (TrackTag t : tags){
            averageTempo = 0;
            for (Individual i : parents){
                averageTempo += i.getSong().getTempo();
                tracksWithTag.add(i.getSong().getTaggedTracks(t));
            }

            Track newTagPart = crossTaggedTracks(tracksWithTag);
            newTagPart.setTag(t);
            child.addTrack(newTagPart);
            
            tracksWithTag.clear();
        }
        
        finalScore.setTempo(averageTempo/parents.size());
        
        return child;
    }
    
    /**
     * Called for every common track tag found in the list of parents, results in one new track,
     * for example melody or beat. 
     * 
     * @param tracksWithTag
     * @return
     */
    private Track crossTaggedTracks(List<List<Track>> tracksWithTag) {
        Part newTagPart = new Part();
        int noteCounter = 0;
        
        //loops all tracks with one tracktag in all parents
        for (List<Track> taggedTracksInParent: tracksWithTag){
            //loops all tracks in one parent with same tracktag

            for (Track taggedTrack: taggedTracksInParent){
                List<Phrase> choppedTrack = chop(taggedTrack.getPart(), noteCounter);
                List<Phrase> morphedTracks = morph(choppedTrack, noteCounter);
                for (Phrase tempPhrase : morphedTracks){
                    newTagPart.add(tempPhrase);
                    if (maxDuration != null && newTagPart.getEndTime() > maxDuration){ 
                        newTagPart.removeLastPhrase();
                        return new Track(newTagPart);
                    };
                }
            }
        }
        return new Track(newTagPart);
    }

    /**
     * Chops a part into sections depending on number of intersections, each sub part contains equally
     * many notes.
     * 
     * @param taggedTrackPart
     * @return
     */
    private List<Phrase> chop(Part taggedTrackPart, int noteCounter) {
        noteCounter = 0;
        List<Phrase> choppedPhrases = new ArrayList<Phrase>();

        for (Phrase phrase : taggedTrackPart.getPhraseArray()){
            Note[] phraseNotes = phrase.getNoteArray();
            noteCounter += phraseNotes.length;
            int numberOfNotes = (int) Math.floor(phraseNotes.length / intersections);
            Phrase newPhrase = new Phrase();
            if (numberOfNotes > 1 ){
                for (int i = 0; i < phraseNotes.length; i++){
                    phraseNotes[i].setPan(0.5);
                    phraseNotes[i].setDynamic(64);
                    
                    if (phraseNotes[i].getDuration() < silenceLength){ 
                        newPhrase.addNote(phraseNotes[i]);
                    }
                    if (i % numberOfNotes == 0 && newPhrase.length() > simplicity){
                        choppedPhrases.add(newPhrase.copy());
                        newPhrase = new Phrase();
                    }
                }
            }
            choppedPhrases.add(phrase);
        }

        return choppedPhrases;
    }
    
    /**
     * Combines a list of phrases into a new list, this list has randomized parts
     * from the parameter list
     * 
     * @param choppedTrack
     * @param noteCounter 
     * @return
     */
    private List<Phrase> morph(List<Phrase> choppedTrack, int noteCounter) {
        List<Phrase> newPhrases = new ArrayList<Phrase>();
        
        for (int i = 0; i < choppedTrack.size(); i++){
            Phrase newPhrase = choppedTrack.get(randomGen.nextInt(choppedTrack.size()));
            newPhrases.add(newPhrase);
        }
        
        return newPhrases;
    }

    /**
     * Returns a list of all common tags that all individuals have
     * 
     * @param parents
     * @return
     */
    private List<TrackTag> getCommonTracks(List<Individual> parents){
        List<TrackTag> commonTrackTags = new ArrayList<TrackTag>();
        TrackTag[] tracks = TrackTag.values();
        
        for (TrackTag tag : tracks){
            commonTrackTags.add(tag);
            for (Individual i : parents){
               if (i.getSong().getTaggedTracks(tag).isEmpty()){
                   commonTrackTags.remove(tag);
                   break;
               }
            }
        }
        return commonTrackTags;
    }
}

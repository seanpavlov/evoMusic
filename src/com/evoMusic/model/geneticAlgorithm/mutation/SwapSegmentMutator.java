package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

/*Mutator which swaps two segments of the tracks in the song by random length  
 *and random start times 
 * */
public class SwapSegmentMutator extends ISubMutator{

    /**
     * Mutator constructor
     * @param mutationprobability 
     * */
    public SwapSegmentMutator(double mutationProbability) {
        super(mutationProbability);
    }

    /**
     * The method swaps two segments of the tracks in the song by random length  
     * and random start times
     * @param Song song to be mutated
     * */
    @Override
    public void mutate(Song song, double probabilityMultiplier) {
        double localProbability = getProbability()*probabilityMultiplier;
        List<Track> tracks = song.copy().getTracks();
        for(int i = tracks.size()-1; i >= 0; i--){
            if(Math.random() < localProbability){ 
               Track track = tracks.get(i);
               /*Retrieve and calculate the tracks end time 
                 with a fraction of either 0, 1/4, 2/4 or 3/4*/
               double trackTime = track.getPart().getEndTime();
               double trackEndTime =  Math.floor(trackTime) + findClosestFraction(trackTime%1);
               //Calculate swap segments length             
               double swapLength = segmentValue(0.25, trackEndTime/2);
               //Calculate first swap start time
               double firstSwap = segmentValue(0, trackEndTime - (swapLength * 2));  
               //Calculate second swap start time
               double secondSwap = (firstSwap + (swapLength * 2) == trackEndTime) 
                            ? firstSwap + swapLength
                            : segmentValue(firstSwap + swapLength, trackEndTime - swapLength);
              
               
               // build a new track with swaped segments
               Track trackWithSwap = swapSegments(track, firstSwap, secondSwap, swapLength, trackEndTime);  
         
               //Flatten track to get as few phrases as possible
               trackWithSwap.flattern();

               //Tag new track with swaped segments with same tag as old track
               trackWithSwap.setTag(track.getTag());
              
               //Add new track with swaped segments if old is removed successfully
               if(song.removeTrack(i) != null){
                   song.addTrack(trackWithSwap);
               }
            }
        }
    }
    
    
    /**
     * Swaps two segments of the track by splitting
     * the track in to segments and building it back 
     * toghether againg where the two target segments have been swapped
     * @param track Track to swap segments in
     * @param first Start value of the first swap segment
     * @param second Start value of the second swap segment
     * @param length Length of the segments to swap
     * @return Track The new track with swaped segments
     * */
    private Track swapSegments(Track track, double first, double second, double length, double trackEndTime){
        /*If second swap segments start time or second added with length
        is larger than tracks end time, return original track*/
        if(second >= trackEndTime || second + length > trackEndTime){
            return track;
        }
        //List of segment tracks
        List<Track> tracks = new ArrayList<Track>();
        
        /*If first swap segments start time is larger than 0,
        retrive segment before it and add to list of segment tracks*/
        if(first > 0){
            tracks.add(findSegment(track, 0 , first, 0));
        }
        //Add second swap segment track to list
        tracks.add(findSegment(track, second, length, first));
        
        //If their is a segment between first and second, add this to segment track list
        if(second - (first + length) > 0){
            tracks.add(findSegment(track, first+length, second - (first+length), first+length));
        } 
        //Add first segment track to list
        tracks.add(findSegment(track, first, length, second));
        
        /*If second swap start time added by lenght is less than track end time
        add last segment track to list*/
        if(second + length < trackEndTime){
            tracks.add(findSegment(track, second+length, trackEndTime,second+length));
        }
       
        //Return appended tracks 
        return appendTracks(tracks);
    }
    
    /**
     * Finds and retreive segments from a track and changes its phrases start times 
     * to the right values
     * @param track The track to find segment from
     * @param from The start value for the segment 
     * @param length The length of the segment
     * @param start The start time to set for the phrases in found segment
     * */
    private Track findSegment(Track track, double from, double length, double start){
        Track trackSegment = getSegment(track,from,length);
        setPhrasesStartTime(trackSegment, start);
        return trackSegment;
    }
    
    
    /**
     * Calculates a random value between start and end time, where end time and
     * the resulting segments lengths fraction is rounded down to closest value of
     * either 0, 1/4, 2/4 or 3/4
     * @param start Start value of the intervall to generate random value from
     * @param end End value of the intervall to generate random value from
     * @return double Random generated value between start and end intervall
     * */
    private double segmentValue(double start, double end){
        end = Math.floor(end) + findClosestFraction(end%1);
        return Math.round(randomDouble(start, end)*4)/4f;
     }
    
    /**
     * Calculates a random value between start and end time
     * @param start Start of the intervall to generate random value from
     * @param end End of the intervall to generate random value from
     * @return double Random generated value between start and end intervall
     * */
    private double randomDouble(double start, double end){
        double random = new Random().nextDouble();
        return start + (random *(end - start));
    }
    
    /**
     * Append tracks in to the the first track in the list
     * @param tracks List of tracks to be appended
     * @Return Track the new track of appended tracks
     * */
    private Track appendTracks(List<Track> tracks){
        Track initialTrack = null;
        if(tracks.size() > 0){
            initialTrack = tracks.remove(0);
            for(Track track : tracks){
                appendTrack(initialTrack, track);
            }
        }
        return initialTrack;
    }
    
    /**
     * Appends two tracks
     * @param track track to append on
     * @param other the track to append
     * */
    private void appendTrack(Track track, Track other){
        for(Phrase phrase : other.getPart().getPhraseArray()){
            track.getPart().appendPhrase(phrase);
        }
    }
    
    /**
     * Return a segment of a Track as a new Track
     * @param track The track to get a segment from
     * @param from The start time of the segment to get
     * @param length The length of the segment to get
     * @return Track the segment as a track
     * */
    private Track getSegment(Track track, double from, double length) {
        Phrase[] phrases = track.getPart().getPhraseArray();

        if (from < 0 || from > track.getPart().getEndTime()) {
            throw new IllegalArgumentException(
                    "Requested a segment starting at: " + from
                            + ". The segment must be in in the interval 0 and "
                            + track.getPart().getEndTime() + "!");
        }
        if (phrases.length < 1) {
            throw new IllegalArgumentException(
                    "Can't get segment from an empty Track!");
        }
        
        
        Part partCopy = track.getPart().copy(from, from + length, false, true, true);
        for(Phrase phrase : partCopy.getPhraseArray()){ 
                Note firstnote = phrase.getNote(0);
                if(firstnote.getPitch() == Note.REST && from != 0){
                    phrase.removeNote(0);
                }
           
                Note lastNote = phrase.getNote(phrase.getNoteArray().length -1);
                if(lastNote != null && lastNote.getPitch() == Note.REST){
                    phrase.removeLastNote();
                }

        }
        return new Track(partCopy);     
    }
    
    /**
     * Finds closest lower fraction to either 0, 1/4, 2/4 or 3/4
     * Example: 0.66 is going to find and return closest fraction of 0.5
     * but 0.49 is going to find and return closest fraction of 0.25
     * @param fraction the fraction to find closest lower 1/4 to
     * @return double the closest lower 1/4 found
     * */
    private static double[] fractionalValues = {0.25, 0.5, 0.75};
    private double findClosestFraction(double fraction){
        if(fraction >= 1.0){
            return 0.0;
        }
        double returnFraction = 0.0;
        double margin = Math.abs(0.0 - fraction);
        for(double fractionValue : fractionalValues){
            if(fractionValue == fraction){
                returnFraction = fraction;
                break;
            }else if(fractionValue < fraction){
                double nextMargin = Math.abs(fractionValue - fraction);
                if(margin > nextMargin){
                    margin = nextMargin;
                    returnFraction = fractionValue;
                }
            }
        }
        return returnFraction;
    }
    
    /**
     * Sets the start time of all the phrases in a track
     * @param track The track to set its phrases start time 
     * @param startTime The time to set as start time on all phrases in track
     * */
    private void setPhrasesStartTime(Track track, double startTime){
        for(Phrase phrase : track.getPart().getPhraseArray()){
            phrase.setStartTime(startTime);
        }
    }
}

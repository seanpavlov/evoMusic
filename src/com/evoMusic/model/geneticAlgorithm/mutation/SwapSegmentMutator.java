package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

/*Mutator which swaps two segments of a track in the song of random length  
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
     * Method swaps two segments of the tracks in song of random length
     * and random start times
     * @param Song song to be mutated
     * */
    @Override
    public void mutate(Song song) {
        ///for(Track track : song.getTracks()){
        List<Track> tracks = song.getTracks();
        for(int i = tracks.size()-1; i >= 0; i--){
            if(Math.random() < getProbability()){ 
                Track track = tracks.get(i);
               //Retrieve the tracks end time
               double trackEndTime = track.getPart().getEndTime();
               //Calculate swap segments length             
               double swapLength = segmentValue(0.25, trackEndTime/2);
               //Calculate first swap start time
               double firstSwap = segmentValue(0, trackEndTime - (swapLength * 2));  
               //Calculate second swap start time
               double secondSwap = (firstSwap + (swapLength * 2) == trackEndTime) 
                            ? firstSwap + swapLength
                            : segmentValue(firstSwap + swapLength, trackEndTime - swapLength);

               //Swap segments in track
               Track trackWithSwap = swapSegments(track, firstSwap, secondSwap, swapLength);      
               //Tag new track with swaped segments with same tag old track
               trackWithSwap.setTag(track.getTag());
               //Remove old track from song
               song.removeTrack(track);
               //Add new track with swaped segments
               song.addTrack(trackWithSwap);
            }
        }
    }
    
    
    /**
     * Swaps two segments of the track 
     * @param track Track to swap segments in
     * @param first Start value of the first swap segment
     * @param second Start value of the second swap segment
     * @param length Length of the segments to swap
     * @return Track The new track with swaped segments
     * */
    private Track swapSegments(Track track, double first, double second, double length){
        //Retrive tracks end time
        double trackEndTime = track.getPart().getEndTime();
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
            tracks.add(track.getSegment(0, first));
        }
        //Add second swap segment track to list
        tracks.add(track.getSegment(second, length));
        //If their is a segment between first and second, add this to segment track list
        if(second - (first + length) > 0){
            tracks.add(track.getSegment(first+length, second - (first+length)));
        } 
        //Add first segment track to list
        tracks.add(track.getSegment(first, length));
        /*If second swap start time added by lenght is less than track end time
        add last segment track to list*/
        tracks.add(track.getSegment(second+length, trackEndTime));
 
        
        //Return appended tracks 
        return appendTracks(tracks);
    }
    
    
    /**
     * Calculates a random value between start and end time where end time is 
     * rounded to closest 1/4 of a beat (.25, .5, .75 or whole beat)
     * @param start Start of the intervall to generate random value from
     * @param end End of the intervall to generate random value from
     * @return double Random generated value between start and end intervall
     * */
    private double segmentValue(double start, double end){
        return Math.round(randomDouble(start, Math.floor(end))*4)/4f;
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
     * Append tracks starting with the first in list
     * @param tracks List of tracks to be appended on to the first in the list
     * @Return Track the new track of appended tracks from the list 
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
     * Append a track on to another track
     * @param track track to append on
     * @param other the track to append
     * */
    private void appendTrack(Track track, Track other){
        track.getPart().addPhraseList(other.getPart().getPhraseArray());
    }
}

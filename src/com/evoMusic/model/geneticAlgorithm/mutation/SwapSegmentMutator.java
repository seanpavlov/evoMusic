package com.evoMusic.model.geneticAlgorithm.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

public class SwapSegmentMutator extends ISubMutator{

    public SwapSegmentMutator(double mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public void mutate(Song song) {
        for(Track track : song.copy().getTracks()){
            if(Math.random() < getProbability()){ 
               double trackEndTime = track.getPart().getEndTime();
               double swapLength = segmentValue(0.25, trackEndTime/2);
               double firstSwap = segmentValue(0, trackEndTime - (swapLength * 2));   
               double secondSwap = (firstSwap + (swapLength * 2) == trackEndTime) 
                            ? firstSwap + swapLength
                            : segmentValue(firstSwap + swapLength, trackEndTime - swapLength);

               
               Track trackWithSwap = swapSegments(track, firstSwap, secondSwap, swapLength);            
               trackWithSwap.setTag(track.getTag());
               song.removeTrack(track);
               song.addTrack(trackWithSwap);
            }
        }
    }
    
    private Track swapSegments(Track track, double first, double second, double length){
        double trackEndTime = track.getPart().getEndTime();
        if(second >= trackEndTime || second + length > trackEndTime){
            return track;
        }
        List<Track> tracks = new ArrayList<Track>();
        
        if(first > 0){
            tracks.add(track.getSegment(0, first));
        }
        tracks.add(track.getSegment(second, length));
        if(second - (first + length) > 0){
            tracks.add(track.getSegment(first+length, second - (first+length)));
        } 
        tracks.add(track.getSegment(first, length));
        tracks.add(track.getSegment(second+length, track.getPart().getEndTime()));
        
        return appendTracks(tracks);
    }
    
    
    
    private double segmentValue(double start, double end){
        return Math.round(randomDouble(start, Math.floor(end))*4)/4f;
     }
    
    private double randomDouble(double start, double end){
        double random = new Random().nextDouble();
        return start + (random *(end - start));
    }
    
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
    
    private void appendTrack(Track track, Track other){
        track.getPart().addPhraseList(other.getPart().getPhraseArray());
    }
    
    

}

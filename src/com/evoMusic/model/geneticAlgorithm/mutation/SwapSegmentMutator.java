package com.evoMusic.model.geneticAlgorithm.mutation;

import jm.music.data.Note;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;

public class SwapSegmentMutator extends ISubMutator{

    public SwapSegmentMutator(double mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public void mutate(Song song) {
        
        Track track = song.getTracks().get(0);    
        Track swapTrack = swapSegments(track, 0.0, 7.0, 0.5);       
        swapTrack.setTag(track.getTag());       
        song.removeTrack(track);
        song.addTrack(swapTrack);
    }
    
    private Track swapSegments(Track track, double first, double second, double length){
        double endTime = track.getPart().getEndTime();
        if(second >= endTime || second + length > endTime){
            return track;
        }
        
        Track firstSegment = track.getSegment(0, first);
        
        Track firstSwapSegment = track.getSegment(first, length);
        
        Track secondSwapSegment = track.getSegment(second, length);
        
        Track lastSegment = track.getSegment(second+length, track.getPart().getEndTime());
        
        firstSegment.insert(secondSwapSegment, first);
    
        if(second - (first + length) > 0){
            Track betweenSegment = track.getSegment(first+length, second - (first+length));  
            firstSegment.insert(betweenSegment, first+length);          
        }
        
        firstSegment.insert(firstSwapSegment, second);
        firstSegment.insert(lastSegment, second+length); 
            
        return firstSegment;
    }

}

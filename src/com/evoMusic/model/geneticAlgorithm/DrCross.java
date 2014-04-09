package com.evoMusic.model.geneticAlgorithm;
import java.util.ArrayList;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.Sort;

public class DrCross {
    private int splits;
    
    public DrCross(int splits){
        this.splits = splits;
    }
    
    public List<Song> crossIndividuals(List<Song> parents){
        List<Song> children = generateChildren(parents);
        return children;
    }

    private List<Song> generateChildren(List<Song> parents) {
        List<Song> children = new ArrayList<Song>();
        
        for (Song parent : parents){
            
            int nrOfTracks = parent.getNbrOfTracks();
            
            for (int i = 0; i < nrOfTracks; i++){
                Track t = parent.getTrack(i);
                Part part = t.getPart();
                List<List<Note>> massmördare = Sort.getSortedNoteList(part);
            }
        }
        
        return children;
    }
}

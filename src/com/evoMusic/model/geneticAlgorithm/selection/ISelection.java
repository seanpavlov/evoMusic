package com.evoMusic.model.geneticAlgorithm.selection;

public interface ISelection {

    /**
     * Selects an individual based on the class of selection.
     * 
     * @return the id of the selected song.
     */
    public int select();

}

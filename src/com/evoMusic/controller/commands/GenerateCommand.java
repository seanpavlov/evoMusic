package com.evoMusic.controller.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.evoMusic.controller.AbstractCommand;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ReverseMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ScaleOfFifthMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SimplifyMutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.UserRater;
import com.evoMusic.util.Translator;

public class GenerateCommand extends AbstractCommand {

    private List<Song> selectedSongs;
    
    /**
     * Creates the generate command
     * @param selectedSongs
     *              reference to the songs selected to be used for generating
     *              individuals
     */
    public GenerateCommand(List<Song> selectedSongs) {
        this.selectedSongs = selectedSongs;
    }
    
    @Override
    public boolean execute(String[] args) {
        if(selectedSongs.size() < 2) {
            return false;
        }
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
        allMut.add(new OctaveMutator(0.1, 1));
        allMut.add(new ReverseMutator(0.1, 4, 4, true));
        allMut.add(new ScaleOfFifthMutator(0.1, 3));
        allMut.add(new SimplifyMutator(0.1, 4, 0.1));
        List<SubRater> subRaters = new LinkedList<SubRater>();
        subRaters.add(new UserRater(1));
        GeneticAlgorithm ga = new GeneticAlgorithm(selectedSongs, new Mutator(allMut, 1), new Crossover(20), new Rater(subRaters));
        ga.setMinimumIterations(10);
        ga.iterate();
        Translator.INSTANCE.playSong(ga.getBest());
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
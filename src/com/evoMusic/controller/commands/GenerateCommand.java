package com.evoMusic.controller.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.evoMusic.controller.AbstractCommand;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRepetionRater;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.Translator;
import com.google.common.collect.Sets;

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
        int iterations = Integer.parseInt(args[0]);
        List<ISubMutator> allMut = new ArrayList<ISubMutator>();
//        allMut.add(new RandomNoteMutator(0.5, 12));
//        allMut.add(new OctaveMutator(0, 1));
//        allMut.add(new ReverseMutator(0, 4, 4, true));
//        allMut.add(new ScaleOfFifthMutator(0, 3));
//        allMut.add(new SimplifyMutator(0, 4, 0.1));
        List<SubRater> subRaters = new LinkedList<SubRater>();
        subRaters.add(new MelodyRepetionRater(1));
        subRaters.add(new ScaleWhizz(1));
        subRaters.add(new BeatRepetitionRater(1));
        Crossover crossover = new Crossover(4);
        crossover.setMinDuration(50);
        crossover.setMaxDuration(200);
        
        GeneticAlgorithm ga = new GeneticAlgorithm(selectedSongs, new Mutator(allMut, .1), crossover, new Rater(subRaters));
        ga.setMinimumIterations(iterations);
        System.out.println("Start iterating");
        ga.iterate();
        Translator.INSTANCE.saveSongToMidi(ga.getBest(), "best");
        return true;
    }
    
    @Override
    public Set<String> getArgSet() {
        return Sets.newHashSet("numberOfIterations");
    }

}
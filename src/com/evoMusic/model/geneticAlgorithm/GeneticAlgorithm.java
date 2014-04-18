package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.selection.RouletteWheelSelection;

public class GeneticAlgorithm {
    private List<Song> originalSongs;
    private List<Individual> bestIndividuals;
    private Mutator mutator;
    private DrCross crossover;
    private Rater rater;
    private RouletteWheelSelection rwSelect;
    int populationSize, nbrOfGenerations, nbrOfBestIndividuals,
            nbrOfCrossoverIndividuals;
    double ratingThreshold;

    public GeneticAlgorithm(List<Song> inputSongs, Mutator mutator,
            DrCross crossover, Rater rater, int populationSize,
            int nbrOfBestIndividuals, int nbrOfCrossoverIndividuals,
            int nbrOfGenerations) {
        originalSongs = inputSongs;
        this.mutator = mutator;
        this.crossover = crossover;
        this.rater = rater;
        this.populationSize = populationSize;
        this.nbrOfGenerations = nbrOfGenerations;
        this.nbrOfBestIndividuals = nbrOfBestIndividuals;
        this.nbrOfCrossoverIndividuals = nbrOfCrossoverIndividuals;
        bestIndividuals = new ArrayList<Individual>();

        // TODO: Initialize weight based on input songs. Decide how we are going
        // to do this.

        // TODO: Fix generating first generation
        List<Song> firstGeneration = generateFirstGeneration();

        List<Individual> ratedFirstGeneration = ratePopulation(firstGeneration);

        selectBestIndividuals(ratedFirstGeneration);

        List<Song> tempPopulation;
        List<Individual> population;
        
        for (int generation = 0; generation < nbrOfGenerations; generation++) {
            tempPopulation = new ArrayList<Song>();
            population = new ArrayList<Individual>();

            // Generate individuals - crossover on best population
            // Rate them and put in best population if better
            List<Song> tempList = new ArrayList<Song>();
            crossover.setParents(individualToSong(bestIndividuals));
            for(int i = 0; i < populationSize; i++){
                if(tempList.size() == 0){
                    tempList = crossover.crossIndividuals();
                }
                tempPopulation.add(tempList.remove(0));
            }
            population = ratePopulation(tempPopulation);
            selectBestIndividuals(population);

            // Select individuals to be crossovered
            tempPopulation = new ArrayList<Song>();
            tempList = new ArrayList<Song>();
            rwSelect = new RouletteWheelSelection(population);
            for(int i = 0; i < populationSize; i++){
                if(tempList.size() == 0){
                    for(int j = 0; j < nbrOfCrossoverIndividuals; j++){
                        tempList.add(population.get(rwSelect.select()).getSong());
                    }
                    crossover.setParents(tempList);
                    tempList = crossover.crossIndividuals();
                }
                tempPopulation.add(tempList.remove(0));
                mutator.mutate(tempPopulation.get(i));
            }
            population = ratePopulation(tempPopulation);
            selectBestIndividuals(population);
        }
    }

    private List<Song> generateFirstGeneration() {
        // TODO: Insert markov initialization here

        for (int individual = 0; individual < populationSize; individual++) {
            // TODO: Insert generate individual and put in list here
            // population[individual] = markov
        }
        return null;
    }

    private List<Individual> ratePopulation(List<Song> population) {
        List<Individual> ratedPopulation = new ArrayList<Individual>();
        for (int individual = 0; individual < populationSize; individual++) {
            ratedPopulation.add(new Individual(population.get(individual),
                    rater.rate(population.get(individual))));
        }
        return ratedPopulation;
    }

    private void selectBestIndividuals(List<Individual> population) {
        for (int individual = 0; individual < populationSize; individual++) {
            check_if_better_loop: for (int bestIndividual = 0; bestIndividual < nbrOfBestIndividuals; bestIndividual++) {
                if (population.get(individual).getRating() > bestIndividuals
                        .get(bestIndividual).getRating()) {
                    for (int shift = nbrOfBestIndividuals; shift > bestIndividual; shift--) {
                        bestIndividuals.set(shift,
                                bestIndividuals.get(shift - 1));
                    }
                    bestIndividuals.set(bestIndividual,
                            population.get(individual));
                    break check_if_better_loop;
                }
            }
        }
    }
    
    private List<Song> individualToSong(List<Individual> individuals){
        List<Song> tempList= new ArrayList<Song>();
        for(int i = 0; i < individuals.size(); i++){
            tempList.add(individuals.get(i).getSong());
        }
        return tempList;
    }
}
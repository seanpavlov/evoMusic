package com.evoMusic.model.geneticAlgorithm.selection;

import java.util.List;

import com.evoMusic.model.geneticAlgorithm.Individual;

public class RouletteWheelSelection implements ISelection {
    private int nbrOfIndividuals;
    private double[] probabilityList;

    /**
     * Calculate the probability of being selected by giving each song the
     * probability of how big part the fitness value is compared to the sum of
     * fitness values on all songs.
     * 
     * @param populationRating is the list of ratings
     */
    public RouletteWheelSelection(List<Individual> populationRating) {
        double sumOfAllFitness = 0;
        nbrOfIndividuals = populationRating.size();
        for (int individual = 0; individual < nbrOfIndividuals; individual++) {
            sumOfAllFitness += populationRating.get(individual).getRating();
        }
        probabilityList = new double[nbrOfIndividuals];
        for (int i = 0; i < nbrOfIndividuals; i++) {
            probabilityList[i] = populationRating.get(i).getRating() / sumOfAllFitness;
        }
    }

    public int select() {
        double rouletteProbability = Math.random();
        double currentProbability = 0;
        for (int i = 0; i < nbrOfIndividuals; i++) {
            currentProbability += probabilityList[i];
            if (currentProbability > rouletteProbability) {
                return i;
            }
        }
        return (nbrOfIndividuals - 1);
    }
}

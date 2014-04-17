package com.evoMusic.model.geneticAlgorithm.selection;

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
    public RouletteWheelSelection(double[] populationRating) {
        double sumOfAllFitness = 0;
        nbrOfIndividuals = populationRating.length;
        for (int individual = 0; individual < nbrOfIndividuals; individual++) {
            sumOfAllFitness += populationRating[individual];
        }
        probabilityList = new double[nbrOfIndividuals];
        for (int i = 0; i < nbrOfIndividuals; i++) {
            probabilityList[i] = populationRating[i] / sumOfAllFitness;
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

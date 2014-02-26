package com.evoMusic.controller;

import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;

public class GAHandler implements Runnable {

    private GeneticAlgorithm geneticAlgorithm;

    public GAHandler() {
    }

    public void setGA(GeneticAlgorithm ga) {
        this.geneticAlgorithm = ga;
    }

    @Override
    public void run() {
        // Temporary run method.

        // geneticAlgorithm.iterate(numberOfIterations);
        while (true) {
            System.out.println("Running GAHandler");
        }

    }

}

package com.evoMusic.model.geneticAlgorithm.rating;

import com.evoMusic.model.Song;

public abstract class SubRater {
    private double targetRating;
    private double influence = 1.0;
    private double influenceMultiplier = 1.0;

    /**
     * Rates a song or a part of it depending on the raters task.
     * 
     * @param song
     *            to be rated
     * @return rate value between 0 and 1
     */
    abstract public double rate(Song song);

    /**
     * @return the current weight of this rater
     */
    public double getTargetRating() {
        return targetRating;
    }

    /**
     * @return the influence of this sub-rater.
     */
    public double getInfluence() {
        return influence;
    }

    /**
     * @return the influence multiplier of this sub-rater.
     */
    public double getInfluenceMultiplier() {
        return influenceMultiplier;
    }

    /**
     * Set the weight of this rater, value must be between 0 and 1
     */
    public void setTargetRating(double targetRating) {
        if (targetRating < 0.0 || targetRating > 1.0) {
            throw new IllegalArgumentException(
                    "Target rating must be a value between 0 and 1");
        } else {
            this.targetRating = targetRating;
        }
    }

    /**
     * Sets the influence of this sub-rater. This determines how much this rater
     * will affect the final rating.
     * 
     * @param influence
     */
    public void setInfluence(double influence) {
        if (influence < 0.0 || influence > 1.0) {
            throw new IllegalArgumentException(
                    "Influence must be a value between 0 and 1");
        } else {
            this.influence = influence;
        }
    }

    /**
     * Sets the influence multiplier of this sub-rater. This affects the
     * influence which decides how much this rater will affect the final rating.
     * 
     * @param influenceMultiplier
     */
    public void setInfluenceMultiplier(double influenceMultiplier) {
        if (influenceMultiplier < 0.0 || influenceMultiplier > 1.0) {
            throw new IllegalArgumentException(
                    "Influence multiplier must be a value between 0 and 1");
        } else {
            this.influenceMultiplier = influenceMultiplier;
        }
    }
}

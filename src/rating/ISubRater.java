package rating;

import model.Song;

public interface ISubRater {
    
    /**
     * Rates a song or a part of it depending on the raters task.
     * 
     * @param song to be rated
     * @return rate value between 0 and 1
     */
    public double rate(Song song);
    
    /**
     * @return the current weight of this rater
     */
    public double getWeight();
    
    /**
     * Set the weight of this rater, value must be between 0 and 1
     */
    public void setWeight(double weight);
    
    /**
     * If the weight is 0, the rater should not be included.
     * 
     * @return if this weight is 0
     */
    public boolean shouldRate();
}

 package rating;
  
 import java.util.ArrayList;
 import java.util.List;
 
 import model.Song;
  
 public class Rater {
     List<ISubRater> subraters = new ArrayList<ISubRater>(); 
     
     /**
      * Initialize all raters with 0 weights
      */
     public Rater() {
 
     }
 
     
     /**
      * rates a song with the help of all subraters in the list
      * 
      * @param song to be rater
      * @return combined rating of the song
      */
     public double rate(Song song) {
         
         double rating = 0.0;
         double weightAvg = 0.0;
         for (ISubRater subRater : subraters){
             weightAvg += subRater.getWeight();
             if (subRater.shouldRate()){
                 rating += subRater.rate(song) * subRater.getWeight();
             }
         }
         rating = rating / subraters.size();
         weightAvg = weightAvg / subraters.size();
         rating = rating / weightAvg;
         return rating;
      }
     
     /**
      * Add a Subrater to the list of raters
      */
     public void addSubRater(ISubRater isub){
         this.subraters.add(isub);
     }
     
     /**
      * Takes a list of songs and initiate the raters weights depending on the
      * rating of each song.
      * 
      * @param songs songs to initiate the rating
      */
     public void initSubRaterWeights(Song[] songs){
         double rating;
         for (ISubRater is : subraters){
             rating = 0;
             for (Song s : songs){
                 rating += is.rate(s);
             }
             rating = rating / songs.length;
             is.setWeight(rating);
         }
     }
 }
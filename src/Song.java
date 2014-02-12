import jm.music.data.Score;


public class Song extends Score {
	
	
	/**
	 * Creates a new Song object by copying all content from the given score.
	 * 
	 * @param score, where all musical notation will be copied from.
	 */
	public Song(Score score) {
		this.addPartList(score.copy().getPartArray());
	}
	
}

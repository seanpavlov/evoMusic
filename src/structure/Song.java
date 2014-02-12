package structure;
import jm.music.data.Score;

/**
 * A class that inherits most of its properties from the Score class.
 *
 */
public class Song extends Score {
	
	/**
	 * Don't know about this one >_>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Song object by copying all content from the given score.
	 * 
	 * @param score, where all musical notation will be copied from.
	 */
	public Song(Score score) {
		this.addPartList(score.copy().getPartArray());
	}
	
}

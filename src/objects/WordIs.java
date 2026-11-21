package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma palavra IS.
 */
public class WordIs extends GameObject implements Pushable{
    public WordIs(Point2D position) {
        super(position, "word-is", 15);
    }
}

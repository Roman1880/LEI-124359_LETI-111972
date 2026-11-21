package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma palavra BOX.
 */
public class WordBox extends GameObject implements Pushable{
    public WordBox(Point2D position) {
        super(position, "word-box", 15);
    }
}

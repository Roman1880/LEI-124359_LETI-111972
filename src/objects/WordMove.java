package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma palavra MOVE.
 */
public class WordMove extends  GameObject implements Pushable{
    public WordMove(Point2D p) {
        super(p, "word-move", 15);
    }
}

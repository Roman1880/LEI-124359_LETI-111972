package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma palavra STOP.
 */
public class WordStop extends GameObject implements Pushable{
    public WordStop(Point2D position) {
        super(position, "word-stop", 15);
    }
}

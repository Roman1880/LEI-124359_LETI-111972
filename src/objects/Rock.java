package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma pedra.
 */
public class Rock extends  GameObject implements Pushable{
    public Rock(Point2D position) {
        super(position, "rock", 15);
    }
}

package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma bandeira.
 */
public class Flag extends GameObject{
    public Flag(Point2D position) {
        super(position, "flag", 15);
    }

    @Override
    public boolean isTransposable() {
        return true;
    }
}

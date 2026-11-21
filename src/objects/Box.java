package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma caixa.
 */
public class Box extends  GameObject{
    public Box(Point2D position) {
        super(position, "box", 15);
    }

    @Override
    public boolean isTransposable() {
        return true;
    }
}

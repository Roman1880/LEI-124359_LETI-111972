package objects;

import pt.iscte.poo.utils.Point2D;

public class Grass extends GameObject{
    public Grass(Point2D position) {
        super(position, "grass", 5);
    }

    @Override
    public boolean isTransposable() {
        return true;
    }
}

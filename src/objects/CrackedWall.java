package objects;

import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.gui.ImageTile;

/**
 * Representa uma parede rachada transponivel.
 */
public class CrackedWall extends GameObject{

    public CrackedWall(Point2D position) {
        super(position, "cracked_wall", 10);
    }

    @Override
    public boolean isTransposable() {
        return true;
    }

}

package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma parede transponivel.
 */
public class CrackedWall extends GameObject {
    public CrackedWall(Point2D position) {
        super(position, "cracked-wall", 10); // Alterado de "cracked_wall" para "cracked-wall"
    }

    public boolean isTransposable() {
        return true;
    }
}
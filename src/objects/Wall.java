package objects;

import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.gui.ImageTile;

/**
 * Representa uma parede normal intransponivel.
 */
public class Wall extends GameObject{

    public Wall(Point2D position) {
        super(position, "wall", 10);
    }
}

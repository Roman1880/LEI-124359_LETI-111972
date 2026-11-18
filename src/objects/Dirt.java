package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa o chao do nivel (Dirt).
 */
public class Dirt extends GameObject {

    /**
     * Construtor do chao.
     *
     * @param position Posicao na grelha
     */

    public Dirt(Point2D position) {
        super(position, "dirt", 1);
    }

    @Override
    public boolean isTransposable() {
        return true;
    }

}

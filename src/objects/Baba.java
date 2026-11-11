package objects;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

/**
 * Representa a personagem principal (Baba).
 */
public class Baba extends GameObject {

    /**
     * Construtor da Baba.
     *
     * @param position Posicao inicial na grelha
     */
    public Baba(Point2D position) {
        super(position, "baba-d", 25);
    }

    /**
     * Move a personagem na direcao especificada e atualiza a imagem.
     *
     * @param d Direcao do movimento (UP, DOWN, LEFT, RIGHT)
     */
    public void move(Direction d) {
        Point2D new_pos = getPosition().plus(d.asVector());
        setPosition(new_pos);
        setName("baba-" + d.name().toLowerCase().charAt(0));
    }
}
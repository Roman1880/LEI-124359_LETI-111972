package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa a palavra MOVE no tabuleiro.
 */
public class WordMove extends WordObject {

    /**
     * Construtor da palavra MOVE.
     *
     * @param position Posicao inicial na grelha
     */
    public WordMove(Point2D position) {
        super(position, "word-move");
    }
}
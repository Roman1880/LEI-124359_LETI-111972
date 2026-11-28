package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa a palavra BOX no tabuleiro.
 */
public class WordBox extends WordObject {

    /**
     * Construtor da palavra BOX.
     *
     * @param position Posicao inicial na grelha
     */
    public WordBox(Point2D position) {
        super(position, "word-box");
    }
}
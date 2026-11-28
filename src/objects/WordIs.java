package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa a palavra IS no tabuleiro.
 */
public class WordIs extends WordObject {

    /**
     * Construtor da palavra IS.
     *
     * @param position Posicao inicial na grelha
     */
    public WordIs(Point2D position) {
        super(position, "word-is");
    }
}
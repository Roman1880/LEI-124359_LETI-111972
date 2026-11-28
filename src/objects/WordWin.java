package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa a palavra WIN no tabuleiro.
 */
public class WordWin extends WordObject {

    /**
     * Construtor da palavra WIN.
     *
     * @param position Posicao inicial na grelha
     */
    public WordWin(Point2D position) {
        super(position, "word-win");
    }
}
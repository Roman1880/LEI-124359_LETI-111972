package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa a palavra STOP no tabuleiro.
 */
public class WordStop extends WordObject {

    /**
     * Construtor da palavra STOP.
     *
     * @param position Posicao inicial na grelha
     */
    public WordStop(Point2D position) {
        super(position, "word-stop");
    }
}
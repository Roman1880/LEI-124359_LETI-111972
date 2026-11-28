package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Classe abstrata base para todos os objetos-palavra do jogo.
 * Todos os objetos-palavra podem ser empurrados e nao sao transponiveis.
 */
public abstract class WordObject extends GameObject implements Pushable {

    private static final int WORD_LAYER = 15;

    /**
     * Construtor de um objeto-palavra.
     *
     * @param position Posicao inicial na grelha
     * @param imageName Nome da imagem associada a palavra
     */
    public WordObject(Point2D position, String imageName) {
        super(position, imageName, WORD_LAYER);
    }
}
package objects;

import pt.iscte.poo.utils.Direction;

/**
 * Interface que identifica os objetos que podem ser empurrados pela personagem.
 */
public interface Pushable {
    /**
     * Empurra o objeto na direcao indicada.
     *
     * @param d Direcao do movimento
     */
    void push (Direction d);
}

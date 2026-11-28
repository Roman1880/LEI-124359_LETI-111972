package objects;

import pt.iscte.poo.utils.Direction;

/**
 * Interface que identifica os objetos que podem ser empurrados pela personagem.
 */
public interface Pushable {

    /**
     * Indica se o objeto pode ser empurrado no estado atual do jogo.
     *
     * @return true se puder ser empurrado, false caso contrario
     */
    default boolean canBePushed() {
        return true;
    }

    /**
     * Empurra o objeto uma unidade na direcao indicada.
     *
     * @param d Direcao do movimento
     */
    default void push(Direction d) {
        if (this instanceof GameObject) {
            GameObject obj = (GameObject) this;
            obj.setPosition(obj.getPosition().plus(d.asVector()));
        }
    }
}
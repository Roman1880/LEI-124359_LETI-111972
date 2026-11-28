package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa um buraco no mapa.
 * Mata a personagem se nao estiver tapado, destroi objetos sobrepostos
 * e pode ser tapado por uma Box.
 */
public class Hole extends GameObject{
    private boolean covered;
    /**
     * Construtor do buraco.
     *
     * @param position Posicao inicial na grelha
     */
    public Hole(Point2D position) {
        super(position, "hole", 8);
    }

    /**
     * Indica se o buraco ja foi tapado por uma caixa.
     *
     * @return true se estiver tapado, false caso contrario
     */
    public boolean isCovered() {
        return covered;
    }
    /**
     * Marca o buraco como tapado (por exemplo, quando uma Box cai nele).
     */
    public void cover() {
        this.covered = true;
    }

    /**
     * O buraco e transponivel para que o jogador ou objetos possam entrar nele.
     *
     * @return true
     */
    @Override
    public boolean isTransposable() {
        return true;
    }
}

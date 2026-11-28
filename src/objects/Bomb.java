package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma bomba no tabuleiro.
 * Pode ser empurrada e acende o rastilho apenas quando empurrada diretamente pelo jogador,
 * explodindo apos 10 ticks.
 */
public class Bomb extends GameObject implements Pushable {

    private static final int FUSE_TICKS = 10;

    private boolean lit;
    private int ticksRemaining;

    /**
     * Construtor da bomba.
     *
     * @param position Posicao inicial na grelha
     */
    public Bomb(Point2D position) {
        super(position, "bomb",15);
        this.lit = false;
        this.ticksRemaining = FUSE_TICKS;
    }

    /**
     * Acende o rastilho da bomba (chamado apenas quando o jogador a empurra diretamente).
     */
    public void ignite() {
        if (!lit) {
            this.lit = true;
            this.ticksRemaining = FUSE_TICKS;
        }
    }

    /**
     * Indica se o rastilho da bomba ja esta aceso.
     *
     * @return true se estiver acesa, false caso contrario
     */
    public boolean isLit() {
        return lit;
    }

    /**
     * Avanca um tick no temporizador da bomba caso o rastilho esteja aceso.
     *
     * @return true se a bomba atingiu 0 ticks e deve explodir, false caso contrario
     */
    public boolean tick() {
        if (!lit) {
            return false;
        }
        ticksRemaining--;
        return ticksRemaining <= 0;
    }
}
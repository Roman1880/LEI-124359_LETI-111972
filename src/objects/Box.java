package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma caixa no tabuleiro.
 * O seu comportamento varia consoante as regras ativas (BOX IS STOP, BOX IS MOVE)
 * e pode tapar buracos (Hole) tornando-se fixa e transponivel.
 */
public class Box extends GameObject implements Pushable {

    private static boolean movableRule = false;
    private static boolean stopRule = false;

    private boolean inHole;

    public Box(Point2D position) {
        super(position, "box",15);
        this.inHole = false;
    }

    public static void setRules(boolean isMove, boolean isStop) {
        movableRule = isMove;
        stopRule = isStop;
    }

    public boolean isInHole() {
        return inHole;
    }

    public void setInHole(boolean inHole) {
        this.inHole = inHole;
    }

    @Override
    public boolean canBePushed() {
        if (inHole) {
            return false;
        }
        return movableRule;
    }

    @Override
    public boolean isTransposable() {
        if (inHole) {
            return true;
        }
        return !stopRule && !movableRule;
    }
}
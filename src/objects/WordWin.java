package objects;

import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma palavra WIN.
 */
public class WordWin extends GameObject implements  Pushable{
    public WordWin(Point2D p){
        super(p, "word-win", 15);
    }
}

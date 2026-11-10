package objects;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;


/**
 * Classe abstrata base para todos os objetos do jogo.
 * Implementa o interface ImageTile para integrar com a GUI.
 */
public abstract class GameObject implements ImageTile {

    private Point2D position;
    private String name;
    private int layer;

    /**
     * Construtor da classe GameObject.
     *
     * @param position Posicao inicial do objeto na grelha
     * @param name Nome da imagem associada ao objeto
     * @param layer Camada de desenho do objeto
     */

    public GameObject(Point2D position, String name, int layer) {
        this.position = position;
        this.name = name;
        this.layer = layer;
    }

    /**
     * Retorna a posicao atual do objeto.
     *
     * @return Posicao Point2D
     */

    public Point2D getPosition() {
        return position;
    }

    /**
     * Atualiza a posicao do objeto.
     *
     * @param position Nova posicao Point2D
     */

    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Retorna o nome da imagem associada ao objeto.
     *
     * @return Nome da imagem
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna a camada de desenho do objeto.
     *
     * @return Numero da camada
     */

    public int getLayer() {
        return layer;
    }

    /**
     * Atualiza o nome da imagem do objeto.
     *
     * @param name Novo nome da imagem
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indica se o objeto pode ser atravessado pela personagem.
     *
     * @return true se for transponivel, false caso contrario
     */

    public boolean isTransposable() {
        return false;
    }
}
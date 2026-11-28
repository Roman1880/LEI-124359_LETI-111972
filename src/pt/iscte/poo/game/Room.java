package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import objects.Baba;
import objects.GameObject;
import objects.Hole;
import objects.Pushable;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

/**
 * Representa uma sala ou nivel do jogo, contendo todos os objetos
 * e o jogador principal (Baba).
 */
public class Room {

    private List<ImageTile> objects;
    private String roomName;
    private Baba player;

    /**
     * Construtor da sala que inicializa a lista de objetos.
     */
    public Room() {
        objects = new ArrayList<>();
    }

    private void setName(String name) {
        roomName = name;
    }

    /**
     * Devolve o nome do ficheiro da sala.
     *
     * @return Nome da sala
     */
    public String getName() {
        return roomName;
    }

    /**
     * Adiciona um objeto grafico a sala.
     *
     * @param obj Objeto a adicionar
     */
    public void addObject(ImageTile obj) {
        objects.add(obj);
    }

    /**
     * Remove um objeto grafico da sala.
     *
     * @param obj Objeto a remover
     */
    public void removeObject(ImageTile obj) {
        objects.remove(obj);
    }

    /**
     * Devolve a lista de todos os objetos presentes na sala.
     *
     * @return Lista de ImageTile da sala
     */
    public List<ImageTile> getObjects() {
        return objects;
    }

    /**
     * Procura um objeto que implemente Pushable e possa ser empurrado
     * numa determinada posicao.
     *
     * @param pos Posicao a verificar
     * @return Objeto Pushable encontrado ou null se nao existir
     */
    public Pushable findPushableAt(Point2D pos) {
        for (ImageTile tile : objects) {
            if (tile instanceof GameObject && tile instanceof Pushable) {
                GameObject obj = (GameObject) tile;
                Pushable pushable = (Pushable) tile;
                if (obj.getPosition().equals(pos) && pushable.canBePushed()) {
                    return pushable;
                }
            }
        }
        return null;
    }

    /**
     * Indica se existe algum objeto Pushable ativo na posicao especificada.
     *
     * @param pos Posicao a verificar
     * @return true se existir um Pushable, false caso contrario
     */
    public boolean hasPushableAt(Point2D pos) {
        return findPushableAt(pos) != null;
    }

    /**
     * Procura um buraco aberto (ainda nao tapado) na posicao indicada.
     *
     * @param pos Posicao a verificar
     * @return Instancia de Hole aberta ou null se nao existir
     */
    public Hole findOpenHoleAt(Point2D pos) {
        for (ImageTile tile : objects) {
            if (tile instanceof Hole) {
                Hole hole = (Hole) tile;
                if (hole.getPosition().equals(pos) && !hole.isCovered()) {
                    return hole;
                }
            }
        }
        return null;
    }

    /**
     * Verifica se todos os objetos presentes na posicao indicada sao transponiveis.
     *
     * @param pos Posicao a verificar
     * @return true se nenhum objeto bloquear a passagem, false caso contrario
     */
    public boolean areObjectsTransposableAt(Point2D pos) {
        for (ImageTile tile : objects) {
            if (tile instanceof GameObject) {
                GameObject obj = (GameObject) tile;
                if (obj.getPosition().equals(pos) && !obj.isTransposable()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Le um ficheiro de nivel e cria a respetiva instancia de Room.
     *
     * @param f Ficheiro de texto com a configuracao da sala
     * @return Nova sala preenchida com os objetos lidos
     */
    public static Room readRoom(File f) {
        Room r = new Room();
        r.setName(f.getName());

        // 1. Usar o RoomLoader para ler o ficheiro .txt correspondente (ex: room0.txt)
        List<ImageTile> tiles = RoomLoader.loadRoom(f.getName());

        // 2. Adicionar os tiles a sala e detetar onde esta o Baba para o definir como player
        for (ImageTile tile : tiles) {
            r.addObject(tile);
            if (tile instanceof Baba) {
                r.player = (Baba) tile;
            }
        }

        return r;
    }

    /**
     * Devolve o jogador (Baba) presente nesta sala.
     *
     * @return Instancia de Baba
     */
    public Baba getPlayer() {
        return player;
    }
}
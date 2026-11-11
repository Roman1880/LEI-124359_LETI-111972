package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import objects.Baba;
import pt.iscte.poo.gui.ImageTile;

public class Room {

    private List<ImageTile> objects;
    private String roomName;
    private Baba player;

    public Room() {
        objects = new ArrayList<>();
    }

    private void setName(String name) {
        roomName = name;
    }

    public String getName() {
        return roomName;
    }

    public void addObject(ImageTile obj) {
        objects.add(obj);
    }

    public List<ImageTile> getObjects() {
        return objects;
    }

    public static Room readRoom(File f) {
        Room r = new Room();
        r.setName(f.getName());

        // 1. Usar o RoomLoader para ler o ficheiro .txt correspondente (ex: room0.txt)
        List<ImageTile> tiles = RoomLoader.loadRoom(f.getName());

        // 2. Adicionar os tiles à sala e detetar onde está o Baba para o definir como player
        for (ImageTile tile : tiles) {
            r.addObject(tile);
            if (tile instanceof Baba) {
                r.player = (Baba) tile;
            }
        }

        return r;
    }

    public Baba getPlayer() {
        return player;
    }
}
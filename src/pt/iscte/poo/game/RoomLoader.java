package pt.iscte.poo.game;

import objects.*;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.gui.ImageTile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class RoomLoader {

    // 1. Mapa de fábricas para os caracteres da grelha (Substitui o switch de carateres)
    private static final Map<Character, Function<Point2D, ImageTile>> TILE_FACTORIES = new HashMap<>();

    // 2. Mapa de fábricas para as entidades do nível (Substitui o switch de entidades)
    private static final Map<String, Function<Point2D, ImageTile>> ENTITY_FACTORIES = new HashMap<>();

    static {
        // Inicializar o mapa da grelha (Módulo de Coleções - Map)
        TILE_FACTORIES.put('X', Wall::new);
        TILE_FACTORIES.put('x', CrackedWall::new);

        // Inicializar o mapa de entidades
        ENTITY_FACTORIES.put("baba", Baba::new);
    }

    public static List<ImageTile> loadRoom(String roomFileName) {
        List<ImageTile> tiles = new ArrayList<>();
        File file = new File("rooms/" + roomFileName);

        try (Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextLine()) return tiles;

            // Ler a linha de dimensões (ex: 21,15)
            scanner.nextLine();

            int y = 0;
            boolean readingGrid = true;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.contains(",")) {
                    readingGrid = false;
                    processEntity(line, tiles);
                    continue;
                }

                if (readingGrid) {
                    // Processar a grelha usando o Map de Coleções em vez de switch
                    for (int x = 0; x < line.length(); x++) {
                        char c = line.charAt(x);
                        Point2D position = new Point2D(x, y);

                        Function<Point2D, ImageTile> factory = TILE_FACTORIES.get(c);
                        if (factory != null) {
                            tiles.add(factory.apply(position));
                        }
                    }
                    y++;
                } else {
                    processEntity(line, tiles);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao ler o ficheiro do nível: " + e.getMessage());
        }

        return tiles;
    }

    private static void processEntity(String line, List<ImageTile> tiles) {
        String[] parts = line.split(",");
        if (parts.length < 3) return;

        String type = parts[0].trim();
        int x = Integer.parseInt(parts[1].trim());
        int y = Integer.parseInt(parts[2].trim());
        Point2D position = new Point2D(x, y);

        // Processar entidades usando o Map de Coleções em vez de switch
        Function<Point2D, ImageTile> factory = ENTITY_FACTORIES.get(type);
        if (factory != null) {
            tiles.add(factory.apply(position));
        }
    }
}
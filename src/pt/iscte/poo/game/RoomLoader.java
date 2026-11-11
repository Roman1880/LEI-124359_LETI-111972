package pt.iscte.poo.game;
import objects.*;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.gui.ImageTile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RoomLoader {

    public static List<ImageTile> loadRoom(String roomFileName) {
        List<ImageTile> tiles = new ArrayList<>();
        File file = new File("rooms/" + roomFileName);

        try (Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextLine()) return tiles;

            // 1. Ler a linha de dimensões (ex: 21,15)
            String dimensions = scanner.nextLine();

            int y = 0;
            boolean readingGrid = true;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // Se a linha tiver vírgulas, passamos para a secção de entidades (ex: baba,2,3,r)
                if (line.contains(",")) {
                    readingGrid = false;
                    processEntity(line, tiles);
                    continue;
                }

                if (readingGrid) {
                    // 2. Processar a grelha de carateres do mapa (X, x, g, etc.)
                    for (int x = 0; x < line.length(); x++) {
                        char c = line.charAt(x);
                        Point2D position = new Point2D(x, y);

                        switch (c) {
                            case 'X': // Parede principal
                                tiles.add(new Wall(position));
                                break;
                            case 'x': // Parede rachada
                                tiles.add(new CrackedWall(position));
                                break;
                            case 'g': // Terra / Chão
                                tiles.add(new Dirt(position));
                                break;
                            case ' ':
                                // Espaço vazio, não adiciona tile
                                break;
                            default:
                                break;
                        }
                    }
                    y++;
                } else {
                    // 3. Processar linhas de entidades adicionais
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

        // Instanciação alinhada com o UML do Checkpoint 1
        switch (type) {
            case "baba":
                tiles.add(new Baba(position));
                break;
            // Outras entidades podem ser adicionadas aqui à medida que avançarmos
        }
    }
}

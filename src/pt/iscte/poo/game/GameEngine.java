package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;

import objects.Baba;
import objects.Box;
import objects.Flag;
import objects.GameObject;
import objects.Pushable;
import objects.WordBox;
import objects.WordIs;
import objects.WordWin;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {

    private static GameEngine INSTANCE = new GameEngine();
    private static final String ROOMS_DIRECTORY = "rooms";

    private Room currentRoom;
    private int lastTickProcessed = 0;

    // Guarda se a regra "BOX IS WIN" esta ativa no estado atual do tabuleiro
    private boolean boxIsWin = false;

    // Impede continuar a processar jogadas depois de o jogador vencer o nivel
    private boolean levelWon = false;

    private GameEngine() {
        currentRoom = Room.readRoom(new File(ROOMS_DIRECTORY + "/room0.txt"));
        // Avalia logo as regras no arranque para saber se BOX IS WIN ja comeca ativa
        checkRules();
        updateGUI();
    }

    public static GameEngine getInstance() {
        return INSTANCE;
    }

    @Override
    public void update(Observed source) {

        if (ImageGUI.getInstance().wasKeyPressed()) {
            int k = ImageGUI.getInstance().keyPressed();

            // So aceita setas de direcao enquanto o nivel ainda nao terminou em vitoria
            if (!levelWon && (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN)) {
                Direction d = Direction.directionFor(k);

                // 1. Tenta mover o Baba (e empurrar um Pushable se houver espaco valido)
                handlePlayerMove(d);

                // 2. Reavalia as regras da sala (ex: BOX IS WIN) apos o movimento
                checkRules();

                // 3. Verifica se o jogador atingiu uma condicao de vitoria
                checkWinCondition();

                // 4. Redesenha a interface com as novas posicoes dos objetos
                updateGUI();
            }
        }
        int t = ImageGUI.getInstance().getTicks();
        while (lastTickProcessed < t) {
            processTick();
        }
        ImageGUI.getInstance().update();
    }

    private void handlePlayerMove(Direction d) {
        Baba player = getPlayer();
        if (player == null || currentRoom == null) {
            return;
        }

        // Posicao imediatamente a frente do Baba na direcao escolhida
        Point2D nextPos = player.getPosition().plus(d.asVector());

        // Nao deixa sair fora dos limites da grelha
        if (!isInsideGrid(nextPos)) {
            return;
        }

        // Verifica se existe algum objeto Pushable na celula seguinte
        Pushable pushableAtNext = findPushableAt(nextPos);
        if (pushableAtNext != null) {
            // Posicao para onde o objeto empurrado iria parar
            Point2D beyondPos = nextPos.plus(d.asVector());

            // So empurra se o destino estiver dentro da grelha, nao tiver outro Pushable
            // e nao estiver bloqueado por um objeto nao transponivel
            if (!isInsideGrid(beyondPos) || hasPushableAt(beyondPos) || !canMoveTo(beyondPos)) {
                return;
            }

            // Empurra o objeto e depois avanca o Baba para a posicao anterior desse objeto
            pushableAtNext.push(d);
            player.move(d);
            return;
        }

        // Se nao ha Pushable a frente, o Baba move-se apenas se a celula for transponivel
        if (canMoveTo(nextPos)) {
            player.move(d);
        }
    }

    // Procura o primeiro objeto Pushable numa dada posicao
    private Pushable findPushableAt(Point2D pos) {
        for (ImageTile tile : currentRoom.getObjects()) {
            // Fazemos cast para GameObject porque ImageTile nao expoe getPosition()
            if (tile instanceof GameObject && tile instanceof Pushable) {
                GameObject obj = (GameObject) tile;
                if (obj.getPosition().equals(pos)) {
                    return (Pushable) tile;
                }
            }
        }
        return null;
    }

    // Atalho booleano para saber se existe algum Pushable numa posicao
    private boolean hasPushableAt(Point2D pos) {
        return findPushableAt(pos) != null;
    }

    // Garante que uma coordenada (x, y) esta dentro das dimensoes da grelha
    private boolean isInsideGrid(Point2D pos) {
        int width = ImageGUI.getInstance().getGridDimension().width;
        int height = ImageGUI.getInstance().getGridDimension().height;
        return pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < width && pos.getY() < height;
    }

    // Varre a grelha a procura da combinacao "BOX IS WIN" na horizontal e na vertical
    private void checkRules() {
        boxIsWin = false;
        if (currentRoom == null) {
            return;
        }

        int width = ImageGUI.getInstance().getGridDimension().width;
        int height = ImageGUI.getInstance().getGridDimension().height;

        // Verificacao horizontal: tres casas seguidas da esquerda para a direita (x, x+1, x+2)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x <= width - 3; x++) {
                if (isBoxIsWinSequence(
                        new Point2D(x, y),
                        new Point2D(x + 1, y),
                        new Point2D(x + 2, y))) {
                    boxIsWin = true;
                    return;
                }
            }
        }

        // Verificacao vertical: tres casas seguidas de cima para baixo (y, y+1, y+2)
        for (int y = 0; y <= height - 3; y++) {
            for (int x = 0; x < width; x++) {
                if (isBoxIsWinSequence(
                        new Point2D(x, y),
                        new Point2D(x, y + 1),
                        new Point2D(x, y + 2))) {
                    boxIsWin = true;
                    return;
                }
            }
        }
    }

    // Confirma se p1 tem WordBox, p2 tem WordIs e p3 tem WordWin
    private boolean isBoxIsWinSequence(Point2D p1, Point2D p2, Point2D p3) {
        return hasTypeAt(p1, WordBox.class)
                && hasTypeAt(p2, WordIs.class)
                && hasTypeAt(p3, WordWin.class);
    }

    // Verifica se existe algum objeto de uma dada classe numa posicao especifica
    private boolean hasTypeAt(Point2D pos, Class<?> clazz) {
        for (ImageTile tile : currentRoom.getObjects()) {
            if (tile instanceof GameObject && clazz.isInstance(tile)) {
                GameObject obj = (GameObject) tile;
                if (obj.getPosition().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Verifica se o Baba venceu o nivel: por tocar numa Flag ou numa Box quando BOX IS WIN esta ativo
    private void checkWinCondition() {
        Baba player = getPlayer();
        if (player == null || levelWon) {
            return;
        }

        Point2D playerPos = player.getPosition();
        boolean onFlag = hasTypeAt(playerPos, Flag.class);
        boolean onWinBox = boxIsWin && hasTypeAt(playerPos, Box.class);

        if (onFlag || onWinBox) {
            levelWon = true;
            ImageGUI.getInstance().setStatusMessage("Vitoria!");
        }
    }

    private void processTick() {
        lastTickProcessed++;
    }

    public void updateGUI() {
        if (currentRoom != null) {
            ImageGUI.getInstance().clearImages();
            ImageGUI.getInstance().addImages(currentRoom.getObjects());
        }
    }

    public Baba getPlayer() {
        return currentRoom.getPlayer();
    }

    // Indica se uma posicao esta dentro da grelha e nao contem nenhum objeto bloqueante
    public boolean canMoveTo(Point2D position) {
        if (!isInsideGrid(position)) {
            return false;
        }
        for (ImageTile tile : currentRoom.getObjects()) {
            if (tile instanceof GameObject) {
                GameObject obj = (GameObject) tile;
                if (obj.getPosition().equals(position) && !obj.isTransposable()) {
                    return false;
                }
            }
        }
        return true;
    }
}
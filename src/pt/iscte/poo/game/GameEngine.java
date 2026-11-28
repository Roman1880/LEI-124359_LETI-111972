package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;

import objects.Baba;
import objects.Bomb;
import objects.Box;
import objects.GameObject;
import objects.Hole;
import objects.Pushable;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

/**
 * Motor principal do jogo (Singleton) que gere o ciclo de jogo,
 * o movimento da personagem e a mudanca de niveis.
 */
public class GameEngine implements Observer {

    private static final String ROOMS_DIRECTORY = "rooms";
    private static GameEngine INSTANCE = new GameEngine();

    private final RuleManager ruleManager = new RuleManager();
    private Room currentRoom;
    private int currentLevel = 0;
    private int lastTickProcessed = 0;
    private boolean gameFinished = false;

    /**
     * Construtor privado do Singleton GameEngine.
     * Carrega o primeiro nivel ao iniciar o jogo.
     */
    private GameEngine() {
        loadLevel(currentLevel);
    }

    /**
     * Devolve a unica instancia de GameEngine.
     *
     * @return Instancia de GameEngine
     */
    public static GameEngine getInstance() {
        return INSTANCE;
    }

    /**
     * Carrega o nivel correspondente ao indice indicado.
     * Se nao existirem mais ficheiros de nivel, termina o jogo com vitoria.
     *
     * @param level Numero do nivel a carregar (ex: 0 para room0.txt)
     */
    private void loadLevel(int level) {
        File roomFile = new File(ROOMS_DIRECTORY + "/room" + level + ".txt");
        if (roomFile.exists()) {
            currentRoom = Room.readRoom(roomFile);
            ruleManager.updateRules(currentRoom);
            updateGUI();
            ImageGUI.getInstance().setStatusMessage("Nivel " + level);
        } else {
            gameFinished = true;
            ImageGUI.getInstance().setStatusMessage("Parabens! Jogo concluido!");
        }
    }

    /**
     * Metodo chamado sempre que ocorre um evento na interface grafica (tecla ou tick).
     *
     * @param source Objeto observado que gerou a notificacao
     */
    @Override
    public void update(Observed source) {
        if (ImageGUI.getInstance().wasKeyPressed()) {
            int k = ImageGUI.getInstance().keyPressed();

            if (!gameFinished && k == KeyEvent.VK_R) {
                loadLevel(currentLevel);
            } else if (!gameFinished && (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT
                    || k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN)) {
                Direction d = Direction.directionFor(k);

                handlePlayerMove(d);
                ruleManager.updateRules(currentRoom);
                checkWinCondition();
                updateGUI();
            }
        }

        int t = ImageGUI.getInstance().getTicks();
        while (lastTickProcessed < t) {
            processTick();
        }
        ImageGUI.getInstance().update();
    }

    /**
     * Gere o movimento do jogador numa dada direcao, incluindo o empurrao
     * de um objeto Pushable caso o espaco seguinte esteja livre.
     *
     * @param d Direcao do movimento
     */
    private void handlePlayerMove(Direction d) {
        Baba player = getPlayer();
        if (player == null || currentRoom == null) {
            return;
        }

        Point2D nextPos = player.getPosition().plus(d.asVector());
        if (!isInsideGrid(nextPos)) {
            return;
        }

        Pushable pushableAtNext = currentRoom.findPushableAt(nextPos);
        if (pushableAtNext != null) {
            Point2D beyondPos = nextPos.plus(d.asVector());
            if (!isInsideGrid(beyondPos) || currentRoom.hasPushableAt(beyondPos) || !canMoveTo(beyondPos)) {
                return;
            }

            if (pushableAtNext instanceof Bomb bomb) {
                bomb.ignite();
            }

            pushableAtNext.push(d);
            handleHoleInteraction(pushableAtNext, beyondPos);
            player.move(d);
            return;
        }

        if (canMoveTo(nextPos)) {
            player.move(d);
        }
    }

    /**
     * Gere a interacao quando um objeto e empurrado para cima de um buraco aberto.
     * Uma caixa tapa o buraco; outros objetos sao destruidos.
     *
     * @param pushedObj Objeto que acabou de ser empurrado
     * @param targetPos Posicao para onde o objeto foi movido
     */
    private void handleHoleInteraction(Pushable pushedObj, Point2D targetPos) {
        Hole openHole = currentRoom.findOpenHoleAt(targetPos);
        if (openHole == null) {
            return;
        }

        if (pushedObj instanceof Box box) {
            openHole.cover();
            box.setInHole(true);
        } else if (pushedObj instanceof GameObject gameObj) {
            currentRoom.removeObject(gameObj);
        }
    }

    /**
     * Verifica se o jogador atingiu uma condicao de vitoria e avanca de nivel.
     */
    private void checkWinCondition() {
        Baba player = getPlayer();
        if (player == null || gameFinished) {
            return;
        }

        if (ruleManager.isWinningPosition(currentRoom, player.getPosition())) {
            currentLevel++;
            loadLevel(currentLevel);
        }
    }

    /**
     * Indica se uma posicao esta dentro da grelha e nao contem nenhum objeto bloqueante.
     *
     * @param position Posicao a verificar
     * @return true se for possivel mover para a posicao, false caso contrario
     */
    public boolean canMoveTo(Point2D position) {
        if (!isInsideGrid(position) || currentRoom == null) {
            return false;
        }

        if (ruleManager.isStopPosition(currentRoom, position)) {
            return false;
        }

        return currentRoom.areObjectsTransposableAt(position);
    }

    private boolean isInsideGrid(Point2D pos) {
        int width = ImageGUI.getInstance().getGridDimension().width;
        int height = ImageGUI.getInstance().getGridDimension().height;
        return pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < width && pos.getY() < height;
    }

    private void processTick() {
        lastTickProcessed++;
    }

    /**
     * Atualiza as imagens apresentadas na interface grafica com os objetos da sala atual.
     */
    public void updateGUI() {
        if (currentRoom != null) {
            ImageGUI.getInstance().clearImages();
            ImageGUI.getInstance().addImages(currentRoom.getObjects());
        }
    }

    /**
     * Devolve a instancia do jogador (Baba) da sala atual.
     *
     * @return Objeto Baba atual
     */
    public Baba getPlayer() {
        return currentRoom.getPlayer();
    }
}
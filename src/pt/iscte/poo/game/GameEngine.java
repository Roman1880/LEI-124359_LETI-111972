package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;

import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

/**
 * Motor principal do jogo (Singleton) que gere o ciclo de jogo,
 * o movimento da personagem, as regras e a mudanca de niveis.
 */
public class GameEngine implements Observer {
    private static final String ROOMS_DIRECTORY = "rooms";

    private Room currentRoom;
    private int currentLevel = 0;
    private int lastTickProcessed = 0;

    // Impede continuar a jogar depois de concluir todos os niveis
    private boolean gameFinished = false;

    private static final Class<?>[] FIRST_WORDS = {
        WordBox.class
    };

    private static final Class<?>[] LAST_WORDS = {
        WordWin.class
    };

    private static final Class<?>[] WINNING_OBJECTS = {
        Box.class
    };

    private static final Class<?>[] DEFAULT_WINNING_OBJECTS = {
        Flag.class
    };

    private boolean[] activeWinningObjects = new boolean[FIRST_WORDS.length];
    private static GameEngine INSTANCE = new GameEngine();

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
            checkRules();
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

            // Tecla R reinicia o nivel atual
            if (!gameFinished && k == KeyEvent.VK_R) {
                loadLevel(currentLevel);
            } else if (!gameFinished && (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT
                    || k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN)) {
                Direction d = Direction.directionFor(k);

                // 1. Tenta mover o Baba (e empurrar um Pushable se houver espaco valido)
                handlePlayerMove(d);

                // 2. Reavalia as regras da sala (ex: BOX IS WIN) apos o movimento
                checkRules();

                // 3. Verifica se o jogador venceu o nivel e avanca para o proximo
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

        // Nao permite sair dos limites da grelha
        if (!isInsideGrid(nextPos)) {
            return;
        }

        // Verifica se existe um objeto Pushable na celula seguinte
        Pushable pushableAtNext = findPushableAt(nextPos);
        if (pushableAtNext != null) {
            Point2D beyondPos = nextPos.plus(d.asVector());

            // So empurra se o destino estiver dentro da grelha, nao tiver outro Pushable e for transponivel
            if (!isInsideGrid(beyondPos) || hasPushableAt(beyondPos) || !canMoveTo(beyondPos)) {
                return;
            }

            pushableAtNext.push(d);
            player.move(d);
            return;
        }

        // Move o jogador se a celula de destino for transponivel
        if (canMoveTo(nextPos)) {
            player.move(d);
        }
    }

    /**
     * Procura um objeto que implemente Pushable numa determinada posicao.
     *
     * @param pos Posicao a verificar
     * @return Objeto Pushable encontrado ou null se nao existir
     */
    private Pushable findPushableAt(Point2D pos) {
        for (ImageTile tile : currentRoom.getObjects()) {
            if (tile instanceof GameObject && tile instanceof Pushable) {
                GameObject obj = (GameObject) tile;
                if (obj.getPosition().equals(pos)) {
                    return (Pushable) tile;
                }
            }
        }
        return null;
    }

    /**
     * Indica se existe algum objeto Pushable na posicao especificada.
     *
     * @param pos Posicao a verificar
     * @return true se existir um Pushable, false caso contrario
     */
    private boolean hasPushableAt(Point2D pos) {
        return findPushableAt(pos) != null;
    }

    /**
     * Verifica se uma coordenada esta dentro dos limites atuais da grelha.
     *
     * @param pos Posicao a validar
     * @return true se estiver dentro da grelha, false caso contrario
     */
    private boolean isInsideGrid(Point2D pos) {
        int width = ImageGUI.getInstance().getGridDimension().width;
        int height = ImageGUI.getInstance().getGridDimension().height;
        return pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < width && pos.getY() < height;
    }

    /**
     * Verifica as regras ativas procurando apenas pelos objetos WordIs
     * e analisando os seus vizinhos imediatos na horizontal e na vertical.
     */
    private void checkRules() {
        activeWinningObjects = new boolean[FIRST_WORDS.length];
        if (currentRoom == null) {
            return;
        }

        for (ImageTile tile : currentRoom.getObjects()) {
            if (tile instanceof WordIs) {
                Point2D isPos = ((GameObject) tile).getPosition();
                checkRule(
                        isPos.plus(Direction.LEFT.asVector()),
                        isPos.plus(Direction.RIGHT.asVector()));
                checkRule(
                        isPos.plus(Direction.UP.asVector()),
                        isPos.plus(Direction.DOWN.asVector()));
            }
        }
    }

    private void checkRule(Point2D nounPosition, Point2D propertyPosition) {
        int nounIndex = getTypeIndexAt(nounPosition, FIRST_WORDS);

        if (nounIndex >= 0 && getTypeIndexAt(propertyPosition, LAST_WORDS) >= 0) { //getTypeIndexAt(propertyPosition, new Class<?>[]{ WordWin.class })
            activeWinningObjects[nounIndex] = true;
        }
    }

    private int getTypeIndexAt(Point2D pos, Class<?>[] types) {
        for (ImageTile tile : currentRoom.getObjects()) {
            if (!(tile instanceof GameObject)) {
                continue;
            }

            GameObject obj = (GameObject) tile;
            if (!obj.getPosition().equals(pos)) {
                continue;
            }

            for (int i = 0; i < types.length; i++) {
                if (types[i].isInstance(obj)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Verifica se existe algum objeto de uma determinada classe numa posicao.
     *
     * @param pos Posicao a verificar
     * @param clazz Classe do objeto procurado
     * @return true se existir um objeto desse tipo na posicao, false caso contrario
     */
    private boolean hasTypeAt(Point2D pos, Class<?> clazz) {
        for (ImageTile tile : currentRoom.getObjects()) {
            if (tile instanceof GameObject) {
                GameObject obj = (GameObject) tile;
                if (obj.getPosition().equals(pos) && clazz.isInstance(obj)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifica se o jogador atingiu uma condicao de vitoria (Flag ou Box com BOX IS WIN)
     * e avanca para o nivel seguinte.
     */
    private void checkWinCondition() {
        Baba player = getPlayer();
        if (player == null || gameFinished) {
            return;
        }

        Point2D playerPos = player.getPosition();
        boolean onDefaultWinningObject =
                hasAnyTypeAt(playerPos, DEFAULT_WINNING_OBJECTS);
        boolean onRuleWinningObject =
                isOnActiveWinningObject(playerPos);

        if (onDefaultWinningObject || onRuleWinningObject) {
            currentLevel++;
            loadLevel(currentLevel);
        }
    }

    private boolean hasAnyTypeAt(Point2D pos, Class<?>[] types) {
        for (Class<?> type : types) {
            if (hasTypeAt(pos, type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnActiveWinningObject(Point2D pos) {
        for (int i = 0; i < activeWinningObjects.length; i++) {
            if (activeWinningObjects[i]
                    && hasTypeAt(pos, WINNING_OBJECTS[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Avanca o contador interno de ticks processados.
     */
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

    /**
     * Indica se uma posicao esta dentro da grelha e nao contem nenhum objeto bloqueante.
     *
     * @param position Posicao a verificar
     * @return true se for possivel mover para a posicao, false caso contrario
     */
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
package pt.iscte.poo.game;

import java.util.Arrays;

import objects.*;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

/**
 * Gere as regras ativas do jogo a partir das frases formadas
 * pelos objetos-palavra presentes na sala atual.
 */
public class RuleManager {

    // Palavras-substantivo (lado esquerdo ou superior do IS)
    private static final Class<?>[] FIRST_WORDS = {
            WordBox.class
    };

    // Objetos do jogo correspondentes a cada palavra em FIRST_WORDS (mesmo indice)
    private static final Class<?>[] TARGET_OBJECTS = {
            Box.class
    };

    // Objetos que dao vitoria por defeito, mesmo sem regras ativas
    private static final Class<?>[] DEFAULT_WINNING_OBJECTS = {
            Flag.class
    };

    // Estados ativos das regras para cada substantivo
    private final boolean[] activeWinningObjects = new boolean[FIRST_WORDS.length];
    private final boolean[] activeStopObjects = new boolean[FIRST_WORDS.length];
    private final boolean[] activeMoveObjects = new boolean[FIRST_WORDS.length];

    /**
     * Reavalia todas as regras ativas da sala procurando pelos objetos WordIs
     * e analisando os seus vizinhos imediatos na horizontal e na vertical.
     *
     * @param room Sala atual onde as regras serao verificadas
     */
    public void updateRules(Room room) {
        Arrays.fill(activeWinningObjects, false);
        Arrays.fill(activeStopObjects, false);
        Arrays.fill(activeMoveObjects, false);

        if (room == null) {
            Box.setRules(false, false);
            return;
        }

        for (ImageTile tile : room.getObjects()) {
            if (tile instanceof WordIs) {
                Point2D isPos = ((GameObject) tile).getPosition();
                checkRule(
                        room,
                        isPos.plus(Direction.LEFT.asVector()),
                        isPos.plus(Direction.RIGHT.asVector()));
                checkRule(
                        room,
                        isPos.plus(Direction.UP.asVector()),
                        isPos.plus(Direction.DOWN.asVector()));
            }
        }

        // Atualiza o estado dinamico da Box (indice 0 corresponde a WordBox)
        Box.setRules(activeMoveObjects[0], activeStopObjects[0]);
    }

    /**
     * Avalia um par de posicoes (substantivo e propriedade) em torno de WordIs
     * e ativa a regra correspondente (WIN, STOP ou MOVE).
     *
     * @param room Sala atual
     * @param nounPosition Posicao do substantivo (esquerda ou cima)
     * @param propertyPosition Posicao da propriedade (direita ou baixo)
     */
    private void checkRule(Room room, Point2D nounPosition, Point2D propertyPosition) {
        int nounIndex = getTypeIndexAt(room, nounPosition, FIRST_WORDS);
        if (nounIndex < 0) {
            return;
        }

        if (hasTypeAt(room, propertyPosition, WordWin.class)) {
            activeWinningObjects[nounIndex] = true;
        } else if (hasTypeAt(room, propertyPosition, WordStop.class)) {
            activeStopObjects[nounIndex] = true;
        } else if (hasTypeAt(room, propertyPosition, WordMove.class)) {
            activeMoveObjects[nounIndex] = true;
        }
    }

    /**
     * Procura um objeto na posicao indicada que pertenca a um dos tipos fornecidos
     * e devolve o indice correspondente no array.
     *
     * @param room Sala atual
     * @param pos Posicao a verificar
     * @param types Array de classes a procurar
     * @return Indice do tipo encontrado ou -1 se nao encontrar nenhum
     */
    private int getTypeIndexAt(Room room, Point2D pos, Class<?>[] types) {
        for (ImageTile tile : room.getObjects()) {
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
     * @param room Sala atual
     * @param pos Posicao a verificar
     * @param clazz Classe do objeto procurado
     * @return true se existir um objeto desse tipo na posicao, false caso contrario
     */
    private boolean hasTypeAt(Room room, Point2D pos, Class<?> clazz) {
        for (ImageTile tile : room.getObjects()) {
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
     * Verifica se existe na posicao um objeto cuja regra correspondente esteja ativa.
     *
     * @param room Sala atual
     * @param pos Posicao a verificar
     * @param activeFlags Array de estados ativos da regra (WIN, STOP ou MOVE)
     * @return true se existir um objeto com a regra ativa, false caso contrario
     */
    private boolean isOnActiveRuleObject(Room room, Point2D pos, boolean[] activeFlags) {
        for (int i = 0; i < activeFlags.length; i++) {
            if (activeFlags[i] && hasTypeAt(room, pos, TARGET_OBJECTS[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indica se a posicao contem algum objeto que permite vencer o nivel
     * (por defeito como Flag ou por regra ativa como BOX IS WIN).
     *
     * @param room Sala atual
     * @param pos Posicao a verificar
     * @return true se a posicao der vitoria, false caso contrario
     */
    public boolean isWinningPosition(Room room, Point2D pos) {
        if (room == null) {
            return false;
        }

        for (Class<?> type : DEFAULT_WINNING_OBJECTS) {
            if (hasTypeAt(room, pos, type)) {
                return true;
            }
        }

        return isOnActiveRuleObject(room, pos, activeWinningObjects);
    }

    /**
     * Indica se a posicao esta bloqueada por um objeto afetado pela regra STOP.
     *
     * @param room Sala atual
     * @param pos Posicao a verificar
     * @return true se existir um objeto com regra STOP ativa, false caso contrario
     */
    public boolean isStopPosition(Room room, Point2D pos) {
        if (room == null) {
            return false;
        }
        return isOnActiveRuleObject(room, pos, activeStopObjects);
    }

    /**
     * Indica se a posicao contem um objeto afetado pela regra MOVE.
     *
     * @param room Sala atual
     * @param pos Posicao a verificar
     * @return true se existir um objeto com regra MOVE ativa, false caso contrario
     */
    public boolean isMovePosition(Room room, Point2D pos) {
        if (room == null) {
            return false;
        }
        return isOnActiveRuleObject(room, pos, activeMoveObjects);
    }
}
package reversi;

import static reversi.MainPanel.*;
import static reversi.MyPanel.MASU;
import static reversi.ReversiSet.*;

public class AI {
    public static final short EASY = 3, NORMAL = 5, DIFFICULT = 7, VERY_DIFFICULT = 9;
    short serchLevel;//先読みレベル
    Thread th;
    private static final int placeValue[][] = {
        {120, -20, 20, 5, 5, 20, -20, 120},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {20, -5, 15, 3, 3, 15, -5, 20},
        {5, -5, 3, 3, 3, 3, -5, 5},
        {5, -5, 3, 3, 3, 3, -5, 5},
        {20, -5, 15, 3, 3, 15, -5, 20},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {120, -20, 20, 5, 5, 20, -20, 120}
    };

    public AI() {
        serchLevel = NORMAL;
    }

    /**
     * 難易度設定
     *
     * @param level 深読みレベル (EASY,NORMAL,DIFFICULT)
     */
    void setLevel(short level) {
        serchLevel = level;
    }

    /**
     * コンピュータの番
     */
    @SuppressWarnings("CallToThreadStopSuspendOrResumeManager")
    void computer() {
        //思考中...の表示開始
        th = new Thread(new Thinking());
        th.start();
//        int temp = minMax(true, serchLevel);//ミニマックス法:返り値は bestI+bestJ*MASU
        int temp = alphaBeta(true, serchLevel, Integer.MIN_VALUE, Integer.MAX_VALUE);//α-β法:返り値は bestI+bestJ*MASU
        int i = temp % MASU, j = temp / MASU;
        //思考中...ストップ
        th.stop();

        Undo undo = new Undo(i, j);
        panel.rev.putOn(i, j, false);
        panel.rev.reverse(undo, false);
        panel.rev.count = panel.rev.getCount();
        panel.rev.endGame();
        panel.rev.nextTurn();
        panel.update(panel.getGraphics());
        if (!panel.rev.canPut() && panel.rev.gameState == PLAY) {//相手(プレイヤー)がパスなら
            panel.drawPass(panel.getGraphics());
            panel.rev.nextTurn();
            panel.update(panel.getGraphics());
            if (!panel.rev.canPut()) {
                panel.rev.passPass = true;
                panel.rev.endGame();
            }
            computer();
        }
    }

    /**
     * ミニマックス法
     *
     * @param flag AIの番かどうか
     * @param level 先読みの手数
     * @return 最終的に(bestI+bestJ*MASU)を返す。子ノードでは、その評価値を返す。
     */
    int minMax(boolean flag, short level) {
        int value,//ノードの評価値
                childValue,//子ノードから伝わってきた評価値
                bestI = 0,
                bestJ = 0;
        if (level == 0 || !panel.rev.canPut()) {
            return valueBoard();
        }
        if (flag) {
            value = Integer.MIN_VALUE;//AIの番では最大の評価値を見つけたい
        } else {
            value = Integer.MAX_VALUE;//AIの番でないときは最小の評価値を見つけたい
        }
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (panel.rev.setable(i, j)) {
                    Undo undo = new Undo(i, j);
                    panel.rev.putOn(i, j, true);
                    panel.rev.reverse(undo, true);
                    panel.rev.nextTurn();
                    childValue = minMax(!flag, (short) (level - 1));//再帰
                    if (flag) {
                        if (childValue > value) {
                            value = childValue;
                            bestI = i;
                            bestJ = j;
                        }
                    } else {
                        if (childValue < value) {
                            value = childValue;
                            bestI = i;
                            bestJ = j;
                        }
                    }
                    panel.rev.undoBoard(undo);
                }
            }
        }
        if (level == serchLevel) {
            return bestI + bestJ * MASU;
        } else {
            return value;
        }
    }

    /**
     * α-β法
     *
     * @param flag AIの番かどうか
     * @param level 先読みの手数
     * @param alpha α値。このノードの評価値はα以上。
     * @param beta β値。このノードの評価値はβ以下。
     * @return 最終的に(bestI+bestJ*MASU)を返す。子ノードでは、その評価値を返す。
     */
    int alphaBeta(boolean flag, short level, int alpha, int beta) {
        int value,//ノードの評価値
                childValue,//子ノードから伝わってきた評価値
                bestI = 0,
                bestJ = 0;
        if (level == 0 || !panel.rev.canPut()) {
            return valueBoard();
        }
        if (flag) {
            value = Integer.MIN_VALUE;//AIの番では最大の評価値を見つけたい
        } else {
            value = Integer.MAX_VALUE;//AIの番でないときは最小の評価値を見つけたい
        }
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (panel.rev.setable(i, j)) {
                    Undo undo = new Undo(i, j);
                    panel.rev.putOn(i, j, true);
                    panel.rev.reverse(undo, true);
                    panel.rev.nextTurn();
                    childValue = alphaBeta(!flag, (short) (level - 1), alpha, beta);//再帰
                    if (flag) {
                        if (childValue > value) {
                            value = childValue;
                            alpha = value;//α値を更新
                            bestI = i;
                            bestJ = j;
                        }
                        if (value > beta) {//βカット
                            panel.rev.undoBoard(undo);
                            return value;
                        }
                    } else {
                        if (childValue < value) {
                            value = childValue;
                            beta = value;//β値を更新
                            bestI = i;
                            bestJ = j;
                        }
                        if (value < alpha) {//αカット
                            panel.rev.undoBoard(undo);
                            return value;
                        }
                    }
                    panel.rev.undoBoard(undo);
                }
            }
        }
        if (level == serchLevel) {
            return bestI + bestJ * MASU;
        } else {
            return value;
        }
    }

    /**
     * 石の数によって評価値を決定する
     *
     * @return コンピュータの石の個数
     */
    int valueBoardManyStone() {

        Counter counter = panel.rev.getCount();
        if (saki.isSelected()) {
            return counter.whiteCount;
        } else {
            return counter.blackCount;
        }
    }

    /**
     * 8×8の時は決まった評価値により評価値を返す。 それ以外の時は、石の数により評価値を返す。
     *
     * @return 評価値を返す
     */
    int valueBoard() {

        if (MASU == 8) {
            int value = 0;
            //評価値は碁盤全体の状態により決まる
            for (int i = 0; i < MASU; i++) {
                for (int j = 0; j < MASU; j++) {
                    value += panel.rev.getStone(i, j) * placeValue[i][j];
                }
            }
            if (saki.isSelected()) {
                return -value;//AIが白の時
            } else {
                return value;//AIが黒の時
            }
        } else {
            return valueBoardManyStone();
        }
    }
}

class Thinking implements Runnable {

    @Override
    public void run() {
        while (true) {
            panel.thinking(panel.getGraphics());
        }
    }
}

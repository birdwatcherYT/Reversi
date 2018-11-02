package reversi;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Point;
import static reversi.MainPanel.*;
import static reversi.MyPanel.*;

public class ReversiSet {

    short board[][], gameState;
    public static final short BLANK = 0, BLACK = 1, WHITE = -1;
    public static final short PLAY = 0, BLACK_WIN = 1, WHITE_WIN = 2, DRAW = 3;
    public static final int SUPER_QUICK = 0, QUICK = 200, NORMAL_SPEED = 300, SLOW = 400;
    public static int sleepTime;
    boolean doublePlay, blackFlag, passPass,reversing;
    Counter count;
    AudioClip se = Applet.newAudioClip(getClass().getResource("se.wav"));
    
    public ReversiSet(boolean doublePlay) {
        this.doublePlay = doublePlay;
        blackFlag = true;
        reversing=passPass = false;
        gameState = PLAY;
        count = new Counter(2, 2);
        board = new short[MASU][MASU];
        board[MASU / 2][MASU / 2] = board[MASU / 2 - 1][MASU / 2 - 1] = BLACK;
        board[MASU / 2 - 1][MASU / 2] = board[MASU / 2][MASU / 2 - 1] = WHITE;
    }

    /**
     * (i,j)に石を置く
     *
     * @param i i行目
     * @param j j列目
     * @param trying コンピューターが思考中かどうか
     */
    void putOn(int i, int j, boolean trying) {
        short color = (blackFlag) ? BLACK : WHITE;        
        reversing=true;
        board[i][j] = color;
        if (!trying) {
            se.play();
            panel.update(panel.getGraphics());
            sleep();
        }
        reversing=false;
    }

    /**
     * (i,j)に石が置けるかどうか
     *
     * @param i i行目
     * @param j j列目
     * @return 置けるかどうか
     */
    boolean setable(int i, int j) {
        if (i < 0 || i >= MASU || j < 0 || j >= MASU) {//範囲外
            return false;
        }
        if (board[i][j] != BLANK) {//空白じゃない
            return false;
        }
        if (setable(i, j, 1, 0) || setable(i, j, 0, 1) || setable(i, j, -1, 0) || setable(i, j, 0, -1)
                || setable(i, j, 1, 1) || setable(i, j, -1, -1) || setable(i, j, 1, -1) || setable(i, j, -1, 1)) {//各方向に関して
            return true;
        }
        return false;
    }

    /**
     * (vecI,vecJ)方向に対して(i,j)に置けるかどうか
     *
     * @param i
     * @param j
     * @param vecI
     * @param vecJ
     * @return
     */
    boolean setable(int i, int j, int vecI, int vecJ) {
        short color = (blackFlag) ? BLACK : WHITE;
        //進める
        i += vecI;
        j += vecJ;
        if (i < 0 || i >= MASU || j < 0 || j >= MASU) {//範囲外
            return false;
        }
        if (board[i][j] == color || board[i][j] == BLANK) {//同じ色か空白
            return false;
        }
        i += vecI;
        j += vecJ;
        while (i >= 0 && i < MASU && j >= 0 && j < MASU) {
            if (board[i][j] == BLANK) {//空白があったら
                return false;
            }
            if (board[i][j] == color) {//自分の色
                return true;
            }
            //進めていく
            i += vecI;
            j += vecJ;
        }
        return false;
    }

    /**
     * (undo.i,undo.j)に置いた時に反転させる
     *
     * @param undo
     * @param trying コンピューターが思考中かどうか
     */
    void reverse(Undo undo, boolean trying) {
        reversing=true;
        if (setable(undo.i, undo.j, 1, 0)) {
            reverse(undo, 1, 0, trying);
        }
        if (setable(undo.i, undo.j, 0, 1)) {
            reverse(undo, 0, 1, trying);
        }
        if (setable(undo.i, undo.j, -1, 0)) {
            reverse(undo, -1, 0, trying);
        }
        if (setable(undo.i, undo.j, 0, -1)) {
            reverse(undo, 0, -1, trying);
        }
        if (setable(undo.i, undo.j, 1, 1)) {
            reverse(undo, 1, 1, trying);
        }
        if (setable(undo.i, undo.j, -1, -1)) {
            reverse(undo, -1, -1, trying);
        }
        if (setable(undo.i, undo.j, 1, -1)) {
            reverse(undo, 1, -1, trying);
        }
        if (setable(undo.i, undo.j, -1, 1)) {
            reverse(undo, -1, 1, trying);
        }
        reversing=false;
    }

    /**
     * (vecI,vecJ)方向に対して(undo.i,undo.j)に置いた場合の反転
     *
     * @param undo
     * @param vecI
     * @param vecJ
     * @param trying コンピューターが思考中かどうか
     */
    void reverse(Undo undo, int vecI, int vecJ, boolean trying) {
        short color = (blackFlag) ? BLACK : WHITE;
        int i = undo.i, j = undo.j;
        //進める
        i += vecI;
        j += vecJ;
        while (board[i][j] != color) {//自分の色が来るまで
            board[i][j] = color;
            undo.position[undo.count++] = new Point(i, j);
            if (!trying) {
                se.play();
                panel.update(panel.getGraphics());
                sleep();
            }
            //進める
            i += vecI;
            j += vecJ;
        }
    }

    /**
     * (undo.i,undo.j)から石を取り除く(元に戻す)
     *
     * @param undo
     */
    void undoBoard(Undo undo) {
        int counter = 0;
        while (undo.position[counter] != null) {
            int i = undo.position[counter].x;
            int j = undo.position[counter].y;
            board[i][j] *= -1;//BLACK=-WHITEである
            counter++;
        }
        board[undo.i][undo.j] = BLANK;//置いた場所を空白に
        nextTurn();
    }

    /**
     * 次のターン
     */
    void nextTurn() {
        blackFlag = !blackFlag;
    }

    /**
     * 勝ち負け判定
     */
    void endGame() {

        if (count.blackCount == 0) {
            gameState = WHITE_WIN;
        } else if (count.whiteCount == 0) {
            gameState = BLACK_WIN;
        }
        if (count.blackCount + count.whiteCount == MASU * MASU || passPass) {
            if (count.blackCount > count.whiteCount) {
                gameState = BLACK_WIN;
            } else if (count.blackCount < count.whiteCount) {
                gameState = WHITE_WIN;
            } else {
                gameState = DRAW;
            }
        }
    }

    /**
     * @return 白と黒の石の数
     */
    Counter getCount() {
        Counter counter = new Counter();
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (board[i][j] == BLACK) {
                    counter.blackCount++;
                }
                if (board[i][j] == WHITE) {
                    counter.whiteCount++;
                }
            }
        }
        return counter;
    }

    /**
     * @return 置くことが出来る場所があるかどうか
     */
    boolean canPut() {
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (setable(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    short getStone(int i, int j) {
        return board[i][j];
    }

    /**
     * 動作一時停止
     */
    static public void sleep() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
        }
    }

    void setSpeed(int sleepTime) {
        ReversiSet.sleepTime = sleepTime;
    }
}

class Counter {

    public int blackCount;
    public int whiteCount;

    public Counter() {
        blackCount = 0;
        whiteCount = 0;
    }

    public Counter(int blackCount, int whiteCount) {
        this.blackCount = blackCount;
        this.whiteCount = whiteCount;
    }
}

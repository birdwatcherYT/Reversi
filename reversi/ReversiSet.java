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
     * (i,j)�ɐ΂�u��
     *
     * @param i i�s��
     * @param j j���
     * @param trying �R���s���[�^�[���v�l�����ǂ���
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
     * (i,j)�ɐ΂��u���邩�ǂ���
     *
     * @param i i�s��
     * @param j j���
     * @return �u���邩�ǂ���
     */
    boolean setable(int i, int j) {
        if (i < 0 || i >= MASU || j < 0 || j >= MASU) {//�͈͊O
            return false;
        }
        if (board[i][j] != BLANK) {//�󔒂���Ȃ�
            return false;
        }
        if (setable(i, j, 1, 0) || setable(i, j, 0, 1) || setable(i, j, -1, 0) || setable(i, j, 0, -1)
                || setable(i, j, 1, 1) || setable(i, j, -1, -1) || setable(i, j, 1, -1) || setable(i, j, -1, 1)) {//�e�����Ɋւ���
            return true;
        }
        return false;
    }

    /**
     * (vecI,vecJ)�����ɑ΂���(i,j)�ɒu���邩�ǂ���
     *
     * @param i
     * @param j
     * @param vecI
     * @param vecJ
     * @return
     */
    boolean setable(int i, int j, int vecI, int vecJ) {
        short color = (blackFlag) ? BLACK : WHITE;
        //�i�߂�
        i += vecI;
        j += vecJ;
        if (i < 0 || i >= MASU || j < 0 || j >= MASU) {//�͈͊O
            return false;
        }
        if (board[i][j] == color || board[i][j] == BLANK) {//�����F����
            return false;
        }
        i += vecI;
        j += vecJ;
        while (i >= 0 && i < MASU && j >= 0 && j < MASU) {
            if (board[i][j] == BLANK) {//�󔒂���������
                return false;
            }
            if (board[i][j] == color) {//�����̐F
                return true;
            }
            //�i�߂Ă���
            i += vecI;
            j += vecJ;
        }
        return false;
    }

    /**
     * (undo.i,undo.j)�ɒu�������ɔ��]������
     *
     * @param undo
     * @param trying �R���s���[�^�[���v�l�����ǂ���
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
     * (vecI,vecJ)�����ɑ΂���(undo.i,undo.j)�ɒu�����ꍇ�̔��]
     *
     * @param undo
     * @param vecI
     * @param vecJ
     * @param trying �R���s���[�^�[���v�l�����ǂ���
     */
    void reverse(Undo undo, int vecI, int vecJ, boolean trying) {
        short color = (blackFlag) ? BLACK : WHITE;
        int i = undo.i, j = undo.j;
        //�i�߂�
        i += vecI;
        j += vecJ;
        while (board[i][j] != color) {//�����̐F������܂�
            board[i][j] = color;
            undo.position[undo.count++] = new Point(i, j);
            if (!trying) {
                se.play();
                panel.update(panel.getGraphics());
                sleep();
            }
            //�i�߂�
            i += vecI;
            j += vecJ;
        }
    }

    /**
     * (undo.i,undo.j)����΂���菜��(���ɖ߂�)
     *
     * @param undo
     */
    void undoBoard(Undo undo) {
        int counter = 0;
        while (undo.position[counter] != null) {
            int i = undo.position[counter].x;
            int j = undo.position[counter].y;
            board[i][j] *= -1;//BLACK=-WHITE�ł���
            counter++;
        }
        board[undo.i][undo.j] = BLANK;//�u�����ꏊ���󔒂�
        nextTurn();
    }

    /**
     * ���̃^�[��
     */
    void nextTurn() {
        blackFlag = !blackFlag;
    }

    /**
     * ������������
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
     * @return ���ƍ��̐΂̐�
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
     * @return �u�����Ƃ��o����ꏊ�����邩�ǂ���
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
     * ����ꎞ��~
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

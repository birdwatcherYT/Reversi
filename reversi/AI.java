package reversi;

import static reversi.MainPanel.*;
import static reversi.MyPanel.MASU;
import static reversi.ReversiSet.*;

public class AI {
    public static final short EASY = 3, NORMAL = 5, DIFFICULT = 7, VERY_DIFFICULT = 9;
    short serchLevel;//��ǂ݃��x��
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
     * ��Փx�ݒ�
     *
     * @param level �[�ǂ݃��x�� (EASY,NORMAL,DIFFICULT)
     */
    void setLevel(short level) {
        serchLevel = level;
    }

    /**
     * �R���s���[�^�̔�
     */
    @SuppressWarnings("CallToThreadStopSuspendOrResumeManager")
    void computer() {
        //�v�l��...�̕\���J�n
        th = new Thread(new Thinking());
        th.start();
//        int temp = minMax(true, serchLevel);//�~�j�}�b�N�X�@:�Ԃ�l�� bestI+bestJ*MASU
        int temp = alphaBeta(true, serchLevel, Integer.MIN_VALUE, Integer.MAX_VALUE);//��-���@:�Ԃ�l�� bestI+bestJ*MASU
        int i = temp % MASU, j = temp / MASU;
        //�v�l��...�X�g�b�v
        th.stop();

        Undo undo = new Undo(i, j);
        panel.rev.putOn(i, j, false);
        panel.rev.reverse(undo, false);
        panel.rev.count = panel.rev.getCount();
        panel.rev.endGame();
        panel.rev.nextTurn();
        panel.update(panel.getGraphics());
        if (!panel.rev.canPut() && panel.rev.gameState == PLAY) {//����(�v���C���[)���p�X�Ȃ�
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
     * �~�j�}�b�N�X�@
     *
     * @param flag AI�̔Ԃ��ǂ���
     * @param level ��ǂ݂̎萔
     * @return �ŏI�I��(bestI+bestJ*MASU)��Ԃ��B�q�m�[�h�ł́A���̕]���l��Ԃ��B
     */
    int minMax(boolean flag, short level) {
        int value,//�m�[�h�̕]���l
                childValue,//�q�m�[�h����`����Ă����]���l
                bestI = 0,
                bestJ = 0;
        if (level == 0 || !panel.rev.canPut()) {
            return valueBoard();
        }
        if (flag) {
            value = Integer.MIN_VALUE;//AI�̔Ԃł͍ő�̕]���l����������
        } else {
            value = Integer.MAX_VALUE;//AI�̔ԂłȂ��Ƃ��͍ŏ��̕]���l����������
        }
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (panel.rev.setable(i, j)) {
                    Undo undo = new Undo(i, j);
                    panel.rev.putOn(i, j, true);
                    panel.rev.reverse(undo, true);
                    panel.rev.nextTurn();
                    childValue = minMax(!flag, (short) (level - 1));//�ċA
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
     * ��-���@
     *
     * @param flag AI�̔Ԃ��ǂ���
     * @param level ��ǂ݂̎萔
     * @param alpha ���l�B���̃m�[�h�̕]���l�̓��ȏ�B
     * @param beta ���l�B���̃m�[�h�̕]���l�̓��ȉ��B
     * @return �ŏI�I��(bestI+bestJ*MASU)��Ԃ��B�q�m�[�h�ł́A���̕]���l��Ԃ��B
     */
    int alphaBeta(boolean flag, short level, int alpha, int beta) {
        int value,//�m�[�h�̕]���l
                childValue,//�q�m�[�h����`����Ă����]���l
                bestI = 0,
                bestJ = 0;
        if (level == 0 || !panel.rev.canPut()) {
            return valueBoard();
        }
        if (flag) {
            value = Integer.MIN_VALUE;//AI�̔Ԃł͍ő�̕]���l����������
        } else {
            value = Integer.MAX_VALUE;//AI�̔ԂłȂ��Ƃ��͍ŏ��̕]���l����������
        }
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (panel.rev.setable(i, j)) {
                    Undo undo = new Undo(i, j);
                    panel.rev.putOn(i, j, true);
                    panel.rev.reverse(undo, true);
                    panel.rev.nextTurn();
                    childValue = alphaBeta(!flag, (short) (level - 1), alpha, beta);//�ċA
                    if (flag) {
                        if (childValue > value) {
                            value = childValue;
                            alpha = value;//���l���X�V
                            bestI = i;
                            bestJ = j;
                        }
                        if (value > beta) {//���J�b�g
                            panel.rev.undoBoard(undo);
                            return value;
                        }
                    } else {
                        if (childValue < value) {
                            value = childValue;
                            beta = value;//���l���X�V
                            bestI = i;
                            bestJ = j;
                        }
                        if (value < alpha) {//���J�b�g
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
     * �΂̐��ɂ���ĕ]���l�����肷��
     *
     * @return �R���s���[�^�̐΂̌�
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
     * 8�~8�̎��͌��܂����]���l�ɂ��]���l��Ԃ��B ����ȊO�̎��́A�΂̐��ɂ��]���l��Ԃ��B
     *
     * @return �]���l��Ԃ�
     */
    int valueBoard() {

        if (MASU == 8) {
            int value = 0;
            //�]���l�͌�ՑS�̂̏�Ԃɂ�茈�܂�
            for (int i = 0; i < MASU; i++) {
                for (int j = 0; j < MASU; j++) {
                    value += panel.rev.getStone(i, j) * placeValue[i][j];
                }
            }
            if (saki.isSelected()) {
                return -value;//AI�����̎�
            } else {
                return value;//AI�����̎�
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

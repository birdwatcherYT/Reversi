package reversi;

import java.awt.Point;
import static reversi.MyPanel.MASU;

public class Undo {

    int i, j, count;
    Point position[];

    /**
     * Undo�̃C���X�^���X
     * @param i �΂�u���ꏊ��i�s
     * @param j �΂�u���ꏊ��j��
     */
    public Undo(int i, int j) {
        this.i = i;
        this.j = j;
        count = 0;
        position = new Point[MASU * MASU];
    }
}

package reversi;

import java.awt.Point;
import static reversi.MyPanel.MASU;

public class Undo {

    int i, j, count;
    Point position[];

    /**
     * Undoのインスタンス
     * @param i 石を置く場所のi行
     * @param j 石を置く場所のj列
     */
    public Undo(int i, int j) {
        this.i = i;
        this.j = j;
        count = 0;
        position = new Point[MASU * MASU];
    }
}

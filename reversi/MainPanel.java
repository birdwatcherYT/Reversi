package reversi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static reversi.MainPanel.*;
import static reversi.ReversiSet.*;

public class MainPanel extends JFrame implements ActionListener {

    static MyPanel panel;
    JMenuBar menu = new JMenuBar();
    JPanel main = new JPanel();
    JMenu playStyle = new JMenu("�v���C�X�^�C�� (P)");
    JMenu level = new JMenu("���x�� (L)");
    JMenu speed = new JMenu("�X�s�[�h (S)");
    JMenu setting = new JMenu("�ݒ� (E)");

    static JCheckBoxMenuItem easy = new JCheckBoxMenuItem("Easy");
    static JCheckBoxMenuItem normal = new JCheckBoxMenuItem("Normal");
    static JCheckBoxMenuItem difficult = new JCheckBoxMenuItem("Difficult");
    static JCheckBoxMenuItem veryDifficult = new JCheckBoxMenuItem("Very difficult");
    static JCheckBoxMenuItem superQuick = new JCheckBoxMenuItem("�ő�");
    static JCheckBoxMenuItem quick = new JCheckBoxMenuItem("����");
    static JCheckBoxMenuItem normalSpeed = new JCheckBoxMenuItem("����");
    static JCheckBoxMenuItem slow = new JCheckBoxMenuItem("�x��");
    static JCheckBoxMenuItem saki = new JCheckBoxMenuItem("��U");
    static JCheckBoxMenuItem ato = new JCheckBoxMenuItem("��U");
    static JCheckBoxMenuItem doublePlay = new JCheckBoxMenuItem("2�l�v���C");
    static JCheckBoxMenuItem implement = new JCheckBoxMenuItem("�⏕");
    ButtonGroup levelGroup = new ButtonGroup();
    ButtonGroup playGroup = new ButtonGroup();
    ButtonGroup speedGroup = new ButtonGroup();

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String[] args) {
        new MainPanel();
    }

    @SuppressWarnings("LeakingThisInConstructor")
    MainPanel() {
        super("���o�[�V");
        setResizable(false);
        main.setLayout(new BorderLayout());

        normal.setState(true);
        saki.setState(true);
        normalSpeed.setState(true);
        implement.setSelected(true);

        panel = new MyPanel();
        Container container = getContentPane();
        container.add(panel);

        levelGroup.add(easy);
        levelGroup.add(normal);
        levelGroup.add(difficult);
        levelGroup.add(veryDifficult);
        playGroup.add(ato);
        playGroup.add(saki);
        playGroup.add(doublePlay);
        speedGroup.add(superQuick);
        speedGroup.add(quick);
        speedGroup.add(normalSpeed);
        speedGroup.add(slow);

        playStyle.setMnemonic('P');
        level.setMnemonic('L');
        easy.setMnemonic('E');
        normal.setMnemonic('N');
        difficult.setMnemonic('D');
        veryDifficult.setMnemonic('V');
        speed.setMnemonic('S');
        setting.setMnemonic('E');

        saki.addActionListener(this);
        ato.addActionListener(this);
        doublePlay.addActionListener(this);
        easy.addActionListener(this);
        normal.addActionListener(this);
        difficult.addActionListener(this);
        veryDifficult.addActionListener(this);
        superQuick.addActionListener(this);
        quick.addActionListener(this);
        normalSpeed.addActionListener(this);
        slow.addActionListener(this);
        implement.addActionListener(this);

        playStyle.add(saki);
        playStyle.add(ato);
        playStyle.add(doublePlay);
        level.add(easy);
        level.add(normal);
        level.add(difficult);
        level.add(veryDifficult);
        speed.add(superQuick);
        speed.add(quick);
        speed.add(normalSpeed);
        speed.add(slow);
        setting.add(implement);
        menu.add(playStyle);
        menu.add(level);
        menu.add(speed);
        menu.add(setting);
        main.add(menu, BorderLayout.NORTH);
        main.add(panel, BorderLayout.CENTER);
        this.add(main);

        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "��U":
                panel.reset(false);
                break;
            case "��U":
                panel.reset(false);
                panel.ai.computer();
                break;
            case "2�l�v���C":
                panel.reset(true);
                break;
            case "Easy":
                panel.ai.setLevel(reversi.AI.EASY);
                break;
            case "Normal":
                panel.ai.setLevel(reversi.AI.NORMAL);
                break;
            case "Difficult":
                panel.ai.setLevel(reversi.AI.DIFFICULT);
                break;
            case "Very difficult":
                panel.ai.setLevel(reversi.AI.VERY_DIFFICULT);
                break;
            case "�ő�":
                panel.rev.setSpeed(SUPER_QUICK);
                break;
            case "����":
                panel.rev.setSpeed(QUICK);
                break;
            case "����":
                panel.rev.setSpeed(NORMAL_SPEED);
                break;
            case "�x��":
                panel.rev.setSpeed(SLOW);
                break;
            case "�⏕":
                panel.hojo = implement.getState();
                panel.update(panel.getGraphics());
                break;
        }
    }
}

class MyPanel extends JPanel implements MouseListener {

    static final int SIZE = 70, MASU = 8;
    static final int W = SIZE * MASU, H = SIZE * MASU + 50;
    boolean hojo;
    ReversiSet rev;
    AI ai;
    Graphics gt;
    Image img;

    public MyPanel() {
        ai = new AI();
        reset(false);
        hojo = implement.isSelected();
        setPreferredSize(new Dimension(W, H));
        this.addMouseListener(this);
    }

    /**
     * ���Z�b�g����
     *
     * @param doublePlay 2�l�v���C���ǂ���
     */
    void reset(boolean doublePlay) {
        rev = new ReversiSet(doublePlay);
        if (superQuick.isSelected()) {
            rev.setSpeed(SUPER_QUICK);
        } else if (quick.isSelected()) {
            rev.setSpeed(QUICK);
        } else if (normalSpeed.isSelected()) {
            rev.setSpeed(NORMAL_SPEED);
        } else if (slow.isSelected()) {
            rev.setSpeed(SLOW);
        }
        repaint();
    }

    /**
     * �w�i�̕`��
     *
     * @param g
     */
    void stageDraw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(0, 0, W, H);
        g.setColor(Color.black);
        for (int i = 0; i <= MASU; i++) {
            g.drawLine(0, i * SIZE, W, i * SIZE);
            g.drawLine(i * SIZE, 0, i * SIZE, 8 * SIZE - 1);
        }
    }

    /**
     * ��Ԃ̕`��
     *
     * @param g
     */
    void infoDraw(Graphics g) {
        g.setFont(new Font(null, Font.BOLD, SIZE / 3));
        switch (rev.gameState) {
            case PLAY:
                g.drawString("BLACK : " + String.valueOf(rev.count.blackCount)
                        + "   WHITE : " + String.valueOf(rev.count.whiteCount)
                        + ((rev.blackFlag) ? "   ���̔�" : "   ���̔�"), 10, MASU * SIZE + 40);
                break;
            case BLACK_WIN:
                g.drawString("BLACK : " + String.valueOf(rev.count.blackCount)
                        + "   WHITE : " + String.valueOf(rev.count.whiteCount)
                        + "   BLACK WIN", 10, MASU * SIZE + 40);
                break;
            case WHITE_WIN:
                g.drawString("BLACK : " + String.valueOf(rev.count.blackCount)
                        + "   WHITE : " + String.valueOf(rev.count.whiteCount)
                        + "   WHITE WIN", 10, MASU * SIZE + 40);
                break;
            case DRAW:
                g.drawString("BLACK : " + String.valueOf(rev.count.blackCount)
                        + "   WHITE : " + String.valueOf(rev.count.whiteCount)
                        + "   DRAW", 10, MASU * SIZE + 40);
                break;
        }
    }

    /**
     * �΂�`��
     *
     * @param g
     */
    void stoneDraw(Graphics g) {
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (rev.board[i][j] == BLANK) {
                    continue;
                }
                switch (rev.board[i][j]) {
                    case BLACK:
                        g.setColor(Color.black);
                        break;
                    case WHITE:
                        g.setColor(Color.white);
                        break;
                }
                g.fillOval(j * SIZE + 3, i * SIZE + 3, SIZE - 6, SIZE - 6);
            }
        }
    }

    /**
     * PASS!!�ƕ\��
     *
     * @param g
     */
    void drawPass(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font(null, Font.BOLD, SIZE / 2));
        g.drawString(((rev.blackFlag) ? "BLACK " : "WHITE ") + "PASS!!", W / 2 - SIZE * 3 / 2, H / 2);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
        }
        update(getGraphics());
    }

    /**
     * �v�l��...��\��
     *
     * @param g
     */
    void thinking(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font(null, Font.BOLD, SIZE / 3));
        g.drawString("�v�l��.....", W - SIZE * 5 / 3, MASU * SIZE + 40);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        g.setColor(Color.green);
        g.fillRect(W - SIZE * 5 / 3, MASU * SIZE + 10, SIZE * 5 / 3, 50);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }

    void drawCanPut(Graphics g) {
        for (int i = 0; i < MASU; i++) {
            for (int j = 0; j < MASU; j++) {
                if (rev.setable(i, j)) {
                    g.setColor(Color.yellow);
                    g.fillOval(j * SIZE + SIZE / 2 - 5, i * SIZE + SIZE / 2 - 5, 10, 10);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        img = createImage(W, H);
        gt = img.getGraphics();
        stageDraw(gt);
        infoDraw(gt);
        stoneDraw(gt);
        if (!rev.reversing && hojo
                && (rev.doublePlay || (!rev.doublePlay && ((rev.blackFlag) ? BLACK : WHITE) == ((saki.isSelected()) ? BLACK : WHITE)))) {
            drawCanPut(gt);
        }
        g.drawImage(img, 0, 0, panel);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int i = e.getY() / SIZE, j = e.getX() / SIZE;
        if (rev.setable(i, j)) {
            Undo undo = new Undo(i, j);
            rev.putOn(i, j, false);
            rev.reverse(undo, false);
            rev.count = rev.getCount();
            rev.endGame();
            rev.nextTurn();
            update(getGraphics());
            if (rev.gameState == PLAY) {
                if (!rev.canPut()) {//���肪�p�X�Ȃ�
                    drawPass(getGraphics());
                    rev.nextTurn();
                    update(getGraphics());
                    if (!rev.canPut()) {
                        rev.passPass = true;
                        rev.endGame();
                    }
                } else {
                    if (!rev.doublePlay) {
                        ai.computer();
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

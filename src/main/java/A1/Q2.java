package A1;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;

class BlockConfig {
    int type;
    int colorType;
    int x;
    int y;

    public BlockConfig(int x, int y, int type, int colorType) {
        this.type = type;
        this.colorType = colorType;
        this.x = x;
        this.y = y;
    }
}

public class Q2{

    final static Boolean[] isPaused = {false};
//    new TetrisBlock(0,0, (int) (Math.random() * 10), (int) (Math.random() * 10) , g, (int) single_unit);
//        new TetrisBlock(0,1, (int) (Math.random() * 10), (int) (Math.random() * 10), g, (int) single_unit);
//        new TetrisBlock(12,1, (int) (Math.random() * 10), (int) (Math.random() * 10), g, (int) single_unit);
    final static BlockConfig[] blocks = new BlockConfig[] {
        new BlockConfig(1,1, (int) (Math.random() * 10), (int) (Math.random() * 10)),
        new BlockConfig(4,19, 0, (int) (Math.random() * 10)),
        new BlockConfig(12,1, (int) (Math.random() * 10), (int) (Math.random() * 10)),
    };

    public static void main(String[] args) {
        final float ratio = (float) (1.7/2);
        int height = 1000;
        Dimension d = new Dimension((int) (height * ratio),height);
        JFrame frame = new JFrame();
        frame.setSize(d);
        frame.setVisible(true);

        JPanel panel = new JPanel() {

            protected void paintComponent(Graphics g) {


                var single_unit = getHeight() / 20;
                int single_unit_int = (int) single_unit;
                this.setSize((int) (getHeight() * ratio + 10), getHeight());


                draw(g, single_unit);

                if(isPaused[0]) {
                    g.drawString("PAUSE", (int)(single_unit_int * 4.2), single_unit_int * 10);
                    g.drawRect(single_unit_int * 4, (int)(single_unit_int * 9.25), (int) (single_unit_int * 2.7), single_unit_int * 1);
                }

                g.drawString("QUIT", (int) (single_unit * 11.5), (int) single_unit * 16);
                g.drawRect(single_unit_int * 11, (int)(single_unit_int * 15.25), (int) (single_unit_int * 2.7), single_unit_int * 1);

                MouseMotionAdapter a = new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        super.mouseMoved(e);

                        if(e.getX() <= (single_unit_int * 10 + 5) && e.getY() <= (single_unit_int*20)) {
                            isPaused[0] = true;
                        } else {
                            isPaused[0] = false;
                        }

                        frame.repaint();
                    }
                };

                MouseAdapter q = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println(e);
                        if(
                                e.getX() >= (single_unit_int * 11)
                                && e.getY() >= (single_unit_int * 16.25)
                                && e.getX() <= (single_unit_int * 13.8)
                                && e.getY() <= (single_unit_int * 17.25)
                        ) {
                            System.exit(0);
                        }
                    }
                };

                frame.addMouseMotionListener(a);
                frame.addMouseListener(q);
            }
        };

        panel.setVisible(true);

        frame.add(panel);

    }

    public static void draw(Graphics g, float single_unit){

        int offset = 0;
        // big rectangle
        g.drawRect(offset,offset,(int) single_unit*10, (int) single_unit*20);

        // small rectangle
        g.drawRect((int) single_unit * 11, offset, (int) single_unit * 6, (int) single_unit * 6);

        g.setFont(new Font("arial",100, (int) (single_unit * 0.7)));
        g.drawString("Level: 1", (int) single_unit * 11, (int) single_unit * 7);
        g.drawString("Lines: 0", (int) single_unit * 11, (int) single_unit * 8);
        g.drawString("Score: 0", (int) single_unit * 11, (int) single_unit * 9);

        for(BlockConfig config: blocks) {
            new TetrisBlock(config.x, config.y, config.type, config.colorType, g, (int) single_unit);
        }

    }
}

class TetrisBlock {
    int x, y, type, colorType;
    private int[][][] coords = {
            {{0,0}, {1,0}, {2,0}, {3,0}}, // linear horizontal
            {{0,0}, {0,1}, {0,2}, {0,3}}, // linear vertical
            {{0,0}, {0,1}, {1,0}, {1,1}}, // square
            {{0,0}, {0,1}, {1,1}, {1,2}}, // zig-zag left - top to bottom right
            {{1,0}, {1,1}, {0,1}, {0,2}}, // zig-zag right - top to bottom left
            {{0,0}, {1,0}, {2,0}, {1,1}}, // T shaped
    };
    private Color[] colors = {
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.orange
    };
    private Color color;
    Graphics g;
    int single_unit;

    public TetrisBlock(int x, int y, int type, int colorType, Graphics g, int single_unit) {
        this.x = x;
        this.y = y;
        this.type = type % coords.length;
        this.colorType = colorType % colors.length;
        this.color = colors[this.colorType];
        this.g = g;
        this.single_unit = single_unit;
        draw();
    }

    public void draw(){
        for(int[] point : coords[type]){
            int x_ = point[0], y_ = point[1];
            Color prevColor = g.getColor();
            g.setColor(color);
            g.fillRect((x+x_) * single_unit,(y+y_) * single_unit, single_unit, single_unit);
            g.setColor(prevColor);
            g.drawRect((x+x_) * single_unit,(y+y_) * single_unit, single_unit, single_unit);
        }
    }
}

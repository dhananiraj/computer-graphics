package A1;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;
import javax.swing.*;

public class Q2{
    public static void main(String[] args) {
        float ratio = (float) (1.7/2);
        int height = 1000;
        Dimension d = new Dimension((int) (height * ratio),height);
        JFrame frame = new JFrame();
        frame.setSize(d);
        frame.setVisible(true);

        JPanel panel = new JPanel() {

            protected void paintComponent(Graphics g) {

                final Boolean[] isPaused = {false};

                var single_unit = getHeight() / 20;
                this.setSize((int) (getHeight() * ratio + 10), getHeight());


                draw(g, single_unit);

//                MouseMotionAdapter a = new MouseMotionAdapter() {
//                    @Override
//                    public void mouseMoved(MouseEvent e) {
//                        super.mouseMoved(e);
//
//                        int single_unit_int = (int) single_unit;
//
//                        if(e.getX() <= (single_unit_int*10) && e.getY() <= (single_unit_int*20)) {
//                            pauseLabel.setVisible(true);
//                        } else {
//                            pauseLabel.setVisible(false);
//                        }
//                    }
//                };

//                frame.addMouseMotionListener(a);

//                    @Override
//                    public void mouseMoved(MouseEvent e) {
//                        super.mouseMoved(e);
////                        System.out.println((single_unit_int*10) + "," + (single_unit_int*20) +"-"+e.getX() +", "+e.getY());
//                    }
//                });
            }
        };

        panel.setVisible(true);

//        frame.add(panel);


//        JLabel pauseLabel = new JLabel() {
//            protected void paintComponent(Graphics g_) {
//                g_.drawString("PAUSED", 100, 100); //these are x and y positions
//            }
//        };
//        pauseLabel.setVisible(true);

        frame.add(panel);

        Label pauseLabel = new Label("PAUSE");
//        frame.add(pauseLabel);

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
        g.drawString("QUIT", (int) single_unit * 11, (int) single_unit * 15);

        new TetrisBlock(0,0, 0, Color.BLUE, g, (int) single_unit);
        new TetrisBlock(0,1, (int) (Math.random() * 10), Color.YELLOW, g, (int) single_unit);
        new TetrisBlock(12,1, (int) (Math.random() * 10), Color.RED, g, (int) single_unit);

    }
}

class TetrisBlock {
    int x, y, type;
    private int[][][] coords = {
            {{0,0}, {1,0}, {2,0}, {3,0}}, // linear horizontal
            {{0,0}, {0,1}, {0,2}, {0,3}}, // linear vertical
            {{0,0}, {0,1}, {1,0}, {1,1}}, // square
            {{0,0}, {0,1}, {1,1}, {1,2}}, // zig-zag left - top to bottom right
            {{1,0}, {1,1}, {0,1}, {0,2}}, // zig-zag right - top to bottom left
            {{0,0}, {1,0}, {2,0}, {1,1}}, // T shaped
    };
    Color color;
    Graphics g;
    int single_unit;

    public TetrisBlock(int x, int y, int type, Color color, Graphics g, int single_unit) {
        this.x = x;
        this.y = y;
        this.type = type % coords.length;
        this.color = color;
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

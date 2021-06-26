package A1;

import java.awt.*;
import java.util.Objects;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Q1{
    public static void main(String[] args) {
        Dimension d = new Dimension(550,550);
        JFrame frame = new JFrame();
        frame.setSize(d);
        frame.setVisible(true);
        frame.add(new JPanel() {
            private static final long serialVersionUID = 1L;
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                Dimension dimension = getSize();
                Vector<Point> corners = new Vector<>();
                int min = Math.min(dimension.height, dimension.width) - 20;
                g2d.translate((dimension.width - min) / 2,5 + (dimension.height - min) / 2);
                corners.add(new Point(0,0));
                corners.add(new Point(0,min));
                corners.add(new Point(min,min));
                corners.add(new Point(min,0));
                draw(g, corners);
            }
        });
    }

    public static void draw(Graphics g, Vector<Point> points){
        if(points.stream().allMatch(point -> point.equals(points.firstElement()))) {
            return;
        }
        Vector<Point> corners = new Vector<>();
        for (int i = 0; i < points.size(); i++) {
            g.drawLine(points.get(i).x,points.get(i).y, points.get((i + 1) % 4).x, points.get((i + 1) % 4).y);
            corners.add(new Point((points.get(i).x + points.get((i + 1) % 4).x) / 2, (points.get(i).y + points.get((i + 1) % 4).y) / 2));
        }
        draw(g, corners);
    }
}

class Point{
    public Integer x, y;

    public Point(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Point() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equals(x, point.x) && Objects.equals(y, point.y);
    }
}

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
                int midX = (corners.get(0).x + corners.get(2).x) / 2, midY = (corners.get(0).y + corners.get(2).y) / 2;
                g2d.translate(midX, midY);
                draw(g, corners, corners);
            }
        });
    }

    public static void draw(Graphics g, Vector<Point> points, Vector<Point> corners){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        if(corners.stream().allMatch(point -> point.equals(points.firstElement()))) {
            return;
        }
        Vector<Point> corners_ = new Vector<>();
        for (int i = 0; i < corners.size(); i++) {
            corners_.add(new Point((corners.get(i).x + corners.get((i + 1) % 4).x) / 2, (corners.get(i).y + corners.get((i + 1) % 4).y) / 2));
        }
        int length = distance(points.get(0),points.get(1));
        g2d.drawRect(-length/2,-length/2, length,length);
        g2d.translate(0, 0);
        float theta = (float) Math.toRadians(45);
        g2d.rotate(theta);
        draw(g, corners_, corners_);
    }

    public static int distance(Point a, Point b){
        return (int) Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
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

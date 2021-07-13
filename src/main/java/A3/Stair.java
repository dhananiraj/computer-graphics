package A3;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Counter {
    public static int count_ = 1;
}

public class Stair {

    static double cX(double R, double i, double del) {
        return R * Math.cos(-1 * i * del);
    }

    static double cY(double R, double i, double del) {
        return R * Math.sin(-1 * i * del);
    }

    static class FilePrinter {
        static PrintWriter pw;
        static File file;

        public static void init(String fname) throws Exception{
            file = new File(fname);
            pw = new PrintWriter(file);
        }

    }

    public static void main(String[] args) throws Exception{

        if (args.length != 3) {
            System.out.println("Please enter n (> 0), alpha (in degrees) and a filename.\n");
            System.exit(1);
        }

        // NUMBER_OF_STAIR_CASES
        int n = Integer.parseInt(args[0]);

        double degree = Double.parseDouble(args[1]);

        if(n <= 0) {
            System.out.println("Please enter n (> 0), alpha (in degrees) and a filename.\n");
            System.exit(1);
        }

        FilePrinter.init(args[2]);

        int steps = n + 4;

        // Angle between each points w.r.t origin
        double del = Math.PI * degree/ 180;

        ArrayList<Point> cylinderTopPoints = new ArrayList<>();
        ArrayList<Point> cylinderBottomPoints = new ArrayList<>();
        ArrayList<Point> outerSpiralPoints = new ArrayList<>();
        ArrayList<Point> innerSpiralPoints = new ArrayList<>();
        ArrayList<stairCase> stairs = new ArrayList<>();

        for(int i = 0, k = 1; i <= 25; i++, k++) {
            double x, y, x_outer, y_outer;
            x = cX(2, i, 2 * Math.PI / 25);
            y = cY(2, i, 2 * Math.PI / 25);

            // for top face
            cylinderTopPoints.add(Point.as( x, y, steps * 2));
            // for bottom face
            cylinderBottomPoints.add(Point.as( x, y, 0));
        }

        for(int i = 0, k = 1; i < n; i++, k++) {
            double x,y,x_outer,y_outer;
            x = cX(2, i, del);
            y = cY(2, i, del);

            x_outer = cX(8, i, del);
            y_outer = cY(8, i, del);

            outerSpiralPoints.add(Point.as( x_outer, y_outer, (steps - k) * 2));

            innerSpiralPoints.add(Point.as( x, y, (steps - k) * 2));

            stairCase aCase = new stairCase();
            aCase.calcAndAddPoints(Point.as(cX(2,0, del),cY(2,0, del),((steps - k) * 2) - 8), i, del, Counter.count_++);
            stairs.add(aCase);


        }


        List<Point> stairsOuterPoints = outerSpiralPoints.stream().map(p -> Point.as( p.x, p.y, p.z - 8)).collect(Collectors.toList());
        List<Point> stairsInnerPoints = innerSpiralPoints.stream().map(p -> Point.as( p.x, p.y, p.z - 8)).collect(Collectors.toList());

        // Print all the points

        printPoints(cylinderTopPoints);
        printPoints(cylinderBottomPoints);
        printPoints(outerSpiralPoints);
        printPoints(stairsOuterPoints);

        stairs.forEach(s -> {
            printPoints(s.vertices);
        });

        // print faces:

        List<List<Integer>> stairLines = new ArrayList<>();

        for (int i = 0; i < outerSpiralPoints.size(); i++) {
            stairLines.add(Arrays.asList(outerSpiralPoints.get(i).i, stairsOuterPoints.get(i).i));
        }

        var cn = cylinderTopPoints.size();

        List<List<Integer>> cylinderFaces = new ArrayList<>();
        cylinderFaces.add(cylinderTopPoints.stream().mapToInt(p -> {return p.i;}).boxed().collect(Collectors.toList()));
        cylinderFaces.add(cylinderBottomPoints.stream().mapToInt(p -> {return p.i;}).boxed().collect(Collectors.toList()));

        List<Integer> outerSpiralLine;
        outerSpiralLine = outerSpiralPoints.stream().mapToInt(p -> {return p.i;}).boxed().collect(Collectors.toList());

        for (int i = 0; i <= cn; i++) {
            cylinderFaces.add(Arrays.asList(
                        cylinderTopPoints.get(i % cn).i,
                        cylinderTopPoints.get((i + 1) % cn).i,
                        cylinderBottomPoints.get((i + 1) % cn).i,
                        cylinderBottomPoints.get(i % cn).i
                    ));
        }

        List<List<Integer>> stairFaces = new ArrayList<>();

        stairs.forEach(s -> {
            stairFaces.add(
                    Arrays.asList(
                            s.vertices.get(0).i,
                            s.vertices.get(2).i,
                            s.vertices.get(3).i,
                            s.vertices.get(1).i
                    )
            );
            stairFaces.add(
                    Arrays.asList(
                            s.vertices.get(4).i,
                            s.vertices.get(6).i,
                            s.vertices.get(7).i,
                            s.vertices.get(5).i
                    )
            );
            stairFaces.add(
                    Arrays.asList(
                            s.vertices.get(0).i,
                            s.vertices.get(4).i,
                            s.vertices.get(6).i,
                            s.vertices.get(2).i
                    )
            );
            stairFaces.add(
                    Arrays.asList(
                            s.vertices.get(2).i,
                            s.vertices.get(6).i,
                            s.vertices.get(7).i,
                            s.vertices.get(3).i
                    )
            );
            stairFaces.add(
                    Arrays.asList(
                            s.vertices.get(0).i,
                            s.vertices.get(4).i,
                            s.vertices.get(5).i,
                            s.vertices.get(1).i
                    )
            );
            stairFaces.add(
                    Arrays.asList(
                            s.vertices.get(1).i,
                            s.vertices.get(5).i,
                            s.vertices.get(7).i,
                            s.vertices.get(3).i
                    )
            );
        });

        // print faces:

        FilePrinter.pw.println("Faces:");
        cylinderFaces.forEach(f -> printFaces(f));
        printLines(outerSpiralLine);
        stairLines.forEach(l -> {
            printLines(l);
        });

        stairFaces.forEach(f -> printFaces(f));
//        printFaces(Arrays.asList(aCase.vertices.get(0).i,aCase.vertices.get(2).i,aCase.vertices.get(3).i,aCase.vertices.get(1).i));


        FilePrinter.pw.flush();
        FilePrinter.pw.close();
    }

    public static void printPoints(List<Point> points){
        points.forEach(p -> {
            FilePrinter.pw.println(String.format("%d %.2f %.2f %.2f", p.i, p.x, p.y, p.z));
        });
    }

    public static void printLines(List<Integer> points) {
        for (int i = 0; i < points.size() - 1; i++) {
            FilePrinter.pw.println(String.format("%d %d.", points.get(i), points.get(i + 1)));
        }
    }

    public static void printFaces(List<Integer> points) {
        int i = 0;
        for(var p : points) {
            FilePrinter.pw.print(String.format("%d", p));
            if(i < points.size() - 1)
                FilePrinter.pw.print(" ");
            i++;
        };
        FilePrinter.pw.println(".");

        i = 0;
        Collections.reverse(points);
        for(var p : points) {
            FilePrinter.pw.print(String.format("%d", p));
            if(i < points.size() - 1)
                FilePrinter.pw.print(" ");
            i++;
        };
        FilePrinter.pw.println(".");
    }
}

class Point {
    public int i;
    public double x,y,z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(int i, double x, double y, double z) {
        this.i = i;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Point as(double x, double y, double z) {
        return new Point(Counter.count_++,x,y,z);
    }

    @Override
    public String toString() {
        return "Point{" +
                "i=" + i +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

class stairCase {
    public List<Point> vertices;
    final int width = 3;
    final double height = 0.5;

    public stairCase(List<Point> vertices) {
        this.vertices = vertices;
    }

    public stairCase() {
        this.vertices = new ArrayList<>();
    }

    double cX(double del, double i, double r, double t) {
        return r * Math.cos((del * i + t) * -1);
    }

    double cY(double del, double i, double r, double t) {
        return r * Math.sin((del * i + t) * -1);
    }

    void calcAndAddPoints(Point center, double i, double del, int k) { // k is the number of point
        //INNER
        // one side ( bottom in 2d diagram )
        double x,y;
        double r = Math.sqrt(Math.pow(center.x,2) + Math.pow(center.y - width/2, 2));
        double t = Math.asin((center.y - width/2) / r);
        x = cX(del, i, r, t);
        y = cY(del, i, r, t);
        vertices.add(Point.as( x, y, center.z));
        vertices.add(Point.as( x, y, center.z - height));


        // second side ( top in 2d diagram )
        t = Math.asin((center.y + width/2) / r);
        x = cX(del, i, r, t);
        y = cY(del, i, r, t);
        vertices.add(Point.as( x, y, center.z));
        vertices.add(Point.as( x, y, center.z - height));

        //OUTTER
        r = Math.sqrt(Math.pow(center.x + 6,2) + Math.pow(center.y - width/2, 2));
        t = Math.asin((center.y - width/2) / r);
        x = cX(del, i, r, t);
        y = cY(del, i, r, t);
        vertices.add(Point.as( x, y, center.z));
        vertices.add(Point.as( x, y, center.z - height));

        t = Math.asin((center.y + width/2) / r);
        x = cX(del, i, r, t);
        y = cY(del, i, r, t);
        vertices.add(Point.as( x, y, center.z));
        vertices.add(Point.as( x, y, center.z - height));
    }
}
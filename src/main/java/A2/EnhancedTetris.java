package A2;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.*;

class BlockConfig {
    int type;
    int colorType;
    int x;
    int y;
    int rotation;

    public BlockConfig(int x, int y, int type, int colorType) {
        this.type = type;
        this.colorType = colorType;
        this.x = x;
        this.y = y;
        rotation = 0;
    }

    public BlockConfig(int x, int y, int type, int colorType, int rotation) {
        this.type = type;
        this.colorType = colorType;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
}

class SingleBlockConfig {
    int colorType;
    int x;
    int y;

    @Override
    public String toString() {
        return "SingleBlockConfig{" +
                "colorType=" + colorType +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public SingleBlockConfig(int colorType, int x, int y) {
        this.colorType = colorType;
        this.x = x;
        this.y = y;
    }

    public SingleBlockConfig setColorType(int colorType) {
        this.colorType = colorType;
        return this;
    }

    public SingleBlockConfig setX(int x) {
        this.x = x;
        return this;
    }

    public SingleBlockConfig setY(int y) {
        this.y = y;
        return this;
    }
}


public class EnhancedTetris {

    final static Boolean[] isPaused = {false};
    final static Boolean[] isOver = {false};

    static int MAIN_X = 20, MAIN_Y = 20;

    static JFrame frame;

    static BlockConfig[] blocks;

    static int cx, cy;

    static BlockConfig currentBlock, nextBlock;

    static Graphics g;

    static float single_unit;

    static ArrayList<SingleBlockConfig> occupied;

    static final int SPEED_MIN = 0;
    static final int SPEED_MAX = 30;
    static final int SPEED_INIT= 15;

    static int M = 10; // 01 to 15
    static int N = 20; // 20 to 40

    static int level_counter = 0;

    static int level = 1;
    static int score = 0;

    static int SPEED;

    static JSlider speed = new JSlider(JSlider.HORIZONTAL,
            SPEED_MIN, SPEED_MAX, SPEED_INIT);

    static JSlider difficulty = new JSlider(JSlider.HORIZONTAL,
            20, 40, 20);

    static JSlider scoreFactor = new JSlider(JSlider.HORIZONTAL,
            1, 15, 10);

    static JSlider zoom = new JSlider(JSlider.HORIZONTAL,
            1, 2, 1);

    static void init() {
        cx = 3;
        cy = 2;
    }

    static int currentType, nextType, currentRotation;

    static boolean[] isPenalty = {false};

    static boolean toLeft, toRight;

    static boolean rotateLeft, rotateRight;

    static int row_blasted = 0;

    static float mapper(float A, float B, float C, float D, float p) {
        float scale = (D-C)/(B-A);
        float offset = -A*(D-C)/(B-A) + C;
        return p * scale + offset;
    }

    static boolean lock_penalty = false;

    static boolean[] resize = {false};

    static class SubThread implements Runnable {

        Thread dropThread;

        public SubThread() {
            this.dropThread = new Thread(this,"Drop the block thread");
        }

        @Override
        public void run() {
            // GameLoop
            while(true){
                try{

                    if(resize[0]) {
                        MAIN_Y = new_dim[0];
                        MAIN_X = new_dim[1];
                        ratio = (MAIN_X + 10) / MAIN_Y;
                        dropThread.sleep(500);
                        resize[0] = false;
                    }

                    if(zoom_now[0]) {
                        zoom_now[0] = false;
                        frame.setSize((int)(w * (1 + (zoom_ - 1) / 10)), (int)(h * (1 + (zoom_ - 1) / 10)) + (int)single_unit);
                    }

                    if(isPaused[0] || isOver[0]) {
                        if(isPenalty[0] && !lock_penalty) {
                            currentType = ((int) (Math.random() * 100)) % Mapper.getAll().length;
                            // Score = Score - Level x M
                            score -= level * M;
                            isPenalty[0] = false;
                            lock_penalty = true;
                        }
                        dropThread.sleep(100);
                        frame.repaint(100);
                        continue;
                    } else {
                        lock_penalty = false;
                    }

                    if(currentBlock == null) {
                        dropThread.sleep(100);
                        continue;
                    }

                    if(isBlockCollidingNow(null)) {
                        int[][] coords = Mapper.rotate(Mapper.getByType(currentBlock.type),currentRotation);
                        for (
                                int[] c: coords
                        ) {
                            occupied.add(new SingleBlockConfig(currentBlock.colorType, cx+c[0], cy+c[1]));
                        }
                        reInit();
                        continue;
                    }


                    if(toLeft) {
                        int[][] coords = Mapper.rotate(Mapper.getByType(currentBlock.type),currentRotation);
                        boolean flg = false;
                        for (int[] c: coords) {
                            if(c[0] + cx <= 0 || (occupied != null && occupied.stream().anyMatch(block -> (block.x <= c[0] + cx - 1 && block.y ==  c[1] + cy + 1)))){
                                flg = true;
                            }
                        }

                        if(cx > -1 && !flg)
                            cx--;
                        toLeft = false;
                    } else if(toRight) {
                        int[][] coords = Mapper.rotate(Mapper.getByType(currentBlock.type),currentRotation);
                        boolean flg = false;
                        for (int[] c: coords) {
                            if(c[0] + cx >= MAIN_X - 1 || (occupied != null && occupied.stream().anyMatch(block -> (block.x >= c[0] + cx + 1 && block.y ==  c[1] + cy + 1)))){
                                flg = true;
                            }
                        }

                        if(cx < 9 && !flg)
                            cx++;
                        toRight = false;
                    }

                    if(rotateLeft) {
                        if(!isBlockCollidingNow(Mapper.rotate(Mapper.getByType(currentBlock.type),(currentRotation + 1) % 4))){
                            currentRotation = (currentRotation + 1) % 4;
                        }
                        rotateLeft = false;
                    }
                    if(rotateRight) {
                        var new_rotation = 0;
                        if(currentRotation == 0) {
                            new_rotation = 3;
                        } else {
                            new_rotation=currentRotation-1;
                        }
                        if(!isBlockCollidingNow(Mapper.rotate(Mapper.getByType(currentBlock.type),new_rotation))) {
                            currentRotation = new_rotation;
                        }
                        rotateRight = false;
                    }

                    cy++;

                    dropThread.sleep((long) mapper(0,30,2000, 300, (float)(SPEED + 0.5 * level)));

                    frame.repaint(100);

                    if(g != null)
                        draw(g,single_unit);

                } catch (Exception e) {
                    System.out.println("ERROR IN DROP THREAD:"+e);
                    e.printStackTrace();
                }
            }
        }

        boolean isBlockCollidingNow(int[][] coords){
            if(coords == null){
                coords = Mapper.rotate(Mapper.getByType(currentBlock.type),currentRotation);
            }
            int cy_next = cy+1;
            for (int[] c:
                    coords) {
                if((occupied != null && occupied.stream().anyMatch(block -> (block.x == c[0]+cx && block.y ==  c[1]+cy_next))) || c[1] + cy_next > MAIN_Y - 1){
                    return true;
                }
            }
            return false;
        }

        public void start() {
            dropThread.start();
            System.out.println(dropThread);
            System.out.println("starting a sub thread: drop thread");
        }
    }

    public static void reInit(){
        if(isOver[0]) {
            return;
        }
        if(occupied.stream().anyMatch(a -> (a.y == 5))){
            isOver[0] = true;
            return;
        }

        removeRows();

        cx = 3;
        cy = 2;
        currentType = nextType;
        nextType = (int) (Math.random() * 10);
        currentRotation=0;
        frame.repaint(1000);
    }

    public static void removeRows(){
        int max_y = occupied.stream().max((o1, o2) -> {return o1.y - o2.y;}).get().y;
//        System.out.println("max_y:"+max_y);
        int min_y = occupied.stream().min((o1, o2) -> {return o1.y - o2.y;}).get().y;
//        System.out.println("max_y:"+min_y);

        for (int i = min_y; i <= max_y; i++) {
            final int n = i;
            long count = (int) occupied.stream().filter(e -> {return e.y == n;}).count();

            if(count == MAIN_X) {
                row_blasted++;
                occupied = (ArrayList<SingleBlockConfig>) occupied.stream()
                                                    .filter(e -> {return e.y != n;})
                                                    .map(e -> {
                                                        if(e.y < n) {
                                                            return new SingleBlockConfig(e.colorType, e.x, e.y + 1);
                                                        } else {
                                                            return e;
                                                        }
                                                    })
                                                    .collect(Collectors.toList());
                score += level * M;
            }

        }
    }


    static class SliderPanel extends JPanel {
        protected void paintComponent(Graphics g_) {
            Color bc = UIManager.getColor ( "Panel.background" );
            Color c = g_.getColor();
            g_.setColor(bc);
            g_.fillRect(0,0,1000,1000);
            g_.setColor(c);
            g_.drawString("Speed", (int) (single_unit * 0.3), (int) (single_unit * 1.2));
            g_.drawString("Difficulty", (int) (single_unit * 0.3), (int) (single_unit * 2.7));
            g_.drawString("S factor", (int) (single_unit * 0.3), (int) (single_unit * 4));
            g_.drawString("Zoom", (int) (single_unit * 0.3), (int) (single_unit * 5.5));
        }
    }

    static float zoom_ = 1;

    static int h = 700, w;

    static float ratio;

    static boolean[] zoom_now = {false};

    static JTextField h_ = new JTextField(2);

    static JTextField w_ = new JTextField(2);

    static int[] new_dim;

    public static void main(String[] args) {

        occupied = new ArrayList<>();
        int height = (int)(700 * zoom_);
        ratio = (float) ((MAIN_X + 12.0) / MAIN_Y);
        w = (int)(h * ratio);
        Dimension d = new Dimension((int) (height * ratio),height + 100);
        frame = new JFrame();
        frame.setSize((int)(w * (1 + (zoom_ - 1) / 10)), (int)(h * (1 + (zoom_ - 1) / 10)) + (int)(h / MAIN_Y));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        toLeft = false;
        toRight = false;
        rotateLeft = false;
        rotateRight = false;

        currentRotation = 0;

        init();

        currentType = (int) (Math.random() * 10);
        nextType= (int) (Math.random() * 10);

        EnhancedTetris.SubThread dropThread = new EnhancedTetris.SubThread();
        dropThread.start();

        single_unit = (float) frame.getHeight() / MAIN_Y;

        JPanel Sliders = new SliderPanel();

        JPanel panel = new JPanel() {

            protected void paintComponent(Graphics g_) {
                h_.setBounds((int)(single_unit * (MAIN_X + 2)), (int) (single_unit * 18.5), (int)single_unit, (int)single_unit);
                w_.setBounds((int)(single_unit * (MAIN_X + 4)), (int) (single_unit * 18.5), (int)single_unit, (int)single_unit);

                speed.setBounds((int)(single_unit * 2.5),(int)(single_unit * 0.2),(int)(single_unit * 6.5),(int)(single_unit * 1));
                speed.setSize((int)(single_unit * 3.5),(int)(single_unit * 2));
                speed.setMajorTickSpacing(5);
                speed.setMinorTickSpacing(1);

                speed.addChangeListener(e -> {
                    var x = speed.getValue();
                    SPEED = x;
                });

                difficulty.setBounds((int)(single_unit * 2.5),(int)(single_unit * 1.5),(int)(single_unit * 6.5),(int)(single_unit * 1));
                difficulty.setSize((int)(single_unit * 3.5),(int)(single_unit * 2));

                difficulty.addChangeListener(e -> {
                    var x = difficulty.getValue();
                    N = x;
                });

                scoreFactor.setBounds((int)(single_unit * 2.5),(int)(single_unit * 3),(int)(single_unit * 6.5),(int)(single_unit * 1));
                scoreFactor.setSize((int)(single_unit * 3.5),(int)(single_unit * 1.8));

                scoreFactor.addChangeListener(e -> {
                    var x = scoreFactor.getValue();
                    M = x;
                });


                zoom.setBounds((int)(single_unit * 2.5),(int)(single_unit * 4.5),(int)(single_unit * 6.5),(int)(single_unit * 1));
                zoom.setSize((int)(single_unit * 3.5),(int)(single_unit * 1.8));
                zoom.addChangeListener(e -> {
                    var x = zoom.getValue();
                    zoom_ = x;
                    zoom_now[0] = true;
                });

                Sliders.repaint(100);
                Sliders.setBounds((int)single_unit * (MAIN_X + 1),(int)single_unit * 10,(int)(single_unit * 6.5),(int)(single_unit * 7));
                Sliders.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));

                blocks = new BlockConfig[] {
                        new BlockConfig(MAIN_X + 2,1, nextType, (int) nextType),
                };

                currentBlock =
                        new BlockConfig(cx,cy, currentType, currentType,currentRotation);

                single_unit = (float) getHeight() / MAIN_Y;
                int single_unit_int = (int) single_unit;
                this.setSize((int)(w * (1 + (zoom_ - 1) / 10)), (int)(h * (1 + (zoom_ - 1) / 10)));

                g = g_;

                g.drawString("h:", (int)(single_unit*(MAIN_X + 1.4)), (int) (19 * (single_unit)));
                g.drawString("w:", (int)(single_unit*(MAIN_X + 3.4)), (int) (19 * (single_unit)));

                if(isOver[0]) {
                    Color prev = g.getColor();
                    g.setColor(Color.white);
                    g.fillRect(single_unit_int * (MAIN_X/2 - 3), (int)(single_unit_int * (MAIN_Y/2 - 0.75)), (int) (single_unit_int * 5.5), single_unit_int * 1);
                    g.setColor(Color.RED);
                    g.setFont(new Font("arial",100, (int) (single_unit * 0.7)));
                    g.drawString("GAME OVER", (int)(single_unit_int * (MAIN_X/2 - 2.5)), single_unit_int * (MAIN_Y/2));
                    g.drawRect(single_unit_int * (MAIN_X/2 - 3), (int)(single_unit_int * (MAIN_Y/2 - 0.75)), (int) (single_unit_int * 5.5), single_unit_int * 1);
                    g.setColor(prev);
                } else {
                    try{
                        occupied.forEach(block -> {
                            Color prevColor = g.getColor();
                            g.setColor(Mapper.getColorByType(block.colorType));
                            g.fillRect((block.x) * single_unit_int,(block.y) * single_unit_int, single_unit_int, single_unit_int);
                            g.setColor(prevColor);
                            g.drawRect((block.x) * single_unit_int,(block.y) * single_unit_int, single_unit_int, single_unit_int);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                draw(g, single_unit);



                if(isPaused[0] && !isOver[0]) {
                    Color prev = g.getColor();
                    g.setColor(Color.white);
                    g.fillRect(single_unit_int * (MAIN_X/2 - 1), (int)(single_unit_int * (MAIN_Y/2 - 0.75)), (int) (single_unit_int * 2.7), single_unit_int * 1);
                    g.setColor(Color.BLUE);
                    g.drawString("PAUSE", (int)(single_unit_int * (MAIN_X/2 - 0.7)), single_unit_int * (MAIN_Y/2));
                    g.drawRect(single_unit_int * (MAIN_X/2 - 1), (int)(single_unit_int * (MAIN_Y/2 - 0.75)), (int) (single_unit_int * 2.7), single_unit_int * 1);
                    g.setColor(prev);
                }



                g.drawString("QUIT", (int) (single_unit * (MAIN_X + 1.5)), (int) single_unit * 18);
                g.drawRect(single_unit_int * (MAIN_X + 1), (int)(single_unit_int * 17.25), (int) (single_unit_int * 2.7), single_unit_int * 1);

                g.drawString("OK", (int) (single_unit * (MAIN_X + 6)), (int) (single_unit * 19.2));
                g.drawRect((int)(single_unit * (MAIN_X + 5.8)), (int)(single_unit * 18.5), (int) (single_unit_int * 1.3), single_unit_int * 1);

                MouseMotionAdapter a = new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        super.mouseMoved(e);
                        // g.drawRect((int)(single_unit * (MAIN_X + 5.8)), (int)(single_unit * 18.5), (int) (single_unit_int * 1.3), single_unit_int * 1);

                        if(e.getX() <= (single_unit_int * MAIN_X + 5) && e.getY() <= (single_unit_int * MAIN_Y)) {
                            isPaused[0] = true;
                        } else {
                            isPaused[0] = false;
                        }

                        if(isPaused[0]) {
                            var x = e.getX();
                            var y = e.getY();

                            var coords = Mapper.rotate(Mapper.getByType(currentBlock.type),currentRotation);
                            Arrays.stream(coords).map(p -> {
                                return new float[]{
                                        single_unit_int * (currentBlock.x + p[0]),
                                        single_unit_int * (currentBlock.y + p[1]),
                                };
                            }).forEach(p -> {
                                if(x >= p[0] && x <= p[0] + single_unit_int && y >= p[1] && y <= p[1] + single_unit_int) {
                                    System.out.println("penalize:"+e.getX()+" "+e.getY());
                                    isPenalty[0] = true;
                                }
                            });


                        }

                        frame.repaint(1000);
                    }
                };

                MouseAdapter q = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if(
                                e.getX() >= (single_unit * (MAIN_X + 5.8))
                                && e.getX() <= (single_unit * (MAIN_X + 7.1))
                                && e.getY() >= (single_unit * 18.5)
                                && e.getY() >= (single_unit * 19.5)
                        ) {
                            new_dim = new int[]{Integer.parseInt(h_.getText().trim()),Integer.parseInt(w_.getText().trim())};
                            resize[0] = true;
                        }

                        // for exit button
                        if(
                                e.getX() >= (single_unit_int * MAIN_X + 1)
                                        && e.getY() >= (single_unit_int * 17.8)
                                        && e.getX() <= (single_unit_int * (MAIN_X + 3.8))
                                        && e.getY() <= (single_unit_int * 19.25)
                        ) {
                            System.exit(0);
                        }

                        if(isPaused[0]) {
                            return;
                        }

                        // for moving the block on left or right
                        if(e.getButton() == 1) {
                            // move left
                            toLeft = true;
                        } else if(e.getButton() == 3) {
                            // move right
                            toRight = true;
                        }
                    }
                };

                frame.add(h_);
                frame.add(w_);

                frame.addMouseMotionListener(a);
                frame.addMouseListener(q);
                frame.addMouseWheelListener(e -> {
                    if(e.getWheelRotation() == 1){
                        rotateLeft = true;
                    } else if (e.getWheelRotation() == -1) {
                        rotateRight = true;
                    }
                });
            }
        };


        Sliders.setVisible(true);
        System.out.println(single_unit);

        Sliders.add(speed);
        Sliders.add(difficulty);
        Sliders.add(scoreFactor);
        Sliders.add(zoom);


        frame.add(Sliders);

        panel.setVisible(true);

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle b = e.getComponent().getBounds();
                e.getComponent().setBounds(b.x, b.y, (int) (b.height * ratio + 10), b.height);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        frame.add(panel);

    }

    public static void draw(Graphics g, float single_unit){

        int offset = 0;
        // big rectangle
        g.drawRect(offset,offset,(int) single_unit*(MAIN_X), (int) single_unit*(MAIN_Y));

        // small rectangle
        g.drawRect((int)(single_unit * (MAIN_X + 1.1)), offset, (int) (single_unit * 6), (int) (single_unit * 6));

//        System.out.println("score"+score);

        g.setFont(new Font("arial",100, (int) (single_unit * 0.7)));
        g.drawString(String.format("Level: %d", level), (int) (single_unit * (MAIN_X + 1)), (int) (single_unit * 7));
        g.drawString(String.format("Lines: %d", row_blasted), (int) (single_unit * (MAIN_X + 1)), (int) (single_unit * 8));
        g.drawString(String.format("Score: %d", score), (int) (single_unit * (MAIN_X + 1)), (int) (single_unit * 9));

        if(isOver[0]) {
            return;
        }

        for(BlockConfig config: blocks) {
            new TetrisBlock(config.x, config.y, config.type, config.type, g, (int) single_unit, 0);
        }

        TetrisBlock cb = new TetrisBlock(currentBlock.x, currentBlock.y, currentBlock.type, currentBlock.type, g, (int) single_unit, currentBlock.rotation);
//        cb.rotate(Mapper.getByType(cb.type),1);
//        g.drawRect( (int) (cx * single_unit),(int) (cy * single_unit),5,5); // debug point

    }
}

class Mapper {
    static private final int[][][] coords = {
            {{0,0}, {1,0}, {2,0}, {3,0}}, // linear horizontal
            {{0,0}, {0,1}, {1,1}, {2,1}}, // L shape - v1
            {{0,1}, {1,1}, {2,1}, {2,0}}, // L shape - v2
            {{0,0}, {0,1}, {1,0}, {1,1}}, // square
            {{1,0}, {2,0}, {0,1}, {1,1}}, // zig-zag left - top to bottom right
            {{0,0}, {1,0}, {1,1}, {2,1}}, // zig-zag right - top to bottom left
            {{0,1}, {1,0}, {2,1}, {1,1}}, // T shaped
    };

    static Color[] colors = {
            Color.GREEN,
            new Color(102,0,153),
            Color.RED,
            new Color(3, 177, 252),
            Color.YELLOW,
            new Color(2, 102, 224),
            new Color(255, 170, 0)
    };

    static int[][][] getAll() {
        return coords;
    }

    static int[][] getByType(int type) {
        return coords[type % coords.length];
    }

    static Color[] getAllColors(){
        return colors;
    }

    static Color getColorByType(int type) {
        return colors[type % colors.length];
    }

    static int[][] rotate(int[][] raw, int n){
        int[][] rotated = rotate_without_translation(raw, n);

        if(n == 0) {
            return rotated;
        }

        int[] min = new int[]{3,3};

        for (int i = 0; i < rotated.length; i++) {


            if(rotated[i][0] < min[0]) {
                min[0] = rotated[i][0];
            }

            if(rotated[i][1] < min[1]) {
                min[1] = rotated[i][1];
            }
        }

        if(min[0]>0 || min[1]>0){
            for (int j = 0; j < rotated.length; j++) {
                rotated[j][0] -= min[0] ;
                rotated[j][1] -= min[1] ;
            }
        }

        return rotated;
    }

    static int[][] rotate_without_translation(int[][] raw,int n) {

        if(n == 0) {
            return raw;
        }

        int[][] rotated = new int[raw.length][2];

        int i = 0;

        for(int[] p: raw) {
            if (Arrays.equals(new int[]{0, 0}, p)) {
                rotated[i] = new int[]{0, 3};
            }
            else if (Arrays.equals(new int[]{1, 0}, p)) {
                rotated[i] = new int[]{0, 2};
            }
            else if (Arrays.equals(new int[]{2, 0}, p)) {
                rotated[i] = new int[]{0, 1};
            }
            else if (Arrays.equals(new int[]{3, 0}, p)) {
                rotated[i] = new int[]{0, 0};
            }
            else if (Arrays.equals(new int[]{0, 1}, p)) {
                rotated[i] = new int[]{1, 3};
            }
            else if (Arrays.equals(new int[]{1, 1}, p)) {
                rotated[i] = new int[]{1, 2};
            }
            else if (Arrays.equals(new int[]{2, 1}, p)) {
                rotated[i] = new int[]{1, 1};
            }
            else if (Arrays.equals(new int[]{3, 1}, p)) {
                rotated[i] = new int[]{1, 0};
            }
            else if (Arrays.equals(new int[]{0, 2}, p)) {
                rotated[i] = new int[]{2, 3};
            }
            else if (Arrays.equals(new int[]{1, 2}, p)) {
                rotated[i] = new int[]{2, 2};
            }
            else if (Arrays.equals(new int[]{2, 2}, p)) {
                rotated[i] = new int[]{2, 1};
            }
            else if (Arrays.equals(new int[]{3, 2}, p)) {
                rotated[i] = new int[]{2, 0};
            }
            else if (Arrays.equals(new int[]{0, 3}, p)) {
                rotated[i] = new int[]{3, 3};
            }
            else if (Arrays.equals(new int[]{1, 3}, p)) {
                rotated[i] = new int[]{3, 2};
            }
            else if (Arrays.equals(new int[]{2, 3}, p)) {
                rotated[i] = new int[]{3, 1};
            }
            else if (Arrays.equals(new int[]{3, 3}, p)) {
                rotated[i] = new int[]{3, 0};
            }
            i++;
        }

        return rotate(rotated,n-1);
    }
}

class TetrisBlock {
    int x, y, type, colorType;
    private Color color;
    Graphics g;
    int single_unit;
    int rotation;

    public TetrisBlock(int x, int y, int type, int colorType, Graphics g, int single_unit, int rotation) {
        this.x = x;
        this.y = y;
        this.type = type % Mapper.getAll().length;
        this.colorType = colorType % Mapper.getAllColors().length;
        this.color = Mapper.getColorByType(this.colorType);
        this.g = g;
        this.single_unit = single_unit;
        this.rotation = rotation;
        draw();
    }

    public void draw(){
        int[][] raw_points = Mapper.getByType(type);
        for(int[] point : Mapper.rotate(raw_points,rotation)){
            int x_ = point[0], y_ = point[1];
            Color prevColor = g.getColor();
            g.setColor(color);
            g.fillRect((x+x_) * single_unit,(y+y_) * single_unit, single_unit, single_unit);
            g.setColor(prevColor);
            g.drawRect((x+x_) * single_unit,(y+y_) * single_unit, single_unit, single_unit);
        }
    }
}

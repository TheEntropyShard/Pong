import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Pong extends JPanel implements Runnable, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int ROCKET_WIDTH = 15;
    private static final int ROCKET_HEIGHT = 70;
    private static final int ROCKET_SPEED = 5;

    private static final int BALL_SIZE = 15;

    private static final int BALL_START_X = (Pong.WIDTH / 2) - (Pong.BALL_SIZE / 2);
    private static final int BALL_START_Y = (Pong.HEIGHT / 2) - (Pong.BALL_SIZE / 2);
    private static int BALL_SPEED_X = 4;
    private static int BALL_SPEED_Y = 4;

    private static final int SCORE_Y = 50;

    private static final double UPS = 60.0D;
    private static final double FPS = 120.0D;

    private static final boolean[] KEYS = new boolean[65535];

    private static final Color BACKGROUND_COLOR = new Color(10, 60, 40);
    private static final Color ROCKET_COLOR = new Color(200, 200, 200);
    private static final Color BALL_COLOR = new Color(200, 200, 200);

    private static final Font FONT = new Font("Arial", Font.PLAIN, 36);

    private final Rectangle leftRocket;
    private final Rectangle rightRocket;
    private final Rectangle ball;

    private int leftScore;
    private int rightScore;

    private boolean gameStarted;

    public Pong() {
        super(true);

        this.setPreferredSize(new Dimension(Pong.WIDTH, Pong.HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);

        this.leftRocket = new Rectangle(0, (Pong.HEIGHT / 2) - (Pong.ROCKET_HEIGHT / 2), Pong.ROCKET_WIDTH, Pong.ROCKET_HEIGHT);
        this.rightRocket = new Rectangle(Pong.WIDTH - Pong.ROCKET_WIDTH, (Pong.HEIGHT / 2) - (Pong.ROCKET_HEIGHT / 2), Pong.ROCKET_WIDTH, Pong.ROCKET_HEIGHT);
        this.ball = new Rectangle(Pong.BALL_START_X, Pong.BALL_START_Y, Pong.BALL_SIZE, Pong.BALL_SIZE);
    }

    @Override
    public void run() {
        long initialTime = System.nanoTime();
        final double timeU = 1000000000 / Pong.UPS;
        final double timeF = 1000000000 / Pong.FPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();

        while(true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;

            if(deltaU >= 1) {
                this.update();
                ticks++;
                deltaU--;
            }

            if(deltaF >= 1) {
                this.repaint(0, 0, Pong.WIDTH, Pong.HEIGHT);
                frames++;
                deltaF--;
            }

            if(System.currentTimeMillis() - timer > 1000) {
                System.out.println("UPS: " + ticks + ", FPS: " + frames);
                frames = 0;
                ticks = 0;
                timer += 1000;
            }

            // This prevents game from eating cpu
            try {
                Thread.sleep(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Pong.KEYS[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Pong.KEYS[e.getKeyCode()] = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Pong.BACKGROUND_COLOR);
        g2.fillRect(0, 0, Pong.WIDTH, Pong.HEIGHT);

        g2.setColor(Pong.ROCKET_COLOR);
        g2.fillRect(this.leftRocket.x, this.leftRocket.y, Pong.ROCKET_WIDTH, Pong.ROCKET_HEIGHT);
        g2.fillRect(this.rightRocket.x, this.rightRocket.y, Pong.ROCKET_WIDTH, Pong.ROCKET_HEIGHT);

        g2.setColor(Pong.BALL_COLOR);
        g2.fillOval(this.ball.x, this.ball.y, Pong.BALL_SIZE, Pong.BALL_SIZE);

        g2.setFont(Pong.FONT);

        String s = this.leftScore + " : " + this.rightScore;
        FontMetrics fontMetrics = g2.getFontMetrics(Pong.FONT);
        int w = fontMetrics.stringWidth(s);

        g2.drawString(s, (Pong.WIDTH / 2) - (w / 2), Pong.SCORE_Y);

        g2.dispose();
    }

    private void update() {
        if(Pong.KEYS[KeyEvent.VK_SPACE]) {
            this.gameStarted = true;
        }

        if(Pong.KEYS[KeyEvent.VK_W]) {
            if(this.leftRocket.y > 0) {
                this.leftRocket.y -= Pong.ROCKET_SPEED;
            }
        }
        if(Pong.KEYS[KeyEvent.VK_S]) {
            if(this.leftRocket.y + Pong.ROCKET_HEIGHT < Pong.HEIGHT) {
                this.leftRocket.y += Pong.ROCKET_SPEED;
            }
        }
        if(Pong.KEYS[KeyEvent.VK_UP]) {
            if(this.rightRocket.y > 0) {
                this.rightRocket.y -= Pong.ROCKET_SPEED;
            }
        }
        if(Pong.KEYS[KeyEvent.VK_DOWN]) {
            if(this.rightRocket.y + Pong.ROCKET_HEIGHT < Pong.HEIGHT) {
                this.rightRocket.y += Pong.ROCKET_SPEED;
            }
        }

        if(this.gameStarted) {
            this.ball.x += Pong.BALL_SPEED_X;
            this.ball.y += Pong.BALL_SPEED_Y;

            if(this.ball.y + Pong.BALL_SIZE > Pong.HEIGHT) {
                Pong.BALL_SPEED_Y *= -1;
            }
            if(this.ball.y + (Pong.BALL_SIZE / 2) < 0) {
                Pong.BALL_SPEED_Y *= -1;
            }

            if(this.ball.x + (Pong.BALL_SIZE / 2) > Pong.WIDTH) {
                this.leftScore++;
                Pong.BALL_SPEED_X *= -1;

                this.ball.x = Pong.BALL_START_X;
                this.ball.y = Pong.BALL_START_Y;
            }
            if(this.ball.x < 0) {
                this.rightScore++;
                Pong.BALL_SPEED_X *= -1;

                this.ball.x = Pong.BALL_START_X;
                this.ball.y = Pong.BALL_START_Y;
            }

            if(this.ball.x < this.leftRocket.x + Pong.ROCKET_WIDTH && this.ball.y > this.leftRocket.y && this.ball.y <= this.leftRocket.y + Pong.ROCKET_HEIGHT) {
                Pong.BALL_SPEED_X *= -1;
            }

            if(this.ball.x + Pong.BALL_SIZE > this.rightRocket.x && this.ball.y > this.rightRocket.y && this.ball.y <= this.rightRocket.y + Pong.ROCKET_HEIGHT) {
                Pong.BALL_SPEED_X *= -1;
            }
        }
    }

    private void start() {
        new Thread(this).start();
    }

    public static void main(String[] args) {
        Pong pong = new Pong();
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(pong);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        pong.start();
    }
}

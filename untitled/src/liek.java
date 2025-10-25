import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class liek extends JFrame {
    // 游戏常量
    private static final int WIDTH = 800;
    private static final int HEIGHT = 300;
    private static final int GROUND = 250;
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = -15;

    // 游戏状态
    private final boolean isRunning = true;
    private boolean isJumping = false;
    private boolean isGameOver = false;
    private int score = 0;
    private int highScore = 0;
    private int gameSpeed = 5;

    // 玩家角色
    private final int dinoX = 100;
    private int dinoY = GROUND;
    private final int dinoWidth = 40;
    private final int dinoHeight = 60;
    private int dinoVelocity = 0;

    // 障碍物
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final Random random = new Random();
    private int obstacleTimer = 0;

    public liek() {
        setTitle("神秘小绿去冒险");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 键盘监听
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!isJumping && !isGameOver) {
                        isJumping = true;
                        dinoVelocity = JUMP_STRENGTH;
                    } else if (isGameOver) {
                        resetGame();
                    }
                }
            }
        });

        // 游戏循环
        Timer timer = new Timer(20, _ -> {
            if (isRunning) {
                update();
                repaint();
            }
        });
        timer.start();
    }

    private void update() {
        if (isGameOver) return;

        // 更新分数
        score++;
        if (score % 100 == 0) {
            gameSpeed++;
        }

        // 恐龙跳跃物理
        if (isJumping) {
            dinoY += dinoVelocity;
            dinoVelocity += GRAVITY;

            if (dinoY >= GROUND) {
                dinoY = GROUND;
                isJumping = false;
                dinoVelocity = 0;
            }
        }

        // 生成障碍物
        obstacleTimer++;
        if (obstacleTimer > 100 + random.nextInt(100)) {
            obstacles.add(new Obstacle(WIDTH, GROUND - random.nextInt(3) * 20, gameSpeed));
            obstacleTimer = 0;
        }

        // 更新障碍物位置‘
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            obstacle.update();

            // 碰撞检测
            if (checkCollision(obstacle)) {
                isGameOver = true;
                if (score > highScore) {
                    highScore = score;
                }
            }

            // 移除屏幕外的障碍物
            if (obstacle.x + obstacle.width < 0) {
                obstacles.remove(i);
                i--;
            }
        }
    }

    private boolean checkCollision(Obstacle obstacle) {
        Rectangle dinoRect = new Rectangle(dinoX, dinoY - dinoHeight, dinoWidth, dinoHeight);
        Rectangle obstacleRect = new Rectangle(obstacle.x, obstacle.y - obstacle.height, obstacle.width, obstacle.height);
        return dinoRect.intersects(obstacleRect);
    }

    private void resetGame() {
        isGameOver = false;
        isJumping = false;
        score = 0;
        gameSpeed = 5;
        dinoY = GROUND;
        dinoVelocity = 0;
        obstacles.clear();
        obstacleTimer = 0;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制地面
        g.setColor(Color.BLACK);
        g.drawLine(0, GROUND, WIDTH, GROUND);

        // 绘制恐龙
        g.setColor(Color.GREEN);
        g.fillRect(dinoX, dinoY - dinoHeight, dinoWidth, dinoHeight);

        // 绘制障碍物
        g.setColor(Color.RED);
        for (Obstacle obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y - obstacle.height, obstacle.width, obstacle.height);
        }

        // 绘制分数
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("分数: " + score, 20, 30);
        g.drawString("最高分: " + highScore, 20, 60);

        // 游戏结束画面
        if (isGameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("游戏结束!", WIDTH / 2 - 100, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("按空格键重新开始", WIDTH / 2 - 80, HEIGHT / 2 + 40);
        }
    }
    private class Obstacle {
        int x;
        int y;
        int width;
        int height;
        int speed;

        public Obstacle(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.width = 20 + random.nextInt(20);
            this.height = 20 + random.nextInt(30);
            this.speed = speed;
        }

        public void update() {
            x -= speed;
        }
    }

    static void main() {
        SwingUtilities.invokeLater(() -> {
            liek game = new liek();
            game.setVisible(true);
        });
    }
}
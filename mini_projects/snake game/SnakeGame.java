
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

public class SnakeGame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StartPanel startPanel;
    private GamePanel gamePanel;

    public SnakeGame() {
        setTitle("🐍 Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        startPanel = new StartPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(startPanel, "Start");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showGame() {
        gamePanel.resetGame();
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocusInWindow();
    }

    public void showStart() {
        cardLayout.show(mainPanel, "Start");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}
class StartPanel extends JPanel {

    public StartPanel(SnakeGame frame) {
        setLayout(null);
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.BLACK);

        JLabel title = new JLabel("🐍 Snake Game", SwingConstants.CENTER);
        title.setBounds(0, 100, 600, 60);
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(Color.GREEN);
        add(title);

        JButton playBtn = new JButton("Play");
        playBtn.setBounds(220, 250, 160, 50);
        playBtn.setFont(new Font("Arial", Font.BOLD, 25));
        add(playBtn);

        JButton quitBtn = new JButton("Quit");
        quitBtn.setBounds(220, 320, 160, 50);
        quitBtn.setFont(new Font("Arial", Font.BOLD, 25));
        add(quitBtn);

        playBtn.addActionListener(e -> frame.showGame());
        quitBtn.addActionListener(e -> System.exit(0));
    }
}


class GamePanel extends JPanel implements ActionListener {

    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int DELAY = 200;

    private final int x[] = new int[(SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE)];
    private final int y[] = new int[(SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE)];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;
    private JButton restartButton;

    private SnakeGame frame;
    private int highScore;

    public GamePanel(SnakeGame frame) {
        this.frame = frame;
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null);

        addKeyListener(new MyKeyAdapter());
        setFocusable(true);

        restartButton = new JButton("Restart");
        restartButton.setBounds(SCREEN_WIDTH/2 - 60, SCREEN_HEIGHT/2 + 80, 120, 40);
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setVisible(false);
        add(restartButton);
        restartButton.addActionListener(e -> resetGame());

        loadHighScore();
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        restartButton.setVisible(false);
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void resetGame() {
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        for (int i = 0; i < x.length; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
        repaint();
    }

    private void loadHighScore() {
        try {
            File file = new File("highscore.txt");
            if(file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                highScore = Integer.parseInt(br.readLine());
                br.close();
            } else highScore = 0;
        } catch(Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("highscore.txt"));
            bw.write("" + highScore);
            bw.close();
        } catch(Exception e) {}
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if(running) {
            // Draw apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for(int i = 0; i < bodyParts; i++){
                if(i == 0) g.setColor(Color.GREEN);
                else g.setColor(new Color(0,180,60));
                g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 8,8);
            }

            // Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.BOLD, 24));
            g.drawString("Score: "+applesEaten+"  High Score: "+highScore, 20, 30);

        } else {
            // Update high score
            if(applesEaten > highScore) {
                highScore = applesEaten;
                saveHighScore();
            }

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2 - 50);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: "+applesEaten))/2, SCREEN_HEIGHT/2);

            restartButton.setVisible(true);
        }
    }

    private void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        for(int i=bodyParts;i>0;i--){
            x[i]=x[i-1]; y[i]=y[i-1];
        }
        switch(direction){
            case 'U' -> y[0]-=UNIT_SIZE;
            case 'D' -> y[0]+=UNIT_SIZE;
            case 'L' -> x[0]-=UNIT_SIZE;
            case 'R' -> x[0]+=UNIT_SIZE;
        }
    }

    private void checkApple(){
        if(x[0]==appleX && y[0]==appleY){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCollisions(){
        for(int i=bodyParts;i>0;i--){
            if(x[0]==x[i] && y[0]==y[i]){
                running=false;
                timer.stop();
                break;
            }
        }
        if(x[0]<0 || x[0]>=SCREEN_WIDTH || y[0]<0 || y[0]>=SCREEN_HEIGHT){
            running=false;
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT -> { if(direction!='R') direction='L'; }
                case KeyEvent.VK_RIGHT -> { if(direction!='L') direction='R'; }
                case KeyEvent.VK_UP -> { if(direction!='D') direction='U'; }
                case KeyEvent.VK_DOWN -> { if(direction!='U') direction='D'; }
            }
        }
    }
}

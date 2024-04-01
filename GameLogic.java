import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameLogic extends JFrame implements ActionListener{
    private Dimension boardSize;
    private final Font font = new Font("Arial", Font.BOLD, 14);

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private int currLevel = 0;
    private final int MaxGhosts = 9;
    private int N_GHOST = 4;
    private int [] dx, dy;
    private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;
    private int lives;
    private static int score;
    private Image ghost, heart, pacmanClose, pacmanUp, pacmanLeft, pacmanRight, pacmanDown;
    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;
    private final int maxSpeed = 6;
    private int PACMAN_CURR_SPEED = 3;
    private int GHOST_CURR_SPEED = 3;
    private boolean speedBoost = false;
    private boolean eatBoost = false;
    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private short[] screenData;
    private Timer timer;
    private Timer speedBoostTimer;

    /**
     * The level data for the first level
     * 0 = blue               * 1 = left boarder
     * 2 = top boarder        * 4 = right boarder
     * 8 = bottom boarder     * 16 = white dots
     * 32 = power pellets     * 64 = speed boost
     */
    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final short level1Data[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final short level2Data[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final short level3Data[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final short[][] levels = {levelData, level1Data, level2Data, level3Data};




    private boolean isDead = false;
    private boolean running = false;
    private boolean isPaused = false;

    /**
     * Loads images from the "images" folder.
     */    private void loadImages() {
        ghost = new ImageIcon("images/ghost.png").getImage();
        heart = new ImageIcon("images/heart.png").getImage();
        pacmanClose = new ImageIcon("images/pacman.png").getImage();
        pacmanUp = new ImageIcon("images/up.png").getImage();
        pacmanDown = new ImageIcon("images/down.png").getImage();
        pacmanLeft = new ImageIcon("images/left.png").getImage();
        pacmanRight = new ImageIcon("images/right.png").getImage();
    }

    /**
     * Initializes the game by loading images, initializing variables, adding key listener, setting focus, and starting the game.
     */
    public GameLogic(){
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }

    /**
     * Initializes the game by setting lives, score, level, number of ghosts, and the speed of Pacman and ghosts.
     */
    private void initGame() {
        lives = 3;
        score += 0;
        initLevel();
        N_GHOST = 4;
        PACMAN_CURR_SPEED = 3;
        GHOST_CURR_SPEED = 3;
    }

    /**
     * Initializes the level by setting the screen data according to the current level.
     */
    private void initLevel() {
        int i;
        for(i = 0; i < N_BLOCKS * N_BLOCKS; i++){
            screenData[i] = levels[currLevel][i];
        }
        continueLevel();
    }

    /**
     * Continues the level by setting the positions and speeds of ghosts and Pacman.
     */
    private void continueLevel() {
        int dx = 1;
        int random;

        for(int i = 0 ; i < N_GHOST; i++){
            ghost_y[i] = 4*BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;

            random = (int)(Math.random() * (currLevel + 1));

            if(random > ghostSpeed[i]) {
                random = ghostSpeed[i];
            }
            ghostSpeed[i] = random;
        }

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dy = 0;
        req_dx = 0;
        isDead = false;
    }

    /**
     * Checks the current state of the maze. It checks if Pacman is on a grid cell, and if so,
     * it checks if there are any special items on the cell (like speed boost or power pellets).
     * It also checks if Pacman can move in the requested direction, and updates Pacman's position accordingly.
     */
    private void checkMaze() {
        int pos;
        int ch = 0;


        if(pacman_x % BLOCK_SIZE == 0 && (pacman_y & BLOCK_SIZE) == 0) {
            pos = pacman_x / BLOCK_SIZE * N_BLOCKS + (int) pacman_y / BLOCK_SIZE;
            ch = screenData[pos];

            //speed boost
            if ((ch & 64) != 0) {
                screenData[pos] = (short) (ch & 63);
                speedBoost = true;
                PACMAN_CURR_SPEED = 7;
                score += 2;

                speedBoostTimer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        speedBoost = false;
                        PACMAN_CURR_SPEED = 3;
                        speedBoostTimer.stop();
                    }
                });
                speedBoostTimer.setRepeats(false);
            }

            //eating boost
            if((ch & 32) != 0){
                screenData[pos] = (short) (ch & 95);
                eatBoost = true;
                score += 2;

                speedBoostTimer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        speedBoost = false;
                        eatBoost = true;
                        speedBoostTimer.stop();
                    }
                });
                speedBoostTimer.setRepeats(false);

            }
            //white dots
            if((ch & 16) != 0){
                screenData[pos] = (short) (ch & 111);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_CURR_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_CURR_SPEED * pacmand_y;
    }

    /**
     * Moves the ghosts in the maze. For each ghost, it checks if the ghost is on a grid cell,
     * and if so, it calculates the possible moves for the ghost. It then updates the ghost's position accordingly.
     */
    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;

        for(int i =0 ; i < N_GHOST ; i++){
            if( (ghost_x[i] % BLOCK_SIZE) == 0 && (ghost_y[i] & BLOCK_SIZE) == 0){
                pos = ghost_x[i] / BLOCK_SIZE * N_BLOCKS + ghost_y[i] / BLOCK_SIZE;

                count = 0;
                /**
                 * Check for possible moves
                 * 1. Check if there is a wall
                 * 2. Check if the ghost is not moving in the opposite direction
                 * 3. Check if the ghost is not moving in the same direction
                 */
                if((screenData[pos] & 1) == 0 && ghost_dx[i] != 1){
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }
                if((screenData[pos] & 2) == 0 && ghost_dy[i] != 1){
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }
                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }
                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }


                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12) && running) {
                if (eatBoost) {
                    // remove ghost
                    ghost_x[i] = -1;
                    ghost_y[i] = -1;
                } else {
                    isDead = true;
                }
            }
        }
    }

    /**
     * Draws a ghost at the specified position.
     */
    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost,x,y,this);
    }

    /**
     * Draws Pacman in the direction it is currently moving.
     */
    private void drawPacman(Graphics2D g2d) {

        if(req_dx == -1){
            g2d.drawImage(pacmanLeft, pacman_x + 1, pacman_y + 1, this);
        }else if (req_dx == 1) {
            g2d.drawImage(pacmanRight, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(pacmanUp, pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(pacmanDown, pacman_x + 1, pacman_y + 1, this);
        }
    }

    /**
     * Moves Pacman in the maze. It checks if all the dots in the maze have been eaten,
     * and if so, it increases the score and initializes the next level.
     */
    private void movePacman() {
        boolean finished = true;
        int i = 0;
        while(i < N_BLOCKS * N_BLOCKS && finished){
            if((screenData[i] & 240) != 0){
                finished = false;
            }
            i++;
        }
        if(finished){
            score += (currLevel+1)*20;

            if(N_GHOST < MaxGhosts - 1){
                N_GHOST += 2;
            }else if(N_GHOST == MaxGhosts - 1){
                N_GHOST++;
            }
            initLevel();
        }
    }

    /**
     * Triggers the death sequence when Pacman is dead. It decreases the number of lives,
     * resets the level, and checks if the game is over.
     */
    private void death() {
        lives--;
        currLevel = 1;
        if(lives == 0){
            running = false;
        }
        continueLevel();
    }

    /**
     * Paints the game components on the screen. It draws the maze, the score, and depending on the game state,
     * it either plays the game or shows the intro screen.
     */
    public void paintComponent(Graphics g){
        super.paintComponents(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, boardSize.width, boardSize.height);

        drawMaze(g2d);
        drawScore(g2d);

        if(running) {
            playGame(g2d);
        }
        else{
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Shows the intro screen with the game title.
     */
    private void showIntroScreen(Graphics2D g2d) {
        String show = "GIDI'S MEGA PACMAN GAME!!!";
        g2d.setColor(Color.yellow);
        g2d.drawString(show, (SCREEN_SIZE) / 2, SCREEN_SIZE / 4);
    }

    /**
     * This method is responsible for the main gameplay loop. It checks if Pacman is dead, and if so, it triggers the death sequence.
     * If Pacman is not dead, it moves Pacman, draws Pacman, moves the ghosts, and checks the state of the maze.
     *
     * @param g2d Graphics2D object used for drawing.
     */
    private void playGame(Graphics2D g2d) {
        if(isDead){
            death();
        }
        else{
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void drawScore(Graphics2D g2d) {
        Font smallFont = new Font("Helvetica", Font.BOLD, 14);
        g2d.setFont(smallFont);
        g2d.setColor(new Color(5, 151, 79));
        String s = "Score: " + score;
        g2d.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for(int i = 0; i < lives ; i++){
            g2d.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        int i = 0;
        int x, y;

        for(y = 0; y < SCREEN_SIZE ; y+=BLOCK_SIZE){
            for(x=0; x <SCREEN_SIZE ; x+= BLOCK_SIZE){

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));

                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }
                if((screenData[i] & 32) != 0){
                    g2d.setColor(new Color(255,255,0));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }
                if((screenData[i] & 64) != 0){
                    g2d.setColor(new Color(255,0,0));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }

    private void initVariables() {
        screenData = new short[N_BLOCKS * N_BLOCKS];
        boardSize = new Dimension(400, 400);
        dx = new int[4];
        dy = new int[4];
        ghost_x = new int[MaxGhosts];
        ghost_dx = new int[MaxGhosts];
        ghost_y = new int[MaxGhosts];
        ghost_dy = new int[MaxGhosts];
        ghostSpeed = new int[N_GHOST];
        timer = new Timer(40, this);
        timer.start();
        //timer.restart();

    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e){
            if(running){
                int key = e.getKeyCode();
                if(key == KeyEvent.VK_LEFT){
                    req_dx = -1;
                    req_dy = 0;
                }
                else if(key == KeyEvent.VK_UP){
                    req_dx = 0;
                    req_dy = -1;
                }
                else if(key == KeyEvent.VK_DOWN){
                    req_dx = 0;
                    req_dy = 1;
                }
                else if(key == KeyEvent.VK_RIGHT){
                    req_dx = 1;
                    req_dy = 0;
                }
                else if(key == KeyEvent.VK_SPACE && timer.isRunning()){
                    running = false;
                }
                else{
                    if(key == KeyEvent.VK_ESCAPE){
                        running = true;
                        initGame();
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

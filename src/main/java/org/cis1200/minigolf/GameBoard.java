package org.cis1200.minigolf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.sqrt;

/**
 * This class manages the game logic and rendering. It includes:
 * - Model: MiniGolf game state
 * - View: Rendering of the board and balls
 * - Controller: Handling user input and orchestrating turns
 *
 * It also integrates Q-learning training logic for the computer player (Player
 * 2).
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private JLabel status; // Current status text

    private Timer p1timer;
    private Timer p2timer;

    // Board constants
    public static final int BOARD_WIDTH = 300;
    public static final int BOARD_HEIGHT = 500;
    private static final int ROWS = 100;
    private static final int COLS = 50;

    private final double fairwayfriction = 0.9;

    private static String[][] hole1 = GolfCourse.currentHoleIndex() == 0
            ? GolfCourse.hole0()
            : GolfCourse.getCurrentHole().layout();

    // Colors for different terrains
    public static final Color ROUGH = new Color(127, 140, 127, 36);
    public static final Color GREEN = new Color(124, 252, 0);
    public static final Color SAND = new Color(244, 164, 96);
    public static final Color FAIRWAY = new Color(38, 141, 38);
    public static final Color WATERS = new Color(29, 29, 237);
    public static final Color FLAG = new Color(0, 0, 0);

    Player1 player1;
    Player1 player2;
    boolean isPlayer1Turn;
    boolean hole0;
    QLearning ql;

    private boolean trainingMode = true;
    private int currentEpisode = 0;
    private int trainingEpisodes = 500;
    private int shotindex;
    private boolean isPlayer1InHole = false;
    private boolean isPlayer2InHole = false;

    private boolean hideBall1 = false;
    private boolean hideball2 = false;

    // Starting coordinates for training
    private double currentcompx = 150;
    private double currentcompy = 470;
    int currentShotIndex;

    ArrayList loadedSequence = new ArrayList<Shot>();

    public GameBoard(JLabel statusInit, JLabel powerlabel) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setFocusable(true);

        player1 = new Player1(150, 470);
        player2 = new Player1(150, 470);

        isPlayer1Turn = true;
        hole0 = true;
        ql = new QLearning();

        shotindex = 0;

        GolfCourse.initializeHoles();
        hole1 = GolfCourse.getCurrentHole().layout();

        loadBestSequence(GolfCourse.currentHoleIndex());
        status = statusInit;

        // Start training at the beginning
        startTraining();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (player1.angle >= 0) {
                            player1.angle -= 15;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (player1.angle <= 345) {
                            player1.angle += 15;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (player1.power < 96) {
                            player1.power += 5;
                            powerlabel.setText(player1.power + "% - Power");
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (player1.power > 4) {
                            player1.power -= 5;
                            powerlabel.setText(player1.power + "% - Power");
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        shotPhysics((int) player1.power, (int) player1.angle, player1);
                        break;
                    default:

                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                repaint(); // repaints the game board
            }
        });
    }

    private void loadBestSequence(int holeIndex) {

        loadedSequence = new ArrayList<>();
        shotindex = 0;
        String filename = "";

        switch (GolfCourse.currentHoleIndex()) {
            case 0:
                filename = "best_sequence_hole_0.txt";
                break;
            case 1:
                filename = "best_sequence_hole_1.txt";
                break;
            case 2:
                filename = "best_sequence_hole_2.txt";
                break;
            case 3:
                filename = "best_sequence_hole_3.txt";
                break;
            case 4:
                filename = "best_sequence_hole_4.txt";
                break;
            default:
                System.err.println("Invalid hole index: " + GolfCourse.currentHoleIndex());
                break;
        }
        // String filename = "best_sequence_hole_" + holeIndex + ".txt";
        System.out.println(filename);
        System.out.println(GolfCourse.currentHoleIndex());

        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextInt()) {
                int angle = sc.nextInt();
                int power = sc.nextInt();
                loadedSequence.add(new Shot(angle, power));
            }
        } catch (Exception e) {
            System.err.println("Could not load sequence for hole " + holeIndex);
            e.printStackTrace();
        }
    }

    private void drawBallAndArrow(Graphics g) {
        g.setColor(Color.WHITE);
        int ballDiameter = 10;
        g.fillOval(
                (int) player1.xcoordinate - ballDiameter / 2,
                (int) player1.ycoordinate - ballDiameter / 2,
                ballDiameter,
                ballDiameter
        );

        // draw the arrow if it's Player 1's turn
        if (isPlayer1Turn) {
            drawArrow(
                    g, (int) player1.xcoordinate, (int) player1.ycoordinate - 20,
                    (int) player1.angle
            );
        }
    }

    private void shotPhysics(int power, int angle, Player1 player) {
        player.angle = angle;
        player.power = power;

        // .println(player.angle + " player angle");
        // System.out.println(player.power + " player power");

        double initialVelocity = power / 4.0;
        double angleRadians = Math.toRadians(angle);

        // Angle 0° = facing right, 90° = facing up
        // vx = cos(angleRadians), vy = sin(angleRadians)
        // positive vy means "up" mathematically
        player.vx = initialVelocity * Math.cos(angleRadians);
        player.vy = initialVelocity * Math.sin(angleRadians);

        if (player == player1) {
            startBallMovementP1(player1);
        } else {
            startBallMovementP2(player2, () -> {
            });
        }
    }

    private void handleCollisions(Player1 player) {
        // wall collisions
        if (player.xcoordinate <= 10 || player.xcoordinate >= BOARD_WIDTH - 10) {
            player.vx = -player.vx * 0.8; // coefficient of resititution
            player.xcoordinate = Math.min(Math.max(player.xcoordinate, 15), BOARD_WIDTH - 15);
        }
        if (player.ycoordinate <= 10 || player.ycoordinate >= BOARD_HEIGHT - 10) {
            player.vy = -player.vy * 0.8;
            player.ycoordinate = Math.min(Math.max(player.ycoordinate, 15), BOARD_HEIGHT - 15);
        }

        int cellWidth = BOARD_WIDTH / COLS;
        int cellHeight = BOARD_HEIGHT / ROWS;

        int ballGridX = (int) (player.xcoordinate / cellWidth);
        int ballGridY = (int) (player.ycoordinate / cellHeight);

        GolfCourse.HoleDesign currentHole = GolfCourse.getCurrentHole();
        GolfCourse.Traps trap = currentHole.withinTrap(ballGridX, ballGridY);

        switch (trap) {
            case SANDS:
                player.vx *= 0.5;
                player.vy *= 0.5;
                break;
            case WATERS:
                player.vx = 0;
                player.vy = 0;
                Water.resetBallToNearestDrop(player);
                break;
            default:
                break;
        }

        if (currentHole.isBallInHole(ballGridX, ballGridY)) {
            if (player == player1) {
                isPlayer1InHole = true;
                hideBall1 = true;
                repaint();
                System.out.println(isPlayer1InHole + "Player1 in hole?");
                System.out.println(isPlayer2InHole + "Player 2 in hole?");
                player1.resetPosition(
                        150, 470
                );
                // System.out.println("Player 1 is in the hole!");
            } else if (player == player2) {
                isPlayer2InHole = true;
                hideball2 = true;
                repaint();
                System.out.println(isPlayer1InHole + "Player1 in hole?");
                System.out.println(isPlayer2InHole + "Player 2 in hole?");
                player2.resetPosition(
                        0, 500

                );
                // System.out.println("Player 2 is in the hole!");
            }

            // check if both players are in the hole
            if (isPlayer1InHole && isPlayer2InHole) {

                System.out.println(loadedSequence);
                endHole();
                hideBall1 = false;
                hideball2 = false;
            }
        }
    }

    private void switchTurns() {
        // System.out.println(Player2.xcoordinate + " Player 2 = x " +
        // Player2.ycoordinate + " PLAYER 2 = y");
        // System.out.println(Player1.xcoordinate + " Player 1 = x " +
        // Player1.ycoordinate + " PLAYER 1 = y");
        isPlayer1Turn = !isPlayer1Turn;
        // If it's now the computer's turn and training is done or ongoing
        if (!isPlayer1Turn) {
            computerPlayerTurn();
        }
    }

    private void endHole() {
        // end the episode
        if (GolfCourse.currentHoleIndex() > 2) {
            displayWinner();
            returnToHomeScreen();
            return;
        }

        // Normal game flow after training
        isPlayer1InHole = false;
        isPlayer2InHole = false;
        GolfCourse.nextHole();
        hole1 = GolfCourse.getCurrentHole().layout();
        loadBestSequence(GolfCourse.currentHoleIndex());
        repaint();
        player1.resetPosition(150, 470);
        player2.resetPosition(150, 470);

    }

    private void displayWinner() {
        String winner;
        if (player1.getStrokes() < (player2.getStrokes() - 1)) {
            winner = "Player 1 wins!";
        } else if (player1.getStrokes() > (player2.getStrokes() - 1)) {
            winner = "Player 2 wins!";
        } else {
            winner = "It's a tie!";
        }
        JOptionPane.showMessageDialog(this, winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void returnToHomeScreen() {
        SwingUtilities.invokeLater(() -> {
            HomeScreen homeScreen = new HomeScreen();
            homeScreen.setVisible(true);
        });

    }

    public double distanceToHole() {
        int holeX = GolfCourse.getCurrentHole().hole().getX();
        int holeY = GolfCourse.getCurrentHole().hole().getY();
        double dx = player2.xcoordinate - holeX;
        double dy = player2.ycoordinate - holeY;
        return sqrt(dx * dx + dy * dy);
    }

    private void computerPlayerTurn() {

        // System.out.println(Player2.xcoordinate + " x" + Player2.ycoordinate + " y");
        System.out.println(currentShotIndex + "CURRENTSHOTINDEX");

        int holeGridX = GolfCourse.getCurrentHole().hole().getX();
        int holeGridY = GolfCourse.getCurrentHole().hole().getY();

        int cellWidth = BOARD_WIDTH / COLS;
        int cellHeight = BOARD_HEIGHT / ROWS;

        double holePixelX = holeGridX * cellWidth;
        double holePixelY = holeGridY * cellHeight;

        int currentX = (int) player2.xcoordinate;
        int currentY = (int) player2.ycoordinate;

        double dx = currentX - holePixelX;
        double dy = currentY - holePixelY;

        if (!isPlayer2InHole) {
            if (shotindex < loadedSequence.size()) {
                Shot s = (Shot) loadedSequence.get(shotindex++);

                System.out.println(s.power + "   Current Loaded Computer Shot");
                System.out.println(s.angle + "   Current Loaded Computer shot angle");

                // System.out.println(distanceToHole());
                // System.out.println(Math.sqrt(Math.pow(currentX - 95, 2) + Math.pow(currentY -
                // 15, 2)));

                // System.out.println(Player1.xcoordinate + "P1 X " + Player1.ycoordinate + " P2
                // Y");

                if (distanceToHole() < 250.0) {

                    double angleRad = Math.atan2(dy, dx);

                    double angleDeg = Math.toDegrees(angleRad) - 3;
                    // System.out.println(angleDeg);
                    // if (angleDeg < 0) angleDeg += 360;

                    double distance = sqrt(dx * dx + dy * dy);
                    int basePower = (int) Math.min(distance / 2, 90);
                    basePower = Math.max(basePower, 50);
                    shotPhysics(basePower, (int) angleDeg, player2);
                } else {

                    shotPhysics(s.power, s.angle, player2);
                }
            } else {
                isPlayer1Turn = true;
                paintComponent(getGraphics());
            }

            // shotPhysics(s.power, s.angle, player2);
            player2.incrementStrokes();
        }

    }

    private int getReward(int posX, int posY, States prevstate, States currentState) {
        int reward = 0;
        if (GolfCourse.getCurrentHole().isBallInHole(posX, posY)) {
            reward += 100;
        } else {
            double previousDistance = prevstate.distanceBinn();
            double currentDistance = currentState.distanceBinn();

            if (currentDistance < previousDistance) {
                reward += 5;
            } else {
                reward -= 2;
            }

            if (GolfCourse.getCurrentHole().withinTrap(posX, posY) != GolfCourse.Traps.NONE) {
                reward -= 60;
            }
        }
        return reward;
    }

    private int discretizeDistance(double distance) {
        int numBins = 5;
        double maxDistance = Math.hypot(COLS, ROWS);
        double binSize = maxDistance / numBins;
        return Math.min((int) (distance / binSize), numBins - 1);
    }

    private int discretizeAngle(double angle) {
        int angleDegrees = (int) Math.toDegrees(angle);
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }
        return (angleDegrees / 15) * 15; // discretize by 15 degrees
    }

    private void startTraining() {
        trainingMode = true;
        currentEpisode = 0;
        ql.setEpslion(1);
        runEpisode();
    }

    private void runEpisode() {
        // Reset state for the new episode
        player2.resetPosition(currentcompx, currentcompy);
        player1.resetPosition(150, 470);
        hole1 = GolfCourse.getCurrentHole().layout();
        isPlayer1Turn = false; // Computer (Player2) goes first in episodes
        computerPlayerTurn();
    }

    private void startBallMovementP1(Player1 player) {
        if (p1timer != null && p1timer.isRunning()) {
            p1timer.stop();
        }

        p1timer = new Timer(16, e -> {
            player1.xcoordinate -= player1.vx;
            player1.ycoordinate -= player1.vy;

            player1.vx *= fairwayfriction;
            player1.vy *= fairwayfriction;

            if (sqrt(player1.vx * player1.vx + player1.vy * player1.vy) < 0.1) {
                player1.vx = 0;
                player1.vy = 0;
                player1.incrementStrokes();
                status.setText(
                        "Player 1 strokes: " +
                                player1.getStrokes() + " Player 2 Strokes: "
                                + (player2.getStrokes() - 1)
                                + "    Hole" + (GolfCourse.currentHoleIndex() + 1)
                );

                p1timer.stop();
                switchTurns();
            }

            handleCollisions(player1);
            repaint();
        });

        p1timer.start();
    }

    private void startBallMovementP2(Player1 player, Runnable callback) {
        if (p2timer != null && p2timer.isRunning()) {
            p2timer.stop();
        }

        p2timer = new Timer(16, e -> {
            player2.xcoordinate -= player2.vx;
            player2.ycoordinate -= player2.vy;

            player2.vx *= fairwayfriction;
            player2.vy *= fairwayfriction;

            if (sqrt(player2.vx * player2.vx + player2.vy * player2.vy) < 0.1) {
                player2.vx = 0;
                player2.vy = 0;

                status.setText(
                        "Player 1 strokes: " + player1.getStrokes()
                                + " Player 2 Strokes: "
                                + (player2.getStrokes() - 1)
                                + "    Hole" + (GolfCourse.currentHoleIndex() + 1)
                );

                p2timer.stop();
                // Once the computer ball stops, switch turns
                switchTurns();
                callback.run();
            }

            handleCollisions(player2);
            repaint();
        });

        p2timer.start();
    }

    public void reset() {

        status.setText("Player 1's Strokes:" + player1.getStrokes());
        repaint();

        player1.resetPosition(150, 470);
        player2.resetPosition(150, 470);
        requestFocusInWindow();
    }

    private void drawPlayer(Graphics g, Player1 player, Color color) {
        int ballDiameter = 10;
        g.setColor(color);
        g.fillOval(
                (int) player.xcoordinate - ballDiameter / 2,
                (int) player.ycoordinate - ballDiameter / 2,
                ballDiameter,
                ballDiameter
        );
    }

    private void drawArrow(Graphics g, int x, int y, int angleDegrees) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5.0f));

        int arrowLength = 50;
        int arrowHeadSize = 10;

        double angleRadians = Math.toRadians(angleDegrees);
        int baseX = x - (int) (arrowLength * Math.cos(angleRadians));
        int baseY = y - (int) (arrowLength * Math.sin(angleRadians));

        g2d.drawLine(baseX, baseY, x, y);

        int leftX = x + (int) (arrowHeadSize * Math.cos(angleRadians + Math.PI / 6));
        int leftY = y + (int) (arrowHeadSize * Math.sin(angleRadians + Math.PI / 6));

        int rightX = x + (int) (arrowHeadSize * Math.cos(angleRadians - Math.PI / 6));
        int rightY = y + (int) (arrowHeadSize * Math.sin(angleRadians - Math.PI / 6));

        g2d.drawLine(baseX, baseY, leftX, leftY);
        g2d.drawLine(baseX, baseY, rightX, rightY);
    }

    private void paintGrid(String[][] grid, Graphics g) {
        super.paintComponent(g);
        int cellWidth = BOARD_WIDTH / COLS;
        int cellHeight = BOARD_HEIGHT / ROWS;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                switch (hole1[row][col]) {
                    case "R":
                        g.setColor(ROUGH);
                        break;
                    case "F":
                        g.setColor(FAIRWAY);
                        break;
                    case "G":
                        g.setColor(GREEN);
                        break;
                    case "S":
                        g.setColor(SAND);
                        break;
                    case "W":
                        g.setColor(WATERS);
                        break;
                    case "H":
                        g.setColor(FLAG);
                        break;
                    default:
                        g.setColor(Color.BLACK);
                        break;
                }
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
        drawBallAndArrow(g);
        drawComputerPlayer(g);
    }

    private void drawComputerPlayer(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) player2.xcoordinate, (int) player2.ycoordinate, 10, 10);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintGrid(hole1, g);

        drawPlayer(g, player1, Color.WHITE);

        drawComputerPlayer(g);

        drawArrow(
                g, (int) player1.xcoordinate, (int) player1.ycoordinate - 20,
                (int) player1.angle
        );

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}

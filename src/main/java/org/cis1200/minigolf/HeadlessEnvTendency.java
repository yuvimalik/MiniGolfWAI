package org.cis1200.minigolf;

public class HeadlessEnvTendency {

    private final int cols = 50;
    private final int rows = 100;
    private final double fairwayfriction = 0.9;

    private Player1 player2;
    private double currentcompx = 150;
    private double currentcompy = 470;
    private boolean waterHit = false;
    private static final int BOARD_WIDTH = 300;
    private static final int BOARD_HEIGHT = 500;

    double ballX, ballY;

    public HeadlessEnvTendency() {
        GolfCourse.initializeHoles();
    }

    public Player1 getPlayer2() {
        return player2;
    }

    public void reset() {
        GolfCourse.resetHoleIndex();
        player2 = new Player1((int) currentcompx, (int) currentcompy);
        waterHit = false;
    }

    public BaselineShot computeBaselineShot() {
        int holeX = GolfCourse.getCurrentHole().hole().getX() + 80;
        int holeY = GolfCourse.getCurrentHole().hole().getY() + 10;

        int currentX = (int) player2.xcoordinate;
        int currentY = (int) player2.ycoordinate;

        double dx = currentX - holeX;
        double dy = currentY - holeY;
        double angleRad = Math.atan2(dy, dx);

        double angleDeg = Math.toDegrees(angleRad);
        System.out.println(angleDeg);
        // if (angleDeg < 0) angleDeg += 360;

        double distance = Math.sqrt(dx * dx + dy * dy);
        int basePower = (int) Math.min(distance / 2, 90);
        basePower = Math.max(basePower, 20);
        int baseAngle = (int) angleDeg;

        return new BaselineShot(baseAngle, basePower);
    }

    public void setHole(int holeIndex) {
        // Set the current hole index
        GolfCourse.setHoleIndex(holeIndex);

        // Retrieve the hole design from GolfCourse
        // GolfCourse.currentHole = GolfCourse.holes.get(holeIndex);

        // Initialize ball position at the starting point (e.g., bottom center)
        ballX = BOARD_WIDTH / 2.0; // Centered horizontally
        ballY = BOARD_HEIGHT - 30; // Near the bottom of the board

        // Log the hole setup
        System.out.println("Set to Hole " + holeIndex);
    }

    public double simulateSequence(java.util.List<Shot> shots) {
        reset();
        for (Shot shot : shots) {
            boolean holeReached = simulateShot(shot.power, shot.angle);
            if (waterHit) {
                // Water encountered ends sequence evaluation
                break;
            }
            if (holeReached) {
                return 0.0;
            }
        }
        return distanceToHole();
    }

    public boolean simulateShot(int power, int angle) {
        double initialVelocity = power / 4.0;
        double angleRadians = Math.toRadians(angle);

        player2.vx = initialVelocity * Math.cos(angleRadians);
        player2.vy = initialVelocity * Math.sin(angleRadians);

        while (true) {
            player2.xcoordinate += player2.vx;
            player2.ycoordinate += player2.vy;

            player2.vx *= fairwayfriction;
            player2.vy *= fairwayfriction;

            if (handleCollisions(player2)) {
                // If water hit, return false immediately
                if (waterHit) {
                    return false;
                }

            }

            double speed = Math.sqrt(player2.vx * player2.vx + player2.vy * player2.vy);
            boolean inHole = GolfCourse.getCurrentHole()
                    .isBallInHole((int) player2.xcoordinate, (int) player2.ycoordinate);
            if (inHole) {
                player2.incrementStrokes();
                return true;
            }
            if (speed < 0.1) {
                player2.vx = 0;
                player2.vy = 0;
                player2.incrementStrokes();
                return false;
            }
        }
    }

    public boolean handleCollisions(Player1 player) {
        if (player.xcoordinate <= 10 || player.xcoordinate >= 300 - 10) {
            player.vx = -player.vx * 0.9;
        }
        if (player.ycoordinate <= 10 || player.ycoordinate >= 500 - 10) {
            player.vy = -player.vy * 0.9;
        }

        int cellWidth = 300 / cols;
        int cellHeight = 500 / rows;

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
                waterHit = true;
                return true;
            default:
                break;
        }
        return false;
    }

    public double distanceToHole() {
        int holeX = GolfCourse.getCurrentHole().hole().getX() + 80;
        int holeY = GolfCourse.getCurrentHole().hole().getY() + 10;
        double dx = player2.xcoordinate - holeX;
        double dy = player2.ycoordinate - holeY;
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public boolean isWaterHit() {
        return waterHit;
    }
}

class BaselineShot {
    int angle;
    int power;

    public BaselineShot(int angle, int power) {
        this.angle = angle;
        this.power = power;
    }
}
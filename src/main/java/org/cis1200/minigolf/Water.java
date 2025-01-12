package org.cis1200.minigolf;

public class Water extends GameObject {
    public Water(int x, int y, int width, int height, String indicator) {
        super(x, y, width, height, indicator);
    }

    @Override
    public void affectsBall() {
        // Logic to reset the ball is handled in GameBoard
    }

    public static void resetBallToNearestDrop(Player1 player) {
        player.xcoordinate = 150; // Example drop point
        player.ycoordinate = 470;
        player.incrementStrokes(); // Penalty stroke
    }

}

package org.cis1200.minigolf;

public class SandTrap extends GameObject {

    public SandTrap(int x, int y, int width, int height, String indicator) {
        super(x, y, width, height, indicator);
    }

    @Override
    public void affectsBall() {
        // Logic to slow down the ball is handled in GameBoard
    }
}

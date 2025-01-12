package org.cis1200.minigolf;

public class Hole extends GameObject {

    public Hole(int x, int y, int width, int height, String indicator) {
        super(x, y, width, height, indicator);
    }

    @Override
    public void affectsBall() {
        // ball stops
    }

    public void endOfHole() {
        // logic for the end of the hole

    }

}

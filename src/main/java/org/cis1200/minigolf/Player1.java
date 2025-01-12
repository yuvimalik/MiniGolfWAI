package org.cis1200.minigolf;

public class Player1 {

    public double xcoordinate;
    public double ycoordinate;
    public double vx = 0.0;
    public double vy = 0.0;
    public double power = 50;
    public double angle = 90;

    private int strokes;

    public Player1(int xcoordinate, int ycoordinate) {
        strokes = 0;
        this.xcoordinate = xcoordinate;
        this.ycoordinate = ycoordinate;
    }

    public double getX() {
        return xcoordinate;
    }

    public double getY() {
        return ycoordinate;
    }

    public void resetPosition(double xc, double yc) {
        this.xcoordinate = xc;
        this.ycoordinate = yc;
        vx = 0.0;
        vy = 0.0;
    }

    public void incrementStrokes() {
        strokes++;
    }

    public int getStrokes() {
        return strokes;
    }

}

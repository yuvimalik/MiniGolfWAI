package org.cis1200.minigolf;

public abstract class GameObject {

    private int x;
    private int y;
    private int width;
    private int height;
    private String[][] currdesign;
    private String indicator;

    public GameObject(int x, int y, int width, int height, String indicator) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.indicator = indicator;
    }

    public String[][] addtodesign() {

        for (int i = x; i < width + x - 1; i++) {
            for (int j = y; j < y + height - 1; j++) {
                if (width + x < 300 && height + y < 500) {
                    currdesign[j][i] = indicator;
                }
            }
        }

        return currdesign;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isBallWithin(int ballX, int ballY) {

        return ballX >= x && ballX <= x + width &&
                ballY >= y && ballY <= y + height;

    }

    // Adds this object to the course design grid
    public void addToDesign(String[][] design) {
        for (int i = x; i < x + width && i < design[0].length; i++) {
            for (int j = y; j < y + height && j < design.length; j++) {
                design[j][i] = indicator;
            }
        }
    }

    public abstract void affectsBall();

}

package org.cis1200.minigolf;

import java.util.ArrayList;
import java.util.List;

public class GolfCourse {
    private static final int ROWS = 100;
    private static final int COLS = 50;
    private static int currenttHoleIndex = 0;
    public static final List<HoleDesign> HOLELIST = new ArrayList<>();

    public enum Traps {
        SANDS, WATERS, NONE
    }

    public static int currentHoleIndex() {
        return currenttHoleIndex;
    }

    public static void resetHoleIndex() {
        currenttHoleIndex = 0;
    }

    public static void setHoleIndex(int holeIndex) {
        currenttHoleIndex = holeIndex;
    }

    // default fairway grid
    public static String[][] generateBaseHole() {
        String[][] holeDesign = new String[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                holeDesign[i][j] = "F"; // Fairway
            }
        }
        return holeDesign;
    }

    // creates all the HOLELIST for the game
    public static void initializeHoles() {
        hole0();
        generateHole1();
        generateHole2();
        generateHole3();
        generateHole4();
        /*
         * holes.add(new HoleDesign(hole0()));
         * holes.add(new HoleDesign(generateHole1()));
         * holes.add(new HoleDesign(generateHole2()));
         * holes.add(new HoleDesign(generateHole3()));
         * holes.add(new HoleDesign(generateHole4()));
         */
    }

    public static HoleDesign getCurrentHole() {
        return HOLELIST.get(currentHoleIndex());
    }

    public static void nextHole() {
        setHoleIndex(currentHoleIndex() + 1);
    }

    public static String[][] hole0() {
        String[][] hole0 = generateBaseHole();
        HoleDesign holeDesign = new HoleDesign(hole0);
        holeDesign.sandTrapss.add(new SandTrap(10, 10, 10, 10, "S"));
        holeDesign.waterTrapss.add(new Water(15, 20, 15, 20, "W"));
        holeDesign.holem = new Hole(15, 5, 2, 2, "H");
        holeDesign.addTrapsToDesign();

        HOLELIST.add(holeDesign);

        return hole0;
    }

    private static String[][] generateHole1() {
        String[][] hole1 = generateBaseHole();
        HoleDesign holeDesign = new HoleDesign(hole1);
        holeDesign.sandTrapss.add(new SandTrap(20, 30, 10, 10, "S"));
        holeDesign.sandTrapss.add(new SandTrap(30, 40, 10, 20, "S"));
        holeDesign.sandTrapss.add(new SandTrap(30, 70, 20, 20, "S"));
        holeDesign.waterTrapss.add(new Water(5, 10, 15, 20, "W"));
        holeDesign.holem = new Hole(45, 5, 2, 2, "H");
        holeDesign.addTrapsToDesign();
        HOLELIST.add(holeDesign);
        return hole1;
    }

    private static String[][] generateHole2() {
        String[][] hole2 = generateBaseHole();
        HoleDesign holeDesign = new HoleDesign(hole2);

        holeDesign.sandTrapss.add(new SandTrap(10, 20, 15, 10, "S"));
        holeDesign.sandTrapss.add(new SandTrap(35, 40, 10, 10, "S"));
        holeDesign.waterTrapss.add(new Water(25, 15, 10, 20, "W"));
        holeDesign.waterTrapss.add(new Water(10, 40, 30, 10, "W"));
        holeDesign.holem = new Hole(45, 5, 2, 2, "H");

        holeDesign.addTrapsToDesign();
        HOLELIST.add(holeDesign);

        return hole2;
    }

    private static String[][] generateHole3() {
        String[][] hole3 = generateBaseHole();
        HoleDesign holeDesign = new HoleDesign(hole3);

        holeDesign.sandTrapss.add(new SandTrap(15, 35, 20, 10, "S"));
        holeDesign.waterTrapss.add(new Water(5, 5, 15, 15, "W"));
        holeDesign.waterTrapss.add(new Water(30, 25, 15, 15, "W"));
        holeDesign.waterTrapss.add(new Water(40, 10, 10, 60, "W"));
        holeDesign.holem = new Hole(50, 5, 2, 2, "H");

        holeDesign.addTrapsToDesign();
        HOLELIST.add(holeDesign);

        return hole3;
    }

    private static String[][] generateHole4() {
        String[][] hole4 = generateBaseHole();
        HoleDesign holeDesign = new HoleDesign(hole4);

        holeDesign.sandTrapss.add(new SandTrap(25, 20, 10, 15, "S"));
        // holeDesign.waterTrapss.add(new Water(10, 30, 30, 10, "W"));
        // holeDesign.waterTrapss.add(new Water(10, 40, 30, 30, "W"));
        holeDesign.holem = new Hole(50, 10, 2, 2, "H");

        holeDesign.addTrapsToDesign();
        HOLELIST.add(holeDesign);

        return hole4;
    }

    public static class HoleDesign {
        private String[][] layoutt;
        private List<SandTrap> sandTrapss = new ArrayList<>();
        private List<Water> waterTrapss = new ArrayList<>();
        private Hole holem;

        public HoleDesign(String[][] layout) {
            this.layoutt = layout;
        }

        public void addTrapsToDesign() {
            for (SandTrap sandTrap : sandTrapss) {
                sandTrap.addToDesign(layoutt);
            }
            for (Water water : waterTrapss) {
                water.addToDesign(layoutt);
            }
            if (holem != null) {
                holem.addToDesign(layoutt);
            }
        }

        public String[][] layout() {
            return layoutt;
        }

        public Hole hole() {
            return holem;
        }

        public List<SandTrap> sandTraps() {
            return sandTrapss;
        }

        public List<Water> waterTraps() {
            return waterTrapss;
        }

        // Detects which trap (if any) the ball is in
        public Traps withinTrap(int x, int y) {
            for (SandTrap sandTrap : sandTrapss) {
                if (sandTrap.isBallWithin(x, y)) {
                    return Traps.SANDS;
                }
            }
            for (Water water : waterTrapss) {
                if (water.isBallWithin(x, y)) {
                    return Traps.WATERS;

                }
            }
            return Traps.NONE;
        }

        public boolean isBallInHole(int x, int y) {
            return holem != null && holem.isBallWithin(x, y);
        }
    }

}

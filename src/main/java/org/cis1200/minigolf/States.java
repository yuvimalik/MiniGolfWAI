package org.cis1200.minigolf;

import java.io.Serializable;
import java.util.Objects;

public class States implements Serializable {
    private final int x;
    private final int y;
    private final int distanceBin;
    private final int angleBin;
    private final boolean inTrap;

    public States(int x, int y, int distanceBin, int angleBin, boolean inTrap) {
        this.x = x;
        this.y = y;
        this.distanceBin = distanceBin;
        this.angleBin = angleBin;
        this.inTrap = inTrap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof States states)) {
            return false;
        }

        return x == states.x &&
                y == states.y &&
                distanceBin == states.distanceBin &&
                angleBin == states.angleBin &&
                inTrap == states.inTrap;
    }

    public int distanceBinn() {
        return distanceBin;
    }

    public int angleBinn() {
        return angleBin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, distanceBin, angleBin, inTrap);
    }

    @Override
    public String toString() {
        return "States{x=" + x + ", y=" + y +
                ", distBin=" + distanceBin + ", angleBin=" + angleBin +
                ", inTrap=" + inTrap + "}";
    }
}
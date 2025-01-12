package org.cis1200.minigolf;

import java.io.Serializable;

public class Action implements Serializable {
    private final int power;
    private final int angle;

    public Action(int power, int angle) {
        this.power = power;
        this.angle = angle;
    }

    public int getPower() {
        return power;
    }

    public int getAngle() {
        return angle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Action)) {
            return false;
        }

        Action action = (Action) o;
        return power == action.power && angle == action.angle;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(power);
        result = 31 * result + Integer.hashCode(angle);
        return result;
    }

    @Override
    public String toString() {
        return "Action{power=" + power + ", angle=" + angle + "}";
    }
}

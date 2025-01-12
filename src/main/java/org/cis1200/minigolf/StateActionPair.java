package org.cis1200.minigolf;

import java.io.Serializable;
import java.util.Objects;

public class StateActionPair implements Serializable {
    private final States state;
    private final Action action;

    public StateActionPair(States state, Action action) {
        this.state = state;
        this.action = action;
    }

    public States getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StateActionPair)) {
            return false;
        }

        StateActionPair that = (StateActionPair) o;
        return Objects.equals(state, that.state) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, action);
    }

    @Override
    public String toString() {
        return "StateActionPair{" +
                "state=" + state +
                ", action=" + action +
                '}';
    }
}
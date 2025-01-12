package org.cis1200.minigolf;

import java.io.*;
import java.util.*;

public class QLearning {

    private Map<StateActionPair, Double> qTable;
    private final List<Action> actions;

    // Hyperparameters
    private double alpha = 0.1; // Learning rate
    private double gamma = 0.9; // Discount factor
    private double epsilon = 1.0; // Exploration rate
    private final double minEpsilon = 0.01;
    private final double epsilonDecay = 0.999;

    public QLearning() {
        qTable = new HashMap<>();
        actions = initializeActions();
    }

    public void setEpslion(int ep) {
        epsilon = ep;
    }

    /**
     * Instead of a large action space, use small adjustments.
     * For example:
     * Power adjustments: -10, 0, +10
     * Angle adjustments: -15°, 0°, +15°
     */
    private List<Action> initializeActions() {
        int[] angleAdjustments = { 0, 15, 40, 60, 110, 150, 190 };
        int[] powerAdjustments = { 20, 40, 60, 80, 100 };

        List<Action> actionList = new ArrayList<>();
        for (int pa : powerAdjustments) {
            for (int aa : angleAdjustments) {
                actionList.add(new Action(pa, aa));
            }
        }
        return actionList;
    }

    /**
     * Epsilon-greedy action selection:
     * - With probability epsilon, select a random action (exploration).
     * - Otherwise, select the best action known so far (exploitation).
     */
    public Action selectAction(States state) {
        if (Math.random() < epsilon) {
            return actions.get(new Random().nextInt(actions.size()));
        } else {
            return selectBestAction(state);
        }
    }

    /**
     * Select the action with the highest Q-value for the given state.
     */
    public Action selectBestAction(States state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        Action bestAction = actions.get(0);
        for (Action action : actions) {
            double q = getQValue(state, action);
            if (q > maxQ) {
                maxQ = q;
                bestAction = action;
            }
        }
        return bestAction;
    }

    /**
     * Update the Q-table using the Q-learning update rule:
     * Q(s,a) ← Q(s,a) + α [r + γ max_a' Q(s', a') - Q(s,a)]
     */
    public void updateQValue(States state, Action action, double reward, States nextState) {
        double currentQ = getQValue(state, action);
        double maxNextQ = getMaxQValue(nextState);
        double newQ = currentQ + alpha * (reward + gamma * maxNextQ - currentQ);
        qTable.put(new StateActionPair(state, action), newQ);
    }

    /**
     * Called at the end of an episode to decay epsilon, reducing exploration over
     * time.
     */
    public void endEpisode() {
        epsilon = Math.max(minEpsilon, epsilon * epsilonDecay);
    }

    private double getMaxQValue(States state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        for (Action a : actions) {
            double q = getQValue(state, a);
            if (q > maxQ) {
                maxQ = q;
            }
        }
        return (maxQ == Double.NEGATIVE_INFINITY) ? 0.0 : maxQ;
    }

    private double getQValue(States state, Action action) {
        return qTable.getOrDefault(new StateActionPair(state, action), 0.0);
    }

    /**
     * Save the Q-table to a file.
     */
    public void saveQTable(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(qTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the Q-table from a file.
     */
    public void loadQTable(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                qTable = (Map<StateActionPair, Double>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

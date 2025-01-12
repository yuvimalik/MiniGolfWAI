package org.cis1200.minigolf;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OfflineTendencyTraining {

    public static void main(String[] args) {
        int attempts = 25000; // number of sequences to try
        int maxShots = 10; // max length of sequence
        HeadlessEnvTendency env = new HeadlessEnvTendency();

        int totalHoles = GolfCourse.HOLELIST.size();

        for (int h = 0; h < totalHoles; h++) {

            GolfCourse.setHoleIndex(h);
            env.setHole(h);

            System.out.println("Training for Hole " + h);

            List<Shot> bestSequence = null;
            double bestReward = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < attempts; i++) {
                env.reset();
                List<Shot> candidateSequence = generateSequenceAimHole(env, maxShots);
                double reward = evaluateSequence(env, candidateSequence);

                // updating the best sequence if the reward is better
                if (reward > bestReward) {
                    bestReward = reward;
                    bestSequence = new ArrayList<>(candidateSequence);
                }
            }

            // saving the best sequence for the current hole
            String filename = "best_sequence_hole" + h + ".txt";
            saveSequence(bestSequence, filename);
            System.out.println("Hole " + h + " best reward: " + bestReward);
        }
    }

    /**
     * Generates a sequence of shots aimed at the hole.
     */
    public static List<Shot> generateSequenceAimHole(HeadlessEnvTendency env, int maxShots) {
        Random rand = new Random();
        List<Shot> seq = new ArrayList<>();
        int length = 1 + rand.nextInt(maxShots);

        for (int s = 0; s < length; s++) {
            BaselineShot baseline = env.computeBaselineShot();

            // computing angle with ±30° variation
            int angle = baseline.angle + rand.nextInt(61) - 30; // ±30°

            angle = clamp(angle, 0, 180);

            int power = baseline.power + rand.nextInt(21) - 10; // ±10 power
            power = clamp(power, 5, 100);

            seq.add(new Shot(angle, power));
            boolean holeOrStop = env.simulateShot(power, angle);

            if (env.isWaterHit()) {
                // water hit, end the sequence early
                break;
            }
            if (holeOrStop) {
                // hole reached, end the sequence
                break;
            }
        }

        return seq;
    }

    /**
     * Evaluates a sequence of shots based on the reward function.
     */
    public static double evaluateSequence(HeadlessEnvTendency env, List<Shot> seq) {
        env.reset();

        double prevDistance = env.distanceToHole();
        int shotsUsed = 0;
        int improvements = 0;
        boolean inHole = false;

        for (Shot s : seq) {
            shotsUsed++;
            boolean holeReached = env.simulateShot(s.power, s.angle);

            double currDistance = env.distanceToHole();
            if (currDistance < prevDistance) {
                improvements++;
            }

            if (holeReached) {
                inHole = true;
                break;
            }
            prevDistance = currDistance;
        }

        if (inHole) {
            return 1000 - (shotsUsed * 10);
        } else {
            double finalDistance = env.distanceToHole();
            return (-finalDistance) + (5 * improvements);
        }
    }

    /**
     * Clamps a value between a minimum and maximum.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Saves a sequence of shots to a file.
     */
    public static void saveSequence(List<Shot> sequence, String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            for (Shot shot : sequence) {
                fw.write(shot.angle + " " + shot.power + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

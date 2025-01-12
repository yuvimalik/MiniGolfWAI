package org.cis1200.minigolf;

import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

public class MiniGolfTesting {

    private static final String TEST_FILENAME = "test_sequence.txt";
    private HeadlessEnvTendency env;

    @BeforeEach
    public void setUp() {
        env = new HeadlessEnvTendency();
        GolfCourse.initializeHoles();
    }

    @AfterEach
    public void tearDown() {
        // Clean up any test files created
        File file = new File(TEST_FILENAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public void testClampBelowMinimum() {
        int result = OfflineTendencyTraining.clamp(-10, 0, 100);
        assertEquals(0, result, "Clamp should return minimum value when input is below minimum.");
    }

    @Test
    public void testClampWithinRange() {
        int result = OfflineTendencyTraining.clamp(50, 0, 100);
        assertEquals(50, result, "Clamp should return the input value when within range.");
    }

    @Test
    public void testClampAboveMaximum() {
        int result = OfflineTendencyTraining.clamp(150, 0, 100);
        assertEquals(100, result, "Clamp should return maximum value when input is above maximum.");
    }

    @Test
    public void testClampAtMinimum() {
        int result = OfflineTendencyTraining.clamp(0, 0, 100);
        assertEquals(
                0, result, "Clamp should return minimum value when input is exactly the minimum."
        );
    }

    @Test
    public void testClampAtMaximum() {
        int result = OfflineTendencyTraining.clamp(100, 0, 100);
        assertEquals(
                100, result, "Clamp should return maximum value when input is exactly the maximum."
        );
    }

    @Test
    public void testEvaluateSequenceHoleNotReached() {
        // creating a sequence that is unlikely to reach the hole
        java.util.List<Shot> sequence = new ArrayList<>();
        sequence.add(new Shot(0, 5)); // Shoots straight right with low power
        sequence.add(new Shot(180, 5)); // Shoots straight left with low power

        double reward = OfflineTendencyTraining.evaluateSequence(env, sequence);
        assertTrue(reward < 1000, "Reward should be less than 1000 when hole is not reached.");
    }

    @Test
    public void testSaveSequence() {
        java.util.List<Shot> sequence = java.util.List.of(new Shot(45, 30), new Shot(90, 60));
        OfflineTendencyTraining.saveSequence(sequence, TEST_FILENAME);

        File file = new File(TEST_FILENAME);
        assertTrue(file.exists(), "Sequence file should be created.");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            assertEquals("45 30", line1, "First shot should be correctly written.");
            assertEquals("90 60", line2, "Second shot should be correctly written.");
            assertNull(br.readLine(), "No additional lines should be present.");
        } catch (IOException e) {
            fail("IOException should not be thrown during file reading.");
        }
    }

    @Test
    public void testComputeBaselineShot() {
        env.reset();
        env.setHole(0);
        BaselineShot baseline = env.computeBaselineShot();

        // manually calculating expected angle and power
        int holeX = GolfCourse.getCurrentHole().hole().getX() + 80;
        int holeY = GolfCourse.getCurrentHole().hole().getY() + 10;
        int currentX = (int) env.getPlayer2().xcoordinate;
        int currentY = (int) env.getPlayer2().ycoordinate;

        double dx = currentX - holeX;
        double dy = currentY - holeY;
        double expectedAngleRad = Math.atan2(dy, dx);
        double expectedAngleDeg = Math.toDegrees(expectedAngleRad);
        if (expectedAngleDeg < 0) {
            expectedAngleDeg += 360;
        }

        int expectedBaseAngle = (int) expectedAngleDeg;

        double distance = Math.sqrt(dx * dx + dy * dy);
        int expectedBasePower = (int) Math.min(distance / 2, 90);
        expectedBasePower = Math.max(expectedBasePower, 20);

        assertEquals(
                expectedBaseAngle, baseline.angle, "Baseline angle should be correctly calculated."
        );
        assertEquals(
                expectedBasePower, baseline.power, "Baseline power should be correctly calculated."
        );
    }

    @Test
    public void testDistanceToHole() {
        env.reset();
        env.setHole(0);

        double initialDistance = env.distanceToHole();

        BaselineShot baseline = env.computeBaselineShot();
        int angle = baseline.angle;
        int power = (int) (initialDistance / 2);

        env.simulateShot(power, angle);
        double newDistance = env.distanceToHole();

        assertTrue(
                newDistance < initialDistance,
                "Distance to hole should decrease after simulating a shot."
        );
    }

    /**
     * Test the setHole method for correct environment setup.
     */
    @Test
    public void testSetHole() {
        env.reset();
        env.setHole(2);

        assertEquals(2, GolfCourse.currentHoleIndex(), "Hole index should be updated to 2.");
        assertEquals(
                150, env.getPlayer2().xcoordinate,
                "Player2 X coordinate should be initialized correctly."
        );
        assertEquals(
                470, env.getPlayer2().ycoordinate,
                "Player2 Y coordinate should be initialized correctly."
        );
    }

    /**
     * Test the handleCollisions method for collision with sand.
     */
    @Test
    public void testHandleCollisionsSand() {
        env.reset();
        env.setHole(0);

        // making a trap in sand at a specific grid

        env.getPlayer2().xcoordinate = 100;
        env.getPlayer2().ycoordinate = 100;
        env.getPlayer2().vx = 10;
        env.getPlayer2().vy = 10;

        boolean result = env.handleCollisions(env.getPlayer2());

        assertEquals(
                5.0, env.getPlayer2().vx,
                0.001, "Player2's velocity should be halved upon hitting sand."
        );
        assertEquals(
                5.0, env.getPlayer2().vy, 0.001,
                "Player2's velocity should be halved upon hitting sand."
        );
        assertFalse(env.isWaterHit(), "Water hit flag should not be set when hitting sand.");
        assertFalse(result, "Handle collisions should return false when sand is hit.");
    }

}

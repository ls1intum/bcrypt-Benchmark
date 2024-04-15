package de.tum.in.www1.artemis;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.DoubleStream;

/**
 * Runs a benchmark and logs the result in a file. Check out the README.md for further usage hints.
 */
public class Benchmark {

    private final static int ITERATIONS_FOR_AVERAGE = 10;

    private final static int MINIMUM_ROUNDS = 9;

    private final static long MINIMUM_HASHING_TIME_NS = 250000000L;

    private final static String PASSWORD = "This is a test password";

    public static void main(String[] args) throws IOException {
        // Prepare logging
        final String title = (args.length > 0 ? args[0] : "Hashing Benchmark").toLowerCase().trim().replace(" ", "_");
        FileHandler fileHandler = new FileHandler(title + ".txt");
        fileHandler.setFormatter(new SimpleFormatter());
        Logger logger = Logger.getLogger("Benchmark");
        logger.addHandler(fileHandler);

        // Start benchmark by logging the benchmark configuration and setting up initial values
        logger.log(Level.INFO, "Starting benchmark " + title);
        String startInfo =
            "Iterations for average calculation: " + ITERATIONS_FOR_AVERAGE + System.lineSeparator() + "Minimum salting rounds: " + MINIMUM_ROUNDS + System.lineSeparator() +
                "Minimum hashing time: " + MINIMUM_HASHING_TIME_NS + " ns" + System.lineSeparator();
        logger.log(Level.INFO, startInfo);

        int rounds = MINIMUM_ROUNDS;
        List<Double> averageMeasurements = new ArrayList<>();
        double hashingTime = 0;

        while (hashingTime < MINIMUM_HASHING_TIME_NS) {
            // Calculate average hashing time for current setting
            final double[] measurements = generateMeasurementsForRounds(rounds);
            final double average = DoubleStream.of(measurements).average().orElse(Double.NaN);

            // Log measurements
            StringBuilder resultLog = new StringBuilder();
            resultLog.append("Number of rounds: ").append(rounds).append(System.lineSeparator());
            for (int i = 0; i < ITERATIONS_FOR_AVERAGE; i++) {
                resultLog.append(i).append(" ").append(measurements[i]).append(" ns").append("; ").append(Math.round(measurements[i] / 1000000)).append(" ms");
                resultLog.append(System.lineSeparator());
            }
            resultLog.append(System.lineSeparator());
            resultLog.append("Average: ").append(average).append(" ns").append("; ").append(Math.round(average / 1000000)).append(" ms");
            resultLog.append(System.lineSeparator()).append("---------------------").append(System.lineSeparator());

            logger.log(Level.INFO, resultLog.toString());

            // Save average for current setting and increment setting
            hashingTime = average;
            averageMeasurements.add(average);
            rounds++;
        }
        // Revert the last increment
        rounds--;

        // Mark the setting with the closest average to the goal time as recommended
        int recommendedRounds = rounds;
        if (averageMeasurements.size() > 1 && Math.abs(averageMeasurements.getLast() - MINIMUM_HASHING_TIME_NS) > Math.abs(
            averageMeasurements.get(averageMeasurements.size() - 2) - MINIMUM_HASHING_TIME_NS)) {
            recommendedRounds--;
        }

        // Print recommendation and exit benchmark
        logger.log(Level.INFO, "Recommended number of rounds: " + recommendedRounds + System.lineSeparator());
        logger.log(Level.INFO, "Stopping benchmark " + title);
    }

    /**
     * Measures the time it takes for one hashing process {@link #ITERATIONS_FOR_AVERAGE} times and returns the result
     *
     * @param rounds number of salting rounds for bcrypt
     * @return array of measurements: Time it takes for one hashing process
     */
    private static double[] generateMeasurementsForRounds(int rounds) {
        double[] measurements = new double[ITERATIONS_FOR_AVERAGE];

        for (int i = 0; i < ITERATIONS_FOR_AVERAGE; i++) {
            final long startTime = System.nanoTime();
            String salt = BCrypt.gensalt(rounds);
            BCrypt.hashpw(PASSWORD, salt);
            final long endTime = System.nanoTime();
            measurements[i] = endTime - startTime;
        }

        return measurements;
    }
}

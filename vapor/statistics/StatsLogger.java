package vapor.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import vapor.exceptions.ErrorLogger;
import vapor.exceptions.ErrorLogger.ErrorType;

/**
 * A class to keep track of profits, revenues, and refund amounts.
 */
public class StatsLogger implements Serializable {

    // The name of the file to be created.
    private static final String STATISTICS_FILENAME = "stats.txt";

    private Float profit;
    private Float revenue;
    private Float refunded;

    private Float dailyProfit;
    private Float dailyRevenue;
    private Float dailyRefunded;

    /**
     * Create a new {@code StatsLogger} and set it's values to zero initially.
     */
    public StatsLogger() {
        this.profit = 0f;
        this.revenue = 0f;
        this.refunded = 0f;

        this.dailyProfit = 0f;
        this.dailyRevenue = 0f;
        this.dailyRefunded = 0f;
    }

    /**
     * Update this {@code statsLogger}'s {@code dailyProfit} and {@code profit}.
     */
    public void updateProfit() {
        this.dailyProfit = (dailyRevenue - dailyRefunded);
        this.profit += (dailyRevenue - dailyRefunded);
    }

    /**
     * Update this {@code statsLogger}'s {@code dailyRevenue} and {@code revenue}.
     */
    public void updateRevenue(Integer amount) {
        this.dailyRevenue += (float) amount/100;
        this.revenue += (float) amount/100;
    }

    /**
     * Update this {@code statsLogger}'s {@code dailyRefunded} and {@code refunded}.
     */
    public void updateRefunded(Integer amount) {
        this.dailyRefunded += (float) amount/100;
        this.refunded += (float) amount/100;
    }

    /**
     * Reset this {@code statsLogger}'s daily statistics.
     */
    public void dailyReset() {
        this.dailyProfit = 0f;
        this.dailyRevenue = 0f;
        this.dailyRefunded = 0f;
    }

    /**
     * Create a text file with name {@code STATISTICS_FILENAME} to record
     * this {@code statsLogger}'s statistics.
     */
    public void report() {
        System.out.println("Attempting to generate statistics...");
        updateProfit();
        try {

            FileWriter fileWriter = new FileWriter(STATISTICS_FILENAME);

            fileWriter.write(profit + "\n");
            fileWriter.write(revenue + "\n");
            fileWriter.write(refunded + "\n");

            fileWriter.write(dailyProfit + "\n");
            fileWriter.write(dailyRevenue + "\n");
            fileWriter.write(dailyRefunded + "\n");

            fileWriter.close();

            dailyReset();

            System.out.println("Statistics generated!");
        } catch (final IOException e) {
            ErrorLogger.log(ErrorType.ERROR, "STATISTICS [FILE: " + STATISTICS_FILENAME + "]",
                    "could not generate statistics.");
        }

        // At the end of the report reset daily stats.
        dailyReset();
    }
}

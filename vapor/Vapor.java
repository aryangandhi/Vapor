package vapor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import vapor.exceptions.ErrorLogger;
import vapor.exceptions.ErrorLogger.ErrorType;
import vapor.transactions.Transaction;

public class Vapor {
    private static final String MARKET_FILENAME = "market.ser";
    private static final String TRANSACTIONS_FILENAME = "daily.txt";

    public static void main(String[] args) {
        if (args.length > 0)
            DatabaseBuilder.buildDatabase(args[0], MARKET_FILENAME);

        File dailyTextFile = new File(TRANSACTIONS_FILENAME);

        // Most important method call! Absolutely necessary.
        vape();

        final Market market = Market.getMarket(MARKET_FILENAME);

        // Execute each transaction within the ArrayList<Transaction> FileParser
        // provides.
        BufferedReader bufferReader;
        ArrayList<Transaction> transactions = null;

        try {
            bufferReader = new BufferedReader(new FileReader(dailyTextFile));
            FileParser parser = new FileParser(bufferReader);
            transactions = parser.parse();
        } catch (final FileNotFoundException e) {
            ErrorLogger.log(ErrorType.ERROR, "MAIN", "could not open daily.txt.");
        }

        if (transactions != null)
            for (final Transaction transaction : transactions)
                if (transaction != null)
                    transaction.execute(market);

        // Process and reset any once-a-day limits and buffers.
        market.endDay();

        // Export market summary as a JSON.
        market.export();

        // Create a text file summary of statistics.
        market.report();

        // save the market with Serialization.
        market.save(MARKET_FILENAME);
    }

    /**
     * Prints the word vapor to the console.
     */
    public static void vape() {
        System.out.println("\n" +
                " ██▒   █▓ ▄▄▄       ██▓███   ▒█████   ██▀███  \n" +
                "▓██░   █▒▒████▄    ▓██░  ██▒▒██▒  ██▒▓██ ▒ ██▒\n" +
                " ▓██  █▒░▒██  ▀█▄  ▓██░ ██▓▒▒██░  ██▒▓██ ░▄█ ▒\n" +
                "  ▒██ █░░░██▄▄▄▄██ ▒██▄█▓▒ ▒▒██   ██░▒██▀▀█▄  \n" +
                "   ▒▀█░   ▓█   ▓██▒▒██▒ ░  ░░ ████▓▒░░██▓ ▒██▒\n" +
                "   ░ ▐░   ▒▒   ▓▒█░▒▓▒░ ░  ░░ ▒░▒░▒░ ░ ▒▓ ░▒▓░\n" +
                "   ░ ░░    ▒   ▒▒ ░░▒ ░       ░ ▒ ▒░   ░▒ ░ ▒░\n" +
                "     ░░    ░   ▒   ░░       ░ ░ ░ ▒    ░░   ░ \n" +
                "      ░        ░  ░             ░ ░     ░     \n" +
                "     ░                                        \n");
    }
}

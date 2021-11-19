package vapor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import vapor.exceptions.ErrorLogger;
import vapor.exceptions.InvalidTransactionCodeException;
import vapor.exceptions.ErrorLogger.ErrorType;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;

/**
 * The {@code TransactionBuilder} receives a Reader for a source file containing
 * String representations of a number of {@code Transaction}s and parses them
 * into a list of {@code Transaction}s according to the specification provided
 */
public class FileParser {
  // reader to act as the {@code FileParser}'s source
  BufferedReader reader;

  /**
   * Create a new FileParser and initialize its source
   * 
   * @param reader {@code BufferedReader} to serve as the {@code FileParser}'s
   *               source
   */
  public FileParser(final BufferedReader reader) {
    this.reader = reader;
  }

  /**
   * Assemble a list of parsed {@code Transaction}s as per the specification
   * 
   * @return an {@code ArrayList} of {@code Transaction}s containing all
   *         transactions in the file
   */
  public ArrayList<Transaction> parse() {
    ArrayList<Transaction> transactions = new ArrayList<>();

    TransactionBuilder transactionParser;
    String rawTransaction;
    try {
      while ((rawTransaction = reader.readLine()) != null) {
        try {
          transactionParser = new TransactionBuilder(rawTransaction);
          transactions.add(transactionParser.parse());
        } catch (final InvalidTransactionCodeException e) {
          ErrorLogger.log(ErrorType.ERROR, "[" + rawTransaction + "]", e.getError());
        }
      }
    } catch (final IOException e) {
      ErrorLogger.log(ErrorType.ERROR, "FILE PARSER", "Failed to process file.");
    }

    return transactions;
  }
}

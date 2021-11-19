package vapor.exceptions;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A stateless class to handle error management and reporting throughout our
 * codebase.
 */
public class ErrorLogger {
  /**
   * Enumerated type to hold possible error log types
   */
  public enum ErrorType {
    ERROR, WARNING
  }

  // The name of the log file where the error summary is stored.
  private static final String ERROR_FILENAME = "ErrorSummary.log";

  /**
   * Log an error message to {@code ERROR_FILENAME.log}. If no such file exists,
   * creates the file.
   *
   * The message is of the format: (ERROR|WARNING): ((<i>contextInfo</i>) -
   * )?(<i>errorMessage</i>)
   * 
   * @param errorType    the {@code ErrorType} of this error
   * @param contextInfo  optional bonus information depending on the type of error
   * @param errorMessage description of the error encountered
   */
  public static void log(final ErrorType errorType, String contextInfo, final String errorMessage) {
    // Insert a divider between contextInfo and errorMessage if contextInfo exists
    if (!contextInfo.equals(""))
      contextInfo += " - ";

    final String error = errorType.name() + ": " + contextInfo + errorMessage + "\n";

    try {
      FileWriter fileWriter = new FileWriter(ERROR_FILENAME, true);

      fileWriter.write(error);
      fileWriter.close();

      System.out.println("Error logged in " + ERROR_FILENAME + ".");
    } catch (final IOException e) {
      System.out.println("Failed to print error (" + error + ").");
    }
  }
}

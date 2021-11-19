package vapor.exceptions;

/**
 * Top level ancestor for all VaporExceptions
 */
public class VaporException extends Exception {
  // Exception error message to be logged
  private final String error;

  /**
   * Create a new {@code VaporException} associated to a certain error
   * 
   * @param error the error message to be logged for this exception
   */
  public VaporException(final String error) {
    this.error = error;
  }

  /**
   * Access the {@code VaporException} error for printing
   * 
   * @return the error string for this exception.
   */
  public String getError() {
    return error;
  }
}

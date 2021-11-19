package vapor.exceptions;

/**
 * Exception to handle an attempt to retrieve a {@code Transaction} code where
 * there was none.
 */
public class InvalidTransactionCodeException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public InvalidTransactionCodeException() {
    super("This transaction code does not describe a valid transaction.");
  }
}

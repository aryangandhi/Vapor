package vapor.exceptions;

/**
 * Exception to handle an attempt to charge a {@code User} more credit than he /
 * she had available.
 */
public class InsufficientFundsException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public InsufficientFundsException(final int needed, final int available) {
    super("Insufficient funds. Have " + (float) needed / 100.f + " of " + (float) available / 100.f);
  }
}

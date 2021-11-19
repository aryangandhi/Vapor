package vapor.exceptions;

/**
 * Exception to handle an attempt to wrongfully query a {@code User} as a
 * {@code Buyer}.
 */
public class NotBuyerException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public NotBuyerException(final String username) {
    super(username + " is not a buyer.");
  }
}

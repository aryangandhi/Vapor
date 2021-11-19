package vapor.exceptions;

/**
 * Exception to handle an attempt to wrongfully query a {@code User} as a
 * {@code Seller}.
 */
public class NotSellerException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public NotSellerException(final String username) {
    super(username + " is not a seller.");
  }
}

package vapor.exceptions;

/**
 * Exception to handle attempted {@code RefundTransaction} by a
 * {@code AdminUser} on the issuing {@code User}.
 */
public class SelfRefundException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public SelfRefundException(final String username) {
    super(username + " cannot refund himself / herself.");
  }
}

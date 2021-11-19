package vapor.exceptions;

/**
 * Exception to handle attempted {@code DeleteTransaction} by a
 * {@code AdminUser} on the {@code User} himself.
 */
public class SelfDeletionException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public SelfDeletionException(final String username) {
    super(username + " is currently logged in user. Cannot be deleted.");
  }
}

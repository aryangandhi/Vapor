package vapor.exceptions;

/**
 * Exception to handle attempted {@code LoginTransaction} when there is already
 * a logged in {@code User} in the {@code Market}.
 */
public class MultipleLoginException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public MultipleLoginException(final String username) {
    super("Cannot login " + username + "; a user is currently logged in.");
  }
}

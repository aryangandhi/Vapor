package vapor.exceptions;

/**
 * Exception to handle cases where a {@code Transaction} attempts to create a
 * {@code User} with an existing username.
 */
public class UserExistsException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public UserExistsException(final String username) {
    super(username + " already exists on the system");
  }
}

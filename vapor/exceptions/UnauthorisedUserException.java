package vapor.exceptions;

/**
 * Exception to handle attempted privileged {@code Transaction} when
 * {@code User} is not privileged ({@code AdminUser}).
 */
public class UnauthorisedUserException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public UnauthorisedUserException() {
    super("No privileged user logged in to perform transaction.");
  }
}

package vapor.exceptions;

/**
 * Exception to handle attempted {@code LogoutTransaction} when no {@code User}
 * is logged into the {@code Market}.
 */
public class NoLoginException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public NoLoginException() {
    super("No user is currently logged into the system.");
  }
}

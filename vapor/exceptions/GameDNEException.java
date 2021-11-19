package vapor.exceptions;

/**
 * Exception to handle attempted accesses of a non-existent {@code Game}.
 */
public class GameDNEException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public GameDNEException(final String gameID) {
    super(gameID + " does not exist in the inventory.");
  }
}

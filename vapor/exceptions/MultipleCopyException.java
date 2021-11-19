package vapor.exceptions;

/**
 * Exception to handle attempted addition of a {@code Game} to a
 * {@code Collection} when a {@code Game} with that name already exists.
 */
public class MultipleCopyException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public MultipleCopyException(final String gameID) {
    super("A copy of " + gameID + " is already in the inventory.");
  }
}

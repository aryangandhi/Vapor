package vapor.exceptions;

/**
 * Attempted to adjust the Database with an illegal new state file.
 */
public class InvalidDBBuilderRecord extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public InvalidDBBuilderRecord(final String line) {
    super("Illegal new database state line: \"" + line + "\" aborting...");
  }
}

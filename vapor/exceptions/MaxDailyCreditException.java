package vapor.exceptions;

/**
 * Attempted to credit a user more than the daily maximum allows.
 */
public class MaxDailyCreditException extends VaporException {
  /**
   * Create a new {@code VaporException} with the given error message.
   */
  public MaxDailyCreditException(final int attempted, final int remaining) {
    super("Attempted to credit " + (float) (attempted - remaining) / 100.f + " more than remaining daily allowance.");
  }
}

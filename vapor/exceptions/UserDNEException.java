package vapor.exceptions;

/**
 * A queried username does not exist
 */
public class UserDNEException extends VaporException {
  /**
   * Create a {@code VaporException} highlighting a non-existent user
   * 
   * @param username the username attempted to query
   */
  public UserDNEException(final String username) {
    super(username + " does not exist in the market.");
  }
}

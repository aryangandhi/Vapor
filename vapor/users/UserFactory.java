package vapor.users;

/**
 * A factory that calls the necessary {@code User} type's constructor
 */
public class UserFactory {
  /**
   * Create a new {@code User} of specified type by calling the necessary
   * constructor.
   * 
   * @param username the username of the new {@code User} to be created.
   * @param credit   the initial value to credit this new {@code User}.
   * @param userType the type of {@code User} to be created.
   * 
   * @return a {@code User} of correct type.
   */
  public static User createTypedUser(final String username, final int credit, final User.UserType userType) {
    switch (userType) {
    case BUYER:
      return new BuyerUser(username, credit);
    case SELLER:
      return new SellerUser(username, credit);
    case FULL:
      return new StandardUser(username, credit);
    case ADMIN:
      return new AdminUser(username, credit);
    default:
      return null;
    }
  }
}

package vapor.transactions;

import vapor.Market;
import vapor.exceptions.UnauthorisedUserException;
import vapor.exceptions.UserExistsException;
import vapor.users.User;
import vapor.users.UserFactory;

/**
 * A {@code Transaction} to create a new {@code User} in a {@code Market}.
 */
public class CreateTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public CreateTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Create a new {@code User} in a {@code Market}.
   * 
   * @param market the {@code Market} to which the {@code User}s should be added.
   */
  public void execute(final Market market) {
    try {
      final User user = UserFactory.createTypedUser(userID1, credit, userType);
      market.addUser(user);
    } catch (final UserExistsException | UnauthorisedUserException e) {
      fail(e.getError());
    }
  }
}

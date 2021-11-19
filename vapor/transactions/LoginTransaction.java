package vapor.transactions;

import vapor.Market;
import vapor.exceptions.MultipleLoginException;
import vapor.exceptions.UserDNEException;
import vapor.users.User;

/**
 * A {@code Transaction} to log a {@code User} into a {@code Market}.
 */
public class LoginTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public LoginTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Log a {@code User} into a {@code Market}.
   */
  @Override
  public void execute(final Market market) {
    try {
      final User user = market.getUser(userID1);
      market.loginUser(user);

      // Issue a warning if transaction record has dirty data.
      warnUserTypeDesync(user);
      warnUserCreditDesync(user);
    } catch (final UserDNEException | MultipleLoginException e) {
      fail(e.getError());
    }
  }
}

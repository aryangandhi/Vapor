package vapor.transactions;

import vapor.Market;
import vapor.exceptions.NoLoginException;
import vapor.users.User;

/**
 * A {@code Transaction} to log a {@code User} out of {@code Market}.
 */
public class LogoutTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public LogoutTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Log a {@code User} out of {@code Market}.
   * 
   * @param market the {@code Market} out of which to log a {@code User}.
   */
  @Override
  public void execute(final Market market) {
    try {
      final User user = market.logoutUser();

      // Issue a warning if transaction record has dirty data.
      warnUsernameDesync(user);
      warnUserTypeDesync(user);
      warnUserCreditDesync(user);
    } catch (final NoLoginException e) {
      fail(e.getError());
    }
  }
}

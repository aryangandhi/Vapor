package vapor.transactions;

import vapor.Market;
import vapor.exceptions.SelfDeletionException;
import vapor.exceptions.UnauthorisedUserException;
import vapor.exceptions.UserDNEException;
import vapor.users.User;

/**
 * A {@code Transaction} to delete a {@code User} from a {@code Market}.
 */
public class DeleteTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public DeleteTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Delete a {@code User} from a {@code Market}.
   * 
   * @param market the {@code Market} from which to delete a {@code User}.
   */
  @Override
  public void execute(final Market market) {
    try {
      final User user = market.getUser(userID1);

      market.removeUser(user);

      // Issue a warning if transaction record has dirty data.
      warnUserTypeDesync(user);
      warnUserCreditDesync(user);
    } catch (final UserDNEException | UnauthorisedUserException | SelfDeletionException e) {
      fail(e.getError());
    }
  }
}

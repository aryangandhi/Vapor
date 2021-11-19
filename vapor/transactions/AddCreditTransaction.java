package vapor.transactions;

import vapor.Market;
import vapor.exceptions.MaxDailyCreditException;
import vapor.exceptions.NoLoginException;
import vapor.exceptions.UserDNEException;
import vapor.users.User;

/**
 * A {@code Transaction} to credit a {@code User} in a {@code Market}.
 */
public class AddCreditTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public AddCreditTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Credit a {@code User} in a {@code Market}.
   * 
   * @param market in which to find the {@code User} to be credited.
   */
  public void execute(final Market market) {
    try {
      // Get the desired user for the credit..
      final User targetUser = market.getActiveUserOrTarget(userID1);

      // Amount actually deposited or exception if > daily max.
      warnMaxBalance(targetUser.addCredit(credit));

      // Issue a warning if transaction record has dirty data.
      warnUsernameDesync(targetUser);
      warnUserTypeDesync(targetUser);
    } catch (final MaxDailyCreditException | NoLoginException | UserDNEException e) {
      fail(e.getError());
    }
  }
}

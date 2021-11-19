package vapor.transactions;

import vapor.Market;
import vapor.exceptions.UnauthorisedUserException;
import vapor.users.User;

/**
 * A {@code Transaction} to toggle an auction sale in a {@code Market}.
 */
public class AuctionSaleTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public AuctionSaleTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Toggle an auction sale in a {@code Market}.
   * 
   * @param market the {@code Market} on which the auction sale should be toggled.
   */
  @Override
  public void execute(final Market market) {
    try {
      market.toggleAuctionSale();

      final User user = market.getActiveUser();

      // Issue a warning if transaction record has dirty data.
      warnUsernameDesync(user);
      warnUserTypeDesync(user);
      warnUserCreditDesync(user);
    } catch (final UnauthorisedUserException e) {
      fail(e.getError());
    }
  }
}

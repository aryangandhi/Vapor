package vapor.transactions;

import vapor.EndOfDay;
import vapor.Game;
import vapor.Listing;
import vapor.Market;
import vapor.exceptions.MultipleCopyException;
import vapor.exceptions.NoLoginException;
import vapor.exceptions.NotSellerException;
import vapor.users.Buyer;
import vapor.users.Seller;
import vapor.users.User;

/**
 * A {@code Transaction} to list a {@code Game} for sale in a {@code Market} by
 * a {@code User}.
 */
public class SellTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public SellTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * List a {@code Game} for sale in a {@code Market} by a {@code User}.
   * 
   * @param market the {@code Market} on which the {@code Game} should be listed.
   */
  @Override
  public void execute(final Market market) {
    try {
      final User activeUser = market.getActiveUser();
      if (activeUser == null)
        throw new NoLoginException();
      if (!activeUser.getUserType().isSeller())
        throw new NotSellerException("Logged in user (" + activeUser.getUsername() + ")");

      // Ensure full-standard and admin do not list games they own.
      if (activeUser.getUserType().isBuyer())
        if (((Buyer) activeUser).getInventory().containsEntry(gameID))
          throw new MultipleCopyException(gameID);

      final Seller seller = (Seller) activeUser;
      seller.list(new Listing(new Game(gameID), price, discount));

      market.addPendingEndOfDay((EndOfDay) seller.getStoreFront());

      // Issue a warning if transaction record has dirty data.
      warnUsernameDesync(activeUser);
    } catch (final NotSellerException | MultipleCopyException | NoLoginException e) {
      fail(e.getError());
    }
  }
}

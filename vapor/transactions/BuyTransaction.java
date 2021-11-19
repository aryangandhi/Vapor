package vapor.transactions;

import vapor.EndOfDay;
import vapor.Listing;
import vapor.Market;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;
import vapor.exceptions.NoLoginException;
import vapor.exceptions.NotBuyerException;
import vapor.exceptions.NotSellerException;
import vapor.exceptions.UserDNEException;
import vapor.users.Buyer;
import vapor.users.Seller;
import vapor.users.User;

/**
 * A {@code Transaction} purchase a {@code Game} for a {@code Buyer} from a
 * {@code Seller} in a {@code Market}.
 */
public class BuyTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public BuyTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Purchase a {@code Game} for a {@code Buyer} from a {@code Seller} in a
   * {@code Market}.
   * 
   * @param market the {@code Market} on which to conduct the purchase.
   */
  @Override
  public void execute(final Market market) {
    try {
      final User activeUser = market.getActiveUser();
      if (activeUser == null)
        throw new NoLoginException();
      if (!activeUser.getUserType().isBuyer())
        throw new NotBuyerException("Logged in user (" + activeUser.getUsername() + ")");

      // Ensure full-standard and admin do not buy games they own.
      if (activeUser.getUserType().isSeller())
        if (((Seller) activeUser).getStoreFront().containsEntry(gameID))
          throw new MultipleCopyException(gameID);

      final Buyer buyer = (Buyer) activeUser;
      final Seller seller = market.getSeller(userID1);

      final Listing listing = seller.getListing(gameID);

      final boolean isAuctionSale = market.getAuctionSale();

      buyer.buy(listing, isAuctionSale);
      warnMaxBalance(seller.sell(listing, isAuctionSale));

      market.addPendingEndOfDay((EndOfDay) buyer.getInventory());

      market.getStats().updateRevenue(listing.getPrice());

      // Issue a warning if transaction record has dirty data.
      warnDesync("name", activeUser.getUsername(), userID2);
    } catch (final UserDNEException | NotBuyerException | NotSellerException | GameDNEException
        | InsufficientFundsException | MultipleCopyException | NoLoginException e) {
      fail(e.getError());
    }
  }
}

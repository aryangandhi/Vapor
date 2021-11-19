package vapor.users;

import vapor.Inventory;
import vapor.Listing;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * User that can buy games on Vapor. A subclass of the abstract User class.
 */

public class BuyerUser extends User implements Buyer {
  // Houses functionality related to purchase of games to prevent duplication
  private final BuyerComponent buyer;

  /**
   * Creates a new {@code User} with the starting information provided.
   *
   * @param username the username for this {@code User}.
   * @param credit   this {@code User}'s initial credit.
   */
  public BuyerUser(final String username, final int credit) {
    super(username, credit, User.UserType.BUYER);
    this.buyer = new BuyerComponent(this);
  }

  /**
   * Purchase a {@code Game} from a {@code Listing} and charge this {@code Buyer}
   * accordingly.
   * 
   * @param listing       the {@code Listing} containing the {@code Game} to be
   *                      bought.
   * @param isAuctionSale a flag for an ongoing auctionSale.
   * @throws InsufficientFundsException the {@code Buyer} doesn't have enough
   *                                    credit.
   * @throws MultipleCopyException      the {@code Buyer} already owns a copy of
   *                                    the {@code Game}.
   */
  public void buy(final Listing listing, final boolean isAuctionSale)
      throws InsufficientFundsException, MultipleCopyException {
    buyer.buy(listing, isAuctionSale);
  }

  /**
   * Credit this {@code Buyer} a refund up to the maximum account balance.
   * 
   * @param credit the amount of credit to be refunded.
   * @return the actual amount refunded.
   */
  public int creditRefund(final int credit) {
    return buyer.creditRefund(credit);
  }

  /**
   *
   * @return - Return this {@code Buyer}'s {@code inventory}.
   */
  public Inventory getInventory() {
    return buyer.getInventory();
  }
}

package vapor.users;

import vapor.Inventory;
import vapor.Listing;
import vapor.StoreFront;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * A user that can buy and sell games on Vapor. A subclass of the abstract User
 * class.
 */

public class StandardUser extends User implements Buyer, Seller {
  // Houses functionality related to purchase of games to prevent duplication
  private final BuyerComponent buyer;
  // Houses functionality related to sale of games to prevent duplication
  private final SellerComponent seller;

  /**
   * Creates a new {@code User} with the starting information provided.
   *
   * @param username the username for this {@code User}.
   * @param credit   this {@code User}'s initial credit.
   */
  public StandardUser(final String username, final int credit) {
    super(username, credit, User.UserType.FULL);
    this.buyer = new BuyerComponent(this);
    this.seller = new SellerComponent(this);
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
   * Add a {@code Listing} to this {@code Seller}'s {@code StoreFront}.
   * 
   * @param listing the {@code Listing} to be added.
   * @throws MultipleCopyException a {@code Listing} for this {@code Game} exists
   *                               in the {@code StoreFront}.
   */
  public void list(final Listing listing) throws MultipleCopyException {
    seller.list(listing);
  }

  /**
   * Access a {@code Listing} in this {@code Seller}'s {@code StoreFront}.
   * 
   * @param gameID the name of the listed {@code Game} to be accessed.
   * @return the {@code Listing} corresponding to the {@code Game} with this
   *         gameID.
   * @throws GameDNEException no {@code Listing} with this {@code Game}'s name
   *                          exists in the {@code StoreFront}.
   */
  public Listing getListing(final String gameID) throws GameDNEException {
    return seller.getListing(gameID);
  }

  /**
   * Return the {@code storeFront} containing the {@code Listing}s listed by this
   * {@code Seller}.
   *
   * @return - the {@code StoreFront}s holding this seller's listings.
   */
  public StoreFront getStoreFront() {
    return seller.getStoreFront();
  }

  /**
   * Process a single purchase of the {@code Listing}.
   * 
   * @param listing       the {@code Listing} purchased.
   * @param isAuctionSale a flag for an ongoing auctionSale.
   * @return the amount credited to the {@code Seller}.
   */
  public int sell(final Listing listing, final boolean isAuctionSale) {
    return seller.sell(listing, isAuctionSale);
  }

  /**
   * Attempt to charge this {@code Seller} the cost of a refund.
   * 
   * @param credit the amount to charge this {@code Seller}.
   * @throws InsufficientFundsException this {@code Seller} hasn't ample enough
   *                                    funds to issue a refund.
   */
  public void chargeRefund(final int credit) throws InsufficientFundsException {
    seller.chargeRefund(credit);
  }

  /**
   *
   * @return - Return this {@code Buyer}'s {@code inventory}.
   */
  public Inventory getInventory() {
    return buyer.getInventory();
  }
}

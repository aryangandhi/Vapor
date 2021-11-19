package vapor.users;

import vapor.Listing;
import vapor.StoreFront;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * User that can sell games on Vapor. A subclass of the abstract User class.
 */
public class SellerUser extends User implements Seller {
  // Houses functionality related to sale of games to prevent duplication
  private final SellerComponent seller;

  /**
   * Creates a new {@code User} with the starting information provided.
   *
   * @param username the username for this {@code User}.
   * @param credit   this {@code User}'s initial credit.
   */
  public SellerUser(final String username, final int credit) {
    super(username, credit, User.UserType.SELLER);
    this.seller = new SellerComponent(this);
  }

  /**
   * Add a {@code Listing} for {@code game} to this {@code Seller}'s
   * {@code StoreFront}.
   * 
   * @param game the {@code game} to be listed.
   * @throws MultipleCopyException a {@code Listing} for this {@code Game} exists
   *                               in the {@code StoreFront}.
   */
  public void list(final Listing game) throws MultipleCopyException {
    seller.list(game);
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
}

package vapor.users;

import java.io.Serializable;

import vapor.Listing;
import vapor.StoreFront;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * Class to encapsulate {@code Seller}s' sale functionality to prevent code
 * duplication across {@code Seller}s.
 */
public class SellerComponent implements Serializable {
  private final User seller; // The {@code Seller} to credit for sales / charge for refunds.
  private final StoreFront storeFront; // The {@code StoreFront} to query for {@code Listing}s.

  /**
   * Construct a {@code SellerComponent} to implement {@code Seller} sell methods.
   * 
   * @param seller the {@code User} on which the {@code SellerComponent} operates.
   */
  protected SellerComponent(final User seller) {
    this.seller = seller;
    this.storeFront = new StoreFront();
  }

  /**
   * Add a {@code Listing} to the {@code StoreFront}.
   * 
   * @param listing the {@code Listing} to be added.
   * @throws MultipleCopyException a {@code Listing} for this {@code Game} exists
   *                               in the {@code StoreFront}.
   */
  protected void list(final Listing listing) throws MultipleCopyException {
    storeFront.addEntry(listing);
  }

  /**
   * Access a {@code Listing} in the {@code StoreFront}.
   * 
   * @param gameID the name of the listed {@code Game} to be accessed.
   * @return the {@code Listing} corresponding to the {@code Game} with this
   *         gameID.
   * @throws GameDNEException no {@code Listing} with this {@code Game}'s name
   *                          exists in the {@code StoreFront}.
   */
  protected Listing getListing(final String gameID) throws GameDNEException {
    return storeFront.getEntry(gameID);
  }

  /**
   * Return the {@code storeFront} containing the {@code Listing}s listed by this
   * {@code Seller}.
   *
   * @return - the {@code StoreFront}s holding this seller's listings.
   */
  public StoreFront getStoreFront() {
    return storeFront;
  }

  /**
   * Process a single purchase of the {@code Listing}.
   * 
   * @param listing       the {@code Listing} purchased.
   * @param isAuctionSale a flag for an ongoing auctionSale.
   * @return the amount credited to the {@code Seller}.
   */
  protected int sell(final Listing listing, final boolean isAuctionSale) {
    final int price = listing.getPrice();
    final int discount = (int) (isAuctionSale ? price * (listing.getDiscount() / 100.f) : 0);

    return seller.forceAddCredit(price - discount);
  }

  /**
   * Attempt to charge this {@code Seller} the cost of a refund.
   * 
   * @param credit the amount to charge this {@code Seller}.
   * @throws InsufficientFundsException this {@code Seller} hasn't ample enough
   *                                    funds to issue a refund.
   */
  protected void chargeRefund(final int credit) throws InsufficientFundsException {
    seller.charge(credit);
  }
}

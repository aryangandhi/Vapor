package vapor.users;

import vapor.Listing;
import vapor.StoreFront;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * This interface is used to group {@code User}s who can sell {@code Game}s.
 */
public interface Seller {
  /**
   * Add a {@code Listing} to this {@code Seller}'s {@code StoreFront}.
   * 
   * @param listing the {@code Listing} to be added.
   * @throws MultipleCopyException a {@code Listing} for this {@code Game} exists
   *                               in the {@code StoreFront}.
   */
  void list(final Listing listing) throws MultipleCopyException;

  /**
   * Access a {@code Listing} in this {@code Seller}'s {@code StoreFront}.
   * 
   * @param gameID the name of the listed {@code Game} to be accessed.
   * @return the {@code Listing} corresponding to the {@code Game} with this
   *         gameID.
   * @throws GameDNEException no {@code Listing} with this {@code Game}'s name
   *                          exists in the {@code StoreFront}.
   */
  Listing getListing(final String gameID) throws GameDNEException;

  /**
   * Return the {@code storeFront} containing the {@code Listing}s listed by this
   * {@code Seller}.
   *
   * @return - the {@code StoreFront}s holding this seller's listings.
   */
  StoreFront getStoreFront();

  /**
   * Process a single purchase of the {@code Listing}.
   * 
   * @param listing       the {@code Listing} purchased.
   * @param isAuctionSale a flag for an ongoing auctionSale.
   * @return the amount credited to the {@code Seller}.
   */
  int sell(final Listing listing, final boolean isAuctionSale);

  /**
   * Attempt to charge this {@code Seller} the cost of a refund.
   * 
   * @param credit the amount to charge this {@code Seller}.
   * @throws InsufficientFundsException this {@code Seller} hasn't ample enough
   *                                    funds to issue a refund.
   */
  void chargeRefund(final int credit) throws InsufficientFundsException;
}

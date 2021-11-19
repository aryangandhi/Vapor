package vapor.users;

import vapor.Inventory;
import vapor.Listing;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * A game buyer; used to group users who can buy games
 */
public interface Buyer {
  /**
   * Purchase a {@code Game} from a {@code Listing} and charge this {@code Buyer}
   * accordingly.
   * 
   * @param listing the {@code Listing} containing the {@code Game} to be bought.
   * @throws InsufficientFundsException the {@code Buyer} doesn't have enough
   *                                    credit.
   * @throws MultipleCopyException      the {@code Buyer} already owns a copy of
   *                                    the {@code Game}.
   */
  void buy(final Listing listing, final boolean isAuctionSale) throws InsufficientFundsException, MultipleCopyException;

  /**
   * Credit this {@code Buyer} a refund up to the maximum account balance.
   * 
   * @param credit the amount of credit to be refunded.
   * @return the actual amount refunded.
   */
  int creditRefund(final int credit);

  /**
   *
   * @return - Return this {@code Buyer}'s {@code inventory}.
   */
  Inventory getInventory();
}

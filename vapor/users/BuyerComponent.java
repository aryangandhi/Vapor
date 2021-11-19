package vapor.users;

import java.io.Serializable;

import vapor.Inventory;
import vapor.Listing;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MultipleCopyException;

/**
 * Class to encapsulate {@code Buyer}s' purchase functionality to prevent code
 * duplication across {@code Buyer}s.
 */
public class BuyerComponent implements Serializable {
  private final User buyer; // The {@code Buyer} to charge for charge for purchases / credit for refunds.
  private final Inventory inventory; // The {@code Inventory} to query for {@code Game}s.

  /**
   * Construct a {@code BuyerComponent} to implement {@code Buyer} buy methods.
   * 
   * @param buyer the {@code User} on which the {@code BuyerComponent} operates.
   */
  protected BuyerComponent(final User buyer) {
    this.buyer = buyer;
    this.inventory = new Inventory();
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
  protected void buy(final Listing listing, final boolean isAuctionSale)
      throws InsufficientFundsException, MultipleCopyException {
    final int price = listing.getPrice();
    final int discount = (int) (isAuctionSale ? price * (listing.getDiscount() / 100.f) : 0);

    buyer.charge(price - discount);

    inventory.addEntry(listing.getGame());
  }

  /**
   * Credit this {@code Buyer} a refund up to the maximum account balance.
   * 
   * @param credit the amount of credit to be refunded.
   * @return the actual amount refunded.
   */
  protected int creditRefund(final int credit) {
    return buyer.forceAddCredit(credit);
  }

  /**
   *
   * @return - this {@code Buyer}'s {@code inventory}.
   */
  public Inventory getInventory() {
    return this.inventory;
  }
}

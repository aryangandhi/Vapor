package vapor.transactions;

import vapor.Market;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.NotBuyerException;
import vapor.exceptions.NotSellerException;
import vapor.exceptions.SelfRefundException;
import vapor.exceptions.UnauthorisedUserException;
import vapor.exceptions.UserDNEException;
import vapor.users.Buyer;
import vapor.users.Seller;

/**
 * A transaction that will refund a user credit.
 */
public class RefundTransaction extends Transaction {
  /**
   * Initialise the arguments for this {@code Transaction} using the information
   * parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public RefundTransaction(final TransactionBuilder source) {
    super(source);
  }

  /**
   * Refund a buyer of a game a credit from the seller's account
   * 
   * @param market the {@code Market} on which to refund a buyer
   */
  @Override
  public void execute(final Market market) {
    try {
      // Verify current user is admin.
      if (!market.isAuthorisedState())
        throw new UnauthorisedUserException();

      final Buyer buyer = market.getBuyer(userID1);
      final Seller seller = market.getSeller(userID2);

      if (buyer == seller)
        throw new SelfRefundException(userID1);

      seller.chargeRefund(credit);
      warnMaxBalance(buyer.creditRefund(credit));

      market.getStats().updateRefunded(credit);
    } catch (final UnauthorisedUserException | UserDNEException | NotBuyerException | NotSellerException
        | InsufficientFundsException | SelfRefundException e) {
      fail(e.getError());
    }
  }
}

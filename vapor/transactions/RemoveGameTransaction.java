package vapor.transactions;

import vapor.exceptions.GameDNEException;
import vapor.exceptions.NoLoginException;
import vapor.exceptions.UserDNEException;
import vapor.users.Buyer;
import vapor.users.Seller;
import vapor.users.User;
import vapor.Market;

/**
 * A {@code Transaction} to remove a {@code User}'s {@code Game} from a
 * {@code Market}.
 */
public class RemoveGameTransaction extends Transaction {
    /**
     * Initialise the arguments for this {@code Transaction} using the information
     * parsed in a {@code TransactionBuilder}.
     * 
     * @param source the {@code TransactionBuilder} from which to initialise
     *               information.
     */
    public RemoveGameTransaction(final TransactionBuilder source) {
        super(source);
    }

    /**
     * Attempt to remove a {@code User}'s {@code Game} from a {@code Market}.
     *
     * @param market the {@code Market} from which the {@code Game} is to be
     *               removed.
     */
    @Override
    public void execute(final Market market) {
        try {
            final User targetUser = market.getActiveUserOrTarget(userID2);

            final User.UserType targetType = targetUser.getUserType();

            if (targetType.isBuyer())
                try {
                    ((Buyer) targetUser).getInventory().remove(gameID);
                } catch (final GameDNEException e) {
                    if (targetType.isSeller())
                        ((Seller) targetUser).getStoreFront().remove(gameID);
                    else
                        throw e;
                }
            else
                ((Seller) targetUser).getStoreFront().remove(gameID);

            warnUsernameDesync(market.getActiveUser());
        } catch (final NoLoginException | GameDNEException | UserDNEException e) {
            fail(e.getError());
        }
    }
}

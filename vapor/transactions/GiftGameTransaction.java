package vapor.transactions;

import vapor.Game;
import vapor.Inventory;
import vapor.Market;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.MultipleCopyException;
import vapor.exceptions.NoLoginException;
import vapor.exceptions.NotBuyerException;
import vapor.exceptions.NotSellerException;
import vapor.exceptions.UserDNEException;
import vapor.users.Buyer;
import vapor.users.Seller;
import vapor.users.User;

/**
 * A transaction to gift a different user a game
 */
public class GiftGameTransaction extends Transaction {
    /**
     * Initialise the arguments for this {@code Transaction} using the information
     * parsed in a {@code TransactionBuilder}.
     * 
     * @param source the {@code TransactionBuilder} from which to initialise
     *               information.
     */
    public GiftGameTransaction(final TransactionBuilder source) {
        super(source);
    }

    /**
     * Attempt to gift a game from an owner to a receiver
     *
     * @param market the {@code Market} to be updated by the transaction
     */
    @Override
    public void execute(final Market market) {
        try {
            final User ownerUser = market.getActiveUserOrTarget(userID1);

            try {
                final Seller recipient = market.getSeller(userID2);

                if (recipient.getStoreFront().containsEntry(gameID))
                    throw new MultipleCopyException(gameID);
            } catch (final NotSellerException e) {
            }

            final Buyer recipient = market.getBuyer(userID2);

            final Inventory recipientInventory = recipient.getInventory();

            if (recipientInventory.containsEntry(gameID))
                throw new MultipleCopyException(gameID);

            Game game = null;

            try {
                final Buyer buyer = market.getBuyer(userID1);
                game = buyer.getInventory().remove(gameID);
            } catch (final NotBuyerException | GameDNEException e) {
                try {
                    final Seller seller = market.getSeller(userID1);
                    game = seller.getStoreFront().remove(gameID).getGame();
                } catch (final NotSellerException e1) {
                    throw new GameDNEException(gameID);
                }
            }

            recipientInventory.addEntry(game);

            // Issue a warning if transaction record has dirty data.
            warnUsernameDesync(ownerUser);
        } catch (final NoLoginException | UserDNEException | NotBuyerException | GameDNEException
                | MultipleCopyException e) {
            fail(e.getError());
        }
    }
}

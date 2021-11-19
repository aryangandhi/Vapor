package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Game;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.RemoveGameTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.BuyerUser;
import vapor.users.User;
import static org.junit.jupiter.api.Assertions.*;

public class RemoveGameTransactionTest {
    Market market;
    AdminUser adminUser;
    BuyerUser buyerUser;
    Game game;

    /**
     * Creates a market, user and game used for testing
     */
    @BeforeEach
    public void setup() throws UserExistsException, MultipleCopyException {
        try {
            market = new Market();
            adminUser = new AdminUser("UUUUUUUUUUUUUUU", 9999);
            buyerUser = new BuyerUser("Dinodaddy#", 1000);
            market.forceAddUser(adminUser);
            market.forceAddUser(buyerUser);
            game = new Game("Smash Bros");
            adminUser.getInventory().addEntry(game);
            adminUser.getInventory().endDay();
            buyerUser.getInventory().addEntry(game);
            buyerUser.getInventory().endDay();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, setup", e.getError());
        }
    }

    /**
     * Test for the case where an admin tries to remove a game from another user's inventory.
     */
    @Test
    public void testAdminRemoveGame() {
        try {
            logIn(adminUser);
            assertTrue(buyerUser.getInventory().containsEntry("Smash Bros"));
            RemoveGameTransaction transaction = (RemoveGameTransaction) getTransaction("08 Smash Bros                UUUUUUUUUUUUUUU Dinodaddy#     ");
            transaction.execute(market);
            assertFalse(buyerUser.getInventory().containsEntry("Smash Bros"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, testAdminRemoveGame", e.getError());
        }
    }

    /**
     * Test for the case where an admin user attempts to remove a game from their own inventory.
     */
    @Test
    public void testAdminSelfRemoveGame() {
        try {
            logIn(adminUser);
            assertTrue(adminUser.getInventory().containsEntry("Smash Bros"));
            RemoveGameTransaction transaction = (RemoveGameTransaction) getTransaction("08 Smash Bros                UUUUUUUUUUUUUUU UUUUUUUUUUUUUUU");
            transaction.execute(market);
            assertFalse(adminUser.getInventory().containsEntry("Smash Bros"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, testAdminSelfRemoveGame", e.getError());
        }
    }

    /**
     * Test for the case where a non-admin user performs the remove game transaction from their own inventory by
     * specifying userID in the last field of the raw transaction code.
     */
    @Test
    public void validNonAdminTest1() {
        try {
            logIn(buyerUser);
            assertTrue(buyerUser.getInventory().containsEntry("Smash Bros"));
            RemoveGameTransaction transaction = (RemoveGameTransaction) getTransaction("08 Smash Bros                Dinodaddy#      Dinodaddy#     ");
            transaction.execute(market);
            assertFalse(buyerUser.getInventory().containsEntry("Smash Bros"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, validNonAdminTest1", e.getError());
        }

    }

    /**
     * Test for the case where a non-admin user performs the remove game transaction from their own inventory without
     * specifying userID in the last field of the raw transaction code.
     */
    @Test
    public void validNonAdminTest2() throws MultipleLoginException, UserExistsException, InvalidTransactionCodeException {
        try {
            logIn(buyerUser);
            assertTrue(buyerUser.getInventory().containsEntry("Smash Bros"));
            RemoveGameTransaction transaction = (RemoveGameTransaction) getTransaction("08 Smash Bros                Dinodaddy#                     ");
            transaction.execute(market);
            assertFalse(buyerUser.getInventory().containsEntry("Smash Bros"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, validNonAdminTest2", e.getError());
        }
    }

    /**
     * Test for the case where an non-admin attempts to remove game from another user's inventory.
     */
    @Test
    public void validNonAdminTest3() throws MultipleLoginException, UserExistsException, InvalidTransactionCodeException {
        try {
            logIn(buyerUser);
            assertTrue(buyerUser.getInventory().containsEntry("Smash Bros"));
            RemoveGameTransaction transaction = (RemoveGameTransaction)     getTransaction("08 Smash Bros                Dinodaddy#      UUUUUUUUUUUUUUU");
            transaction.execute(market);
            assertFalse(buyerUser.getInventory().containsEntry("Smash Bros"));
            assertTrue(adminUser.getInventory().containsEntry("Smash Bros"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, validNonAdminTest3", e.getError());
        }
    }


    /**
     * Creates a TransactionBuilder object used to return a Transaction object
     *
     * @param rawTransaction String representing a transaction
     * @return {@code Transaction}
     */
    public Transaction getTransaction(String rawTransaction) {
        try {
            TransactionBuilder transactionBuilder = new TransactionBuilder(rawTransaction);
            return transactionBuilder.parse();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RemoveGameTransactionTest, getTransaction", e.getError());
            return null;
        }
    }

    /**
     * Helper function logs a user into the market
     * @param user User object
     */
    public void logIn(User user) throws MultipleLoginException {
        market.loginUser(user);
    }
}


package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Game;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.GiftGameTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.StandardUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

public class GiftGameTransactionTest {

    Market market;
    Game game;
    StandardUser user1;
    StandardUser user2;
    AdminUser user3;
    StandardUser user4;

    /**
     * creates a market, game and users used for testing
     */
    @BeforeEach
    public void setup() {
        try {
            market = new Market();
            user1 = new StandardUser("Aryan", 1000);
            user2 = new StandardUser("Raymond", 100);
            user3 = new AdminUser("Juan", 1000);
            game = new Game("Smash Bros");
            user1.getInventory().addEntry(game);
            user1.getInventory().endDay();
            market.forceAddUser(user1);
            market.forceAddUser(user2);
            market.forceAddUser(user3);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "GiftTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the execute method in GiftGameTransaction to see if it performs as intended
     */
    @Test
    public void testExecute() {
        assertFalse(user2.getInventory().containsEntry("Smash Bros"));
        userGiftHelper();
        assertTrue(user2.getInventory().containsEntry("Smash Bros"));
        assertFalse(user1.getInventory().containsEntry("Smash Bros"));
    }

    /**
     * Test for the case where users gift the same game to each other multiple times (ie. user1 gifts game to user2 and
     * user2 gifts game from user1 to user2)
     */
    @Test
    public void multipleUserTest() {

        multipleUserHelper();
        assertFalse(user2.getInventory().containsEntry("Smash Bros"));
        assertTrue(user3.getInventory().containsEntry("Smash Bros"));
    }

    /**
     * Helper function that performs a gift game transaction between two users
     */
    public void userGiftHelper() {
        try {
            logIn(user1);
            GiftGameTransaction transaction = (GiftGameTransaction) getTransaction("09 Smash Bros                " +
                    "Aryan           Raymond        ");
            transaction.execute(market);

            user2.getInventory().endDay();
            market.logoutUser();

        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "GiftTransactionTest, setup", e.getError());
        }
    }

    /**
     * Helper function that performs a gift game transaction between multiple users
     */
    public void multipleUserHelper(){
        try {
            userGiftHelper();
            logIn(user2);
            GiftGameTransaction transaction = (GiftGameTransaction) getTransaction("09 Smash Bros                " +
                    "Raymond         Juan           ");
            transaction.execute(market);
            market.logoutUser();

        }catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "GiftTransactionTest, setup", e.getError());
        }
    }

    /**
     * Test for the case where a gift is being gifted to a user that does not exist in market
     */
    @Test
    public void userDNETest() {
        logIn(user1);
        user4 = new StandardUser("Ibra", 1000);
        GiftGameTransaction transaction = (GiftGameTransaction) getTransaction("09 Smash Bros                " +
                "Aryan           Ibra           ");
        transaction.execute(market);
        assertFalse(user4.getInventory().containsEntry("Smash Bros"));
        assertTrue(user1.getInventory().containsEntry("Smash Bros"));
    }

    /**
     * Test for the case where an admin performs a gift game transaction between two other users
     */
    @Test
    public void adminGiftTest() {
        logIn(user3);
        GiftGameTransaction transaction = (GiftGameTransaction) getTransaction("09 Smash Bros                " +
                "Aryan           Raymond        ");
        transaction.execute(market);
        assertFalse(user1.getInventory().containsEntry("Smash Bros"));
        assertTrue(user2.getInventory().containsEntry("Smash Bros"));
    }

    /**
     * Test for the case where a gift game transaction is performed where the user being gifted already owns the game.
     */
    @Test
    public void ownedGiftTest() {
        try {
            logIn(user1);
            user2.getInventory().addEntry(game);
            GiftGameTransaction transaction = (GiftGameTransaction) getTransaction("09 Smash Bros                " +
                    "Aryan           Raymond        ");
            transaction.execute(market);
            assertTrue(user1.getInventory().containsEntry("Smash Bros"));
            assertTrue(user2.getInventory().containsEntry("Smash Bros"));
            market.logoutUser();

        }catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "GiftTransactionTest, setup", e.getError());
        }

    }

    /**
     * Test for the case where the a user is gifting a game to themselves
     */
    @Test
    public void selfGiftTest() {
        logIn(user1);
        GiftGameTransaction transaction = (GiftGameTransaction) getTransaction("09 Smash Bros                " +
                "Aryan           Aryan          ");
        transaction.execute(market);
        assertTrue(user1.getInventory().containsEntry("Smash Bros"));
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "GiftTransactionTest, getTransaction", e.getError());
            return null;
        }
    }

    /**
     * Helper function logs a user into the market
     *
     * @param user User object
     */
    public void logIn(User user) {
        try {
            market.loginUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "GiftTransactionTest, logIn", e.getError());
        }
    }

}

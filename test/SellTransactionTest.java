package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.SellTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.SellerUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of SellTransaction.java
 */
public class SellTransactionTest {

    Market market;
    SellerUser user;

    /**
     * Sets up a seller user and market used for testing
     * @throws UserExistsException a {@code User} with that username already
     *      *                                             exists.
     */
    @BeforeEach
    public void setup() {
        try {
            user = new SellerUser("Aryan", 1000);
            market = new Market();
            market.forceAddUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "SellTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the execute method in SellTransaction.java to see if it performs as intended
     */
    @Test
    public void testExecute() {
        try {
            logIn(user);
            SellTransaction transaction = (SellTransaction) getTransaction("03 Smash Bros                Aryan           10.00 010.00");
            transaction.execute(market);
            assertThrows(GameDNEException.class, () -> user.getStoreFront().getEntry("Smash Bros"));
            user.getStoreFront().endDay();
            assertDoesNotThrow(() -> user.getStoreFront().getEntry("Smash Bros"));
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "SellTransactionTest, testExecute", e.getError());
        }
    }

    /**
     * Tests the case where the sellerID in the given transaction string is different than the userID of the active user
     * in the market.
     */
    @Test
    public void randomSellerTest() {
        try {
            logIn(user);
            SellTransaction transaction = (SellTransaction) getTransaction("03 Smash Bros                Dinodaddy#      10.00 010.00");
            transaction.execute(market);
            assertThrows(GameDNEException.class, () -> user.getStoreFront().getEntry("Smash Bros"));
            user.getStoreFront().endDay();
            assertDoesNotThrow(() -> user.getStoreFront().getEntry("Smash Bros"));
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "SellTransactionTest, randomSellerTest", e.getError());
        }
    }

    /**
     * Tests the case execute method in SellTransaction.java where a seller tries to sell a game that they are already
     * selling
     */
    @Test
    public void multipleSellTest() {
        testExecute();
        logIn(user);
        SellTransaction transaction = (SellTransaction) getTransaction("03 Smash Bros                Dinodaddy#      10.00 100.00");
        transaction.execute(market);
        assertEquals(1, user.getStoreFront().getEntries().length);
        user.getStoreFront().endDay();
        assertEquals(1, user.getStoreFront().getEntries().length);
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "SellTransactionTest, getTransaction", e.getError());
            return null;
        }
    }

    /**
     * Helper function logs a user into the market
     * @param user User object
     */
    public void logIn(User user) {
        try {
            market.loginUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "SellTransactionTest, getTransaction", e.getError());
        }
    }

}

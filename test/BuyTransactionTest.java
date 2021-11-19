package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.BuyTransaction;
import vapor.transactions.SellTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.BuyerUser;
import vapor.users.SellerUser;
import vapor.users.StandardUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of BuyTransaction.java
 */
public class BuyTransactionTest {

    Market market;
    BuyerUser buyerUser;
    SellerUser sellerUser;

    /**
     * Creates a market and users used for testing
     */
    @BeforeEach
    public void setup() {
        try {
            market = new Market();
            buyerUser = new BuyerUser("Ibra", 100000);
            sellerUser = new SellerUser("Yomama", 100000);
            market.forceAddUser(buyerUser);
            market.forceAddUser(sellerUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "BuyTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the functionality of the execute method in BuyTransaction.java
     */
    @Test
    public void testExecute() {
        assertEquals(0, buyerUser.getInventory().getEntries().length);
        sellGame();
        logIn(buyerUser);
        BuyTransaction transaction = (BuyTransaction) getTransaction("04 Smash Bros                Yomama          Ibra           ");
        transaction.execute(market);
        assertTrue(buyerUser.getInventory().containsEntry("Smash Bros"));
        assertEquals(99000, buyerUser.getCredit());
        assertEquals(101000, sellerUser.getCredit());
    }

    /**
     * Test whether an Invalid Transaction is executed.
     */
    @Test
    public void testInvalidTransaction() {
        assertEquals(0, buyerUser.getInventory().getEntries().length);
        sellGame();
        logIn(buyerUser);
        BuyTransaction transaction = (BuyTransaction) getTransaction("04 Smash Bros                Ibra            Yomama         ");
        transaction.execute(market);
        assertEquals(0, buyerUser.getInventory().getEntries().length);
        assertEquals(100000, buyerUser.getCredit());
        assertEquals(100000, sellerUser.getCredit());
    }

    /**
     * Helper function that creates a SellerUser that lists a game for sale
     */
    public void sellGame() {
        try {
            logIn(sellerUser);
            SellTransaction transaction = (SellTransaction) getTransaction("03 Smash Bros                Yomama          10.00 010.00");
            transaction.execute(market);
            sellerUser.getStoreFront().endDay();
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "buyTransactionTest, sellGame", e.getError());
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "BuyTransactionTest, getTransaction", e.getError());
            return null;
        }
    }

    /**
     * Test for the case where a user (full standard or admin) tries to buy a game they are selling
     */
    @Test
    public void selfBuyTest(){
        StandardUser standardUser = new StandardUser("Aryan", 1000);
        try {
            logIn(standardUser);
            SellTransaction sellTransaction = (SellTransaction) getTransaction("03 Smash Bros                Aryan           10.00 010.00");
            sellTransaction.execute(market);
            standardUser.getStoreFront().endDay();
            BuyTransaction buyTransaction = (BuyTransaction) getTransaction("04 Smash Bros                Aryan           Aryan          ");
            buyTransaction.execute(market);
            market.logoutUser();

        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "buyTransactionTest, sellGame", e.getError());
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "BuyTransactionTest, logIn", e.getError());
        }
    }
}

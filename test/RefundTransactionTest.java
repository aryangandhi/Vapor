package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.RefundTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.BuyerUser;
import vapor.users.SellerUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of RefundTransaction.java
 */
public class RefundTransactionTest {

    Market market;
    AdminUser adminUser;
    BuyerUser buyerUser;
    SellerUser sellerUser;

    /**
     * Sets up a market and admin user for test purposes
     */
    @BeforeEach
    public void setup() throws UserExistsException {
        try {
            market = new Market();
            adminUser = new AdminUser("Juan", 1000);
            market.forceAddUser(adminUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RefundTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the execute method in RefundTransaction.java
     */
    @Test
    public void testExecute() {
        logIn(adminUser);
        setBuyerUser("Dinodaddy#", 10000);
        setSellerUser("BBBBBBBBBBBBB", 10000);
        RefundTransaction transaction = (RefundTransaction) getTransaction("05 Dinodaddy#      " +
                "BBBBBBBBBBBBB   000010.00");
        transaction.execute(market);
        assertEquals(11000, buyerUser.getCredit());
        assertEquals(9000, sellerUser.getCredit());
    }

    /**
     *  Create and add a new buyer user to the market.
     *
     * @param userID username for the user being created.
     * @param credit the amount of credits for the user being created.
     */
    public void setBuyerUser(String userID, int credit) {
        try {
            buyerUser = new BuyerUser(userID, credit);
            market.forceAddUser(buyerUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RefundTransactionTest, setBuyerUser", e.getError());
        }
    }

    /** Create and add a new seller user to the market.
     *
     * @param userID username for the user being created.
     * @param credit the amount of credits for the user being created.
     */
    public void setSellerUser(String userID, int credit) {
        try {
            sellerUser = new SellerUser(userID, credit);
            market.forceAddUser(sellerUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RefundTransactionTest, setSellerUser", e.getError());
        }
    }


    /**
     * Test for the case where the buyer's credit goes over the max limit if the refund transaction is performed
     */
    @Test
    public void overBuyerLimitTest() {
        logIn(adminUser);
        setBuyerUser("Dinodaddy#", 99999000);
        setSellerUser("BBBBBBBBBBBBB", 10000);
        RefundTransaction transaction = (RefundTransaction) getTransaction("05 Dinodaddy#      " +
                "BBBBBBBBBBBBB   000010.00");
        transaction.execute(market);
        assertEquals(9000, sellerUser.getCredit());
        assertEquals(99999999, buyerUser.getCredit());
    }

    /**
     * Test for the case where seller's credit goes below zero if the refund transaction is performed      a valid {@code Transaction}.
     */
    @Test
    public void belowSellerLimitTest() {
        logIn(adminUser);
        setBuyerUser("Dinodaddy#", 99999000);
        setSellerUser("BBBBBBBBBBBBB", 100);
        RefundTransaction transaction = (RefundTransaction) getTransaction("05 Dinodaddy#      " +
                "BBBBBBBBBBBBB   000010.00");
        transaction.execute(market);
        assertEquals(99999000, buyerUser.getCredit());
        assertEquals(100, sellerUser.getCredit());
    }

    /**
     * Test for the case where the a user is being refunded to themselves
     */
    @Test
    public void selfUserRefundTest() {
        logIn(adminUser);
        setBuyerUser("Dinodaddy#", 99999000);
        RefundTransaction transaction = (RefundTransaction) getTransaction("05 Dinodaddy#      " +
                "Dinodaddy#      000010.00");
        transaction.execute(market);
        assertEquals(99999000, buyerUser.getCredit());
    }

    /**
     * Test for the case an admin performs a refund transaction for themselves
     */
    @Test
    public void selfAdminRefundTest() {
        logIn(adminUser);
        setSellerUser("BBBBBBBBBBBBB", 10000);
        RefundTransaction transaction = (RefundTransaction) getTransaction("05 Juan            " +
                "BBBBBBBBBBBBB   000010.00");
        transaction.execute(market);
        assertEquals(2000, adminUser.getCredit());
    }

    /**
     * Test for the case the buyer and seller ID's are swapped in the transaction code
     */
    @Test
    public void swapTest() {
        logIn(adminUser);
        setBuyerUser("Dinodaddy#", 10000);
        setSellerUser("BBBBBBBBBBBBB", 10000);
        RefundTransaction transaction = (RefundTransaction) getTransaction("05 BBBBBBBBBBBBB   " +
                "Dinodaddy#      000010.00");
        transaction.execute(market);
        assertEquals(10000, buyerUser.getCredit());
        assertEquals(10000, sellerUser.getCredit());
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RefundTransactionTest, logIn", e.getError());
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "RefundTransactionTest, getTransaction", e.getError());
            return null;
        }
    }

}

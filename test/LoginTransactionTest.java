package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.ErrorLogger;
import vapor.exceptions.InvalidTransactionCodeException;
import vapor.exceptions.UserExistsException;
import vapor.exceptions.VaporException;
import vapor.transactions.LoginTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.BuyerUser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of LoginTransaction.java
 */
public class LoginTransactionTest {

    Market market;
    AdminUser user;

    /**
     * Creates a market and user used for testing
     */
    @BeforeEach
    public void setup() {
        try {
            market = new Market();
            user = new AdminUser("Dinodaddy#", 1000);
            market.forceAddUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "LoginTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the execute method in LoginTransaction.java
     */
    @Test
    public void testExecute() {
        try {
            assertNull(market.getActiveUser());
            LoginTransaction transaction = (LoginTransaction) getTransaction("00 Dinodaddy#      AA 000010.00");
            transaction.execute(market);
            assertEquals(user, market.getActiveUser());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "LoginTransactionTest, testExecute", e.getError());
        }
    }

    /**
     * Test for the case where multiple users try to login at the same time
     */
    @Test
    public void testMultipleLogin() {
        try {
            testExecute();
            BuyerUser buyerUser = new BuyerUser("UUUUUUUUUUUUUUU", 9999);
            market.forceAddUser(buyerUser);
            LoginTransaction transaction = (LoginTransaction) getTransaction("00 UUUUUUUUUUUUUUU BS 000099.99");
            transaction.execute(market);
            assertEquals(user, market.getActiveUser());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "LoginTransactionTest, testMultipleLogin", e.getError());
        }
    }

    /**
     * Test for the case where a user that does not exist in the market tries to login
     */
    @Test
    public void testUserDNE() {
        try {
            assertNull(market.getActiveUser());
            LoginTransaction transaction = (LoginTransaction) getTransaction("00 UUUUUUUUUUUUUUU BS 000099.99");
            transaction.execute(market);
            assertNull(market.getActiveUser());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "LoginTransactionTest, testUserDNE", e.getError());
        }
    }
    /**
     * Creates a TransactionBuilder object used to return a Transaction object
     *
     * @param rawTransaction String representing a transaction
     * @return {@code Transaction}
     */
    public Transaction getTransaction(String rawTransaction) throws InvalidTransactionCodeException {
        try {
            TransactionBuilder transactionBuilder = new TransactionBuilder(rawTransaction);
            return transactionBuilder.parse();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "LoginTransactionTest, getTransaction", e.getError());
            return null;
        }
    }
}

package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.InvalidTransactionCodeException;
import vapor.exceptions.MultipleLoginException;
import vapor.exceptions.UserExistsException;
import vapor.transactions.LogoutTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.StandardUser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of LogoutTransaction.java
 */
public class LogoutTransactionTest {

    Market market;
    AdminUser adminUser;
    StandardUser standardUser;

    /**
     * Creates a market and users for testing
     */
    @BeforeEach
    public void setup() throws UserExistsException {
        market = new Market();
        adminUser = new AdminUser("Aryan", 1000);
        standardUser = new StandardUser("Raymond", 100);
        market.forceAddUser(adminUser);
    }

    /**
     * Tests the execute method in LogoutTransaction.java
     * @throws MultipleLoginException
     * @throws InvalidTransactionCodeException
     */
    @Test
    public void testExecute() throws MultipleLoginException, InvalidTransactionCodeException {
        market.loginUser(adminUser);
        assertNotNull(market.getActiveUser());
        LogoutTransaction transaction = (LogoutTransaction) getTransaction("10 Aryan           AA 000010.00");
        transaction.execute(market);
        assertNull(market.getActiveUser());
    }

    /**
     * Test for the case where the logged in user attempts to logout another user.
     */
    @Test
    public void otherUserLogoutTest() throws InvalidTransactionCodeException, MultipleLoginException, UserExistsException {
        market.forceAddUser(standardUser);
        market.loginUser(standardUser);
        assertNotNull(market.getActiveUser());
        LogoutTransaction transaction = (LogoutTransaction) getTransaction("10 Aryan           AA 000010.00");
        transaction.execute(market);
        assertNull(market.getActiveUser());
    }

    /**
     * Creates a TransactionBuilder object used to return a Transaction object
     *
     * @param rawTransaction String representing a transaction
     * @return {@code Transaction}
     * @throws InvalidTransactionCodeException the record provided does not describe
     *                                            a valid {@code Transaction}.
     */
    public Transaction getTransaction(String rawTransaction) throws InvalidTransactionCodeException {
        TransactionBuilder transactionBuilder = new TransactionBuilder(rawTransaction);
        return transactionBuilder.parse();
    }
}

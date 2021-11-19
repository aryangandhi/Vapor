package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.DeleteTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.BuyerUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of DeleteTransaction.java
 */
public class DeleteTransactionTest {

    Market market;
    AdminUser adminUser;
    BuyerUser buyerUser;

    /**
     * Sets up a market and users used for testing purposes
     */
    @BeforeEach
    public void setup() {
        try {
            market = new Market();
            adminUser = new AdminUser("Raymond", 1000);
            buyerUser = new BuyerUser("Dinodaddy#", 0);
            market.forceAddUser(adminUser);
            market.forceAddUser(buyerUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "DeleteTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the execute method in DeleteTransaction.java as intended
     */
    @Test
    public void testExecute() {
        logIn(adminUser);
        assertDoesNotThrow(() -> market.getUser("Dinodaddy#"));
        DeleteTransaction transaction = (DeleteTransaction) getTransaction("02 Dinodaddy#      BS 000000.00");
        transaction.execute(market);
        assertThrows(UserDNEException.class, () -> market.getUser("Dinodaddy#"));
    }

    /**
     * Tests the case where an admin user attempts to delete themselves
     */
    @Test
    public void selfDeleteTest() {
        logIn(adminUser);
        assertDoesNotThrow(() -> market.getUser("Raymond"));
        DeleteTransaction transaction = (DeleteTransaction) getTransaction("02 Raymond         AA 000010.00");
        transaction.execute(market);
        assertDoesNotThrow(() -> market.getUser("Raymond"));

    }

    /**
     * Tests the case where a non-admin user attempts to delete another user
     */
    @Test
    public void nonAdminDeleteTest() {
        logIn(buyerUser);
        assertDoesNotThrow(() -> market.getUser("Raymond"));
        DeleteTransaction transaction = (DeleteTransaction) getTransaction("02 Raymond         AA 000010.00");
        transaction.execute(market);
        assertDoesNotThrow(() -> market.getUser("Raymond"));
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "DeleteTransactionTest, getTransaction", e.getError());
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "DeleteTransactionTest, logIn", e.getError());
        }
    }

}

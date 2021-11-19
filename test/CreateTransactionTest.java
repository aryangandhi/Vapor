package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.CreateTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.AdminUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of CreateTransaction.java
 */
public class CreateTransactionTest {

    Market market;
    AdminUser adminUser;

    /**
     * Creates a market and admin user used for test purposes
     */
    @BeforeEach
    public void setup() {
        try {
            market = new Market();
            adminUser = new AdminUser("UUUUUUUUUUUUUUU", 1000);
            market.forceAddUser(adminUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "CreateTransactionTest, setup", e.getError());
        }

    }

    /**
     * Creates a TransactionBuilder object used to return a Transaction object
     *
     * @param rawTransaction String representing a transaction
     * @return {@code Transaction}
     */
    public Transaction getTransactionBuilder(String rawTransaction) {
        try {
            TransactionBuilder transactionBuilder = new TransactionBuilder(rawTransaction);
            return transactionBuilder.parse();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "CreateTransactionTest, getTransactionBuilder", e.getError());
            return null;
        }
    }

    public void logIn(User user) {
        try {
            market.loginUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "CreateTransactionTest, logIn", e.getError());
        }
    }

    /**
     * Tests the functionality of .execute() in CreateTransaction.java as intended
     */
    @Test
    public void testExecute() {
        try {
            logIn(adminUser);
            CreateTransaction transaction = (CreateTransaction) getTransactionBuilder("01 Dinodaddy#      FS 000010.00");
            assertDoesNotThrow(() -> transaction.execute(market));
            assertDoesNotThrow(() -> market.getUser("Dinodaddy#"));
            assertEquals(User.UserType.FULL, market.getUser("Dinodaddy#").getUserType());
            assertEquals(1000, market.getUser("Dinodaddy#").getCredit());
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "CreateTransactionTest, testExecute", e.getError());
        }
    }

    /**
     * Test for the case where an non-admin user tries to create a user
     */
    @Test
    public void unprivilegedCreate() {
        try {
            testExecute();
            logIn(market.getUser("Dinodaddy#"));
            CreateTransaction transaction = (CreateTransaction) getTransactionBuilder("01 Aryan           AA 000001.00");
            transaction.execute(market);
            assertThrows(UserDNEException.class, () -> market.getUser("Aryan"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "CreateTransactionTest, unprivilegedCreate", e.getError());
        }
    }
}

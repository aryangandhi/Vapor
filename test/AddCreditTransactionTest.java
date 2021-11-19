package test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.AddCreditTransaction;
import vapor.transactions.Transaction;
import vapor.transactions.TransactionBuilder;
import vapor.users.StandardUser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of AddCreditTransaction.java
 */
public class AddCreditTransactionTest {
    TransactionBuilder transactionBuilder;
    String addCredit;
    Market market;
    DisplayNameGenerator.Standard user;

    /**
     * Creates a user, market and transaction builder used for testing.
     */
    @BeforeEach
    public void setup(){
        market = new Market();
    }

    /**
     * Tests the functionality of the execute method in AddCreditTransaction.java.
     */
    @Test
    public void testExecute() {
        try {
            userHelper(1000);
            AddCreditTransaction transaction = (AddCreditTransaction) getTransaction("000010.00");
            transaction.execute(market);
            assertEquals(2000, market.getUser("Dinodaddy#").getCredit());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, testExecute", e.getError());
        }
    }

    /**
     * Helper function to create the TransactionBuilder
     * @param addCredit string representing add credit transaction.
     * @return transaction for the inputted raw transaction string.
     */
    public Transaction getTransaction(String addCredit) {
        try {
            String credit = addCreditHelper(addCredit);
            return new TransactionBuilder(credit).parse();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, getTransaction", e.getError());
            return null;
        }
    }

    /**
     * Helper function to create the Standard User
     *
     * @param credit credit of users
     *
     */
    public void userHelper(int credit) {
        try {
            StandardUser user = new StandardUser("Dinodaddy#", credit);
            market.forceAddUser(user);
            market.loginUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, userHelper", e.getError());
        }

    }

    /**
     * Helper function to create the string representing AddCreditTransaction
     * @param credit credit being added to user
     * @return String representing AddCreditTransaction
     */
    public String addCreditHelper(String credit){ return "06 Dinodaddy#      FS " + credit; }

    /**
     * Test for the case where the amount of credit added goes over the daily limit.
     *
     * @throws InvalidTransactionCodeException the record provided does not describe
     *                                                                    a valid {@code Transaction}.
     * @throws UserDNEException no {@code User} with username.
     */
    @Test
    public void overDailyLimit() throws InvalidTransactionCodeException, UserDNEException, UserExistsException, MultipleLoginException {
        userHelper(1000);
        AddCreditTransaction transaction = (AddCreditTransaction) getTransaction("001000.00");
        transaction.execute(market);
        assertEquals(101000, market.getUser("Dinodaddy#").getCredit());

    }

    /**
     * Tests for multiple add credit transactions in a day that eventually go over the daily limit
     */
    @Test
    public void multipleAddCredit() {
        try {
            userHelper(1000);
            AddCreditTransaction transaction = (AddCreditTransaction) getTransaction("000600.00");
            transaction.execute(market);
            assertEquals(61000, market.getUser("Dinodaddy#").getCredit());
            addCredit = "06 Dinodaddy#      FS 000200.00";
            transactionBuilder = new TransactionBuilder(addCredit);
            AddCreditTransaction addCreditTransaction_2 = (AddCreditTransaction) transactionBuilder.parse();
            addCreditTransaction_2.execute(market);
            assertEquals(81000, market.getUser("Dinodaddy#").getCredit());
            addCredit = "06 Dinodaddy#      FS 000500.00";
            transactionBuilder = new TransactionBuilder(addCredit);
            AddCreditTransaction addCreditTransaction_3 = (AddCreditTransaction) transactionBuilder.parse();
            addCreditTransaction_3.execute(market);
            assertEquals(81000, market.getUser("Dinodaddy#").getCredit());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, multipleAddCredit", e.getError());
        }

    }

    /**
     * Test for the case where user's credit is at the limit and credit is being added.
     */
    @Test
    public void overMaxLimit() {
        try {
            userHelper(99999999);
            AddCreditTransaction transaction = (AddCreditTransaction) getTransaction("000000.01");
            transaction.execute(market);
            assertEquals(99999999, market.getUser("Dinodaddy#").getCredit());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, overMaxLimit", e.getError());
        }
    }

    /**
     * Test for the case where the add credit transaction make user's credit over max limit if credit is added.
     */
    @Test
    public void goToMaxLimit() {
        try {
            userHelper(99999998);
            AddCreditTransaction transaction = (AddCreditTransaction) getTransaction("000000.02");
            transaction.execute(market);
            assertEquals(99999999, market.getUser("Dinodaddy#").getCredit());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, goToMaxLimit", e.getError());
        }
    }

    /**
     * Test for the case where the credit is being added over the daily limit while simultaneously going over the user's
     * max credit limit.
     */
    @Test
    public void overBothLimit() {
        try {
            userHelper(99990000);
            AddCreditTransaction transaction = (AddCreditTransaction) getTransaction( "001000.01");
            transaction.execute(market);
            assertEquals(99990000, market.getUser("Dinodaddy#").getCredit());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AddCreditTransactionTest, overBothLimit", e.getError());
        }
    }

    /**
     * Sets the market and user to null after all tests.
     */
    @AfterEach
    public void reset(){
        market = null;
        user = null;
    }

}

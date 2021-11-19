package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vapor.Market;
import vapor.exceptions.*;
import vapor.transactions.*;
import vapor.users.AdminUser;
import vapor.users.BuyerUser;
import vapor.users.SellerUser;
import vapor.users.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of AuctionSaleTransaction.java
 */
public class AuctionSaleTransactionTest {
    Market market;
    AdminUser adminUser;
    SellerUser sellerUser;
    BuyerUser buyerUser;

    @BeforeEach
    public void setup() {
        try {
            market = new Market();
            adminUser = new AdminUser("UUUUUUUUUUUUUUU", 1000);
            buyerUser = new BuyerUser("Dinodaddy#", 10000);
            sellerUser = new SellerUser("Yomama", 10000);
            market.forceAddUser(adminUser);
            market.forceAddUser(buyerUser);
            market.forceAddUser(sellerUser);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AuctionSaleTransactionTest, setup", e.getError());
        }
    }

    /**
     * Tests the execute method in AuctionSaleTransaction.java
     */
    @Test
    public void testExecute() {
        try {
            sellGame("010.00");
            logIn(adminUser);
            assertFalse(market.saleActivated);
            AuctionSaleTransaction transaction = (AuctionSaleTransaction) getTransaction("07 UUUUUUUUUUUUUUU AA 000010.00");
            transaction.execute(market);
            market.logoutUser();
            testSaleActivated();
            testDiscount(10000, 9100, 10000, 10900);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AuctionSaleTransactionTest, testExecute", e.getError());
        }
    }


    /**
     * Helper function to test if the SaleActivated in market is true
     */
    public void testSaleActivated(){ assertTrue(market.saleActivated); }

    /**
     * Test to see if the discount price is applied when buying a game on sale
     */
    public void testDiscount(int buyerBefore, int buyerAfter, int sellerBefore, int sellerAfter) {
        assertEquals(buyerBefore, buyerUser.getCredit());
        assertEquals(sellerBefore, sellerUser.getCredit());
        buyGame();
        assertTrue(market.saleActivated);
        assertEquals(buyerAfter, buyerUser.getCredit());
        assertEquals(sellerAfter, sellerUser.getCredit());
    }


    /**
     * Helper function that performs a buy transaction in the market
     */
    public void buyGame() {
        try {
            logIn(buyerUser);
            BuyTransaction transaction = (BuyTransaction) getTransaction("04 Smash Bros                Yomama          Dinodaddy#     ");
            transaction.execute(market);
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AuctionSaleTransactionTest, buyGame", e.getError());
        }
    }

    /**
     * Helper function that creates a SellerUser that lists a game for sale
     */
    public void sellGame(String price)  {
        try {
            logIn(sellerUser);
            SellTransaction transaction = (SellTransaction) getTransaction("03 Smash Bros                Yomama          10.00 "+price);
            transaction.execute(market);
            sellerUser.getStoreFront().endDay();
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AuctionSaleTransactionTest, sellGame", e.getError());
        }
    }

    /**
     * Test the execute method for the auction sale transaction where a game is minimum price ($0.01)
     * but it has a discount associated with it
     */
    @Test
    public void minPriceTest() {
        try {
            sellGame("000.01");
            logIn(adminUser);
            assertFalse(market.saleActivated);
            AuctionSaleTransaction transaction = (AuctionSaleTransaction) getTransaction("07 UUUUUUUUUUUUUUU AA 000010.00");
            transaction.execute(market);
            market.logoutUser();
            testSaleActivated();
            testDiscount(10000, 9999, 10000, 10001);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AuctionSaleTransactionTest, minPriceTest", e.getError());
        }
    }

    /**
     * Creates a TransactionBuilder object used to return a Transaction object      a valid {@code Transaction}.
     */
    public Transaction getTransaction(String rawTransaction) {
        try {
            TransactionBuilder transactionBuilder = new TransactionBuilder(rawTransaction);
            return transactionBuilder.parse();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "AuctionSaleTransactionTest, getTransaction", e.getError());
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
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "<NAME>TransactionTest, test<Name>", e.getError());
        }
    }
}

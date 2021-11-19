package test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import vapor.Market;
import vapor.exceptions.*;
import static org.junit.jupiter.api.Assertions.*;
import vapor.users.*;


/**
 * Tests the functionality of Market.java
 */
public class MarketTest {
    Market market;
    AdminUser admin_user;
    BuyerUser buyer_user;
    SellerUser seller_user;
    StandardUser standard_user;

    /**
     * Creates a market and users used for testing
     */
    @BeforeEach
    public void setup() {
        market = new Market();
        admin_user = new AdminUser("Aryan", 0);
        buyer_user = new BuyerUser("Raymond", 20);
        seller_user = new SellerUser("Juan", 10);
        standard_user = new StandardUser("Ibra", 100);
    }

    /**
     * Tests the functionality of market.loginUser()
     */
    @Test
    public void test_loginUser() {
        try {
            market.loginUser(admin_user);
            assertEquals(market.getActiveUser(), admin_user);
            MultipleLoginException exception = assertThrows(MultipleLoginException.class, () -> {market.loginUser(admin_user);});
            String expectedMessage = "Cannot login Aryan; a user is currently logged in.";
            String actualMessage = exception.getError();
            assertEquals(expectedMessage, actualMessage);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_loginUser", e.getError());
        }
    }

    /**
     * Tests the functionality of market.existsUser()
     */
    @Test
    public void test_existsUser() {
        this.unprivUser_helper();
        assertTrue(market.existsUser("Raymond"));
        assertTrue(market.existsUser("Aryan"));
        assertTrue(market.existsUser("Ibra"));
        assertFalse(market.existsUser("Greg"));
    }

    /**
     * Tests the functionality of market.forceAddUser()
     */
    @Test
    public void test_forceAddUser() {
        try {
            assertEquals(buyer_user, market.forceAddUser(buyer_user));
            assertTrue(market.getUsers().containsKey(buyer_user.getUsername()));
            assertThrows(UserExistsException.class, () -> {market.forceAddUser(buyer_user);});
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_loginUser", e.getError());
        }
    }

    /**
     * Tests the functionality of market.isAuthorisedState()
     */
    @Test
    public void test_isAuthorisedState() {
        try {
            market.loginUser(admin_user);
            assertTrue(market.isAuthorisedState());
            market.logoutUser();
            market.loginUser(buyer_user);
            assertFalse(market.isAuthorisedState());
            market.logoutUser();
            assertNull(market.getActiveUser());
            assertFalse(market.isAuthorisedState());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_isAuthorisedState", e.getError());
        }
    }

    /**
     * Tests the functionality of market.addUser()
     */
    @Test
    public void test_addUser() {
        try {
            market.loginUser(admin_user);
            assertDoesNotThrow(() -> {market.addUser(admin_user);});
            assertTrue(market.getUsers().containsKey(admin_user.getUsername()));
            assertDoesNotThrow(() -> {market.addUser(buyer_user);});
            assertTrue(market.getUsers().containsKey(buyer_user.getUsername()));
            market.logoutUser();
            market.loginUser(buyer_user);
            assertThrows(UnauthorisedUserException.class, () -> {market.addUser(seller_user);});
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_addUser", e.getError());
        }
    }

    /**
     * Tests the functionality of market.removeUser()
     */
    @Test
    public void test_removeUser() {
        try {
            this.unprivUser_helper();
            market.loginUser(admin_user);
            assertDoesNotThrow(() -> market.removeUser(buyer_user));
            assertFalse(market.getUsers().containsKey(buyer_user.getUsername()));
            assertThrows(SelfDeletionException.class, () -> market.removeUser(admin_user));
            market.logoutUser();
            market.loginUser(seller_user);
            assertThrows(UnauthorisedUserException.class, () -> market.removeUser(admin_user));
            assertThrows(UnauthorisedUserException.class, () -> {market.addUser(seller_user);});
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_removeUser", e.getError());
        }
    }

    /**
     * Test the functionality of market.getActiveUser()
     */
    @Test
    public void test_getActiveUser() {
        try {
            assertNull(market.getActiveUser());
            market.loginUser(admin_user);
            assertEquals(admin_user, market.getActiveUser());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_getActiveUser", e.getError());
        }
    }

    /**
     * Test the functionality of market.getUser()
     */
    @Test
    public void test_getUser() {
        try {
            this.privUser_helper(admin_user);
            assertEquals(admin_user, market.getUser("Aryan"));
            UserDNEException exception = assertThrows(UserDNEException.class, () -> { market.getUser("Juan");});
            String expectedMessage = "Juan does not exist in the market.";
            String actualMessage = exception.getError();
            assertEquals(expectedMessage, actualMessage);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_getUser", e.getError());
        }
    }

    /**
     * Helper function that adds and logs a privileged user into the market
     * @param user user to be added to the market.
     */
    private void privUser_helper(User user) {
        try {
            market.loginUser(user);
            market.addUser(user);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, privUser_helper", e.getError());
        }
    }

    /**
     * Tests the functionality of market.getBuyerUser()
     */
    @Test
    public void test_getBuyer() {
        try {
            this.unprivUser_helper();
            market.loginUser(buyer_user);
            assertEquals(buyer_user, market.getBuyer("Raymond"));
            NotBuyerException exception = assertThrows(NotBuyerException.class, () -> { market.getBuyer("Juan");});
            String expectedMessage = "Juan is not a buyer.";
            String actualMessage = exception.getError();
            assertEquals(expectedMessage, actualMessage);
            UserDNEException dne_exception = assertThrows(UserDNEException.class, () -> { market.getUser("Greg");});
            String expected_Message = "Greg does not exist in the market.";
            String actual_Message = dne_exception.getError();
            assertEquals(expected_Message, actual_Message);
            assertEquals(standard_user, market.getBuyer("Ibra"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_getBuyer", e.getError());
        }
    }

    /**
     * Helper function that adds and logs a privilege user as well as unprivileged users into the market
     */
    private void unprivUser_helper() {
        try {
            this.privUser_helper(admin_user);
            market.addUser(buyer_user);
            market.addUser(seller_user);
            market.addUser(standard_user);
            market.logoutUser();
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, unprivUser_helper", e.getError());
        }
    }

    /**
     * Test the functionality of market.logoutUser()
     */
    @Test
    public void test_logoutUser() {
        try {
            assertThrows(NoLoginException.class, () -> market.logoutUser());
            market.loginUser(admin_user);
            assertDoesNotThrow(() -> market.logoutUser());
            assertNull(market.getActiveUser());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_logoutUser", e.getError());
        }
    }


    /**
     * Test the functionality of market.getSeller()
     */
    @Test
    public void test_getSeller() {
        try {
            this.unprivUser_helper();
            assertEquals(seller_user, market.getSeller("Juan"));
            NotSellerException exception = assertThrows(NotSellerException.class, () -> { market.getSeller("Raymond");});
            String expectedMessage = "Raymond is not a seller.";
            String actualMessage = exception.getError();
            assertEquals(expectedMessage, actualMessage);
            UserDNEException dne_exception = assertThrows(UserDNEException.class, () -> { market.getUser("Greg");});
            String expected_Message = "Greg does not exist in the market.";
            String actual_Message = dne_exception.getError();
            assertEquals(expected_Message, actual_Message);
            assertEquals(standard_user, market.getBuyer("Ibra"));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_getSeller", e.getError());
        }
    }

    /**
     * Tests the functionality of market.toggleAuctionSale()
     */
    @Test
    public void test_toggleAuctionSale() {
        try {
            assertFalse(market.saleActivated);
            market.loginUser(admin_user);
            assertDoesNotThrow(() -> market.toggleAuctionSale());
            assertTrue(market.saleActivated);
            market.logoutUser();
            market.loginUser(buyer_user);
            assertThrows(UnauthorisedUserException.class, () -> market.toggleAuctionSale());
            assertTrue(market.saleActivated);
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "MarketTest, test_toggleAuctionSale", e.getError());
        }
    }

    /**
     * Resets the market to null
     */
    @AfterEach
    public void reset(){
        market = null;
    }
}

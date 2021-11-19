package test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import vapor.Game;
import vapor.Listing;

/**
 * Tests the functionality of Listing.java
 */
public class ListingTest {
    Listing listing;
    Game game;

    /**
     * Creates a game and listing object used for testing
     */
    @BeforeEach
    public void setup(){
        game = new Game("Smash Bros");
        listing = new Listing(game, 50, 0.1f);
    }

    /**
     * Tests the functionality of Listing.getGame()
     */
    @Test
    public void test_getGame(){
        assertEquals(game, listing.getGame());
    }

    /**
     * Tests the functionality of Listing.getPrice()
     */
    @Test
    public void test_getPrice(){
        assertEquals(50, listing.getPrice());
    }

    /**
     * Tests the functionality of Listing.getDiscount()
     */
    @Test
    public void test_getDiscount(){
        assertEquals(0.1f, listing.getDiscount());
    }

}

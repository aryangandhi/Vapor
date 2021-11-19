package test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import vapor.StoreFront;
import vapor.Listing;
import vapor.Game;
import vapor.exceptions.ErrorLogger;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.MultipleCopyException;
import vapor.exceptions.VaporException;

/**
 * Tests the functionality of StoreFront.java
 */
public class StoreFrontTest {
    Listing listing;
    StoreFront storeFront;
    Game game;

    /**
     * Creates a game, listing and storeFront object used for testing.
     */
    @BeforeEach
    public void setup(){
        game = new Game("Smash Bros");
        listing = new Listing(game, 10, 0.1f);
        storeFront = new StoreFront();
    }

    /**
     * Tests the functionality of StoreFront.addEntry()
     */
    @Test
    public void test_addEntry(){
        assertDoesNotThrow(() -> storeFront.addEntry(listing));
        MultipleCopyException exception = assertThrows(MultipleCopyException.class, () -> storeFront.addEntry(listing));
        String expected_message = "A copy of Smash Bros is already in the inventory.";
        String actual_message = exception.getError();
        assertEquals(expected_message, actual_message);
    }

    /**
     * Tests the functionality of StoreFront.containsEntry()
     */
    @Test
    public void test_containsEntry() {
        try {
            assertFalse(storeFront.containsEntry(game.getName()));
            storeFront.addEntry(listing);
            storeFront.endDay();
            assertTrue(storeFront.containsEntry(game.getName()));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "StoreFrontTest, test_containsEntry", e.getError());
        }
    }

    /**
     * Tests the functionality of StoreFront.getEntry
     */
    @Test
    public void test_getEntry() {
        try {
            GameDNEException exception = assertThrows(GameDNEException.class, () -> storeFront.getEntry(game.getName()));
            String expected_message = "Smash Bros does not exist in the inventory.";
            String actual_message = exception.getError();
            assertEquals(expected_message, actual_message);
            storeFront.addEntry(listing);
            storeFront.endDay();
            assertEquals(listing, storeFront.getEntry(game.getName()));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "StoreFrontTest, test_getEntry", e.getError());
        }
    }


}

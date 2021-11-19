package test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import vapor.Inventory;
import vapor.Game;
import vapor.exceptions.ErrorLogger;
import vapor.exceptions.GameDNEException;
import vapor.exceptions.MultipleCopyException;
import vapor.exceptions.VaporException;

/**
 * Tests the functionality of Inventory.java
 */
public class InventoryTest {
    Inventory inventory;
    Game game;

    /**
     * Creates an inventory and game used for testing
     */
    @BeforeEach
    public void setup() {
        inventory = new Inventory();
        game = new Game("Smash Bros");
    }

    /**
     * Tests the functionality of inventory.addEntry
     */
    @Test
    public void test_addEntry() {
        try {
            assertEquals(game, inventory.addEntry(game));
            assertThrows(MultipleCopyException.class, () -> inventory.addEntry(game));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "InventoryTest, test<Name>", e.getError());
        }
    }

    /**
     * Tests the functionality of inventory.getEntry
     */
    @Test
    public void test_getEntry() {
        try {
            assertThrows(GameDNEException.class, () -> inventory.getEntry(game.getName()));
            inventory.addEntry(game);
            inventory.endDay();
            assertEquals(game, inventory.getEntry(game.getName()));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "InventoryTest, test_getEntry", e.getError());
        }
    }

    /**
     * Tests the functionality of inventory.containsEntry
     */
    @Test
    public void test_containsEntry() {
        try {
            assertFalse(inventory.containsEntry(game.getName()));
            inventory.addEntry(game);
            assertTrue(inventory.containsEntry(game.getName()));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "InventoryTest, test_containsEntry", e.getError());
        }
    }

    /**
     * Resets the market to null
     */
    @AfterEach
    public void reset() {
        inventory = null;
    }
}

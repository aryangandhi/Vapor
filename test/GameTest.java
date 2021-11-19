package test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import vapor.Game;

/**
 * Tests the functionality of GameTest.java
 */
public class GameTest {

    Game game;

    /**
     * Creates a game
     */
    @BeforeEach
    public void setup(){
        game = new Game("Smash Bros");
    }

    /**
     * Tests the functionality of game.getName()
     */
    @Test
    public void test_getName(){
        assertEquals("Smash Bros", game.getName());
    }

    /**
     * Resets the game;
     */
    @AfterEach
    public void reset(){
        game = null;
    }
}

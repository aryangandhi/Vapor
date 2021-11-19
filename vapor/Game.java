package vapor;

import java.io.Serializable;

/**
 * A representation of a {@code Game} belonging to a {@code Buyer} through
 * purchase or receipt as a gift.
 */
  public class Game implements Serializable {
  private final String name; // Title of this {@code Game}.

  /**
   * Construct a new {@code Game} with the information provided.
   * 
   * @param gameID the name of the {@code Game}.
   */
  public Game(final String gameID) {
    this.name = gameID;
  }

  /**
   * Query the name of this {@code Game}.
   * 
   * @return The name of this {@code Game}.
   */
  public String getName() {
    return name;
  }
}

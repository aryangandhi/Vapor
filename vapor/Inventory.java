package vapor;

import vapor.exceptions.MultipleCopyException;

/**
 * Container to hold user {@code Game}s and manage access and manipulation.
 */
public class Inventory extends Catalogue<Game> {
  /**
   * Add a {@code Game} to the {@code Inventory}.
   * 
   * @param game the {@code Game} to be added.
   * @return the {@code Game} for further processing.
   * @throws MultipleCopyException a {@code Game} with this name exists in the
   *                               {@code Inventory}.
   */
  @Override
  public Game addEntry(final Game game) throws MultipleCopyException {
    return addEntryWithKey(game.getName(), game);
  }

  /**
   * Return an array of the {@code Game}s in the {@code Inventory}.
   *
   * @return entries' values as a {@code Game[]}.
   */
  @Override
  public Game[] getEntries() {
    return this.getEntryMap().values().toArray(new Game[0]);
  }
}

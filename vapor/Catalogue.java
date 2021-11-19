package vapor;

import java.io.Serializable;
import java.util.HashMap;

import vapor.exceptions.GameDNEException;
import vapor.exceptions.MultipleCopyException;

/**
 * Abstraction of {@code StoreFront} and {@code Inventory} into a base class
 * using generics.
 */
public abstract class Catalogue<T> implements Serializable, EndOfDay {
  // Holds the collection of {@code T}s for fast access
  private final HashMap<String, T> entries;
  private final HashMap<String, T> newEntries;

  /**
   * No arg constructor to initialise entries collection.
   */
  public Catalogue() {
    entries = new HashMap<>();
    newEntries = new HashMap<>();
  }

  /**
   * Query whether a {@code T} already exists in this {@code Catalogue}.
   * 
   * @param ID the key for the {@code T}.
   * @return true if this {@code Catalogue} contains the {@code T}, false
   *         otherwise.
   */
  public boolean containsEntry(final String ID) {
    if (entries.get(ID) != null)
      return true;
    if (newEntries.get(ID) != null)
      return true;

    return false;
  }

  /**
   * Add a {@code T} to the {@code Catalogue} with the key specified.
   * 
   * @param ID    the key to assign the {@code T} to.
   * @param entry the {@code T} to be added.
   * @return the {@code T} for further processing.
   * @throws MultipleCopyException a {@code T} with this name exists in the
   *                               {@code Catalogue}.
   */
  protected T addEntryWithKey(final String ID, final T entry) throws MultipleCopyException {
    if (containsEntry(ID))
      throw new MultipleCopyException(ID);

    newEntries.put(ID, entry);

    return entry;
  }

  /**
   * Add a {@code T} to the {@code Catalogue}.
   * 
   * @param entry the {@code T} to be added.
   * @return the {@code T} for further processing.
   * @throws MultipleCopyException a {@code T} with this name exists in the
   *                               {@code Catalogue}.
   */
  public abstract T addEntry(final T entry) throws MultipleCopyException;

  /**
   * Remove a {@code T} from this {@code Catalogue}.
   * 
   * @param ID the key corresponding to the {@code T} to be deleted.
   * @return the entry deleted from entries.
   * @throws GameDNEException no {@code T} with this ID exists in this
   *                          {@code Catalogue}.
   */
  public T remove(final String ID) throws GameDNEException {
    final T entry = entries.get(ID);

    if (entry == null)
      throw new GameDNEException(ID);

    entries.remove(ID);

    return entry;
  }

  /**
   * Query this {@code Catalogue}s entries.
   * 
   * @return this {@code Catalogue}s entries.
   */
  protected HashMap<String, T> getEntryMap() {
    return entries;
  }

  /**
   * Access a {@code T} in the {@code Catalogue}.
   * 
   * @param ID the name of the {@code T}.
   * @return the {@code T} corresponding to the name provided.
   * @throws GameDNEException no {@code T} with this name exists in the
   *                          {@code Catalogue}.
   */
  public T getEntry(final String ID) throws GameDNEException {
    final T entry = entries.get(ID);

    if (entry == null)
      throw new GameDNEException(ID);

    return entry;
  }

  /**
   * Return an array of the {@code T}s in the {@code Catalogue}.
   *
   * @return entries' values as a {@code T[]}.
   */
  public abstract T[] getEntries();

  /**
   * Make buffered listings available for purchase and clear buffer.
   */
  public void endDay() {
    entries.putAll(newEntries);

    newEntries.clear();
  }
}

package vapor;

import vapor.exceptions.MultipleCopyException;

/**
 * Container to hold {@code Seller} {@code Listing}s and manage access and
 * manipulation.
 */
public class StoreFront extends Catalogue<Listing> {
  /**
   * Add a {@code Listing} to the {@code StoreFront}.
   * 
   * @param listing the {@code Listing} to be added.
   * @return the {@code Listing} for further processing.
   * @throws MultipleCopyException a {@code Listing} with this name exists in the
   *                               {@code StoreFront}.
   */
  @Override
  public Listing addEntry(final Listing listing) throws MultipleCopyException {
    return addEntryWithKey(listing.getGame().getName(), listing);
  }

  /**
   * Return an array of the {@code Listing}s in the {@code StoreFront}.
   *
   * @return entries' values as a {@code Listing[]}.
   */
  @Override
  public Listing[] getEntries() {
    return this.getEntryMap().values().toArray(new Listing[0]);
  }
}

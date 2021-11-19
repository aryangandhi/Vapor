package vapor;

/**
 * A representation of a {@code Listing} made by a {@code Seller}-type
 * {@code User} for the sake of selling a {@code Game}.
 */
public class Listing implements java.io.Serializable {
  private final Game game; // {@code Game} being listed.
  private final int price; // Base cost of the {@code Game}.
  private final float discount; // Percentage discount applied in an auction sale.

  /**
   * Create a new {@code Listing} with the information provided.
   * 
   * @param game     the {@code Game} being listed.
   * @param price    the base cost of the {@code Game}.
   * @param discount the percentage discount applied in an auction sale.
   */
  public Listing(final Game game, final int price, final float discount) {
    this.game = game;
    this.price = price;
    this.discount = discount;
  }

  /**
   * Query the {@code Game} being listed.
   * 
   * @return the {@code Game} being listed.
   */
  public Game getGame() {
    return game;
  }

  /**
   * Query the base cost of the {@code Game}.
   * 
   * @return the base cost of the {@code Game}.
   */
  public int getPrice() {
    return price;
  }

  /**
   * Query the percentage discount applied in an auction sale.
   * 
   * @return the percentage discount applied in an auction sale.
   */
  public float getDiscount() {
    return discount;
  }
}

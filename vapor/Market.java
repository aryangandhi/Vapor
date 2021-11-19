package vapor;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import vapor.exceptions.MultipleLoginException;
import vapor.exceptions.NoLoginException;
import vapor.exceptions.NotBuyerException;
import vapor.exceptions.NotSellerException;
import vapor.exceptions.UnauthorisedUserException;
import vapor.exceptions.UserDNEException;
import vapor.exceptions.UserExistsException;
import vapor.exceptions.SelfDeletionException;
import vapor.statistics.StatsLogger;
import vapor.users.Buyer;
import vapor.users.Seller;
import vapor.users.User;

/**
 * This class represents the overall state of the store/market. This is where
 * all users and games are stored, and are updated after each day. This class
 * also includes any market-wide actions.
 */

public class Market implements Serializable, EndOfDay {

  private final HashMap<String, User> users;
  private User activeUser = null;
  private final StatsLogger stats;
  public boolean saleActivated;

  private HashSet<EndOfDay> endOfDayBuffer;

  /**
   * Initializes an empty market.
   */
  public Market() {
    this.users = new HashMap<String, User>();
    this.saleActivated = false;
    this.stats = new StatsLogger();
    this.endOfDayBuffer = new HashSet<>();
  }

  /**
   * @return The list of users currently in the market.
   */
  public HashMap<String, User> getUsers() {
    return this.users;
  }

  /**
   * Add a new {@code User} to the {@code Market} regardless of if logged in
   * {@code User} is an {@code ADMIN}.
   * 
   * @param user the {@code User} to be added.
   * @return returns the {@code user} being created.
   * @throws UserExistsException The {@code user} already exists in the market.
   */
  public User forceAddUser(final User user) throws UserExistsException {
    final String username = user.getUsername();

    if (existsUser(username))
      throw new UserExistsException(username);

    this.users.put(username, user);

    return user;
  }

  /**
   * Check whether a {@code User} with this username exists on the {@code Market}.
   * 
   * @param username the username for which to search for associations.
   * @return true if the {@code User} exists on the {@code Market}, false
   *         otherwise.
   */
  public boolean existsUser(final String username) {
    return users.get(username) != null;
  }

  /**
   * Adds a newly created {@code User} to the collection of {@code User}s.
   * 
   * @param user the newly created {@code User} to add.
   * @return the {@code User} for further processing.
   * @throws UserExistsException       a {@code User} with that username already
   *                                   exists.
   * @throws UnauthorisedUserException the currently logged in {@code User} is not
   *                                   privileged.
   */
  public User addUser(final User user) throws UserExistsException, UnauthorisedUserException {
    if (isAuthorisedState())
      return forceAddUser(user);

    throw new UnauthorisedUserException();
  }

  /**
   * Queries whether the logged in {@code User} is privileged.
   * 
   * @return true if logged in {@code User} is an {@code ADMIN}, false otherwise.
   */
  public boolean isAuthorisedState() {
    final User authorisingUser = getActiveUser();

    return authorisingUser != null && authorisingUser.getUserType().isPrivileged();
  }

  /**
   * Removes a {@code User} from the collection of all {@code User}s.
   * 
   * @param user The {@code User} to be removed.
   * @throws UnauthorisedUserException the currently logged in {@code User} is not
   *                                   privileged.
   * @throws SelfDeletionException     attempting to delete the {@code User}
   *                                   authorising the deletion himself.
   */
  public void removeUser(final User user) throws UnauthorisedUserException, SelfDeletionException {
    if (!isAuthorisedState())
      throw new UnauthorisedUserException();

    if (getActiveUser() == user)
      throw new SelfDeletionException(user.getUsername());

    users.remove(user.getUsername());
  }

  /**
   * Return the {@code User} with the given username in the market, or throw an
   * exception otherwise.
   * 
   * @param username The username of the desired {@code User}.
   * @return The {@code User} with the given username.
   * 
   * @throws UserDNEException Signifies no {@code User} with given username.
   */
  public User getUser(final String username) throws UserDNEException {
    final User user = users.get(username);

    if (user != null)
      return user;

    throw new UserDNEException(username);
  }

  /**
   * Get a {@code Buyer} by username.
   * 
   * @param username the name of the {@code Buyer} desired.
   * @return the {@code Buyer} desired.
   * @throws UserDNEException  no {@code User} with username.
   * @throws NotBuyerException the {@code User} with the username is not a
   *                           {@code Buyer}.
   */
  public Buyer getBuyer(final String username) throws UserDNEException, NotBuyerException {
    final User user = getUser(username);

    if (user.getUserType().isBuyer())
      return (Buyer) user;

    throw new NotBuyerException(username);
  }

  /**
   * Get a {@code Seller} by username.
   * 
   * @param username the name of the {@code Seller} desired
   * @return the {@code Seller} desired.
   * @throws UserDNEException   no {@code User} with username.
   * @throws NotSellerException the {@code User} with the username is not a
   *                            {@code Seller}.
   */
  public Seller getSeller(final String username) throws UserDNEException, NotSellerException {
    final User user = getUser(username);

    if (user.getUserType().isSeller())
      return (Seller) user;

    throw new NotSellerException(username);
  }

  /**
   * @return The currently logged in user, null if none.
   */
  public User getActiveUser() {
    return activeUser;
  }

  /**
   * Access the target user of a transaction based on username and he / she who is
   * currently logged in if any.
   * 
   * @param username the name of the target specified.
   * @return the active user, or the user specified by username if the active user
   *         is an admin and the username is valid.
   * @throws NoLoginException no user is logged in, ergo the premise is false and
   *                          there is neither active user nor target.
   * @throws UserDNEException Signifies no {@code User} with {@code username}.
   */
  public User getActiveUserOrTarget(final String username) throws NoLoginException, UserDNEException {
    // Get active user, either admin or other.
    final User activeUser = getActiveUser();
    if (activeUser == null)
      throw new NoLoginException();

    // Active user is not admin, return self.
    if (!isAuthorisedState())
      return activeUser;

    // Active user is Admin
    // If ID provided is authentic, return that user
    return getUser(username);
  }

  /**
   * Set a {@code User} as the activeUser.
   * 
   * @param user the {@code User} to whom the activeUser should be set.
   * @throws MultipleLoginException there is already an activeUser logged in.
   */
  public void loginUser(final User user) throws MultipleLoginException {
    if (getActiveUser() != null)
      throw new MultipleLoginException(user.getUsername());
    this.activeUser = user;
  }

  /**
   * Unset a {@code User} as the activeUser and replace with null.
   * 
   * @return the logged out user.
   * @throws NoLoginException there is no logged in user to log out.
   */
  public User logoutUser() throws NoLoginException {
    final User user = getActiveUser();

    if (user == null)
      throw new NoLoginException();

    this.activeUser = null;

    return user;
  }

  /**
   * Toggles the state of the auction sale.
   * 
   * @throws UnauthorisedUserException the currently logged in {@code User} is not
   *                                   privileged.
   */
  public void toggleAuctionSale() throws UnauthorisedUserException {
    if (!isAuthorisedState())
      throw new UnauthorisedUserException();

    this.saleActivated = !this.saleActivated;
  }

  /**
   * Query whether there is an ongoing auctionSale.
   * 
   * @return whether there is an ongoing auctionSale.
   */
  public boolean getAuctionSale() {
    return saleActivated;
  }

  /**
   * Export a JSON representation of the {@code market}'s {@code users}.
   */
  public void export() {
    MarketToJSON.toJSON(this);
  }

  /**
   * @return - this {@code Market}'s {@code stats}.
   */
  public StatsLogger getStats() {
    return this.stats;
  }

  /**
   * Export a text representation this {@code Market}'s statistics to be read by
   * the webpage.
   */
  public void report() {
    this.stats.report();
  }

  /**
   * Deserialize the given {@code Market} if it exists. Otherwise, create a new
   * one.
   * 
   * @param loadFileName the file containing the market to load.
   * @return the new market.
   */
  public static Market getMarket(final String loadFileName) {
    final File marketFile = new File(loadFileName);

    if (marketFile.exists())
      return SerializeMarket.load(loadFileName);
    else
      return new Market();
  }

  /**
   * Serialize the given {@code Market} to a filename given.
   * 
   * @param saveFileName the name of the file to which the market should be saved.
   */
  public void save(final String saveFileName) {
    SerializeMarket.save(this, saveFileName);
  }

  /**
   * Track a newly pending EndOfDay for later processing.
   * 
   * @param endOfDay the EndOfDay to be processed.
   */
  public void addPendingEndOfDay(final EndOfDay endOfDay) {
    if (!endOfDayBuffer.contains(endOfDay))
      endOfDayBuffer.add(endOfDay);
  }

  /**
   * Process all {@code EndOfDay}s' days' ends and reset the buffer.
   */
  public void endDay() {
    for (final EndOfDay endOfDay : endOfDayBuffer)
      endOfDay.endDay();

    endOfDayBuffer.clear();
  }
}

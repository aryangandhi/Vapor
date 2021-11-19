package vapor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vapor.exceptions.ErrorLogger;
import vapor.exceptions.InvalidDBBuilderRecord;
import vapor.exceptions.MultipleCopyException;
import vapor.exceptions.UserExistsException;
import vapor.exceptions.ErrorLogger.ErrorType;
import vapor.users.Buyer;
import vapor.users.Seller;
import vapor.users.User;
import vapor.users.UserFactory;
import vapor.users.User.UserType;

/**
 * Reinitialise the database (serialised {@code Market} file) based on a
 * user-provided state.
 */
public class DatabaseBuilder {
  /**
   * Enumerated set of possible read states and their patterns.
   */
  private enum State {
    USER("^(AA|FS|SS|BS)(\\S.{14})(\\d{8})$"), GAME("^\\S.{24}$"), LISTING("^(\\S.{24})(\\d{2}\\.\\d{2})(\\d{5})");

    // Regex corresponding to records for this state.
    private final Pattern pattern;

    /**
     * Create a new state that accepts formatted records.
     * 
     * @param regex the format to accept for records.
     */
    private State(final String regex) {
      pattern = Pattern.compile(regex);
    }

    /**
     * Retrieve the compiled regex for this state.
     * 
     * @return the compiled regex for this state.
     */
    public Pattern getPattern() {
      return pattern;
    }
  }

  // A buffer market to hold the pending changes to system state.
  private static Market newStateMarket;
  // A buffer user to hold the pending new user to be added.
  private static User newUserBuffer;

  /**
   * Create a new database with the users described in the sourceFilename database
   * builder construction document.
   * 
   * @param sourceFilename      the filename containing the
   * @param destinationFilename the filename where will save
   *                            {@code newStateMarket}.
   */
  public static void buildDatabase(final String sourceFilename, final String destinationFilename) {
    final BufferedReader DBReader = openDBSource(sourceFilename);
    if (DBReader == null)
      return;

    newStateMarket = new Market();

    try {
      loadRecords(DBReader);
    } catch (final InvalidDBBuilderRecord e) {
      ErrorLogger.log(ErrorType.ERROR, "DATABASE BUILDER", e.getError());

      return;
    }

    newStateMarket.save(destinationFilename);

    try {
      DBReader.close();
    } catch (final IOException e) {
      ErrorLogger.log(ErrorType.ERROR, "DATABASE BUILDER [FILE: " + sourceFilename + "]", e.getMessage());
    }
  }

  /**
   * Open the new DB source file to parse.
   * 
   * @param filename the file to be opened.
   * @return A bufferedreader to the file for parsing.
   */
  private static BufferedReader openDBSource(final String filename) {
    final File DBSource = new File(filename);

    try {
      return new BufferedReader(new FileReader(DBSource));
    } catch (final FileNotFoundException e) {
      ErrorLogger.log(ErrorType.ERROR, "DATABASE BUILDER [FILE: " + filename + "]", e.getMessage());

      return null;
    }
  }

  /**
   * load all new users from the new DB source.
   * 
   * @param DBReader the source to iterate through for user records.
   * @throws InvalidDBBuilderRecord failed to satisfactorily parse a record.
   */
  private static void loadRecords(final BufferedReader DBReader) throws InvalidDBBuilderRecord {
    State state = State.USER;

    int userNumber = 0;
    String line = "";

    try {
      while ((line = DBReader.readLine()) != null) {
        state = initNewUser(line);

        if (state == State.GAME)
          state = addGames(DBReader);
        if (state == State.LISTING)
          state = addListings(DBReader);

        commitNewUser();
        userNumber++;
      }
    } catch (final IOException e) {
      ErrorLogger.log(ErrorType.ERROR, "DATABASE BUILDER [USER: " + userNumber + "]", e.getMessage());
    }
  }

  /**
   * Ensure a given record matches the required format.
   * 
   * @param pattern the format to be matched.
   * @param record  the record to be tested.
   * @return the matcher containing the matches.
   * @throws InvalidDBBuilderRecord the record did not match the format.
   */
  private static Matcher forceMatchRecordFormat(final Pattern pattern, final String record)
      throws InvalidDBBuilderRecord {
    final Matcher matcher = pattern.matcher(record);

    if (!matcher.matches())
      throw new InvalidDBBuilderRecord(record);

    return matcher;
  }

  /**
   * Create a new user with the given information.
   * 
   * @param record the record containing the user information.
   * @return the next State to parse.
   * @throws InvalidDBBuilderRecord the record did not match the correct format.
   */
  private static State initNewUser(final String record) throws InvalidDBBuilderRecord {
    final Matcher userMatcher = forceMatchRecordFormat(State.USER.getPattern(), record);

    final User.UserType userType = User.UserType.getUserType(userMatcher.group(1));
    final String username = userMatcher.group(2).trim();
    final int credit = Integer.parseInt(userMatcher.group(3));

    newUserBuffer = UserFactory.createTypedUser(username, credit, userType);

    if (newUserBuffer.getUserType() == UserType.SELLER)
      return State.LISTING;
    else
      return State.GAME;
  }

  /**
   * Insert the newly created user in the market.
   * 
   * @throws InvalidDBBuilderRecord the market already had a copy of this user,
   *                                ergo, the decision to add the user was wrong.
   */
  private static void commitNewUser() throws InvalidDBBuilderRecord {
    try {
      newStateMarket.forceAddUser(newUserBuffer);
    } catch (final UserExistsException e) {
      ErrorLogger.log(ErrorType.WARNING, "DATABASE BUILDER", e.getError());
      throw new InvalidDBBuilderRecord("ADD NEW USER");
    }
  }

  /**
   * Populate a user's inventory with the listed games.
   * 
   * @param gameReader the reader in a Game reading state.
   * @return the next State to parse.
   * @throws InvalidDBBuilderRecord the records did not match the correct format.
   */
  private static State addGames(final BufferedReader gameReader) throws IOException, InvalidDBBuilderRecord {
    final int numGames = getCount(gameReader.readLine());

    final Inventory inventory = ((Buyer) newUserBuffer).getInventory();

    String gameRecord;
    for (int i = 0; i < numGames; i++) {
      gameRecord = gameReader.readLine();

      final Matcher gameMatcher = forceMatchRecordFormat(State.GAME.getPattern(), gameRecord);

      final String gameID = gameMatcher.group(0).trim();

      try {
        inventory.addEntry(new Game(gameID));
      } catch (final MultipleCopyException e) {
        ErrorLogger.log(ErrorType.WARNING, "DATABASE BUILDER", e.getError());
        throw new InvalidDBBuilderRecord(gameRecord);
      }
    }

    inventory.endDay();

    if (newUserBuffer.getUserType() == UserType.BUYER)
      return State.USER;
    return State.LISTING;
  }

  /**
   * Populate a user's storefront with the listed games.
   * 
   * @param listingReader the reader in a Listing reading state.
   * @return the next State to parse.
   * @throws InvalidDBBuilderRecord the records did not match the correct format.
   */
  private static State addListings(final BufferedReader listingReader) throws IOException, InvalidDBBuilderRecord {
    final int numGames = getCount(listingReader.readLine());

    final StoreFront storeFront = ((Seller) newUserBuffer).getStoreFront();

    String listingRecord;
    for (int i = 0; i < numGames; i++) {
      listingRecord = listingReader.readLine();

      final Matcher listingMatcher = forceMatchRecordFormat(State.LISTING.getPattern(), listingRecord);

      final String gameID = listingMatcher.group(1).trim();
      final Game game = new Game(gameID);
      final float discount = Float.parseFloat(listingMatcher.group(2));
      final int price = Integer.parseInt(listingMatcher.group(3));

      try {
        storeFront.addEntry(new Listing(game, price, discount));
      } catch (final MultipleCopyException e) {
        ErrorLogger.log(ErrorType.WARNING, "DATABASE BUILDER", e.getError());
        throw new InvalidDBBuilderRecord(listingRecord);
      }
    }

    storeFront.endDay();

    return State.USER;
  }

  /**
   * Get the number of elements for the following read state.
   * 
   * @param record the record containing the number of elements to expect.
   * @return the number of elements in the following read state.
   * @throws InvalidDBBuilderRecord the record did not describe a number of
   *                                elements.
   */
  private static int getCount(final String record) throws InvalidDBBuilderRecord {
    final Pattern countPattern = Pattern.compile("^\\d+$");

    final Matcher countMatcher = forceMatchRecordFormat(countPattern, record);

    return Integer.parseInt(countMatcher.group(0));
  }
}

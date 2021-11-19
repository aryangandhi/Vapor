package vapor.transactions;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import vapor.exceptions.InvalidTransactionCodeException;
import vapor.transactions.Transaction.TransactionType;
import vapor.users.User;

/**
 * A class to process the fields of a daily transaction file record.
 */
public class TransactionBuilder {
  /**
   * Length and format information for transaction record elements.
   */
  private enum TransactionField {
    CODE_TYPE(1, "^(\\d{2})"), // XX
    USER_TYPE(1, "(AA|BS|SS|FS)"), // TT
    USER_ID(1, "(\\S.{14})"), // UUUUUUUUUUUUUUU or SSSSSSSSSSSSSSS
    GAME_ID(1, "(\\S.{24})"), // IIIIIIIIIIIIIIIIIIIIIIIII
    DISCOUNT(1, "(\\d{2}\\.\\d{2})"), // DDDDD
    CREDIT(3, "((\\d{6})\\.(\\d{2}))"), // CCCCCCCCC
    PRICE(3, "((\\d{3})\\.(\\d{2}))"), // PPPPPP
    OPTIONAL_USER_ID(1, "(.{15})"); // UUUUUUUUUUUUUUU or SSSSSSSSSSSSSSS or "_______________"

    private final int numGroups; // number of groups in this field.
    private final String format; // field expected format.

    /**
     * Constructor for a new TransactionField with the information provided.
     * 
     * @param numGroups the field length.
     * @param format    the field expected format.
     */
    private TransactionField(final int numGroups, final String format) {
      this.numGroups = numGroups;
      this.format = format;
    }

    /**
     * Query the number of groups for this field.
     * 
     * @return the number of groups for this field.
     */
    public int getNumGroups() {
      return numGroups;
    }

    /**
     * Query this TransactionField's format.
     * 
     * @return this TransactionField's format.
     */
    public String getFormat() {
      return format;
    }
  }

  /**
   * Aggregate TransactionField container with parsing information.
   */
  protected enum TransactionFieldSequence {
    // XX UUUUUUUUUUUUUUU TT CCCCCCCCC
    XUTC(Arrays.asList(TransactionField.CODE_TYPE, TransactionField.USER_ID, TransactionField.USER_TYPE,
        TransactionField.CREDIT)),
    // XX UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS CCCCCCCCC
    XUSC(Arrays.asList(TransactionField.CODE_TYPE, TransactionField.USER_ID, TransactionField.USER_ID,
        TransactionField.CREDIT)),
    // XX IIIIIIIIIIIIIIIIIII SSSSSSSSSSSSS DDDDD PPPPPP
    XISDP(Arrays.asList(TransactionField.CODE_TYPE, TransactionField.GAME_ID, TransactionField.USER_ID,
        TransactionField.DISCOUNT, TransactionField.PRICE)),
    // XX IIIIIIIIIIIIIIIIIII SSSSSSSSSSSSSSS UUUUUUUUUUUUUU
    XISU(Arrays.asList(TransactionField.CODE_TYPE, TransactionField.GAME_ID, TransactionField.USER_ID,
        TransactionField.USER_ID)),
    // XX IIIIIIIIIIIIIIIIIII UUUUUUUUUUUUUU SSSSSSSSSSSSS
    /**
     * Note: Used to differentiate RemoveGameTransaction from GiftGame and BuyGame
     */
    XIUS(Arrays.asList(TransactionField.CODE_TYPE, TransactionField.GAME_ID, TransactionField.USER_ID,
        TransactionField.OPTIONAL_USER_ID));

    private final Pattern fieldParser; // Regex Pattern to retrieve fields.

    /**
     * Create a new TransactionFieldSequence given an ordered list of fields.
     * 
     * @param fields a List containing the fields in sequence.
     */
    private TransactionFieldSequence(final List<TransactionField> fields) {
      final String fieldParserFormat = fields.stream().map(TransactionField::getFormat)
          .collect(Collectors.joining(DELIMITER)) + "$";

      fieldParser = Pattern.compile(fieldParserFormat);
    }

    /**
     * Query this TransactionFieldSequence's fieldParser.
     * 
     * @return this TransactionFieldSequence's fieldParser.
     */
    private Pattern getFieldParser() {
      return fieldParser;
    }

    /**
     * Create a new {@code TransactionFieldSequence} to match the given
     * {@code Transaction} record.
     * 
     * @param rawTransaction the record to which the
     *                       {@code TransactionFieldSequence} corresponds.
     * @return the correct {@code TransactionFieldSequence} to parse the record.
     * @throws InvalidTransactionCodeException this record does not describe a
     *                                         transaction.
     */
    private static TransactionFieldSequence getSequence(final String rawTransaction)
        throws InvalidTransactionCodeException {
      // Match code at beginning of rawTransaction
      final String format = TransactionField.CODE_TYPE.getFormat();
      final Pattern codePattern = Pattern.compile(format);

      final Matcher m = codePattern.matcher(rawTransaction);
      if (!m.find())
        throw new InvalidTransactionCodeException();

      final int code = Integer.parseInt(m.group(0));

      final TransactionType type = TransactionType.getType(code);
      if (type == null)
        throw new InvalidTransactionCodeException();

      return type.getFormat();
    }
  }

  // The character which delimits all fields.
  private static final String DELIMITER = " ";
  // The number of decimal places that appear after each numeric value.
  private static final int DECIMALS = 2;

  // A transaction code specifying a type of {@code Transaction}
  protected Transaction.TransactionType code;

  // The {@code UserType} to which a {@code Transaction} pertains.
  protected User.UserType userType;

  // A {@code User} ID as it appears in:
  protected String userID1; // - 1st user argument in a transaction code.
  protected String userID2; // - 2nd user argument in a transaction code.

  // A {@code Game} ID as it appears on our {@code Market}.
  protected String gameID;

  // Values corresponding to:
  protected int credit; // - an amount of credit for a {@code Transaction}.
  protected int price; // - a price for a {@code Listing}.
  protected float discount; // - a discount to be applied to a {@code Listing}.

  // A {@code TransactionFieldSequence} for parsing this record.
  private final TransactionFieldSequence sequence;
  // The raw code from the daily transaction file
  protected final String rawTransaction;
  // The matched fields.
  private final Matcher parsedFields;

  private int groupID;

  /**
   * Retrieve a {@code Transaction.TransactionType} code from its String
   * representation in the raw transaction and return its relevant enumerated
   * {@code Transaction.TransactionType}
   * 
   * @return and enum element containing the {@code Transaction.TransactionType}
   *         of the {@code Transaction}
   * @throws InvalidTransactionCodeException the record provided does not describe
   *                                          a valid {@code Transaction}.
   */
  private Transaction.TransactionType getCode() throws InvalidTransactionCodeException {
    final String rawCode = getField(groupID);

    final int numericCode = Integer.parseInt(rawCode);

    code = Transaction.TransactionType.getType(numericCode);

    if (code == null)
      throw new InvalidTransactionCodeException();

    groupID += TransactionField.CODE_TYPE.getNumGroups();

    return code;
  }

  /**
   * Read a String representation of a numeric field as an integer count of least
   * significant digits.
   * 
   * @param field    the numeric field to be read.
   * @param rawValue the String containing the value to be converted.
   * @return the integer count of the least significant digit.
   */
  private int numericAsInt(final TransactionField field, final String rawValue) {
    final Pattern numberPattern = Pattern.compile(field.format);

    final Matcher numberMatcher = numberPattern.matcher(rawValue);

    numberMatcher.matches();

    final int wholeNumbers = Integer.parseInt(numberMatcher.group(2));
    final int decimalPoints = Integer.parseInt(numberMatcher.group(3));

    final int offset = (int) Math.pow(10, DECIMALS);

    return wholeNumbers * offset + decimalPoints;
  }

  /**
   * Read a String representation of a numeric field as a float.
   * 
   * @param rawValue the String containing the value to be converted.
   * @return the float containing this value.
   */
  private float numericAsFloat(final String rawValue) {
    return Float.parseFloat(rawValue);
  }

  /**
   * Retrieve a user's account type from the raw transaction and update
   * {@code userType} with the enumerated {@code UserType} element accordingly
   * 
   * @return the new state of the {@code TransactionParser}
   */
  private User.UserType getUserType(final String rawUserType) {
    return User.UserType.getUserType(rawUserType);
  }

  /**
   * Constructor for a new TransactionParser for the record provided.
   * 
   * @param rawTransaction the record to be operated on.
   * @throws InvalidTransactionCodeException the record provided does not describe
   *                                         a valid {@code Transaction}.
   */
  public TransactionBuilder(final String rawTransaction) throws InvalidTransactionCodeException {
    this.groupID = 0;

    this.rawTransaction = rawTransaction;

    this.sequence = TransactionFieldSequence.getSequence(rawTransaction);

    this.parsedFields = sequence.getFieldParser().matcher(rawTransaction);
    if (!parsedFields.matches())
      throw new InvalidTransactionCodeException();
  }

  /**
   * Access the i-th field of the rawTransaction.
   * 
   * @param index the field to be accessed.
   * @return String representation of the cleaned field.
   */
  public String getField(final int index) {
    return parsedFields.group(index).trim();
  }

  /**
   * Grab the parsed userID1 from parsedFields and update userID1.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parseUserID1() {
    userID1 = getField(groupID);

    groupID += TransactionField.USER_ID.getNumGroups();

    return this;
  }

  /**
   * Grab the parsed userID2 from parsedFields and update userID2.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parseUserID2() {
    userID2 = getField(groupID);

    groupID += TransactionField.USER_ID.getNumGroups();

    return this;
  }

  /**
   * Grab the parsed user type from parsedFields and update user type.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parseUserType() {
    userType = getUserType(getField(groupID));

    groupID += TransactionField.USER_TYPE.getNumGroups();

    return this;
  }

  /**
   * Grab the parsed credit from parsedFields and update credit.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parseCredit() {
    credit = numericAsInt(TransactionField.CREDIT, getField(groupID));

    groupID += TransactionField.CREDIT.getNumGroups();

    return this;
  }

  /**
   * Grab the parsed gameID from parsedFields and update gameID.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parseGameID() {
    gameID = getField(groupID);

    groupID += TransactionField.GAME_ID.getNumGroups();

    return this;
  }

  /**
   * Grab the parsed discount from parsedFields and update discount.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parseDiscount() {
    discount = numericAsFloat(getField(groupID));

    groupID += TransactionField.DISCOUNT.getNumGroups();

    return this;
  }

  /**
   * Grab the parsed price from parsedFields and update price.
   * 
   * @return this TransactionBuilder for chaining.
   */
  public TransactionBuilder parsePrice() {
    price = numericAsInt(TransactionField.PRICE, getField(groupID));

    groupID += TransactionField.PRICE.getNumGroups();

    return this;
  }

  /**
   * Use the parsed fields to generate a {@code Transaction}.
   * 
   * @return the {@code Transaction} specified by the record.
   */
  private Transaction process() {
    switch (code) {
    case LOGIN:
      return new LoginTransaction(this);
    case CREATE:
      return new CreateTransaction(this);
    case DELETE:
      return new DeleteTransaction(this);
    case SELL:
      return new SellTransaction(this);
    case BUY:
      return new BuyTransaction(this);
    case REFUND:
      return new RefundTransaction(this);
    case ADD_CREDIT:
      return new AddCreditTransaction(this);
    case AUCTION_SALE:
      return new AuctionSaleTransaction(this);
    case REMOVE:
      return new RemoveGameTransaction(this);
    case GIFT:
      return new GiftGameTransaction(this);
    case LOGOUT:
      return new LogoutTransaction(this);

    default:
      return null;
    }
  }

  /**
   * Process rawTransaction and generate a {@code Transaction} as specified.
   * 
   * @return the {@code Transaction} generated by the parsed components.
   * @throws InvalidTransactionCodeException the record provided does not describe
   *                                         a valid {@code Transaction}.
   */
  public Transaction parse() throws InvalidTransactionCodeException {
    groupID = 1;

    code = getCode();

    switch (sequence) {
    case XUTC:
      // XX UUUUUUUUUUUUUUU TT CCCCCCCCC
      return parseUserID1().parseUserType().parseCredit().process();

    case XUSC:
      // XX UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS CCCCCCCCC
      return parseUserID1().parseUserID2().parseCredit().process();

    case XISDP:
      // XX IIIIIIIIIIIIIIIIIIIIIIIII SSSSSSSSSSSSSSS DDDDD PPPPPP
      return parseGameID().parseUserID1().parseDiscount().parsePrice().process();

    case XISU:
      // XX IIIIIIIIIIIIIIIIIIIIIIIII SSSSSSSSSSSSSSS UUUUUUUUUUUUUUU
    case XIUS:
      // XX IIIIIIIIIIIIIIIIIIIIIIIII UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS
      return parseGameID().parseUserID1().parseUserID2().process();

    default:
      return null;
    }
  }
}

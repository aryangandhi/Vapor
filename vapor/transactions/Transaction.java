package vapor.transactions;

import java.util.HashMap;

import vapor.Market;
import vapor.exceptions.ErrorLogger;
import vapor.transactions.TransactionBuilder.TransactionFieldSequence;
import vapor.users.User;
import vapor.users.User.UserType;

/**
 * Parent class for {@code Transaction}s to facilitate the Command pattern;
 * allowing {@code Transaction}s to be performed by simply calling execute with
 * the {@code Market} to be modified.
 */
public abstract class Transaction {
  /**
   * A set of enumerated elements corresponding to different forms of transaction
   * that can be performed, as well as their codes per specification.
   */
  public enum TransactionType {
    // enumerated transaction types
    LOGIN(0, TransactionFieldSequence.XUTC), // {@code LoginTransaction}
    CREATE(1, TransactionFieldSequence.XUTC), // {@code CreateTransaction}
    DELETE(2, TransactionFieldSequence.XUTC), // {@code DeleteTransaction}
    SELL(3, TransactionFieldSequence.XISDP), // {@code SellTransaction}
    BUY(4, TransactionFieldSequence.XISU), // {@code BuyTransaction}
    REFUND(5, TransactionFieldSequence.XUSC), // {@code RefundTransaction}
    ADD_CREDIT(6, TransactionFieldSequence.XUTC), // {@code AddCreditTransaction}
    AUCTION_SALE(7, TransactionFieldSequence.XUTC), // {@code AuctionSaleTransaction}
    REMOVE(8, TransactionFieldSequence.XIUS), // {@code RemoveTransaction}
    GIFT(9, TransactionFieldSequence.XISU), // {@code GiftTransaction}
    LOGOUT(10, TransactionFieldSequence.XUTC); // {@code LogoutTransaction}

    private final int code; // related transaction file numerical codes
    private final TransactionFieldSequence format; // the format of this transaction's code

    /**
     * Associate a new transaction with a corresponding numerical code
     * 
     * @param code the numerical code for this transaction
     */
    private TransactionType(final int code, final TransactionFieldSequence format) {
      this.code = code;
      this.format = format;
    }

    // Quick lookup of transaction types by code for transaction file parsing
    private static final HashMap<Integer, TransactionType> codeToType = new HashMap<>();

    // Initialise codeToTransactionType at program outset
    static {
      for (TransactionType type : TransactionType.values()) {
        codeToType.put(type.code, type);
      }
    }

    /**
     * Query transaction types by code for initialisation.
     * 
     * @param code the code corresponding to the transaction type.
     * @return the transaction type corresponding to the code.
     */
    public static TransactionType getType(final int code) {
      return codeToType.get(code);
    }

    /**
     * Query the format of this transaction type's code.
     * 
     * @return the TransactionFieldSequence describing this transaction type's code.
     */
    public TransactionFieldSequence getFormat() {
      return format;
    }
  }

  // Transaction error information
  private final Transaction.TransactionType type; // A failing transaction's type
  private final String code; // The transaction code for a failing transaction

  // Information pertaining to users
  protected int credit; // - a user's available credit
  protected String userID1; // - 1st user argument in a transaction code.
  protected String userID2; // - 2nd user argument in a transaction code.
  protected User.UserType userType; // - a user's type

  // Information pertaining to games
  protected String gameID; // - the title of a game
  protected int price; // - the price of a game
  protected float discount; // - the discount percentage for a game

  /**
   * Initialise the parameters for a newly created transaction using the
   * information parsed in a {@code TransactionBuilder}.
   * 
   * @param source the {@code TransactionBuilder} from which to initialise
   *               information.
   */
  public Transaction(final TransactionBuilder source) {
    type = source.code;

    code = source.rawTransaction;

    credit = source.credit;
    userID1 = source.userID1;
    userID2 = source.userID2;
    userType = source.userType;

    gameID = source.gameID;
    price = source.price;
    discount = source.discount;
  }

  /**
   * To be overloaded to provide the specific functionality needed for each
   * {@code Transaction}.
   * 
   * @param market the {@code Market} to be updated by the {@code Transaction}.
   */
  public abstract void execute(final Market market);

  /**
   * Generate the contextInfo for an error given the {@code Transaction}'s code
   * and relevant type.
   * 
   * @return a String containing error contextInfo.
   */
  private String getContext() {
    return type.name() + ": (" + code + ")";
  }

  /**
   * Error handling behaviour to print a failed {@code Transaction}'s type, code,
   * and an error message describing the failure.
   * 
   * @param error the error message to be printed.
   */
  protected void fail(final String error) {
    ErrorLogger.log(ErrorLogger.ErrorType.ERROR, getContext(), error);
  }

  /**
   * Error handling behaviour to print a warning message with a
   * {@code Transaction}'s type, code, and non-standard behaviour observed.
   * 
   * @param error the error message to be printed.
   */
  protected void warn(final String error) {
    ErrorLogger.log(ErrorLogger.ErrorType.WARNING, getContext(), error);
  }

  /**
   * Issue a warning if a balance could not be issued to a {@code User}.
   * 
   * @param credited the amount the {@code User} should be credited.
   */
  protected void warnMaxBalance(final int credited) {
    if (credited != credit) {
      final float trueCredited = (float) credited / 100.f;
      warn("Attempted credit exceeds max balance. " + trueCredited + " issued instead.");
    }
  }

  /**
   * Issue a warning if a user field provided does not match that on the system.
   * 
   * @param field the discrepant field.
   * @param exp   the expected value (as String).
   * @param act   the actual value (as String).
   */
  protected void warnDesync(final String field, final String exp, final String act) {
    if (!exp.equals(act))
      warn("Possible data desync: User " + field + " (" + exp + ") does not match provided (" + act + ").");
  }

  /**
   * Issue a warning if the username provided does not match that on the system.
   * 
   * @param user the user containing the username on the system.
   */
  protected void warnUsernameDesync(final User user) {
    final String expName = user.getUsername();
    final String actName = userID1;
    warnDesync("name", expName, actName);
  }

  /**
   * Issue a warning if the user type provided does not match that on the system.
   * 
   * @param user the user containing the user type on the system.
   */
  protected void warnUserTypeDesync(final User user) {
    final UserType expType = user.getUserType();
    final UserType actType = userType;
    warnDesync("type", expType.name(), actType.name());
  }

  /**
   * Issue a warning if the user credit provided does not match that on the
   * system.
   * 
   * @param user the user containing the user credit on the system.
   */
  protected void warnUserCreditDesync(final User user) {
    final int expCredit = user.getCredit();
    final int actCredit = credit;
    if (expCredit != actCredit) {
      final String expCreditNoRound = "" + expCredit / 100 + "." + expCredit % 100;
      final String actCreditNoRound = "" + actCredit / 100 + "." + actCredit % 100;
      warnDesync("credit", expCreditNoRound, actCreditNoRound);
    }
  }
}

package vapor.users;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import vapor.EndOfDay;
import vapor.exceptions.InsufficientFundsException;
import vapor.exceptions.MaxDailyCreditException;

/**
 * Abstract class that has overlapping attributes and abilities of both standard
 * users and admin users. Parent class to standard and admin user subclasses.
 */
public abstract class User implements Serializable, EndOfDay {
  /**
   * A set of enumerated elements corresponding to different types of user
   * accounts, as well as their codes as per specification
   */
  public enum UserType {
    BUYER("BS"), SELLER("SS"), FULL("FS"), ADMIN("AA");

    private final String userTypeCode;

    private UserType(final String userTypeCode) {
      this.userTypeCode = userTypeCode;
    }

    private static final HashMap<String, UserType> codeToUserType = new HashMap<>();

    static {
      for (UserType userType : UserType.values()) {
        codeToUserType.put(userType.userTypeCode, userType);
      }
    }

    public static UserType getUserType(final String userTypeCode) {
      return codeToUserType.get(userTypeCode);
    }

    public String getUserTypeCode() {
      return userTypeCode;
    }

    // Set of UserTypes that are cleared for buying.
    private static final HashSet<UserType> buyerTypes = new HashSet<UserType>(Arrays.asList(BUYER, FULL, ADMIN));
    // Set of UserTypes that are cleared for selling.
    private static final HashSet<UserType> sellerTypes = new HashSet<UserType>(Arrays.asList(SELLER, FULL, ADMIN));

    /**
     * Query whether a {@code User} is a buyer.
     * 
     * @return true if the {@code UserType} is a buyerType, false otherwise.
     */
    public boolean isBuyer() {
      return buyerTypes.contains(this);
    }

    /**
     * Query whether a {@code User} is a seller.
     * 
     * @return true if the {@code UserType} is a sellerType, false otherwise.
     */
    public boolean isSeller() {
      return sellerTypes.contains(this);
    }

    /**
     * Check if a {@code UserType} is privileged.
     * 
     * @return true if {@code ADMIN}, false otherwise.
     */
    public boolean isPrivileged() {
      return this == ADMIN;
    }
  }

  // Each User's maximum allowable credit per clarification to spec.
  private static final int MAX_CREDIT = 99999999;
  private static final int MAX_DAILY = 100000;

  private int credit;
  private int dailyCreditAvailability;
  private final String username;
  private final UserType userType;

  /**
   * Create a new {@code User} with the given information.
   * 
   * @param username the name to give the new {@code User}.
   * @param credit   the new {@code User}'s starting credit.
   * @param userType the new {@code User}'s {@code UserType}.
   */
  public User(final String username, final int credit, final UserType userType) {
    this.credit = credit;
    this.username = username;
    this.userType = userType;
    dailyCreditAvailability = MAX_DAILY;
  }

  /**
   * Adds credit to the user's balance irrespective of daily allowance.
   *
   * @param credit amount to be credited to user balance.
   * @return amount added to the user's balance.
   */
  public int forceAddCredit(int credit) {
    final int space = MAX_CREDIT - this.credit;

    if (credit > space)
      credit = space;

    this.credit += credit;

    return credit;
  }

  /**
   * Adds credit to the user's balance
   *
   * @param credit amount to be credited to user balance
   * @return amount added to the user's balance.
   * @throws MaxDailyCreditException user attempted to add more credit than
   *                                 permissible in a day
   */
  public int addCredit(final int credit) throws MaxDailyCreditException {
    if (credit > dailyCreditAvailability)
      throw new MaxDailyCreditException(credit, dailyCreditAvailability);

    final int credited = forceAddCredit(credit);

    dailyCreditAvailability -= credited;

    return credited;
  }

  /**
   * Query this {@code User}'s credit.
   * 
   * @return the amount of credit.
   */
  public int getCredit() {
    return credit;
  }

  /**
   * Charge a {@code User} a given price and return the {@code User}'s new credit.
   * 
   * @param price the amount to charge.
   * @return the remaining credit.
   * @throws InsufficientFundsException this {@code User} doesn't have enough
   *                                    credit.
   */
  public int charge(final int price) throws InsufficientFundsException {
    if (price > credit)
      throw new InsufficientFundsException(price, credit);

    return credit -= price;
  }

  /**
   * Access the {@code UserType} of this {@code User}.
   * 
   * @return A {@code UserType} enum member corresponding to the correct
   *         {@code UserType}.
   */
  public UserType getUserType() {
    return userType;
  }

  /**
   * Access the name of this {@code User}.
   * 
   * @return A String containing this {@code User}'s name.
   */
  public String getUsername() {
    return username;
  }

  /**
   * reset the daily addCredit limit.
   */
  public void endDay() {
    dailyCreditAvailability = MAX_DAILY;
  }
}

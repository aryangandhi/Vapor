package test;

import org.junit.jupiter.api.*;
import vapor.exceptions.*;
import vapor.transactions.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 *Tests the functionality of TransactionBuilder.java
 */
public class TransactionBuilderTest {
    TransactionBuilder transactionBuilder;
    String login;
    String delete;
    String create;
    String addCredit;
    String logout;
    String auctionSale;
    String buy;
    String sell;
    String refund;
    String gift;

    /**
     * Creates strings for each type of transaction before each test
     */
    @BeforeEach
    public void setup(){
        login = "00 UUUUUUUUUUUUUUU AA 000010.00";
        create = "01 Dinodaddy#      FS 000010.00";
        delete = "02 Dinodaddy#      FS 000010.00";
        addCredit = "06 Dinodaddy#      FS 000010.00";
        auctionSale = "07 UUUUUUUUUUUUUUU AA 000010.10";
        buy = "04 Smash Bros                Dinodaddy#      UUUUUUUUUUUUUUU";
        sell = "03 Smash Bros                Dinodaddy#      10.00 010.00";
        refund = "05 BBBBBBBBBBBBB   Dinodaddy#      000010.00";
        logout = "10 UUUUUUUUUUUUUUU AA 000010.00";
        gift = "09 Smash Bros                Dinodaddy#      UUUUUUUUUUUUUUU";

    }

    /**
     * Tests the functionality of getField() in TransactionBuilder.java
     */
    @Test
    public void test_getField() {
        try {
            transactionBuilder = transactionBuilder_helper(login);
            assertEquals("00", transactionBuilder.getField(1));
            assertEquals("UUUUUUUUUUUUUUU", transactionBuilder.getField(2));
            assertEquals("AA", transactionBuilder.getField(3));
            assertEquals("000010.00", transactionBuilder.getField(4));
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "TransactionBuilderTest, test_getField", e.getError());
        }
    }

    /**
     * Tests the functionality of the constructor of TransactionBuilder object
     */
    @Test
    public void test_constructor() {
        assertDoesNotThrow(() -> new TransactionBuilder(login));
        assertDoesNotThrow(() -> new TransactionBuilder(create));
        assertDoesNotThrow(() -> new TransactionBuilder(delete));
        assertDoesNotThrow(() -> new TransactionBuilder(addCredit));
        assertDoesNotThrow(() -> new TransactionBuilder(auctionSale));
        assertDoesNotThrow(() -> new TransactionBuilder(buy));
        assertDoesNotThrow(() -> new TransactionBuilder(sell));
        assertDoesNotThrow(() -> new TransactionBuilder(logout));
        assertDoesNotThrow(() -> new TransactionBuilder(refund));
        assertDoesNotThrow(() -> new TransactionBuilder(gift));
        assertThrows(InvalidTransactionCodeException.class, () -> new TransactionBuilder("ji"));
    }

    /**
     * Tests the functionality of parse() in TransactionBuilder.java
     */
    @Test
    public void test_parse() {
        try {
            assertEquals(LoginTransaction.class, transactionBuilder_helper(login).parse().getClass());
            assertEquals(CreateTransaction.class, transactionBuilder_helper(create).parse().getClass());
            assertEquals(DeleteTransaction.class, transactionBuilder_helper(delete).parse().getClass());
            assertEquals(AddCreditTransaction.class, transactionBuilder_helper(addCredit).parse().getClass());
            assertEquals(AuctionSaleTransaction.class, transactionBuilder_helper(auctionSale).parse().getClass());
            assertEquals(BuyTransaction.class, transactionBuilder_helper(buy).parse().getClass());
            assertEquals(SellTransaction.class, transactionBuilder_helper(sell).parse().getClass());
            assertEquals(LogoutTransaction.class, transactionBuilder_helper(logout).parse().getClass());
            assertEquals(RefundTransaction.class, transactionBuilder_helper(refund).parse().getClass());
            assertEquals(GiftGameTransaction.class, transactionBuilder_helper(gift).parse().getClass());
        } catch (final VaporException e) {
            ErrorLogger.log(ErrorLogger.ErrorType.ERROR, "TransactionBuilderTest, test_parse", e.getError());
        }
    }

    public TransactionBuilder transactionBuilder_helper(String transaction) throws InvalidTransactionCodeException {
        return new TransactionBuilder(transaction);
    }

    /**
     * Tests if wrong inputs for transactions throws InvalidTransactionCodeException as expected.
     */
    @Test
    public void test_bad_inputs(){
        HashMap <String, String[]> bad_inputs = new HashMap<String, String[]>();
        String s1 = "00  UUUUUUUUUUUUUU AA 000010.00";
        String s2 = "11 UUUUUUUUUUUUUUU AA 000010.00";
        String s3 = "00 UUUUUUUUUUUUUUUU AA 000010.00";
        String s4 = "00 UUUUUUUUUUUUUUU YO 000010.00";
        String s5 = "00 UUUUUUUUUUUUUUU AA 000010000";
        bad_inputs.put("format1", new String[]{s1, s2, s3, s4, s5});
        String s6 = "03  Smash Bros                Dinodaddy#      10.00 010.00";
        String s7 = "03 Smash Bros                Dinodaddy#      1.000 000.00";
        String s8 = "03 Smash Bros                 Dinodaddy#     10.00 010.00";
        String s9 = "04 Smash Bros                Dinodaddy#      10.00 010.00";
        String s10 = "03 Smash Bros                Dinodaddy#       10000 010000";
        bad_inputs.put("format2", new String[]{s6,s7,s8,s9, s10});
        String s11 = "05  BBBBBBBBBBBBB   Dinodaddy#      000010.00";
        String s12 = "01 BBBBBBBBBBBBB   Dinodaddy#      000010000";
        bad_inputs.put("format3", new String[]{s11, s12});
        String s14 = "04 Smash Bros                Dinodaddy#      UUUUUUUUUUUUUU";
        String s15 = "02 Smash Bros                Dinodaddy#      UUUUUUUUUUUUUUU";
        bad_inputs.put("format4", new String[]{s14, s15});

        for (String[] list: bad_inputs.values()){
            for (String string: list){
                assertThrows(InvalidTransactionCodeException.class, () -> transactionBuilder_helper(string));
            }

        }
    }

}

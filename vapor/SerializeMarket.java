package vapor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import vapor.exceptions.ErrorLogger;
import vapor.exceptions.ErrorLogger.ErrorType;

/**
 * A class tasked with the serialization and deserialization of a {@code Market}.
 */
public class SerializeMarket {
    
    /** Attempt to deserialize and return the market object from {@code filename}.
     *  Returns null if no such object exists.
     *
     * @param fileName - The file where the market is stored.
     * @return - The market loaded from {@code filename}, null otherwise.
     */
    public static Market load(String fileName){
        Market market = null;
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream input = new ObjectInputStream(file);
            market = (Market) input.readObject();

            input.close();
            file.close();

            System.out.println("Market was deserialized!");
        } catch (final IOException | ClassNotFoundException e) {
            ErrorLogger.log(ErrorType.ERROR, "MARKET SERIALIZATION [FILE: " + fileName + "]",
                    "deserialization failed.");
        }
        return market;
    }

    /** Attempt to serialize {@code market} in the file {@code marketFileName}
     *
     * @param market - The market to be serialized.
     * @param marketFileName - the name of the file where we want to save {@code market}.
     */
    public static void save(Market market, String marketFileName) {
        try {
            FileOutputStream file = new FileOutputStream(marketFileName);
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(market);

            output.close();
            file.close();

            System.out.println("Market was serialized!");
        } catch (final IOException e) {
            ErrorLogger.log(ErrorType.ERROR, "MARKET SERIALIZATION [FILE: " + marketFileName + "]",
                    "serialization failed.");
        }
    }
}

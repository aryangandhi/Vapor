package vapor;

import vapor.exceptions.ErrorLogger;
import vapor.exceptions.ErrorLogger.ErrorType;
import vapor.users.*;
import vapor.users.User.UserType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * A class with the purpose of providing the front-end with a JSON representation
 * of the {@code market}.
 */
public class MarketToJSON {

    // The filename of the JSON file to be created.
    private static final String JSON_FILENAME = "users.json";


    /** This method creates {@code JSON_FILENAME}, it writes the contents of {@code arr} to this file.
     *
     * @param arr - The JSON array with objects we intend to write to {@code JSON_FILENAME}.
     */
    private static void createFile(JSONArray arr) {
        try {
            // Writing to a file
            File file = new File(JSON_FILENAME);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);

            System.out.println("Writing users to users.json");

            fileWriter.write(arr.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch (final IOException e) {
            ErrorLogger.log(ErrorType.ERROR,
                    "FRONT-END JSON EXPORT [FILE: " + JSON_FILENAME + "]", e.getMessage());
        }
    }


    /** returns a JSONArray {@code gamesArray} which holds JSON representations of
     *  the games in {@code games}.
     *
     *  The JSON objects {@code gameObj} which represent the {@code game}s in {@code games}
     *  have the following format:
     *
     *  - name: The name of {@code game}.
     *
     * @param games - the list of games to be stored in the JSONArray {@code gamesArray}.
     * @return - returns {@code gamesArray} of {@code gameObj}s.
     */
    private static JSONArray gamesJSONArray(Game[] games) {
        JSONArray gamesArray = new JSONArray();

        for (Game game: games) {
            // Create a JSON obj for each game.
            JSONObject gameObj = new JSONObject();
            gameObj.put("name", game.getName());

            gamesArray.add(gameObj);
        }
        return gamesArray;
    }


    /** returns a JSONArray {@code listingsArray} which holds JSON representations of
     *  the listings in {@code listings}.
     *
     *  The JSON objects {@code listingObj} have the following format:
     *
     *  - name: The name of {@code listing}'s game.
     *  - price: The price of {@code listing}.
     *  - discount: The discount of {@code listing} during an auction sale.
     *
     * @param listings - the {@code listing}s to be stored in the JSONArray {@code listingArray}.
     * @return - the JSONArray {@code listingArray} which contains {@code listingObj}s.
     */
    private static JSONArray listingsJSONArray(Listing[] listings) {
        JSONArray listingsArray = new JSONArray();

        for (Listing listing: listings) {
            // Create a JSON obj for each listing.
            JSONObject listingObj = new JSONObject();
            JSONObject gameObj = new JSONObject();
            gameObj.put("name", listing.getGame().getName());
            listingObj.put("game", gameObj);
            listingObj.put("price", listing.getPrice());
            listingObj.put("discount", listing.getDiscount());

            listingsArray.add(listingObj);
        }
        return listingsArray;
    }


    /** Return a JSONObject representation of {@code user}.
     *  The JSON representation will store the following:
     *   - username: this unique ID of {@code user}.
     *   - type: the type of {@code user} ("AA", "SS", "FS", "BS").
     *   - balance: the credits available to {@code user}.
     *
     * @param user - the {@code user} we wish to represent with a JSONObject.
     * @return - the {@code userObj} which represents the user.
     */
    private static JSONObject createUserJSON(User user) {
        // Create a user JSON obj which contains username, type, and balance.
        JSONObject userObj = new JSONObject();
        userObj.put("username", user.getUsername());
        userObj.put("type", user.getUserType().getUserTypeCode());
        userObj.put("balance", user.getCredit());

        return userObj;
    }


    /** Creates JSON representation of {@code market}. {@code market}'s users/games are
     *  stored in {@code usersArray}. {@code usersArray} is stored in a created JSON file.
     *
     * @param market - the Market obj we intend to represent as a JSON file.
     */
    public static void toJSON(Market market) {
        JSONArray usersArray = new JSONArray();

        // Add a JSON representation of each user to the usersArray.
        for (User user: market.getUsers().values()) {
            JSONObject userObj = createUserJSON(user);

            // All users except Sellers have an inventory of games they own.
            if (user.getUserType().isBuyer()) {
                Buyer buyer = (Buyer)user;
                JSONArray gamesArray = gamesJSONArray(buyer.getInventory().getEntries());
                userObj.put("games", gamesArray);
            }
//
            // All users except Buyers have an listings of games for sale.
            if (user.getUserType().isSeller()) {
                Seller seller = (Seller)user;
                JSONArray listingsArray = listingsJSONArray(seller.getStoreFront().getEntries());
                userObj.put("listings", listingsArray);
            }

            usersArray.add(userObj);
        }

        // Create a JSON file to house usersArray.
        createFile(usersArray);
    }
}

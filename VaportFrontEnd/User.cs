using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd
{
    class User
    {
        public string username { get; set; }
        public List<Game> games { get; set; }
        public List<Listing> listings { get; set; }
        public int balance { get; set; }
        public string type { get; set; }

        public int creditsAdded = 0;
        private int MAXCREDITS = 99999999;
        private int MAXDAILYCREDITS = 100000;

        public void AddGame(Game game)
        {
            games.Add(game);
        }

        public void CreateAdminAccount()
        {
            username = "admin";
            games = new List<Game>();
            balance = 0;
            type = "AA";
        }

        public void CreateNew(string username, string type)
        {
            this.username = username;
            this.type = type;
            games = new List<Game>();
            listings = new List<Listing>();
            balance = 0;
            creditsAdded = 0;
        }

        public bool BuyGame(Listing game, User seller, Market market)
        {
            // Make sure the user is priveleged to buy a game
            if (type == "SS")
            {
                MainWindow.ShowMessage("Seller type users cannot buy games.");
                return false;
            }

            if (listings.Contains(game))
            {
                MainWindow.ShowMessage("You cannot buy your own listing.");
                return false;
            }

            if (!game.canBuy)
            {
                MainWindow.ShowMessage("You cannot buy a game that was listed on the same day.");
                return false;
            }

            if (games.Contains(game.game))
            {
                MainWindow.ShowMessage("You already own this game.");
                return false;
            }

            // Check if the user has enough balance to purchase this game
            if (game.price > balance)
            {
                MainWindow.ShowMessage("You do not have enough balance.");
                return false;
            }

            balance -= game.price;
            seller.balance += game.price;

            // Add the game to the user, and add the transaction to the list of today's transactions
            games.Add(game.game);
            game.game.canGift = false;
            Transactions.BuyTransaction transaction = new Transactions.BuyTransaction(game.getName(), seller.username, username);
            market.AddTransaction(transaction.getCode());

            return true;
        }

        public bool Refund(string FROMusername, string TOusername, string amount, Market market)
        {
            if (type != "AA")
            {
                MainWindow.ShowMessage("You are not priveleged to do this.");
                return false;
            }

            User fromUser;
            User toUser;
            Transactions.RefundTransaction transaction;

            amount = amount.Replace(".", "");
            int refundAmount = int.Parse(amount);

            toUser = market.Users.Find(i => i.username == TOusername);
            fromUser = market.Users.Find(i => i.username == FROMusername);

            if (toUser == null || fromUser == null)
            {
                MainWindow.ShowMessage("Could not find both users.");
                return false;
            }

            if (fromUser.balance < refundAmount)
            {
                MainWindow.ShowMessage(FROMusername + " does not have enough balance.");
                return false;
            }

            if (toUser.balance + refundAmount > MAXCREDITS)
            {
                MainWindow.ShowMessage(TOusername + "cannot take anymore balance.");
                return false;
            }

            fromUser.balance -= refundAmount;
            toUser.balance += refundAmount;

            transaction = new Transactions.RefundTransaction(TOusername, FROMusername, amount);
            market.AddTransaction(transaction.getCode());

            MainWindow.ShowMessage("Transaction successful.");
            return true;
        }

        public bool ListGame(string name, string price, string discount, Market market, Dictionary<Listing, string> marketGames)
        {
            int cost;
            float gameDiscount;
            Game game;
            Listing listing;

            price = price.Replace(".", "");

            try
            {
                cost = int.Parse(price);
                gameDiscount = float.Parse(discount);
            }
            catch (FormatException e)
            {
                MainWindow.ShowMessage("Invalid price or discount input.");
                Console.WriteLine("ListGame error: " + e.Message);
                return false;
            }

            game = new Game();
            game.name = name;
            game.canGift = false;
            
            listing = new Listing();
            listing.price = cost;
            listing.game = game;
            listing.canBuy = false;

            listings.Add(listing);

            Transactions.SellTransaction transaction;
            transaction = new Transactions.SellTransaction(name, username, gameDiscount, cost);

            marketGames.Add(listing, username);
            market.AddTransaction(transaction.getCode());

            MainWindow.ShowMessage("Game listed successfuly.");
            return true;
        }

        public bool AddCredits(string amount, Market market)
        {
            int credits;

            if (amount.Split('.').Length > 2)
            {
                amount = amount.Replace(".", "");
                amount = amount.Insert(amount.Length - 2, ".");
            }

            // Do this before changing the amount to an int format since the daily.txt needs to have decimals.
            Transactions.AddCreditTransaction transaction;
            transaction = new Transactions.AddCreditTransaction(username, type, amount);
            
            amount = amount.Replace(".", "");

            try
            {
                credits = int.Parse(amount);
            }
            catch ( FormatException e)
            {
                MainWindow.ShowMessage("Invalid input.");
                Console.WriteLine("AddCredits error: " + e.Message);
                return false;
            }

            if (creditsAdded + credits > MAXDAILYCREDITS)
            {
                MainWindow.ShowMessage("Daily deposit limit reached.");
                //credits = MAXDAILYCREDITS - creditsAdded;
                //creditsAdded = MAXDAILYCREDITS;
                //balance += credits;

                // Need to add the transaction here as well since even though we are at the limit, daily.txt needs to know about the attempt
                market.AddTransaction(transaction.getCode());
                return true;
            }

            if (balance + credits > MAXCREDITS)
            {
                MainWindow.ShowMessage("Max credits reached. Issuing transaction.");
                balance = MAXCREDITS;
                creditsAdded = MAXCREDITS - balance;
            }
            else
            {
                balance += credits;
                creditsAdded += credits;
                MainWindow.ShowMessage("Credits added successfully.");
            }

            market.AddTransaction(transaction.getCode());
            return true;
        }

        public bool GiftGame(string receiverUsername, Game game, Market market)
        {
            User receiver = market.Users.Find(i => i.username == receiverUsername);
            Transactions.GiftGameTransaction transaction;

            if (receiver.games.Contains(game))
            {
                MainWindow.ShowMessage(receiverUsername + " already owns " + game.name + ".");
                return false;
            }

            // TODO: check this actually works
            if (!game.canGift)
            {
                MainWindow.ShowMessage("Cannot gift a game that was listed/purchased on the same day.");
                return false;
            }

            if (games.Contains(game))
                games.Remove(game);

            transaction = new Transactions.GiftGameTransaction(game.name, username, receiverUsername);
            market.AddTransaction(transaction.getCode());
            receiver.AddGame(game);
            MainWindow.ShowMessage("Transaction successful.");
            return true;


        }
    }
}

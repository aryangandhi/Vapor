using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace VaportFrontEnd
{
    class Market
    {
        public List<User> Users { get; set; }
        public List<Game> games { get; set; }
        public List<string> transactions { get; set; }

        public User currentUser { get; set; }


        public Market()
        {
            Users = new List<User>();
            games = new List<Game>();
            transactions = new List<string>();
        }

        public bool Login(string username)
        {
            int userIndex = Users.FindIndex(item => item.username == username);
            if (userIndex == -1)
                return false;

            currentUser = Users[userIndex];

            string balance = currentUser.balance.ToString();
            if (balance.Length > 3)
                balance = balance.Insert(balance.Length - 2, ".");
            else if (balance.Length == 2)
                balance = "0." + balance;
            else
                balance = "0.0" + balance;

            //currentUser.listings = new List<Listing>();
            Transactions.LoginTransaction loginTransaction = new Transactions.LoginTransaction(currentUser.username, currentUser.type, balance);
            transactions.Add(loginTransaction.getCode());
            return true;
        }

        public void AddTransaction(string transaction)
        {
            transactions.Add(transaction);
        }

        public User getUser(string username)
        {
            int index = Users.FindIndex(item => item.username == username);
            if (index == -1)
                return null;

            return Users[index];
        }

        public void CreateAccount(string username, string type)
        {
            string userType;
            User user = new User();
            Transactions.CreateTransaction transaction;

            Dictionary<string, string> types = new Dictionary<string, string>
            {
                {"Admin", "AA"},
                {"Full-Standard", "FS"},
                {"Buyer", "BS"},
                {"Seller", "SS"},
            };

            userType = types[type];
            user.CreateNew(username, userType);
            Users.Add(user);

            MainWindow.ShowMessage("Account successfully created.");

            transaction = new Transactions.CreateTransaction(username, userType, "0.00");
            transactions.Add(transaction.getCode());
        }

        public void AddUser(User user)
        {
            Users.Add(user);
        }

        public void Logout()
        {
            string balance = currentUser.balance.ToString();
            if (balance.Length > 3)
                balance = balance.Insert(balance.Length - 2, ".");
            else if (balance.Length == 2)
                balance = "0." + balance;
            else
                balance = "0.0" + balance;

            Transactions.LogoutTransaction logoutTransaction = new Transactions.LogoutTransaction(currentUser.username, currentUser.type, balance);
            transactions.Add(logoutTransaction.getCode());

            File.AppendAllLines("../daily.txt", transactions);
            transactions.Clear();
        }
    }
}

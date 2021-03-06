using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Text.Json;
using System.Text.RegularExpressions;
using System.IO;

namespace VaportFrontEnd
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        Market market;
        Dictionary<Listing, string> games = new Dictionary<Listing, string>();
        public MainWindow()
        {
            InitializeComponent();
            market = new Market();

            // TODO: Create a market.Initialize function to move all the general setup to
            // Deserializing the JSON file
            List<User> users = new List<User>();
            try
            {
                string jsonString = File.ReadAllText("../users.json");
                if (jsonString != "")
                {
                    users = JsonSerializer.Deserialize<List<User>>(jsonString);
                    market.Users = users;
                }
            }
            catch (Exception e)
            {
                ShowMessage("Could not open users.json (Should be generated by back-end component).");
                Close();
                return;
            }

            // By now the program could launch successfully
            File.WriteAllText("../daily.txt", string.Empty); // This also creates the file if it does not exist

            foreach (User user in users)
            {
                user.creditsAdded = 0;
                if (user.listings != null) // Check this incase a user never had their games instantiated
                {
                    foreach (Listing game in user.listings)
                    {
                        games.Add(game, user.username);
                        game.canBuy = true; // Reset canBuy for every listing, since its a new day
                        game.game.canGift = true;
                    }
                }

                // Reset canGift for every game that every user owns, since its a new day
                if (user.games != null)
                    foreach (Game game in user.games)
                        game.canGift = true;
            }

            GamesListBox.ItemsSource = games;
            allUsersListBox.ItemsSource = market.Users;
            userTypesComboBox.ItemsSource = new List<string> { "Admin", "Full-Standard", "Buyer", "Seller" };
            userTypesComboBox.SelectedIndex = 0;
        }

        private void loginButton_Click(object sender, RoutedEventArgs e)
        {
            if (!market.Login(usernameField.Text))
                    return;

            listedGamesListBox.ItemsSource = market.currentUser.listings;
            ownedGamesListBox.ItemsSource = market.currentUser.games;

            // TODO: Show a text here that says you failed to login, if so.
            LOGIN.Visibility = Visibility.Hidden;
            MAIN.Visibility = Visibility.Visible;

            if (market != null)
                signedinText.Content = "Signed in as: " + market.currentUser.username;

            // Buyer users cannot sell a game, so disable the UI.
            if (market.currentUser.type == "BS")
            {
                BuyerInfo.Visibility = Visibility.Visible;
                ListGameButton.IsEnabled = false;
                ListingGameDiscount.IsEnabled = false;
                ListingGameName.IsEnabled = false;
                ListingGamePrice.IsEnabled = false;
            }

            adminPanelButton.Visibility = market.currentUser.type == "AA" ? Visibility.Visible : Visibility.Hidden;
            UpdateBalanceView();
        }

        public void UpdateBalanceView()
        {
            if (market != null)
            {
                string balance = market.currentUser.balance.ToString();
                if (balance.Length > 3)
                    balance = balance.Insert(balance.Length - 2, ".");
                else if (balance.Length == 2)
                    balance = "0." + balance;
                else
                    balance = "0.0" + balance;
                availableCreditsText.Content = "Available Balance: $" + balance;
                currentBalanceLabel.Content = "Current Balance: $" + balance;
            }
        }

        public static void ShowMessage(string msg)
        {
            InfoBox messageWindow;
            messageWindow = new InfoBox(msg);
            messageWindow.Show();
        }

        private void buyButton_Click(object sender, RoutedEventArgs e)
        {
            var selected = games.ElementAt(GamesListBox.SelectedIndex);
            Listing game = selected.Key;
            User seller = market.getUser(selected.Value);

            // Check if the buy was successful.
            if (!market.currentUser.BuyGame(game, seller, market))
                return;

            ShowMessage("Transaction successful.");
            ownedGamesListBox.Items.Refresh();
            UpdateBalanceView();
        }

        private void logoutButton_Click(object sender, RoutedEventArgs e)
        {
            market.Logout();
            market.currentUser = null;
            
            MAIN.Visibility = Visibility.Hidden;
            LOGIN.Visibility = Visibility.Visible;
        }

        private void GamesListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (gameDetails.Visibility == Visibility.Hidden)
                gameDetails.Visibility = Visibility.Visible;

            var selected = games.ElementAt(GamesListBox.SelectedIndex);
            Listing game = selected.Key;
            User seller = market.getUser(selected.Value);

            gameDescName.Content = game.getName();
            sellerNamedesc.Content = "sold by: " + seller.username;
        }

        private void backbutton_Click(object sender, RoutedEventArgs e)
        {
            PROFILE.Visibility = Visibility.Hidden;
            ADMINPANEL.Visibility = Visibility.Hidden;
            MAIN.Visibility = Visibility.Visible;
        }

        private void profileButton_Click(object sender, RoutedEventArgs e)
        {
            MAIN.Visibility = Visibility.Hidden;
            PROFILE.Visibility = Visibility.Visible;
            usernameLabel.Content = market.currentUser.username;
            UpdateBalanceView();
        }

        private void ListGameButton_Click(object sender, RoutedEventArgs e)
        {
            if (!ListingGamePrice.Text.Contains("."))
                ListingGamePrice.Text += ".00";

            market.currentUser.ListGame(ListingGameName.Text, ListingGamePrice.Text, ListingGameDiscount.Text, market, games);
            listedGamesListBox.Items.Refresh();
            GamesListBox.Items.Refresh();
        }

        private void AddCreditsButton_Click(object sender, RoutedEventArgs e)
        {
            if (!AddCreditsAmount.Text.Contains("."))
                AddCreditsAmount.Text += ".00";

            if (AddCreditsAmount.Text.Length > 9)
                AddCreditsAmount.Text = "999999.99";

            if (market.currentUser.AddCredits(AddCreditsAmount.Text, market))
                UpdateBalanceView();
        }

        private void NumberValidationTextBox(object sender, TextCompositionEventArgs e)
        {
            Regex regex = new Regex("^[^(\\d{0,6}(?:\\.\\d{0,2})?)]");
            e.Handled = regex.IsMatch(e.Text);
        }

        private void GiftGameButton_Click(object sender, RoutedEventArgs e)
        {
            ListBox listBox = listedGamesListBox.SelectedIndex != -1 ? listedGamesListBox : ownedGamesListBox;
            Game game;

            if (listBox == ownedGamesListBox)
                game = market.currentUser.games.Find(i => i.name == listBox.SelectedItem.ToString());
            else
                game = market.currentUser.listings.Find(i => i.getName() == listBox.SelectedItem.ToString()).game;

            ownedGamesListBox.Items.Refresh();
            market.currentUser.GiftGame(GiftGameUsername.Text, game, market);
        }

        private void listedGamesListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ownedGamesListBox.SelectedIndex = -1;
        }

        private void ownedGamesListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            listedGamesListBox.SelectedIndex = -1;
        }

        private void CreateAccountButton_Click(object sender, RoutedEventArgs e)
        {
            market.CreateAccount(CreateUsernameAccount.Text, userTypesComboBox.SelectedItem.ToString());
            allUsersListBox.Items.Refresh();
        }

        private void adminPanelButton_Click(object sender, RoutedEventArgs e)
        {
            MAIN.Visibility = Visibility.Hidden;
            ADMINPANEL.Visibility = Visibility.Visible;
        }

        private void refundButton_Click(object sender, RoutedEventArgs e)
        {
            if (!refundAmount.Text.Contains("."))
                refundAmount.Text += ".00";

            if (refundAmount.Text.Length > 9)
                refundAmount.Text = "999999.99";

            market.currentUser.Refund(refundFROMuser.Text, refundTOuser.Text, refundAmount.Text, market);
            UpdateSelectedUserInfo();
        }

        private void allUsersListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (selectedDetails.Visibility == Visibility.Hidden)
                selectedDetails.Visibility = Visibility.Visible;

            UpdateSelectedUserInfo();
        }

        private void UpdateSelectedUserInfo()
        {
            if (allUsersListBox.SelectedIndex == -1)
                return;

            User user = (User)allUsersListBox.SelectedItem;
            string balance = user.balance.ToString();

            if (balance.Length > 2)
                balance = balance.Insert(balance.Length - 2, ".");

            selectedUsername.Content = user.username;
            selectedBalance.Content = "Current balance: $" + balance;
            selectedType.Content = "User type: " + user.type;
        }
    }
}

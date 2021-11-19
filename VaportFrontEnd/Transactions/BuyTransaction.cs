using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd.Transactions
{
    class BuyTransaction
    {
        public string code { get; set; }
        public string gameName { get; set; }
        public string sellerUsername { get; set; }
        public string buyerUsername { get; set; }

        public BuyTransaction(string gameName, string sellerUsername, string buyerUsername)
        {
            code = "04";
            this.gameName = gameName.PadRight(25);
            this.sellerUsername = sellerUsername.PadRight(15);
            this.buyerUsername = buyerUsername.PadRight(15);
        }

        public string getCode()
        {
            return code + " " + gameName + " " + sellerUsername + " " + buyerUsername;
        }
    }
}

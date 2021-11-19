using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd.Transactions
{
    class SellTransaction
    {
        public string code { get; set; }
        public string gameName { get; set; }
        public string sellerUsername { get; set; }
        public string discountPercentage { get; set; }
        public string salePrice { get; set; }

        public SellTransaction(string gameName, string sellerUsername, float discount, float price)
        {
            string gamePrice = price.ToString();
            gamePrice = gamePrice.Insert(gamePrice.Length - 2, ".");

            string sale = discount.ToString();
            if (sale.Length == 2)
                sale += ".00";
            else if (sale.Length == 1)
                sale = "0" + sale + ".00";

            code = "03";
            this.gameName = gameName.PadRight(25);
            this.sellerUsername = sellerUsername.PadRight(15);
            this.discountPercentage = sale;
            this.salePrice = gamePrice.PadLeft(6, '0');
        }

        public string getCode()
        {
            return code + " " + gameName + " " + sellerUsername + " " + discountPercentage + " " + salePrice;
        }
    }
}

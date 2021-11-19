using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VaportFrontEnd.Transactions
{
    class RefundTransaction
    {
        public string code { get; set; }
        public string toUsername { get; set; }
        public string fromUsername { get; set; }
        public string refundAmount { get; set; }

        public RefundTransaction(string toUsername, string fromUsername, string amount)
        {
            code = "05";
            this.toUsername = toUsername.PadRight(15);
            this.fromUsername = fromUsername.PadRight(15);

            if (amount.Length > 3)
                amount = amount.Insert(amount.Length - 2, ".");
            else if (amount.Length == 2)
                amount = "0." + amount;
            else
                amount = "0.0" + amount;

            refundAmount = amount.PadLeft(9, '0');
        }

        public string getCode()
        {
            return code + " " + toUsername + " " + fromUsername + " " + refundAmount;
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VaportFrontEnd.Transactions
{
    class GiftGameTransaction
    {
        public string code { get; set; }
        public string gameName { get; set; }
        public string ownerUsername { get; set; }
        public string receiverUsername { get; set; }

        public GiftGameTransaction(string gameName, string ownerUsername, string receiverUsername)
        {
            code = "09";
            this.gameName = gameName.PadRight(25);
            this.ownerUsername = ownerUsername.PadRight(15);
            this.receiverUsername = receiverUsername.PadRight(15);
        }

        public string getCode()
        {
            return code + " " + gameName + " " + ownerUsername + " " + receiverUsername;
        }
    }
}

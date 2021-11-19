using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd.Transactions
{
    abstract class AccountTransactionBase
    {
        public string code { get; set; }
        public string username { get; set; }
        public string usertype { get; set; }
        public string availableCredit { get; set; }

        public AccountTransactionBase(string username, string usertype, string availableCredit)
        {
            this.username = username.PadRight(15);
            this.usertype = usertype.PadRight(2);
            this.availableCredit = availableCredit.PadLeft(9, '0');
        }

        public string getCode()
        {
            return code + " " + username + " " + usertype + " " + availableCredit;
        }
    }
}

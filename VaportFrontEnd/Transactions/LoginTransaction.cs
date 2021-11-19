using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd.Transactions
{
    class LoginTransaction : AccountTransactionBase
    {
        public LoginTransaction(string username, string usertype, string availableCredit) : base(username, usertype, availableCredit)
        {
            code = "00";
        }
    }
}

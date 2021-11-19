using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd.Transactions
{
    class LogoutTransaction : AccountTransactionBase
    {
        public LogoutTransaction(string username, string usertype, string availableCredit) : base(username, usertype, availableCredit)
        {
            code = "10";
        }
    }
}

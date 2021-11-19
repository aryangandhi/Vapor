using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd.Transactions
{
    class AddCreditTransaction : AccountTransactionBase
    {
        public AddCreditTransaction(string username, string usertype, string availableCredit) : base(username, usertype, availableCredit)
        {
            code = "06";
        }
    }
}

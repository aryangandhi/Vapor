using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VaportFrontEnd.Transactions
{
    class CreateTransaction : AccountTransactionBase
    {
        public CreateTransaction(string username, string usertype, string availableCredit) : base(username, usertype, availableCredit)
        {
            code = "01";
        }
    }
}

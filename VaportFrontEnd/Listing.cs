using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VaportFrontEnd
{
    class Listing
    {
        public Game game { get; set; }
        public int price { get; set; }
        public float discount { get; set; }

        public bool canBuy = true;

        public string getName()
        {
            return game.name;
        }

        public override string ToString()
        {
            return game.name;
        }
    }
}

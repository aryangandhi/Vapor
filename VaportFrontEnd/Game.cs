using System;
using System.Collections.Generic;
using System.Text;

namespace VaportFrontEnd
{
    class Game
    {
        public string name { get; set; }
        public bool canGift = true;
        public override string ToString()
        {
            return name;
        }
    }
}

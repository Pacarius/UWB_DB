using Server.src.SQLIF;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server.src.ClientTypes
{
    internal class EspClient : Client
    {
        internal override string Secret { get; set; } = "ChiFatEntry";
        internal override async Task UpdateAsync(string t)
        {
            switch (Stage)
            {
                case 0:
                    if (l == Secret)
                    {
                        Stage = 1;
                        await w.WriteLineAsync("VALID");
                    }
                    else
                    {
                        await w.WriteLineAsync("ERROR");
                        Dead = true;
                    }
                    return;
                case 1:
                    EspHandler handler = new(t);
                    Console.WriteLine(t);
                    await handler.runHandler();
                    Dead = true;
                    return;

            }
        }
    }
}

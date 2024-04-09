
using System.ComponentModel.Design;
using System.Net.Sockets;

namespace Server
{
    class Unused1 : Client
    {
        internal override string Secret { get; set; } = "ChiFatRegi";
        internal override async Task UpdateAsync(string l)
        {
            switch (Stage)
            {
                case 0:
                    if (l == Secret)
                    {
                        Stage = 1;
                        await w.WriteLineAsync("Secret Accepted.");
                    }
                    else { await w.WriteLineAsync("Secret error.");
                        Dead = true;
                    }
                    return;
                case 1:
                    int a = await Program.sqlConnector.ExeNonQ(new($"INSERT {l} INTO mydb.ActiveChips"));
                    if (a == -1)
                    {
                        await w.WriteLineAsync("SQLServer may be down. Skipping all client steps.");
                        Dead = true;
                    }
                    else if (a < 1)
                    {
                        Console.WriteLine("Invalid Insert.");
                        await w.WriteLineAsync("Invalid.");
                    }
                    else
                    {
                        await w.WriteLineAsync("Processed.");
                        Stage = 2;
                    }
                    return;
                case 2:
                    Dead = true;
                    return;
            }
        }
    }
}
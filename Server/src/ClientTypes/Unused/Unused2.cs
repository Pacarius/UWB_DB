using System.Net.Sockets;

namespace Server
{
    class Unused2 : Client
    {
        internal override string Secret { get; set; } = "ChiFatPublic";
        string[] Modes = { "StreetSync", "AddEntry"};
        //This is for human visibility.
        internal override async Task UpdateAsync(string l)
        {
            switch (Stage)
            {
                case < 0:
                    await RunCMD(l);
                    return;
                case 0:
                    if (l == Secret)
                    {
                        Stage = 1;
                        await w.WriteLineAsync("Secret Accepted.");
                    }
                    return;
                case 1:
                    int tmp = 0;
                    if (int.TryParse(l, out tmp))
                    {
                        if(tmp > 0 && tmp < Modes.Length)
                        {
                            Stage = -tmp;
                            //This is stupid.
                            await w.WriteLineAsync("Mode Selected.");
                        }
                        else
                        {
                            await w.WriteLineAsync("Mode error. Disconnecting client.");
                            Dead = true;
                        }
                    }
                    else { 
                        await w.WriteLineAsync("Mode error. Disconnecting client.");
                        Dead = true;
                    }
                    return;

            }
        }
        async Task<bool> RunCMD(string args)
        {
            int tmp = -Stage;
            switch (tmp)
            {
                case 1:
                    await StreetSync(args);
                    return true;
                default: return false;
            }
        }
        async Task StreetSync(string PostId)
        {
            SQLResult result = await Program.sqlConnector.ExeQ(new($"SELECT LOCATION FROM mydb.lamppostinfo WHERE ID = '{PostId}'"));
            Console.WriteLine(result.valid);
            if (result.valid && result.reader.Read())
            {
                await w.WriteLineAsync((string?)result.reader[0]);
                await result.reader.CloseAsync();
            }
            Dead = true;
        }
        async Task AddEntry(string PostId,string PlateId, string DateTime)
        {

        }
    }
}
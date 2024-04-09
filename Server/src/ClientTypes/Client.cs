using System.Net.Sockets;

abstract class Client
{
    public TcpClient client;
    internal NetworkStream stream;
    internal StreamReader? r;
    internal StreamWriter? w;
    internal bool Locked = false;
    internal virtual string Secret { get; set; } = "";
    internal int Stage = 0;
    internal String? l;
    internal virtual bool Notify {get; set;} = false;
    public bool Dead { get; internal set; } = false;
    public async Task Run()
    {
        stream = client.GetStream();
        r = new StreamReader(stream);
        w = new StreamWriter(stream);
        if(Notify)
            Console.WriteLine("Client Connected");
        while (true)
        {
            Update();
            if (Dead)
            {
                w.Flush();
                Close();
                Console.WriteLine("Client Disconnected");
                break;
            }
           
        }
    }
    void Close()
    {
        r.Close();
        w.Close();
        stream.Close();
    }
    internal async Task ClearPipes()
    {
        await w.FlushAsync();
        Locked = false;
    }
    async Task Update()
    {
        if (Locked) return;
        Locked = true;
        l = await r.ReadLineAsync();
        await UpdateAsync(l);
        await ClearPipes();
    }
    async Task ReturnCode(ReturnCodes code)
    {
        int tmp = 0;
        switch (code)
        {
            case ReturnCodes.INVALID:
                tmp = 0;
                return;
            case ReturnCodes.VALID:
                tmp = 1;
                return;
            case ReturnCodes.SQLERROR:
                tmp = -1;
                return;
        }
        await ReturnCode(tmp);
    }
    async Task ReturnCode(int code)
    {
        if (code == -1)
        {
            await w.WriteLineAsync("SQLERROR");
        }
        else if (code < 1)
        {
            await w.WriteLineAsync("INVALID");
        }
        else
        {
            await w.WriteLineAsync("VALID");
            Stage = 2;
        }
    }
    enum ReturnCodes
    {
        INVALID = 0,
        VALID,
        SQLERROR
    }
    internal abstract Task UpdateAsync(string t);

    public static explicit operator Client(Func<Client> v)
    {
        throw new NotImplementedException();
    }
}
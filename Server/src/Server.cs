using System.Data;
using System.Data.Common;
using System.Net;
using System.Net.Sockets;
namespace Server
{
    //var svr = new Server("127.0.0.1");
    //await svr.Run();
    //Console.WriteLine("Goodby, World!");

    class Server
    {
        IPEndPoint ipep;
        TcpListener? listener;
        public bool Running;
        public List<Client> clients { get; private set;}
        private CancellationTokenSource cts;
        ServerUsage usage;
        string Usage() => usage.ToString();

        public Server(ServerUsage usage, Func<Client> client, ushort port, string host = "127.0.0.1")
        {
            this.usage = usage;
            IPAddress ip = IPAddress.Parse(host);
            ipep = new(ip, port);
            Running = false;
            clients = new();
            createClient = client;
            this.cts = new CancellationTokenSource();
        }

        public void Stop()
        {
            Running = false;
            cts.Cancel();
        }
        public async Task Run()
        {
            listener = new(ipep);
            listener.Start();
            Running = true;
            Console.WriteLine($"{Usage()} server is running.");
            while (Running)
            {
                TcpClient c = await listener.AcceptTcpClientAsync(cts.Token);
                Client client = createClient();
                client.client = c;
                clients.Add(client);
                Task clientTask = client.Run(); //don't await
                clientTask.ContinueWith(t => clients.Remove(client));
            }
        }
        Func<Client> createClient;
        Client client(TcpClient stream)
        {
            return null;
        }
    }
    public enum ServerUsage{
            Undeclared = 0,
            Registrator,
            Public
        }
}

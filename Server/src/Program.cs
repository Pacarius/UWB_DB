using Server.src;
using Server.src.ClientTypes;
using Server.src.SQLIF;
using System.Diagnostics.Metrics;
using System.Numerics;
using Windows.Foundation;

namespace Server
{
    internal class Program
    {
        public static SqlConnector sqlConnector { get; private set; } = new SqlConnector(SqlMode: false);
        static List<Server> Servers = new(){
            new(ServerUsage.Public, () => new EspClient(), port: 42069, "0.0.0.0"),
        };
        static List<List<Client>> clientLists = new() { 
            Servers[0].clients,
        };
        public static bool BoundsCheck { get; private set; } = true;
        static void Main(string[] args)
        {
            foreach(string arg in args){
                if(arg.ToLower() == "--sql") sqlConnector = new SqlConnector(SqlMode: true);
                if(arg.ToLower() == "--nbc") BoundsCheck = false;
            } 
            // Console.WriteLine(args[0].ToLower() == "--sql");
            //EspHandler handler = new("[anchor_1;26444;28.26],[anchor_2;26444;10],[anchor_1;2333;36.94],[anchor_2;2333;11]");
            //var tmp = new Vector2[10];
            //foreach(var a in tmp)foreach(var b in MiscHelper.GetCoordinates(new string[]{"1111", "2222"}).Result.posSet)Console.WriteLine(b);
            //return;
            RunServers();
            // foreach(var a in Task.Run(() => MiscHelper.GetCoordinates(new string[]{"TEST0", "TEST1"})).Result.posSet) Console.WriteLine(a);

        }
        static void RunServers()
        {
            foreach (Server S in Servers)
                S.Run();
            while (Servers[0].Running)
            {
                Thread.Sleep(1000);
            }
            Console.WriteLine("Goodbye, World!");
        }
    }
}
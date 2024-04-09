using System.Net.Sockets;

namespace Server
{
    class DefaultClient : Client
    {
        internal override bool Notify { get; set; } = true;
        internal override async Task UpdateAsync(string l)
        {
                await w.WriteLineAsync("You are standing in an open field west of a white house, with a boarded front door. There is a small mailbox here.");
                await w.WriteAsync(">");
                await w.FlushAsync();
            try
            {
                Console.WriteLine(l);
                await w.WriteLineAsync("Invalid command " + l);
            }catch (Exception ex) { Dead = true; }
        }
    }
}
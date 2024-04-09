using System.Collections.Generic;
using System.Diagnostics;
using System.Numerics;
using System.Reflection.Metadata.Ecma335;
using Azure.Core.GeoJson;
using Windows.Foundation;

namespace Server.src.SQLIF
{
    internal class EspHandler
    {
        List<Entry> entries = new();
        Dictionary<string, List<int>> VehicleOccurences = new();
        string Street = "Hoi Yuen Road";
        int returnValue = -1;
        readonly string[]? tokens;

        public EspHandler(string t)
        {
            if (!t.StartsWith("[") || !t.EndsWith("]")) return;
            // [lamppostid;vehicleid;distance],[lamppostid;vehicleid;distance],[lamppostid;vehicleid;distance]  
            tokens = t.Replace(" ", "").Split(',');
        }
        public async Task runHandler()
        {
            if (tokens == null) return;
            for (int i = 0; i < tokens.Length; i++)
            {
                string token = tokens[i];
                if (token.StartsWith("[") && token.EndsWith("]"))
                {
                    string[] tmp = token[1..^1].Split(';');
                    Entry entry = new Entry();
                    entry.distance = float.Parse(tmp[2]);
                    entry.LamppostID = tmp[0];
                    entry.VehicleID = tmp[1];
                    entries.Add(entry);
                    if (!VehicleOccurences.ContainsKey(entry.VehicleID)) VehicleOccurences.Add(entry.VehicleID, new List<int>());
                    VehicleOccurences[entry.VehicleID].Add(i);
                }
            }
            await Calculate();
        }
        async Task Calculate()
        {
            //List<Task> tasks = new(VehicleOccurences.Count);
            //int i = 0;
            foreach (KeyValuePair<string, List<int>> keyValuePair in VehicleOccurences)
            {
                Console.WriteLine(keyValuePair.Key);
                // Vector2 Position = new();
                Vector2[] positions = Array.Empty<Vector2>();
                string VehicleID = keyValuePair.Key;
                if (keyValuePair.Value.Count == 2)
                {
                    Entry[] tmp = keyValuePair.Value.Select(index => entries[index]).ToArray();
                    var a = tmp.Select(i => i.LamppostID).ToArray();
                    foreach (var t in a) Console.WriteLine(t);
                    PosStreetSet set = await MiscHelper.GetCoordinates(a);
                    Street = set.street;
                    float[] dists = tmp.Select(i => i.distance).ToArray();
                    positions = MiscHelper.Duolaterate(set.posSet[tmp[0].LamppostID], set.posSet[tmp[1].LamppostID], dists[0], dists[1]);
                    // Position = await MiscHelper.InBounds(Street, positions);
                }
                else if (keyValuePair.Value.Count > 3)
                {
                    Entry[] tmp = keyValuePair.Value.Select(index => entries[index]).ToArray();
                    Array.Sort(tmp, (x, y) => x.distance.CompareTo(y.distance));
                    //3 closest ones should be first 3
                    tmp = tmp[..3];
                    //foreach (var t in tmp) Console.WriteLine(t);
                }
                else return;
                // if (!Position.Equals(new()))
                AddEntry(positions, Street, VehicleID);
            }
        }
        public async Task AddEntry(string X, string Y, string Street = "SampleStreet", string VehicleID = "00000000")
        {
            returnValue = await Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.vehiclelog (VehicleInfo_VehicleID, GridX, GridY, Location) VALUES ('{VehicleID}', '{X}', '{Y}', '{Street}')"));
            if(returnValue < 0) return;
            SQLResult result = Task.Run(() => Program.sqlConnector.ExeQ($"SELECT Value FROM logcache WHERE VehicleInfo_VehicleID = {VehicleID}")).Result;
            if (result.valid && result.reader.Read())
            {
                //returnValue = await Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.VehicleWarning (VehicleInfo_VehicleID, Reason) VALUES ('{VehicleID}', 'AUTOMATIC')"));
            }
        }
        async Task AddEntry(Vector2[] positions, string Street = "SampleStreet", string VehicleID = "00000000"){
            string tmp = "";
            foreach(var position in positions){
                tmp += $"('{VehicleID}', '{position.X:N8}', '{position.Y:N8}', '{Street}'),";
            }
            tmp = tmp.Remove(tmp.Length - 1);
            tmp += ';';
            returnValue = await Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.vehiclelog (VehicleInfo_VehicleID, GridX, GridY, Location) VALUES {tmp}"));
        }
    }
    struct Entry
    {
        public string LamppostID;
        public string VehicleID;
        public float distance;
    }


    /*        static public VehicleEntry newEntry(string Entry){
 // [lamppostid;vehicleid;distance],[lamppostid;vehicleid;distance],[lamppostid;vehicleid;distance]       
            if(!Entry.StartsWith("[") || Entry.EndsWith("]")) return null;
            string[] tmp = Entry[1..^1].Split(';');
            string tmpm = "Hoi Yuen Road";
            return new VehicleEntry(tmp[2][0], tmp[3][0], tmpm, tmp[1]);
        }*/
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server.src.SQLIF
{
    internal class SQLConnectSample
    {
        public void AddEntry(char X, char Y, string Street = "SampleStreet", string VehicleID = "00000000", bool Debug = true)
        {
            int A = Task.Run(() => Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.VehicleLog (VehicleInfo_VehicleID, GridX, GridY, Location) VALUES ('{VehicleID}', '{X}', '{Y}', '{Street}')"))).Result;
            SQLResult result = Task.Run(() => Program.sqlConnector.ExeQ($"SELECT Value FROM LogCache WHERE VehicleInfo_VehicleID = {VehicleID}")).Result;
            if (result.valid && result.reader.Read())
            {
                if(Debug)
                    Console.WriteLine((int)result.reader[0]);
                int B = Task.Run(() => Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.VehicleWarning (VehicleInfo_VehicleID, Reason) VALUES ('{VehicleID}', 'AUTOMATIC')"))).Result;
            }
        }
    }
}

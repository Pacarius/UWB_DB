using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
namespace Server.src.SQLIF
{
    internal class VehicleEntry
    {
        char X = ' ', Y = ' ';
        string Street = "", VehicleID = "00000000";
        public bool Valid => VehicleID.Length > 0 || VehicleID.Length <= 8;
        static public bool Production = true;
        public int code = -1;
        public VehicleEntry(char X, char Y, string Street, string VehicleID){
            this.X = X;
            this.Y = Y;
            this.Street = Street;
            this.VehicleID = VehicleID;
        }
        public async Task AddEntry(){
            code = await Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.VehicleLog (VehicleInfo_VehicleID, GridX, GridY, Location) VALUES ('{VehicleID}', '{X}', '{Y}', '{Street}')"));
            SQLResult result = await Program.sqlConnector.ExeQ($"SELECT Value FROM LogCache WHERE VehicleInfo_VehicleID = {VehicleID}");
            if (result.valid && result.reader.Read())
            {
                if ((int)result.reader[0] > 3)
                {
                    code = await Program.sqlConnector.ExeNonQ(new($"INSERT INTO mydb.VehicleWarning (VehicleInfo_VehicleID, Reason) VALUES ('{VehicleID}', 'AUTOMATIC')"));
                    Violation();
                }
            }
            return;
        }
        void Violation()
        {
            Console.WriteLine(":Big Chungus");
        }
    }
}
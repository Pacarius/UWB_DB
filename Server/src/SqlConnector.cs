using System;
using System.Data;
using System.Numerics;
using System.Windows;
using System.Reflection.PortableExecutable;
using Azure;
using MySqlConnector;
using Windows.Foundation;

internal class SqlConnector
{
    readonly string connectionString;
    public readonly bool mode;
    internal SqlConnector(string IP = "db", ushort port = 6969, bool SqlMode = true)
    {
        MySqlConnectionStringBuilder builder = new()
        {
            Port = port,
            Server = IP,
            UserID = "root",
            Password = "vtccenter",
            Database = "mydb",
            AllowPublicKeyRetrieval = true
        };
        connectionString = builder.ToString();
        mode = SqlMode;
    }
    internal SqlConnector(bool Pipe)
    {
        MySqlConnectionStringBuilder builder = new()
        {
            ConnectionProtocol = MySqlConnectionProtocol.NamedPipe,
            PipeName = "MySQL",
            Server = "localhost",
            UserID = "server",
            Password = "vtccenter",
            Database = "mydb",
            AllowPublicKeyRetrieval = true
        };
        connectionString = builder.ToString();
    }
    //internal LongLat readLonLat(string LamppostID)
    //{
    //    MySqlConnection conn = new(connectionString);
    //    conn.Open();
    //    string cmd = $"SELECT Longitude, Latitude FROM mydb.lamppostinfo WHERE ID='{LamppostID}'";
    //    MySqlCommand command = new(cmd, conn);
    //    MySqlDataReader reader = command.ExecuteReader();
    //    while(reader.Read())
    //    {
    //        LongLat lon = new LongLat((decimal)reader[0], (decimal)reader[1]);
    //        return lon;
    //    }
    //    return new();
    //}
    internal async Task<int> ExeNonQ(MySqlCommand cmd)
    {
        if (mode)
            try
            {
                MySqlConnection conn = new(connectionString);
                conn.Open();
                cmd.Connection = conn;
                int tmp = await cmd.ExecuteNonQueryAsync();
                await conn.DisposeAsync();
                return tmp;
            }
            catch (Exception ex) { Console.WriteLine(ex); return -1; }
        else { 
            Console.WriteLine(cmd.CommandText);
            return -1;
        }
    }
    internal async Task<SQLResult> ExeQ(string Cmd)
    {
        if (mode)
        try
        {
            MySqlConnection conn = new(connectionString);
            conn.Open();
            MySqlCommand cmd = new(Cmd, conn);
            MySqlDataReader tmp = await cmd.ExecuteReaderAsync();
            return new SQLResult(true, tmp, conn);
        }
        catch (Exception ex) { Console.WriteLine(ex); return new SQLResult(false); }
        else
        {
            Console.WriteLine(Cmd);
            return new SQLResult(false);
        }
    }
}
public struct SQLResult
{
    public readonly bool valid;
    public readonly MySqlDataReader? reader;
    public MySqlConnection conn;
    public SQLResult(bool valid, MySqlDataReader reader, MySqlConnection connection)
    {
        this.valid = valid;
        this.reader = reader;
        this.conn = connection;
    }
    public SQLResult(bool valid)
    {
        this.valid = valid;
        this.reader = null;
    }
}
//public struct LongLat
//{
//    public bool valid;
//    public decimal Long;
//    public decimal Lat;
//    public LongLat(decimal a, decimal b)
//    {
//        valid = true;
//        Long = a;
//        Lat = b;
//    }
//    public LongLat()
//    {
//        valid = false;
//        Long = 0;
//        Lat = 0;
//    }
//}


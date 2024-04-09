using Microsoft.AspNetCore.Localization;
using MySqlConnector;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;

namespace Server.src
{
    internal class MiscHelper
    {
        public static Vector2[] Duolaterate(Vector2 pos1, Vector2 pos2, float dist1, float dist2)
        {
            float distBetweenPoints = Vector2.Distance(pos1, pos2);
            float a = (dist1 * dist1 - dist2 * dist2 + distBetweenPoints * distBetweenPoints) / (2 * distBetweenPoints);
            Console.WriteLine($"{pos1} : {dist1} ; {pos2} : {dist2}");
            //add doesnt fucking intersect check
            float tmp = dist1 * dist1 - a * a;
            if (tmp < 0) {
                Console.WriteLine("Radii do not intersect.");
                return Array.Empty<Vector2>();
                    };
            float h = (float)Math.Sqrt(dist1 * dist1 - a * a);
            Vector2 P = pos1 + a * (pos2 - pos1) / distBetweenPoints;
            Vector2 pos3a = new(P.X + h * (pos2.Y - pos1.Y) / distBetweenPoints, P.Y - h * (pos2.X - pos1.X) / distBetweenPoints);
            Vector2 pos3b = new(P.X - h * (pos2.Y - pos1.Y) / distBetweenPoints, P.Y + h * (pos2.X - pos1.X) / distBetweenPoints);
            return new Vector2[] { pos3a, pos3b };
        }
        public static Vector2 Trilaterate(Vector2 pos1, Vector2 pos2, Vector2 pos3, float dist1, float dist2, float dist3)
        {
            Vector2 ex = Vector2.Normalize(pos1 - pos3);
            float i = Vector2.Dot(ex, pos2 - pos3);
            Vector2 ey = Vector2.Normalize(pos2 - pos3 - i * ex);
            float d = Vector2.Distance(pos1, pos3);
            float j = Vector2.Dot(ey, pos2 - pos3);

            float x = (dist1 * dist1 - dist2 * dist2 + d * d) / (2 * d);
            float y = (dist1 * dist1 - dist3 * dist3 + i * i + j * j) / (2 * j) - (i / j) * x;

            Vector2 triPos = pos3 + x * ex + y * ey;
            return triPos;
        }
        public static async Task<PosStreetSet> GetCoordinates(string[] LamppostID)
        {
            if (!Program.sqlConnector.mode)
            {
                PosStreetSet streetSet = new()
                {
                    street = "TEST",
                    posSet = new() {
                        { LamppostID[0], new Vector2(10, 17) },
                        { LamppostID[1], new Vector2(46, 17) }
                    }
                };
                return streetSet;
            }
            PosStreetSet set = new(LamppostID.Length);
            foreach(string id in LamppostID)
            {
                SQLResult result = await Program.sqlConnector.ExeQ($"SELECT GridX, GridY, Street_Location FROM mydb.lamppostinfo WHERE LamppostID='{id}'");
                if(!result.valid) return new()
                {
                    street = "TEST",
                    posSet = new() {
                        { LamppostID[0], new Vector2(10, 17) },
                        { LamppostID[1], new Vector2(46, 17) }
                    }
                };
                Vector2 pos = new();
                if (!result.valid || result.reader == null) return new();
                while (result.reader.Read())
                {
                    for (int j = 0; j < result.reader.FieldCount; j++)
                    {
                        if (j == 0) pos.X = (int)result.reader[j];
                        else if (j == 1) pos.Y = (int)result.reader[j];
                        else set.street ??= (string)result.reader[j];
                    }
                }
                await result.conn.DisposeAsync();
                set.posSet.Add(id, pos);
            }
            //for (int i = 0; i < LamppostID.Length; i++)
            //{
            //    SQLResult result = await Program.sqlConnector.ExeQ($"SELECT GridX, GridY, Street_Location FROM mydb.lamppostinfo WHERE LamppostID='{LamppostID[i]}'");
            //    if (!result.valid || result.reader == null) return new();
            //    while (result.reader.Read())
            //    {
            //        for (int j = 0; j < result.reader.FieldCount; j++)
            //        {
            //            if (j == 0) set.pos[i].X = (int)result.reader[j];
            //            else if (j == 1) set.pos[i].Y = (int)result.reader[j];
            //            set.street ??= (string)result.reader[j];
            //        }
            //    }
            //}
            return set;
        }
        struct Bounds
        {
            public int StartX;
            public int StartY;
            public int EndX;
            public int EndY;
        }
        public static async Task<Vector2> InBounds(string Street, Vector2[] inputs)
        {
            Rect[] bounds = new Rect[2];
            SQLResult result = await Program.sqlConnector.ExeQ($"SELECT StartX, StartY, EndX, EndY FROM mydb.streetbounds WHERE Street_Location='{Street}'");
            List<int> parsedInts = new();
            List<Vector2> validResults = new();

                if (Program.sqlConnector.mode)
                {
                    List<Bounds> boundsList = new();
                    while (await result.reader.ReadAsync())
                    {
                        for (int i = 0; i < result.reader.FieldCount; i++)
                        {
                            parsedInts.Add((int)result.reader[i]);
                        }
                    }
                    await result.conn.DisposeAsync();
                    if (parsedInts.Count % 4 != 0) return new();
                    bounds = new Rect[parsedInts.Count / 4];
                    for (int i = 0; i < bounds.Length; i++)
                    {
                        int tmp = i * 4;
                        bounds[i] = new Rect(new Vector2(parsedInts[tmp], parsedInts[tmp + 1]), new Vector2(parsedInts[tmp + 2], parsedInts[tmp + 3]));
                    }
                }
                else
                    bounds = new Rect[2]{ new Rect(new Vector2(6, 14), new Vector2(47, 15)), new Rect(new Vector2(12, 17), new Vector2(41, 13)) };
                for (int i = 0; i < inputs.Length; i++)
                {
                    for (int j = 0; j < bounds.Length; j++)
                    {
                        if (bounds[j].Contains(new(inputs[i].X, inputs[i].Y)))
                            if (!validResults.Contains(inputs[i])) validResults.Add(inputs[i]);
                            else continue;
                        else Console.WriteLine($"Not in bounds: {inputs[i].X}, {inputs[i].Y}");
                    }
                }
            if(validResults.Count == 1) return validResults[0];
            else return new();
        }
    }
    public struct PosStreetSet
    {
        public Dictionary<string, Vector2> posSet;
        public string street;
        public PosStreetSet(int size)
        {
            posSet = new(size);
            street = null;
        }
    }
    public class Rect{
        private readonly float XMin;
        private readonly float YMin;
        public readonly float Width;
        public readonly float Height;
        public Rect(Vector2 PointA, Vector2 PointB){
            XMin = Math.Min(PointA.X, PointB.X);
            YMin = Math.Min(PointA.Y, PointB.Y);
            Width = Math.Max(Math.Max(PointA.X, PointB.X) - XMin, 0);
            Height = Math.Max(Math.Max(PointA.Y, PointB.Y) - YMin, 0);
        }
        private float XMax => XMin + Width;
        float YMax => YMin + Height;
        public bool Contains(Vector2 target){
            return (target.X >= XMin) && (target.X < XMax) && (target.Y >= YMin) && (target.Y < YMax);
        }
    }
}

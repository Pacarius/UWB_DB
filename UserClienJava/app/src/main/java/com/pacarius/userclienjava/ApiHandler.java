package com.pacarius.userclienjava;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiHandler {
    private List<Coordinates> lampposts;
    private List<Coordinates> vehicles;
    private String ip = "10.0.2.2";
    private Runnable OnApiFire = () -> {};
    public ApiHandler(List<Coordinates> lampposts, List<Coordinates> vehicles) {
        this.lampposts = lampposts;
        this.vehicles = vehicles;
        getLampposts();
        getVehicles();
    }
    private final List<Thread> threads = new ArrayList<>();
    public void getVehicles() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    URL url = new URL("http://" + ip + ":9696/vehicles");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    Log.d("ApiHandlerVehicle", sb.toString());
                    parseVehicles(sb.toString());
                    con.disconnect();
                    OnApiFire.run();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        });
        threads.add(thread);
        thread.start();
    }
    public void getLampposts() {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("http://" + ip + ":9696/lampposts");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                Log.d("ApiHandlerVehicle", sb.toString());
                parseVehicles(sb.toString());
                Log.d("ApiHandlerLamppost", sb.toString());
                parseLampposts(sb.toString());
                con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        threads.add(thread);
        thread.start();
    }
    private void parseLampposts(String response) throws JSONException {
        JSONArray parsedRes = new JSONArray(response);
        for (int i = 0; i < parsedRes.length(); i++) {
            JSONObject obj = parsedRes.getJSONObject(i);
            String type = obj.getString("type");
            int x = obj.getInt("x");
            int y = obj.getInt("y");
            String id = obj.getString("id");
            lampposts.add(new Coordinates(CoordType.valueOf(type), x, y, id));
        }
    }

    private void parseVehicles(String response) throws JSONException {
        vehicles.clear();
        JSONArray parsedRes = new JSONArray(response);
        for (int i = 0; i < parsedRes.length(); i++) {
            JSONObject obj = parsedRes.getJSONObject(i);
            String type = obj.getString("type");
            int x = obj.getInt("x");
            int y = obj.getInt("y");
            String id = obj.getString("id");
            vehicles.add(new Coordinates(CoordType.valueOf(type), x, y, id));
        }
    }

    public void setOnApiFire(Runnable runnable) {
        this.OnApiFire = runnable;
    }

    public void setIp(String newIp) {
        this.ip = newIp;
    }
    public void postNewBooking(String sttName, String Coordinates, String Date, String Time, String Reason){
        new Thread(() -> {
            try {
                URL url = new URL("http://" + ip + ":9696/booking");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.getOutputStream().write(String.format("{\"streetName\":\"%s\",\"coordinates\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"reason\":\"%s\"}", sttName, Coordinates, Date, Time, Reason).getBytes());
                con.getInputStream();
                con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

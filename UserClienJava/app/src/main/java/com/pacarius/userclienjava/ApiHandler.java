package com.pacarius.userclienjava;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiHandler {
    private List<Coordinates> lampposts;
    private List<Coordinates> vehicles;
    private String ip = "192.168.196.240";
    private Runnable OnApiFire = () -> {};
    public ApiHandler(List<Coordinates> lampposts, List<Coordinates> vehicles) {
        this.lampposts = lampposts;
        this.vehicles = vehicles;
        getLampposts();
        getVehicles();
    }
    public void getVehicles() {
        new Thread(() -> {
            while (true) {
                try {
                    URL url = new URL("http://" + ip + ":9696/vehicles");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String response = reader.readLine();
                    parseVehicles(response);
                    con.disconnect();
                    OnApiFire.run();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void getLampposts() {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + ip + ":9696/lampposts");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = reader.readLine();
                parseLampposts(response);
                con.disconnect();
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }).start();
    }
    private void parseLampposts(String response) {
        String tmp = response.replace("[", "").replace("]", "");
        String[] items = tmp.split("\\},");
        List<Coordinates> a = new ArrayList<>();
        for (String item : items) {
            String cleanedItem = item.replace("{", "").replace("}", "").replace("\n", "");
            String[] parts = cleanedItem.split(",");
            String type = parts[0].split(":")[1].replace("\"", "").trim();
            int x = Math.round(Float.parseFloat(parts[1].split(":")[1].replace("\"", "")));
            int y = Math.round(Float.parseFloat(parts[2].split(":")[1].replace("\"", "")));
            String id = parts[3].split(":")[1].replace("\"", "");
            a.add(new Coordinates(CoordType.valueOf(type), x, y, id));
        }
        lampposts.addAll(a);
    }

    private void parseVehicles(String response) {
        String tmp = response.replace("[", "").replace("]", "");
        String[] items = tmp.split("\\},");
        vehicles.clear();
        for (String item : items) {
            String cleanedItem = item.replace("{", "").replace("}", "").replace("\n", "");
            String[] parts = cleanedItem.split(",");
            String type = parts[0].split(":")[1].replace("\"", "").trim();
            int x = Math.round(Float.parseFloat(parts[1].split(":")[1].replace("\"", "")));
            int y = Math.round(Float.parseFloat(parts[2].split(":")[1].replace("\"", "")));
            String id = parts[3].split(":")[1].replace("\"", "");
            vehicles.add(new Coordinates(CoordType.valueOf(type), x, y, id));
        }
    }

    public void setOnApiFire(Runnable runnable) {
        this.OnApiFire = runnable;
    }

    public void setIp(String newIp) {
        this.ip = newIp;
    }
}

package com.example.userclientjava;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ApiHandler {
    List<Coordinates> vehicles;
    List<Coordinates> lampposts;

    public ApiHandler(List<Coordinates> vehicles, List<Coordinates> lampposts) throws MalformedURLException {
        this.vehicles = vehicles;
        this.lampposts = lampposts;
        new Thread(new getVehicles()).start();
        new Thread(new getLampposts()).start();
    }

    List<ApiOnFire> onfireEvent;

    public void addListener(ApiOnFire listener) {
        onfireEvent.add(listener);
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    String ip = "127.0.0.1";
    String baseUrl = "http://" + ip + ":9696";

    static boolean isActive = false;

    class getVehicles implements Runnable {
        URL url = new URL(baseUrl + "/vehicles");

        getVehicles() throws MalformedURLException {
        }

        @Override
        public void run() {
            while (isActive)
                try {
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    InputStream in = con.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    char[] buffer = new char[1024];
                    int len = reader.read(buffer);
                    parseVehicles(Arrays.toString(buffer));
                    con.disconnect();
                    for (ApiOnFire i : onfireEvent) i.OnFire();
                    sleep(1000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    class getLampposts implements Runnable {
        URL url = new URL(baseUrl + "/lampposts");

        getLampposts() throws MalformedURLException {
        }

        @Override
        public void run() {
            while (isActive)
                try {
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    InputStream in = con.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    char[] buffer = new char[1024];
                    int len = reader.read(buffer);
                    parseLampposts(Arrays.toString(buffer));
                    con.disconnect();
                    for (ApiOnFire i : onfireEvent) i.OnFire();
                    sleep(1000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    private void parseVehicles(String response) {
        String[] tmp = response.replace("[", "").replace("]", "").split("},");
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = tmp[i].replace("{", "").replace("}", "").replace("\n", "");
        }
        vehicles.clear();
        for (String s : tmp) {
            String[] parts = s.split(",");
            String type = parts[0].split(":")[1].replace("\"", "").replace(" ", "");
            int x = Math.round(Float.parseFloat(parts[1].split(":")[1].replace("\"", "")));
            int y = Math.round(Float.parseFloat(parts[2].split(":")[1].replace("\"", "")));
            String id = parts[3].split(":")[1].replace("\"", "");
            vehicles.add(new Coordinates(CordType.valueOf(type), x, y, id));
        }
    }

    private void parseLampposts(String response) {
        String[] tmp = response.replace("[", "").replace("]", "").split("},");
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = tmp[i].replace("{", "").replace("}", "").replace("\n", "");
        }
        lampposts.clear();
        for (String s : tmp) {
            String[] parts = s.split(",");
            String type = parts[0].split(":")[1].replace("\"", "").replace(" ", "");
            int x = Math.round(Float.parseFloat(parts[1].split(":")[1].replace("\"", "")));
            int y = Math.round(Float.parseFloat(parts[2].split(":")[1].replace("\"", "")));
            String id = parts[3].split(":")[1].replace("\"", "");
            lampposts.add(new Coordinates(CordType.valueOf(type), x, y, id));
        }
    }
}


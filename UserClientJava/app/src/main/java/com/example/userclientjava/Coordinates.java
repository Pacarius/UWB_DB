package com.example.userclientjava;

public class Coordinates {
    public CordType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    CordType type;
    int x;
    int y;
    String id;
    public Coordinates(CordType type, int x, int y, String id){
        this.type = type;
        this.x = x;
        this.y = y;
        this.id = id;
    }

}
enum CordType {
    Lamppost,
    Vehicle,
    Empty;
}

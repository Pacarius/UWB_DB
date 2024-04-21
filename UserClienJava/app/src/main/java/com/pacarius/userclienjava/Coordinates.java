package com.pacarius.userclienjava;

public class Coordinates {
    private CoordType type;
    private final int x;
    private final int y;
    private final String id;

    public Coordinates(CoordType type, int x, int y, String id) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public CoordType getType() {
        return type;
    }

    public void setType(CoordType type) {
        this.type = type;
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
}


package com.pacarius.client

data class Coordinates(var type : CoordType, val x: Int, val y : Int, val id : String){}
enum class CoordType{
    Lamppost,
    Vehicle,
    Empty;
}

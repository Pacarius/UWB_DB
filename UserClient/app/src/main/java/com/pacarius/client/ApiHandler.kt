package com.pacarius.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class ApiHandler(val lampposts: MutableList<Coordinates>, val vehicles: MutableList<Coordinates>, ) {
    init {
//        getLocations()
        getLampposts()
        getVehicles()
    }
    public var OnApiFire : () -> Unit = {}
    public var ip = "192.168.196.240"
    fun getVehicles() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val url = URL("http://$ip:9696/vehicles")
                try {
                    val con = url.openConnection() as HttpURLConnection
                    val inputStream = con.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.readText()
                    parseVehicles(response)
                    con.disconnect()
                } catch (e: Exception) {
                    delay(1000)
                }
                OnApiFire()
                delay(1000)
            }
        }
    }
    private fun getLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val url = URL("http://$ip:9696/vehicles")
                    val con = url.openConnection() as HttpURLConnection
                    try {
                        val inputStream = con.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = reader.readText()
                        parseVehicles(response)
                    } finally {
                        con.disconnect()
                    }
                    val url2 = URL("http://$ip:9696/lampposts")
                    val con2 = url2.openConnection() as HttpURLConnection
                    try {
                        val inputStream = con2.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = reader.readText()
                        parseLampposts(response)
                    } finally {
                        con2.disconnect()
                    }
                    OnApiFire()
                    delay(1000)
                } catch (e: Exception) {
                    delay(1000)
                }

            }
        }
    }
    fun getLampposts() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("http://$ip:9696/lampposts")
            try {
                val con = url.openConnection() as HttpURLConnection
                val inputStream = con.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                parseLampposts(response)
                con.disconnect()
            } catch (e: Exception) {
                delay(1000)
            }

        }
    }

    private fun parseLampposts(response: String) {
        val tmp = response.removeSurrounding("[", "]").split("},")
            .map {
                it.replace("{", "").replace("}", "")
                    .replace("\n", "")
            }
        val a = mutableListOf<Coordinates>()
        val tmp2 = tmp.map { it.split(",") }
        for (i in tmp2) {
            val type = i[0].split(":")[1].replace("\"", "").replace(" ", "")
            val x = i[1].split(":")[1].replace("\"", "").toFloat().roundToInt()
            val y = i[2].split(":")[1].replace("\"", "").toFloat().roundToInt()
            val id = i[3].split(":")[1].replace("\"", "")
            a.add(Coordinates(CoordType.valueOf(type), x, y, id))
        }
        lampposts.addAll(a)
    }
    private fun parseVehicles(response: String){
        val tmp = response.removeSurrounding("[", "]").split("},")
            .map {
                it.replace("{", "").replace("}", "")
                    .replace("\n", "")
            }
        vehicles.clear()
        val tmp2 = tmp.map { it.split(",") }
        for (i in tmp2) {
            val type = i[0].split(":")[1].replace("\"", "").replace(" ", "")
            val x = i[1].split(":")[1].replace("\"", "").toFloat().roundToInt()
            val y = i[2].split(":")[1].replace("\"", "").toFloat().roundToInt()
            val id = i[3].split(":")[1].replace("\"", "")
            vehicles.add(Coordinates(CoordType.valueOf(type), x, y, id))
        }
    }
}
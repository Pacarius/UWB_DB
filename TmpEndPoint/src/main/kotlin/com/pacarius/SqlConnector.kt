package com.pacarius

import com.pacarius.models.CoordType
import com.pacarius.models.Coordinates
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class SqlConnector(private val lampposts: MutableList<Coordinates>, private val vehicles: MutableList<Coordinates>) {
    private var job = SupervisorJob()
    private lateinit var scope: CoroutineScope
    private lateinit var exceptionHandler: CoroutineExceptionHandler

    init {
        exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception in SqlConnector")
            // Cancel the existing job
            job.cancel()
            // Create a new job
            job = SupervisorJob()
            // Update the scope with the new job
            scope = CoroutineScope(Dispatchers.IO + job + exceptionHandler)
            // Relaunch the coroutine
            scope.launch {
                delay(2000) // delay for 1 second
                scope.launchSqlConnector()
            }
        }
        scope = CoroutineScope(Dispatchers.IO + job + exceptionHandler)
        scope.launchSqlConnector()
    }

    private fun CoroutineScope.launchSqlConnector() = launch {
        val conn: Connection? = withContext(Dispatchers.IO) { DriverManager.getConnection("jdbc:mysql://db:6969/mydb?permitMysqlScheme&user=root&password=vtccenter&allowPublicKeyRetrieval=true") }
        val st: Statement? = conn?.createStatement()
        val rs: ResultSet = st!!.executeQuery("Select * from mydb.lamppostinfo")
        while (rs.next()) {
            lampposts.add(Coordinates(CoordType.Lamppost, rs.getFloat("GridX"), rs.getFloat("GridY"), rs.getString("LamppostID")))
        }
        launch {
            while (isActive) {
                val rs: ResultSet = st.executeQuery("SELECT t1.* FROM vehiclelog t1 WHERE t1.LogUUID = (SELECT t2.LogUUID FROM vehiclelog t2 WHERE t2.VehicleInfo_VehicleID = t1.VehicleInfo_VehicleID ORDER BY t2.LogUUID DESC LIMIT 1)")
                vehicles.removeIf { it.type == CoordType.Vehicle }
                while (rs.next()) {
                    val x = rs.getFloat("GridX")
                    val y = rs.getFloat("GridY")
                    vehicles.add(Coordinates(CoordType.Vehicle, x, y, rs.getString("VehicleInfo_VehicleID")))
                }
                delay(1000) // delay for 1 second before the next update
            }
        }
    }

    fun stop() {
        job.cancel()
    }
}
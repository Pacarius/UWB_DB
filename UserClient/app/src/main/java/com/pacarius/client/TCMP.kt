package com.pacarius.client

import android.util.Log
import com.marsounjan.icmp4a.Icmp
import com.marsounjan.icmp4a.Icmp4a
import kotlinx.coroutines.runBlocking

class TCMP {
    init {
        runBlocking {
            val icmp = Icmp4a()
            try {
                val status = icmp.ping(host = "10.0.2.2")
                val result = status.result
                when (result) {
                    is Icmp.PingResult.Success -> Log.d(
                        "ICMP",
                        "(${status.ip.hostAddress}) ${result.packetSize} bytes - ${result.ms} ms"
                    )

                    is Icmp.PingResult.Failed -> Log.d(
                        "ICMP",
                        "(${status.ip.hostAddress}) Failed: ${result.message}"
                    )
                }
            } catch (error: Icmp.Error.UnknownHost) {
                Log.d("ICMP", "Unknown host")
            }
        }

    }
}
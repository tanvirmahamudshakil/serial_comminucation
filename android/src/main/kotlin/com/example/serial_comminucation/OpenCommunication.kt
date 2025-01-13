/*
 * OpenCommunication.java
 * Created by: Mahad Asghar on 12/08/2022.
 *
 *  Copyright © 2022 BjsSoftSolution. All rights reserved.
 */
package com.example.serial_comminucation

import android.text.TextUtils
import android.util.Log
import cn.lalaki.SerialPort
import java.io.File
import com.google.common.hash.HashCode
import io.flutter.plugin.common.EventChannel


class OpenCommunication {
    var communication: OpenCommunication = OpenCommunication()
    private var serialPort: SerialPort? = null


    fun getSerialList() : List<String>? {
       return File("/dev/").listFiles { _, s -> s.contains("ttys", ignoreCase = true) }
            ?.sortedBy { it.name }?.map { it.absolutePath }
    }

    fun open(name: String,isAscii: Boolean, baudRate: Int) {
        if(serialPort != null) {
            serialPort?.close()
            serialPort = null
        }
        serialPort = SerialPort(name, baudRate, object : SerialPort.DataCallback {
            override fun onData(data: ByteArray) {
                val hexStr = HashCode.fromBytes(data).toString()
                CustomEventHandler.sendEvent(hexStr + "\n")
            }
        })
    }

    fun close() {
        serialPort?.close()
        serialPort = null
    }
}
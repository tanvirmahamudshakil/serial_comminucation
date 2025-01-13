/*
 * OpenCommunication.java
 * Created by: Mahad Asghar on 12/08/2022.
 *
 *  Copyright © 2022 BjsSoftSolution. All rights reserved.
 */
package com.example.serial_comminucation

import android.text.TextUtils
import android.util.Log
import android_serialport_api.SerialPortFinder
import android_serialport_api.port.BaseReader
import android_serialport_api.port.SerialApiManager



class OpenCommunication {
    private var spManager: SerialApiManager? = null
    private var baseReader: BaseReader? = null
    private var currentPort: String? = null
    var logChannel: String = ""
    var readChannel: String = ""

    var entries: List<String> = ArrayList()
    var entryValues: List<String> = ArrayList()

    fun destroyResources() {
        spManager!!.destroy()
    }

    fun initData() {
        spManager = SerialApiManager.getInstances().setLogInterceptor { type, port, isAscii, log ->
            Log.d(
                "SerialPortLog", StringBuffer()
                    .append("Serial Port ：").append(port)
                    .append("\ndata format ：").append(if (isAscii) "ascii" else "hexString")
                    .append("\ntype：").append(type)
                    .append("messages：").append(log).toString()
            )
            logChannel += "\n" + (StringBuffer()
                .append(" ").append(port)
                .append(" ").append(if (isAscii) "ascii" else "hexString")
                .append(" ").append(type)
                .append("：").append(log)
                .append("\n").toString())
            CustomEventHandler.sendEvent(
                mapOf(
                    "LogChannel" to logChannel,

                    "readChannel" to readChannel,

                )
            )
        }
        baseReader = object : BaseReader() {
            override fun onParse(port: String, isAscii: Boolean, read: String) {
                Log.d(
                    "SerialPortRead", StringBuffer()
                        .append(port).append("/").append(if (isAscii) "ascii" else "hex")
                        .append(" read：").append(read).append("\n").toString()
                )
                readChannel += "\n" + (StringBuffer()
                    .append(port).append("/").append(if (isAscii) "ascii" else "hex")
                    .append(" read：").append(read).append("\n").toString())
                CustomEventHandler.sendEvent(
                    mapOf(
                        "LogChannel" to logChannel,

                        "readChannel" to readChannel,

                    )
                )
            }
        }
    }

    fun sendDeviceData(): List<String> {
        val mSerialPortFinder = SerialPortFinder()
        entries = mSerialPortFinder.allDevices.toList()
        entryValues = mSerialPortFinder.allDevicesPath.toList()
        return entryValues
    }


    fun open(name: String, isAscii: Boolean, baudRate: Int) {
        initData()
        var checkPort = name
        if (TextUtils.isEmpty(checkPort)) {
            return
        } else if (TextUtils.equals(checkPort, "other")) {
            checkPort = name
            if (TextUtils.isEmpty(checkPort)) {
                return
            }
        }

        if (TextUtils.equals(currentPort, checkPort)) {
            return
        }

        if (!TextUtils.isEmpty(currentPort)) {
            // Close the CurrentPort serial port
            spManager!!.stopSerialPort(currentPort)
        }

        if (entryValues.contains(checkPort)) {
            currentPort = checkPort
            spManager!!.startSerialPort(checkPort, isAscii, baseReader, baudRate)
            changeCode(isAscii)
        }
    }


    fun close() {
        if (!TextUtils.isEmpty(currentPort)) {
            // currentPort
            spManager!!.stopSerialPort(currentPort)
            currentPort = ""
        }
    }

    fun send(sendCommand: String?) {
        if (TextUtils.isEmpty(currentPort)) {
            return
        }

        if (TextUtils.isEmpty(sendCommand)) {
            return
        }
        // send data
        spManager!!.send(currentPort, sendCommand)
    }

    private fun changeCode(isAscii: Boolean) {
        if (TextUtils.isEmpty(currentPort)) {
            return
        }
        spManager!!.setReadCode(currentPort, isAscii)
    }
}
package com.example.serial_comminucation

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** SerialComminucationPlugin */
class SerialComminucationPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private var methodChannel: MethodChannel? = null
  private var eventChannel: EventChannel? = null
  var communication: OpenCommunication = OpenCommunication()
  private var receiver: CustomEventHandler? = null

  private val methodChannelName = "dev.ak.flutter_serial/embedded_serial_method_channel"
  private val eventChannelName = "dev.ak.flutter_serial/embedded_serial_event_channel"

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    setupChannels(flutterPluginBinding.binaryMessenger, flutterPluginBinding.applicationContext);
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, methodChannelName)
    methodChannel?.setMethodCallHandler(this)
  }
  private fun setupChannels(messenger: BinaryMessenger, context: Context) {
    eventChannel = EventChannel(messenger, eventChannelName)
    receiver = CustomEventHandler()
    eventChannel?.setStreamHandler(receiver)
  }




  private fun teardownChannels() {
    methodChannel!!.setMethodCallHandler(null)
    eventChannel!!.setStreamHandler(null)
    receiver!!.onCancel(null)
    eventChannel = null
    receiver = null
  }


  override fun onMethodCall(call: MethodCall, result: Result) {
    val argments = (call.arguments() as Map<String, String>?)
    when (call.method) {
      "embeddedSerial/availablePorts" -> {
        val list = communication.getSerialList() ?: emptyList()
        result.success(list)
      }

      "embeddedSerial/open" -> communication.open(
        argments!!["serialPort"]!!, argments["dataFormat"].toBoolean(), argments["baudRate"]!!
          .toInt()
      )

      "embeddedSerial/close" -> communication.close()

      "embeddedSerial/destroy" -> communication.close()
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
    methodChannel!!.setMethodCallHandler(null)
    teardownChannels()
  }
}

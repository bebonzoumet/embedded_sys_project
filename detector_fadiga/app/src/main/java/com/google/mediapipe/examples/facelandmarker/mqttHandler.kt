package com.google.mediapipe.examples.facelandmarker

import android.util.Log
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback


class mqttHandler {
    var mqttClient: MqttAndroidClient? = null
    fun mqttConnect(applicationContext: Context, brokeraddr: String, clientuser: String, clientpwd: String): MqttAndroidClient {
        // ClientId is a unique id used to identify a client
        val clientId = MqttClient.generateClientId()


        // Create an MqttAndroidClient instance
        mqttClient = MqttAndroidClient(applicationContext, "tcp://$brokeraddr", clientId)


        // ConnectOption is used to specify username and password
        val connOptions = MqttConnectOptions()
        connOptions.userName = clientuser
        connOptions.password = clientpwd.toCharArray()
        return mqttClient as MqttAndroidClient
    }
    fun mqttSetReceiveListener() {
        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                // Connection Lost
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                // A message has been received
                val data = String(message.payload, charset("UTF-8"))
                // Place the message into a specific TextBox object
                editTextRcvMsg.editText!!.setText(data)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Delivery complete
            }
        })
    }
}
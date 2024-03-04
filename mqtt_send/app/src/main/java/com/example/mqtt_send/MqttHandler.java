package com.example.mqtt_send;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHandler {

    private MqttClient client;

    public void connect(String brokerURL, String clientId) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(brokerURL, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);
        } catch (MqttException e) {
            Log.e("MQTT", "Error connecting to MQTT broker: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        // Implement the logic to check the connection status
        return client != null && client.isConnected();
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                client.publish(topic, mqttMessage);
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Error publishing message: " + e.getMessage());
        }
    }

    public void subscribe(String topic, IMqttMessageListener listener) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic, listener);
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Error subscribing to topic: " + e.getMessage());
        }
    }
}

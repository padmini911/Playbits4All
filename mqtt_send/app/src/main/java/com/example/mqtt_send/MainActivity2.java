package com.example.mqtt_send;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity2 extends AppCompatActivity {



    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.textView);
        subscribeToTopic();
    }

    private void subscribeToTopic() {
        final String topic = "hhh";
        MqttHandler mqttHandler = MainActivity.getMqttHandler();
        if (mqttHandler != null) {
            mqttHandler.subscribe(topic, new MqttMessageListener());
        } else {
            Log.e(TAG + "MQTT_SEND", "MqttHandler is null");
        }
    }

    private class MqttMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            final String payload = new String(message.getPayload());
            Log.d(TAG + "MQTT_SEND", "Message received on topic: " + topic + " - " + payload);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Setting text to TextView: " + payload);
                    textView.setText(payload);
                }
            });
        }
    }




}

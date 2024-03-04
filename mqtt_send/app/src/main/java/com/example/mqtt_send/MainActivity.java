package com.example.mqtt_send;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {



    private static MqttHandler mqttHandler;

    private EditText edit1;
    private EditText edit2;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable currentTask;

    private boolean isButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit1 = findViewById(R.id.et1);
        edit2 = findViewById(R.id.et2);

        mqttHandler = new MqttHandler();

        findViewById(R.id.frontButton).setOnTouchListener(new ButtonTouchListener(frontTask));
        findViewById(R.id.backButton).setOnTouchListener(new ButtonTouchListener(backTask));
        findViewById(R.id.rightButton).setOnTouchListener(new ButtonTouchListener(rightTask));
        findViewById(R.id.leftButton).setOnTouchListener(new ButtonTouchListener(leftTask));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void onButtonPressed(Runnable task) {
        isButtonPressed = true;
        startTask(task);
    }

    private void onButtonReleased() {
        isButtonPressed = false;
        stopTask();
    }

    private void startTask(Runnable task) {
        if (currentTask == task) return;
        handler.removeCallbacksAndMessages(null);
        currentTask = task;
        handler.post(task);
    }

    private void stopTask() {
        handler.removeCallbacksAndMessages(null);
    }

    private final Runnable frontTask = new Runnable() {
        @Override
        public void run() {
            if (isButtonPressed) {
                connectAndPublish("hhh", "200,200");
                handler.postDelayed(this, 500);
            }
        }
    };

    private final Runnable backTask = new Runnable() {
        @Override
        public void run() {
            if (isButtonPressed) {
                connectAndPublish("hhh", "190,190");
                handler.postDelayed(this, 500);
            }
        }
    };

    private final Runnable rightTask = new Runnable() {
        @Override
        public void run() {
            if (isButtonPressed) {
                connectAndPublish("hhh", "200,-190");
                handler.postDelayed(this, 500);
            }
        }
    };

    private final Runnable leftTask = new Runnable() {
        @Override
        public void run() {
            if (isButtonPressed) {
                connectAndPublish("hhh", "-190,200");
                handler.postDelayed(this, 500);
            }
        }
    };

    private void connectAndPublish(String topic, String message) {
        String brokerUrl = "tcp://" + edit1.getText().toString() + ":1883";
        String clientId = edit2.getText().toString();

        mqttHandler.connect(brokerUrl, clientId);

        if (mqttHandler.isConnected()) {
            Log.d(TAG + ":MQTT_SEND", "MQTT connected successfully");
            publishMessage(topic, message);
        } else {
            showToast("Failed to connect to MQTT broker");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void publishMessage(String topic, String message) {
        try {
            mqttHandler.publish(topic, message);
            Log.d(TAG + ":MQTT_SEND", "Published Message: " + message);
        } catch (Exception e) {
            Log.e(TAG + ":MQTT_SEND", "Failed to publish message: " + e.getMessage());
        }
    }

    private class ButtonTouchListener implements View.OnTouchListener {
        private final Runnable task;

        public ButtonTouchListener(Runnable task) {
            this.task = task;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onButtonPressed(task);
                    break;
                case MotionEvent.ACTION_UP:
                    onButtonReleased();
                    break;
            }
            return true;
        }
    }

    public static MqttHandler getMqttHandler() {
        return mqttHandler;
    }
}

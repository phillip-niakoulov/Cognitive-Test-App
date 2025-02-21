package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.*;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MQTTServer implements Runnable {
    private MqttClient client;
    private boolean running = false;
    private final Main mainFrame;
    public String BROKER;
    public String TOPIC;
    public int messageCount;
    public File csvFile;

    public MQTTServer(Main mainFrame, String broker, String topic) {
        this.mainFrame = mainFrame;
        csvFile = new File("emotivData.csv");
//        if (csvFile.exists()) {
//            csvFile.delete();
//        }
        try {
            BROKER = broker;
            TOPIC = topic;
            client = new MqttClient(broker, MqttClient.generateClientId());
        } catch (MqttException e) {
            System.err.println("Error creating MQTT client: " + e.getMessage());
        }
    }

    private static Double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String && "NaN".equals(value)) {
            return Double.NaN; // or return a default value like 0.0
        }
        return 0.0; // Default value if not a number or NaN
    }

    public void run() {
        try {
            client.connect();
            client.subscribe(TOPIC, (t, message) -> {
                String payload = new String(message.getPayload());
//                System.out.println("Message received: " + payload);
                mainFrame.setStatus("Received \"" + message + "\" from " + TOPIC + ".".repeat(++messageCount % 3 + 1));

                try {

                    JSONObject jsonObject = (JSONObject) JSONValue.parse(payload);
                    if (jsonObject != null) {
                        System.out.println(jsonObject);


                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("emotivData.csv", true))) {
                            // Write header
                            writer.write("%s".format(String.valueOf(jsonObject)));
                            writer.newLine();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            this.running = true;
            mainFrame.setStatus("Connected to " + BROKER + "/" + TOPIC);


            JOptionPane.showMessageDialog(mainFrame, "Connected to broker", "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Subscribed to topic: " + TOPIC);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Error connecting to broker", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("MQTT error: " + e.getMessage());
        }
    }

    public void stop() {
        if (client != null) {
            this.running = false;
            try {
                client.disconnect();
                System.out.println("Disconnected from MQTT broker.");
            } catch (MqttException e) {
                System.err.println("Error while disconnecting: " + e.getMessage());
            } finally {
                client = null;
            }
        } else {
            System.out.println("Client is already disconnected or was never connected.");
        }
    }

    public boolean isRunning() {
        return running;
    }
}
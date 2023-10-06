// Import JAR-file and save in lib-folder
// https://repo1.maven.org/maven2/org/eclipse/paho/org.eclipse.paho.client.mqttv3/1.2.5/
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class ArduinoMQTT {

    public int arduinoMqtt() {
        AtomicInteger moveFromArduino = new AtomicInteger(-1);  // To safely handle concurrency
        MqttClient client = null;

        try {
        String brokerUrl = "ssl://1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud:8883";
        String clientId = "JavaRPSSample";
        client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        // Connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("W-bot");
        options.setPassword("W-bot123!".toCharArray());
        options.setCleanSession(true);
        options.setSocketFactory(SSLSocketFactory.getDefault());

        // SSL properties
        Properties sslProps = new Properties();
        sslProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
        options.setSSLProperties(sslProps);

        // Connect
        client.connect(options);

        // Subscribe to Topic - Getting move from Arduino player
        client.subscribe("game/rps/arduino", (topic, msg) -> {
            int move = Integer.parseInt(new String(msg.getPayload()));
            // Now `move` has the value from Arduino. Print to console.
            System.out.println("Received move from Arduino: " + move);
        });

        // Publish to Topic - Sending move to Arduino player
        // Assume that 'userMove' is an int that represents the player's move (1, 2, or 3).
        int userMove = 2; // Rock: 1, Paper: 2, Scissors: 3
        client.publish("game/rps/java", new MqttMessage(String.valueOf(userMove).getBytes()));

        } catch (MqttException e) {
            e.printStackTrace();
        } finally {
            // Ensure resources are freed and system is clean from potential memory leaks
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                    client.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        // ... (possible wait logic)

        return moveFromArduino.get();
    } 
    
}

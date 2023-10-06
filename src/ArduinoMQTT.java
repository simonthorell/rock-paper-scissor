// Import JAR-file and save in lib-folder
// https://repo1.maven.org/maven2/org/eclipse/paho/org.eclipse.paho.client.mqttv3/1.2.5/
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ArduinoMQTT {
    private static MqttClient client;
    private int playerID;
    private String playerTopic = "sten-sax-pase/player" + playerID;
    private static String messageTopic = "sten-sax-pase/message";
    private BlockingQueue<Integer> messageQueue = new LinkedBlockingQueue<>();

    public ArduinoMQTT(int playerID) throws MqttException {
        this.playerID = playerID;
        this.playerTopic = "sten-sax-pase/player" + playerID;
        String brokerUrl = "ssl://1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud:8883";
        String clientId = String.valueOf(playerID);
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

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Handle connection lost
            }
        
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Parse and add to queue
                messageQueue.put(Integer.parseInt(new String(message.getPayload())));
            }
        
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Handle delivery complete
            }
        });
        client.subscribe(playerTopic);  // Assuming `playerTopic` is the topic for incoming moves
    }

    // Method 1 - Asking arduino player to play - returns the button pressed
    public static void askToPlay(String displayMsg) throws MqttException, InterruptedException {
        client.publish(messageTopic, new MqttMessage(displayMsg.getBytes()));
        // return getArduinoMove();
    }

    // Method 2 - Sending a countdown over MQTT that is displayed on the Arduino
    public static void countDownAndThrow(String[] messages) throws MqttException, InterruptedException {
        // EXAMPLE: String[] messages = {"3", "2", "1", "Rock", "Paper", "Scissors!"};
        for (String msg : messages) {
            client.publish(messageTopic, new MqttMessage(msg.getBytes()));
            Thread.sleep(1000);  // 1 second pause
        }
    }

    // Method 3 - Getting the Arduino player's move - returns the button pressed
    public int getArduinoMove() {
        try {
            // Simply take the next move from the queue, waiting if necessary
            return messageQueue.take();
        } catch (InterruptedException e) {
            // Log and handle interruption appropriately for your use case
            Thread.currentThread().interrupt();
            return -1;  // Or handle it in some other appropriate manner
        }
    }

    // Method 4 - Displaying the game result on the Arduino
    public static void displayGameResult(String gameResultMsg) throws MqttException, InterruptedException {
        client.publish(messageTopic, new MqttMessage(gameResultMsg.getBytes()));
    }

    // Method 5 - Disconnecting from the MQTT broker
    public void disconnect() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}

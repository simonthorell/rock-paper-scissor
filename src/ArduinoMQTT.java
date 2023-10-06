// Import JAR-file and save in lib-folder
// https://repo1.maven.org/maven2/org/eclipse/paho/org.eclipse.paho.client.mqttv3/1.2.5/
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/* EXAMPLE USAGE
    public static void main(String[] args) throws MqttException, InterruptedException {
        String displayMessage = "Push any button to play!";
        String[] countDownMsg = {"3", "2", "1", "Rock", "Paper", "Scissors!"};
        String gameResultMsg = "Player" + playerID + " won!"; 

        ArduinoMQTT onlinePlayer1 = new ArduinoMQTT(playerID);  // Add playerID of player1
        ArduinoMQTT onlinePlayer2 = new ArduinoMQTT(playerID);  // Add playerID of player2
        
        // Example usage of the methods
        int chosenButton = onlinePlayer1.askToPlay(displayMessage);
        onlinePlayer1.countDownAndThrow(countDownMsg);
        int arduinoMove = onlinePlayer1.getArduinoMove();
        ... do the same for player 2 ...
        
        // game logic here

        onlinePlayer1.displayGameResult(gameResultMsg);
        
        onlinePlayer1.disconnect();  // Don't forget to disconnect at the end or when no longer needed
    }
*/ 

public class ArduinoMQTT {
    private MqttClient client;
    private int playerID;
    private String topic = "sten-sax-pase/player" + playerID;

    public ArduinoMQTT(int playerID) throws MqttException {
        this.playerID = playerID;
        this.topic = "sten-sax-pase/player" + playerID;
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
    }

    // Method 1 - Asking arduino player to play - returns the button pressed
    public int askToPlay(String displayMsg) throws MqttException, InterruptedException {
        client.publish(topic, new MqttMessage(displayMsg.getBytes()));
        return getArduinoMove();
    }

    // Method 2 - Sending a countdown over MQTT that is displayed on the Arduino
    public void countDownAndThrow(String[] messages) throws MqttException, InterruptedException {
        // EXAMPLE: String[] messages = {"3", "2", "1", "Rock", "Paper", "Scissors!"};
        for (String msg : messages) {
            client.publish(topic, new MqttMessage(msg.getBytes()));
            Thread.sleep(1000);  // 1 second pause
        }
    }

    // Method 3 - Getting the Arduino player's move - returns the button pressed
    public int getArduinoMove() throws InterruptedException {
        AtomicInteger arduinoMove = new AtomicInteger(-1);
        CountDownLatch latch = new CountDownLatch(1);  // To wait for a message from Arduino

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Implement what should happen if the connection is lost
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                arduinoMove.set(Integer.parseInt(new String(message.getPayload())));
                latch.countDown();  // Release the latch
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Implement what should happen when delivery is complete
            }
        });

        // Subscribe to get Arduino’s move
        try {
            client.subscribe(topic);
            latch.await();  // Wait for Arduino's move
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return arduinoMove.get();
    }

    // Method 4 - Displaying the game result on the Arduino
    public void displayGameResult(String gameResultMsg) throws MqttException, InterruptedException {
        client.publish(topic, new MqttMessage(gameResultMsg.getBytes()));
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

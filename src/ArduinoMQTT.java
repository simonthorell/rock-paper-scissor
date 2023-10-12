import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/* 
TODO
 * Listen for confirmation message before swapping
 * Fix the rest of the logic after ID assignning
 */


public class ArduinoMQTT {
    private MqttClient client;
    private final String messageTopic = "sten-sax-pase/#";
    private final String playerTopic = "sten-sax-pase/player" + this.playerID;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private int playerID = 0;
    private boolean sentPlayerID = false;
    private int lastMove;

    public ArduinoMQTT(int playerID) throws MqttException{
        this.playerID = playerID;
        String brokerUrl = "ssl://1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud:8883";
        String clientId = String.valueOf(playerID);
        client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("W-bot");
        options.setPassword("W-bot123!".toCharArray());
        options.setCleanSession(true);
        options.setSocketFactory(SSLSocketFactory.getDefault());

        Properties sslProps = new Properties();
        sslProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
        options.setSSLProperties(sslProps);

        client.connect(options);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Handle connection lost
            }
        
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Parse and add to queue
                messageQueue.put(new String(message.getPayload()));
                if(!sentPlayerID){ //If we've sent out player ID no need to listen to it anymore
                    getIDRequest();
                }else if(sentPlayerID){
                    decodeMove();
                }
            }
        
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Handle delivery complete
            }
        });

        client.subscribe(messageTopic);
    }

    private void decodeMove() throws MqttException{
        try {
            String jsonMessage = messageQueue.take();
            JSONObject jsonIncMsg = new JSONObject(jsonMessage);
            if(jsonIncMsg.has("move")){
                this.lastMove = jsonIncMsg.getInt("move");
            }

        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        }
    }

    private void getIDRequest() throws MqttException{
        try {
            String jsonMessage = messageQueue.take();
            JSONObject jsonIncMsg = new JSONObject(jsonMessage);
            if(jsonIncMsg.has("MAC"))
            {
                if(jsonIncMsg.has("playerID")){
                    if(jsonIncMsg.getInt("playerID") == this.playerID){
                        sentPlayerID = true;
                        client.subscribe(playerTopic);
                        return;
                    }
                }
                System.out.println(jsonIncMsg.getLong("MAC"));
                JSONObject jsonOutMsg = new JSONObject();
                jsonOutMsg.put("MAC", jsonIncMsg.getLong("MAC"));
                jsonOutMsg.put("playerID", this.playerID);
                client.publish("sten-sax-pase/message", new MqttMessage(jsonOutMsg.toString().getBytes()));
                //sentPlayerID = true;
                //client.subscribe(playerTopic); //change to the appropriate player topic
            } 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

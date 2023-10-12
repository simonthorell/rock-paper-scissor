import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

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
    private String playerTopic;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private int playerID;
    private boolean sentPlayerID = false;
    private int lastMove = -1;
    private boolean hasMoveReady = false;

    public ArduinoMQTT(int playerID) throws MqttException{
        this.playerID = playerID;
        playerTopic = "sten-sax-pase/player" + this.playerID;
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
                System.out.printf("GOT MOVE: %d\n", this.lastMove);
                hasMoveReady = true;
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
                        System.out.println(playerTopic);
                        System.out.printf("Locked in player %d with mac %d\n", this.playerID, jsonIncMsg.getLong("MAC"));
                        return;
                    }
                }
                System.out.println(jsonIncMsg.getLong("MAC"));
                JSONObject jsonOutMsg = new JSONObject();
                jsonOutMsg.put("MAC", jsonIncMsg.getLong("MAC"));
                jsonOutMsg.put("playerID", this.playerID);
                client.publish("sten-sax-pase/message", new MqttMessage(jsonOutMsg.toString().getBytes()));
            } 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendResultMQTT(int result) throws MqttException, InterruptedException{
        JSONObject jsonMsgOut = new JSONObject();
        jsonMsgOut.put("result", result);
        client.publish(playerTopic, new MqttMessage(jsonMsgOut.toString().getBytes()));
    }

    //If its connected and active with a arduino
    public boolean isActive(){
        return this.sentPlayerID;
    }

    public boolean hasMoveReady(){
        return this.hasMoveReady;
    }

    /* 
     * Gets the last move sent
     * 1 rock
     * 2 paper
     * 3 scissor
     */
    public int getLastMove(){
        hasMoveReady = false;
        return this.lastMove;
    }

    /* TODO: IMPLEMENT
     * Send a boolean for if the player won or lost
     * this handles the MQTT sending to the arduino
     * -1 lost
     *  0 tie
     * +1 win
     */
    public void sendResult(int winOrLose) throws MqttException, InterruptedException{
        sendResultMQTT(winOrLose);
    }
}

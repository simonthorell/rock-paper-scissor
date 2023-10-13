import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import javax.net.ssl.SSLSocketFactory;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ArduinoMQTT {
    private MqttClient client;
    private final String messageTopic = "sten-sax-pase/#";
    private String playerTopic;
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private int playerID;
    private boolean lockedInArduino = false;
    private int lastMove = -1;
    private boolean hasMoveReady = false;

    public ArduinoMQTT(int playerID) throws MqttException{
        this.playerID = playerID;
        playerTopic = "sten-sax-pase/player" + this.playerID;

        //Basic connection stuff
        String brokerUrl = "ssl://1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud:8883";
        String clientId = String.valueOf(playerID);
        client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        //Connection options, username, password and SSL
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("W-bot");
        options.setPassword("W-bot123!".toCharArray());
        options.setCleanSession(true);
        options.setSocketFactory(SSLSocketFactory.getDefault());

        //SSL setup
        Properties sslProps = new Properties();
        sslProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
        options.setSSLProperties(sslProps);

        //Connect
        client.connect(options);

        //Callback function to intercept messages and dispatch
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Handle connection lost
                //We dont care about it atm
            }
        
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Parse and add to queue
                messageQueue.put(new String(message.getPayload()));

                if(!lockedInArduino){ //if we need arduino still 
                    getIDRequest();
                }else if(lockedInArduino){ //we have one locked in
                    decodeMove();
                }
            }
        
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Handle delivery complete
                //We dont care about this
            }
        });

        //Start listening to a topic
        client.subscribe(messageTopic);
    }

    private void decodeMove() throws MqttException{
        try {
            String jsonMessage = messageQueue.take(); //get message
            JSONObject jsonIncMsg = new JSONObject(jsonMessage); //make into json format
            if(jsonIncMsg.has("move")){ //nullcheck for move
                this.lastMove = jsonIncMsg.getInt("move"); //set as last move
                System.out.printf("GOT MOVE: %d\n", this.lastMove);
                hasMoveReady = true; //flip flag
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void getIDRequest() throws MqttException{
        try {
            String jsonMessage = messageQueue.take(); //grab message
            JSONObject jsonIncMsg = new JSONObject(jsonMessage); //make into json format
            if(jsonIncMsg.has("MAC")) //Look for MAC key
            {
                if(jsonIncMsg.has("playerID")){ //null check
                    if(jsonIncMsg.getInt("playerID") == this.playerID){ //If its our playerID
                        lockedInArduino = true; //locked in our arduino confirmed
                        client.subscribe(playerTopic); //change to a subtopic for the player
                        System.out.printf("Locked in player %d with mac %d\n", this.playerID, jsonIncMsg.getLong("MAC"));
                        return; //return and stop executing
                    }
                }

                //Send out MAC recieved + our playerID to hopefully bind to a arduino
                JSONObject jsonOutMsg = new JSONObject();
                jsonOutMsg.put("MAC", jsonIncMsg.getLong("MAC")); //Add the incoming MAC adress under the key "MAC"
                jsonOutMsg.put("playerID", this.playerID); //Add our player ID under the key "playerID"
                client.publish("sten-sax-pase/message", new MqttMessage(jsonOutMsg.toString().getBytes())); //send the message
            } 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //Just sends result over MQTT
    private void sendResultMQTT(int result) throws MqttException, InterruptedException{
        JSONObject jsonMsgOut = new JSONObject();
        jsonMsgOut.put("result", result);
        client.publish(playerTopic, new MqttMessage(jsonMsgOut.toString().getBytes()));
    }

    //If its ready for you to read a move
    private boolean hasMoveReady(){
        return this.hasMoveReady;
    }

    /* 
     * Gets the last move sent
     * 0 rock
     * 1 paper
     * 2 scissor
     */
    private int getLastMove(){
        hasMoveReady = false;
        return this.lastMove - 1;
    }

    //If its connected and active with a arduino
    public boolean isActive(){
        return this.lockedInArduino;
    }

    /* 
     * Replaces displayGameResult from MqttPlayer.java
     * TODO: See if I can get a string displayed on arduino as well
     * 
     * Send a int for if the player won, lost or tie
     * this handles the MQTT sending to the arduino
     * -1 lost
     *  0 tie
     * +1 win
     */
    public void sendResult(int winOrLose) throws MqttException, InterruptedException{
        sendResultMQTT(winOrLose);
    }

    //Since we might need to wait this can take a while to responde
    public int getMove(){
        while(!hasMoveReady()){
            //TODO: This will spam the shit out of console, make it actually useful :)
            System.out.println("DEBUG: Waiting for response " + this.playerID);
        }
        return getLastMove();
    }

    /* 
     * TODO: Implement askToPlay()
     * Might not be needed since the python script is actively looking for
     * a ArduinoMQTT object to bind to and the object itself is
     * created listening to those calls, could refactor code so this object
     * isnt listening when created but instead starts listening when this is called
     * and I might be able to get a string to be displayed on the arduino
     */
    public void askToPlay(String displayMsg, int countPlayerID) throws MqttException{

    }

    /* 
     * TODO: Implement countDownAndThrow()
     * Might be hard to implement with how string work or rather dont in c++
     * Could be easier to have a predefined message and just have that trigger
     * on the arduino instead of sending a string but we could probably make it work
     */
    public void countDownAndThrow(String[] messages) throws MqttException, InterruptedException{

    }

    /* 
     * TODO: Implement displayNextMatch()
     * This needs implementing in arduino and Arduino_player.py aswell
     * Goal is to display who you are playing against?
     */
    public void displayNextMatch(TournamentTree.Node nextMatch) throws MqttException, InterruptedException{

    }

    /* 
     * TODO: Implement askToPlayAgain()
     * Changed to a no arg, easier to keep the play again isolated on arduino
     * and triggered when needed since we dont need a dynamic message.
     * Currently its setup to just keep playing and never stop so
     * unsure if its needed, but I can add that the arduino waits
     * for this to tell it to ask to play again
     */
    public void askToPlayAgain() throws MqttException, InterruptedException{

    }

    public void disconnect(){
        if(client != null && client.isConnected()){
            try {
                client.disconnect();
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

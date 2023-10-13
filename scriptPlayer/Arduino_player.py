import time
import ssl
import json
import paho.mqtt.client as mqtt
import serial
import threading
import uuid
import os

#USB stuff
baud_rate = 9600
usb_port = "COM3"

player_id = -1
result = 5

BROKER_URL = "1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud"
PORT = 8883
USERNAME = "W-bot"
PASSWORD = "W-bot123!"
TOPIC_PREFIX = "sten-sax-pase/"

def on_connect(client, userdata, flags, rc): 
    client.subscribe(f"{TOPIC_PREFIX}message")
    print(f"\033[1mConnected successfully to Sten Sax Påse!\033[0m \033[3m(Connection result code {str(rc)})\033[0m")

def on_message(client, userdata, message):
    global player_id, result
    
    payload_str = message.payload.decode("utf-8")       #Decode the incoming message as a utf-8 string
    payload_json = json.loads(payload_str)              #make a json object of it
    print(payload_json)                                 #debug print for my sanity
    latest_player_id = payload_json.get("playerID")     #Get playerID
    incoming_MAC = payload_json.get("MAC")              #Get MAC
    incoming_result = payload_json.get("result")        #Get result
    
    if(incoming_MAC):                                                               #If we had a MAC key in the incoming message
        if(uuid.getnode() == incoming_MAC & player_id != -1):                       #Check if its our MAC adress and that we havent already binded to a playerID
            print("uuid match")                                                     #Debug text
            player_id = latest_player_id                                            #Bind our playerID
                                                                                    
            payload_dict = {"playerID" : player_id, "MAC" : int(uuid.getnode())}    #The info we want to send
            client.publish(f"{TOPIC_PREFIX}message", json.dumps(payload_dict))      #Confirmation message with playerID + mac so backend knows which one it picked
            client.subscribe(f"{TOPIC_PREFIX}player{player_id}")                    #Change the topic we are subscribed to into the playerID specific one
    
    if(incoming_result):                                                            #If we have a result key in the json
        result = payload_json.get("result")                                         #Set the global result variable to it, will be used by the other thread
    
def mqtt_listener(client): #Setup the mqtt client
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()
    
def request_player_id(client): #Request a player ID over MQTT under the request topic with our mac adress
    client.publish(f"{TOPIC_PREFIX}Request", json.dumps({"message" : "NEED PLAYER ID", "MAC" : uuid.getnode()}))

def arduinoUSBDecode(incoming_byte, client, serial_bus):
    global player_id
    match incoming_byte: #Runs for each byte thats sent
        
        case 49: #ASCII 1 rock
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"move" : "1"}))
            
        case 50: #ASCII 2 paper
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"move" : "2"}))
            
        case 52: #ASCII 4 scissor
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"move" : "3"}))

        case 68: #ASCII D request ID
            print("Waiting for ID")
            if(player_id == -1): #The default value
                request_player_id(client)
            else: #We have gotten a player ID so send to arduino
                serial_bus.write(player_id.to_bytes(1, "big"))
                #Big is big endian

def serial_listen(client):
    global result
    
    #Try to open the serial bus
    try:
        serial_bus = serial.Serial(usb_port, baud_rate, timeout = 1)
        
    #If we cant open the serial bus terminate the entire python script
    except:
        print(f"Unable to open serial port {usb_port} terminating")
        os._exit(1)
        
    
    while True:
        if serial_bus.in_waiting > 0: #Serial bus has some data
            message = serial_bus.readline() #Read the data
            for byte in message: #Step through the bytes
                arduinoUSBDecode(byte, client, serial_bus)
                
        elif result != 5: #Just chose a arbitrary value, 5 means we have intercepted a W/L/T over MQTT
            match result: #Simple switch to get the actual value
                case 1:
                    serial_bus.write(b'W') #Write win to arduino
                    result = 5
                    
                case 0:
                    serial_bus.write(b'T') #NYI on arduino side
                    result = 5
                    
                case -1:
                    serial_bus.write(b'L') #Write loss to arduino
                    result = 5

#Making sure it only runs if its run as main and not imported
if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)
    
    #Make 2 threads, one handling USB and one listening on MQTT
    serial_listen_thread = threading.Thread(target = serial_listen, args=(client, ))
    mqtt_listen_thread = threading.Thread(target = mqtt_listener, args=(client, ))
    
    #Start the threads
    mqtt_listen_thread.start()
    time.sleep(2.2)
    serial_listen_thread.start()

    #This will never run, but its waiting for the threads to complete and we wait for them to join back up to the main thread
    mqtt_listen_thread.join()
    serial_listen_thread.join()
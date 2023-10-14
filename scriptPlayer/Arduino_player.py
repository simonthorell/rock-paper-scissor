import time
import ssl
import json
import paho.mqtt.client as mqtt
import serial
import threading
import re
from os import _exit

#USB stuff
baud_rate = 9600
usb_port = "COM3"

player_id = -1
result = 5
displayMessage = ""

BROKER_URL = "1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud"
PORT = 8883
USERNAME = "W-bot"
PASSWORD = "W-bot123!"
TOPIC_PREFIX = "sten-sax-pase/"

def on_connect(client, userdata, flags, rc): 
    client.subscribe(f"{TOPIC_PREFIX}message")
    print(f"\033[1mConnected successfully to Sten Sax Påse!\033[0m \033[3m(Connection result code {str(rc)})\033[0m")

def on_message(client, userdata, message):
    global player_id, result, displayMessage
    
    #TODO
    #Player \d won!
    #For some reason this cant be converted by json.loads
    
    payload_str = message.payload.decode("utf-8")       #Decode the incoming message as a utf-8 string
    try:
        payload_json = json.loads(payload_str)              #make a json object of it
        print(payload_json)                                 #debug print for my sanity
        incoming_message = payload_json.get("message")      #Get the message
        
        if(incoming_message):                               #If we have a message key in the json
            msgPayload = payload_json.get("message")       
            
            if (msgPayload == "Push any button to play!" and player_id == -1):
                player_id = payload_json.get("playerID")
                time.sleep(1)
                msg = { "message" : str(player_id)}
                client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps(msg))
                
            elif (re.search("^Player\s\d\swon!$",msgPayload)):
                getWinner(msgPayload)
                
            elif (msgPayload == "Tie!"):
                result = 0
                
            elif (msgPayload == "3"):
                result = 3
                
            elif (msgPayload == "write"):
                displayMessage = payload_json.get("write")
                result = 2
    except ValueError as e:
        print(e)
        print("Invalid json")
            
def getWinner(msgPayload):
    global result 
    winnerID = int(msgPayload[8])
    result = 1 if winnerID == player_id else -1
    print("Debug winner")
                
    
def mqtt_listener(client): #Setup the mqtt client
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()

def arduinoUSBDecode(incoming_byte, client, serial_bus):
    global player_id
    match incoming_byte: #Runs for each byte thats sent
        
        case 49: #ASCII 1 rock
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message" : "0"}))
            
        case 50: #ASCII 2 paper
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message" : "1"}))
            
        case 52: #ASCII 4 scissor
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message" : "2"}))

        case 68: #ASCII D request ID
            print("Waiting for ID")
            if(player_id != -1):
                serial_bus.write(player_id.to_bytes(1, "big"))
                #Big is big endian
                
def arduinoWriteToScreen(serial_bus, message):
    print("attempting to write")
    if(len(message) > 32): #warning that string too long
        print("String too long, will looked borked")
        
    if(re.search("^.{16}\s", message)): #If 17th character is a whitespace remove it
        message = message[:16] + message[17:]
    
    serial_bus.write(b'\x01') #Enable flag for arduino
    serial_bus.write(str.encode(message))
    serial_bus.write(b'\x00') #Null-termination

def serial_listen(client):
    global result, displayMessage
    
    #Try to open the serial bus
    try:
        serial_bus = serial.Serial(usb_port, baud_rate, timeout = 1)
        #serial_bus.write(b'\x05') #Acknowledge to raise flag in arduino that connection is made
        
    #If we cant open the serial bus terminate the entire python script
    except:
        print(f"Unable to open serial port {usb_port} terminating")
        _exit(1)
        
    
    while True:
        if serial_bus.in_waiting > 0: #Serial bus has some data
            message = serial_bus.readline() #Read the data
            print(f"INCOMING USB: {message}")
            for byte in message: #Step through the bytes
                arduinoUSBDecode(byte, client, serial_bus)
                
        elif result != 5: #Just chose a arbitrary value, 5 means we have intercepted a W/L/T over MQTT
            match result: #Simple switch to get the actual value
                case 1: #Write win to arduino
                    serial_bus.write(b'W') 
                    result = 5
                    
                case 0: #Write tie to arduino
                    serial_bus.write(b'T') 
                    result = 5
                    
                case -1: #Write loss to arduino
                    serial_bus.write(b'L') 
                    result = 5
                    
                case 2: #Write to screen
                    arduinoWriteToScreen(serial_bus, displayMessage)
                    displayMessage = "" #Reset the global string
                    result = 5
                    
                case 3: #Start countdown on arduino
                    serial_bus.write(b'\x02')
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
import time
import ssl
import json
import paho.mqtt.client as mqtt
import serial
import threading
import uuid
import os
from queue import Queue

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
    #global expect_return, player_id, latest_player_id, ask_for_input
    global player_id, result
    
    payload_str = message.payload.decode("utf-8")
    payload_json = json.loads(payload_str)
    print(payload_json)
    latest_player_id = payload_json.get("playerID")
    incoming_MAC = payload_json.get("MAC")
    incoming_result = payload_json.get("result")
    
    if(incoming_MAC):
        if(uuid.getnode() == incoming_MAC & player_id != -1):
            print("uuid match")
            player_id = latest_player_id
            #Confirmation message with playerID + mac so backend knows which one it picked
            payload_dict = {"playerID" : player_id, "MAC" : int(uuid.getnode())}
            client.publish(f"{TOPIC_PREFIX}message", json.dumps(payload_dict))
            client.subscribe(f"{TOPIC_PREFIX}player{player_id}")
    
    if(incoming_result):
        result = payload_json.get("result")
    
def mqtt_listener(client):
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()
    
def request_player_id(client):
    client.publish(f"{TOPIC_PREFIX}Request", json.dumps({"message" : "NEED PLAYER ID", "MAC" : uuid.getnode()}))

def arduinoUSBDecode(incoming_byte, client, serial_bus):
    global player_id
    match incoming_byte:
        
        case 49: #ASCII 1 rock
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"move" : "1"}))
            
        case 50: #ASCII 2 paper
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"move" : "2"}))
            
        case 52: #ASCII 4 scissor
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"move" : "3"}))

        case 68: #ASCII D request ID
            print("Waiting for ID")
            if(player_id == -1):
                request_player_id(client)
            else:
                serial_bus.write(player_id.to_bytes(1, "big"))

def serial_listen(client):
    global result
    try:
        serial_bus = serial.Serial(usb_port, baud_rate, timeout = 1)
        
    except:
        print(f"Unable to open serial port {usb_port} terminating")
        os._exit(1)
        
    
    while True:
        if serial_bus.in_waiting > 0:
            message = serial_bus.readline()
            for byte in message:
                arduinoUSBDecode(byte, client, serial_bus)
        elif result != 5:
            match result:
                case 1:
                    serial_bus.write(b'W')
                    result = 5
                    
                case 0:
                    serial_bus.write(b'T') #NYI
                    result = 5
                    
                case -1:
                    serial_bus.write(b'L')
                    result = 5

if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)
    
    serial_listen_thread = threading.Thread(target = serial_listen, args=(client, ))
    mqtt_listen_thread = threading.Thread(target = mqtt_listener, args=(client, ))
    
    mqtt_listen_thread.start()
    time.sleep(2.2)
    serial_listen_thread.start()

    mqtt_listen_thread.join()
    serial_listen_thread.join()
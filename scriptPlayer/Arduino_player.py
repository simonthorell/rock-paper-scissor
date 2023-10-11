import time
import ssl
import json
import paho.mqtt.client as mqtt
import serial
import threading
import uuid

#USB stuff
baud_rate = 9600
usb_port = "COM3"

player_id = -1

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
    global player_id
    
    payload_str = message.payload.decode("utf-8")
    payload_json = json.loads(payload_str)
    display_message = payload_json.get("message")
    latest_player_id = payload_json.get("playerID")
    expect_return = payload_json.get("expectReturn")
    incoming_MAC = payload_json.get("MAC")
    
    if(uuid.getnode() == incoming_MAC):
        print("uuid match")
        player_id = latest_player_id
        #Confirmation message with playerID + mac so backend knows which one it picked
        client.publish(f"{TOPIC_PREFIX}message", json.dumps({"playerID" : {player_id}, "MAC" : uuid.getnode()}))
    
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
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message" : "ROCK"}))
            
        case 50: #ASCII 2 paper
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message" : "PAPER"}))
            
        case 52: #ASCII 4 scissor
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message" : "SCISSOR"}))

        case 68: #ASCII D request ID
            print("Waiting for ID")
            if(player_id == -1):
                request_player_id(client)
            else:
                serial_bus.write(player_id.to_bytes(1, "big"))

def serial_listen(client):
    serial_bus = serial.Serial(usb_port, baud_rate, timeout = 1)
    
    while True:
        if serial_bus.in_waiting > 0:
            message = serial_bus.readline()
            for byte in message:
                arduinoUSBDecode(byte, client, serial_bus)

if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)
    
    serial_listen_thread = threading.Thread(target = serial_listen, args=(client,))
    mqtt_listen_thread = threading.Thread(target = mqtt_listener, args=(client,))
    
    mqtt_listen_thread.start()
    time.sleep(2.2)
    serial_listen_thread.start()

    mqtt_listen_thread.join()
    serial_listen_thread.join()
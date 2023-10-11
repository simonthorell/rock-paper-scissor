import time
import ssl
import json
import paho.mqtt.client as mqtt
import serial
import threading


#USB stuff
baud_rate = 9600
usb_port = "COM3"

BROKER_URL = "1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud"
PORT = 8883
USERNAME = "W-bot"
PASSWORD = "W-bot123!"
TOPIC_PREFIX = "sten-sax-pase/"

def on_connect(client, userdata, flags, rc):
    client.subscribe(f"{TOPIC_PREFIX}message")

def on_message(client, userdata, flags, rc):
    global expect_return, player_id, latest_player_id, ask_for_input
    
def mqtt_listener(client):
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()

def arduinoUSBDecode(incoming_byte):
    match incoming_byte:
        
        case 49: #ASCII 1 rock
            client.publish()
            
        case 50: #ASCII 2 paper
            client.publish()
            
        case 52: #ASCII 4 scissor
            client.publish()

        case 68: #ASCII D request ID
            print("Waiting for ID")

def serial_listen():
    serial_bus = serial.Serial(usb_port, baud_rate, timeout = 1)
    
    while True:
        if serial_bus.in_waiting > 0:
            message = serial_bus.readline()
            for byte in message:
                arduinoUSBDecode(byte)

if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)
    
    serial_listen_thread = threading.Thread(target = serial_listen, args=(client,))
import paho.mqtt.client as mqtt
import json
import ssl
import threading
from time import sleep
import serial
import sys

BROKER_URL = "1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud"
PORT = 8883
USERNAME = "W-bot"
PASSWORD = "W-bot123!"
TOPIC_PREFIX = "sten-sax-pase/"

player_id = None
latest_player_id = None
expect_return = False
ask_for_input = False

def waiting(wait_message):
    global expect_return
    while not expect_return:
        print(f"\r\033[3m{wait_message}\033[0m", end="", flush=True)
        for _ in range(5):
            sleep(0.5)
            if expect_return: 
                break
            print(".", end="", flush=True)

def on_connect(client, userdata, flags, rc):    
    print(f"\033[1mConnected successfully to Sten Sax Påse!\033[0m \033[3m(Connection result code {str(rc)})\033[0m")
    client.subscribe(f"{TOPIC_PREFIX}message")

def on_message(client, userdata, message):
    global expect_return, player_id, latest_player_id, ask_for_input

    payload_str = message.payload.decode('utf-8')
    payload_json = json.loads(payload_str)
    display_message = payload_json.get('message')
    latest_player_id = payload_json.get('playerID')
    expect_return = payload_json.get('expectReturn')

    if (player_id is None) and (latest_player_id is 1):
        print(f"\n{display_message}")
    elif display_message is None:
        pass
    elif latest_player_id is None:
        print(f"{display_message}")
    elif (player_id is None) and (latest_player_id is not player_id):
        pass
        expect_return = False
    else:
        print("Waiting for other players...")
        expect_return = False

    ask_for_input = expect_return

def mqtt_listener(client):
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context=ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()

def user_input(client):
    global player_id, latest_player_id, ask_for_input
    
    while True:
        if ask_for_input is True:
            user_command = input("Enter a command: ")

            if player_id is None:
                player_id = latest_player_id
                print(f"Welcome Player {player_id}!")

            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message": user_command}))
            ask_for_input = False
            
def arduinoUSBDecode(incoming_byte, client, serial_bus):
    global player_id, latest_player_id
    match incoming_byte:
        
        case 49: #0b00110001 #Ascii 1
            print("Rock")
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message": "1"}))
            
        case 50: #0b00110010 #Ascii 2
            print("Paper")
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message": "2"}))
            
        case 52: #0b00110100 #Ascii 4
            print("Scissor")
            client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message": "3"}))
            
        case 68: #0b01000100: #Upper case D ascii
            print("requesting player ID")
            #player_id = None #temp shit
            #latest_player_id = 2 #temp shit
            #just copied the structure from user_input()
            if player_id == None:
                player_id = latest_player_id
                print("playerID: " + str(player_id))
                serial_bus.write(player_id.to_bytes(1, "big")) #Convert to 1 byte so arduino can read
                client.publish(f"{TOPIC_PREFIX}player{player_id}", json.dumps({"message": "test"}))
                            
def serial_listener(client):
    baud_rate = 9600
    usb_port = "COM3"
    serial_bus = serial.Serial(usb_port, baud_rate, timeout = 1)
    
    while True:
        if serial_bus.in_waiting > 0:
            message = serial_bus.readline()
            for byte in message:
                arduinoUSBDecode(byte, client, serial_bus)

if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)

    listener_thread = threading.Thread(target=mqtt_listener, args=(client,))
    user_input_thread = threading.Thread(target=user_input, args=(client,))
    serial_listen_thread = threading.Thread(target = serial_listener, args=(client,))

    listener_thread.start()
    sleep(2.2) # Give some time for the connection to establish
    waiting("Waiting for host to start game")
    
    arduino = False
    for i in sys.argv:
        if(i == "-A"):
            print("Arduino Mode")
            serial_listen_thread.start()
            serial_listen_thread.join()
            arduino = True
            
    if not arduino:
        print("Default mode")
        user_input_thread.start()
        user_input_thread.join()

    listener_thread.join()

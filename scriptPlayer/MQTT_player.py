import paho.mqtt.client as mqtt
import json
import ssl
import threading
from time import sleep

BROKER_URL = "1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud"
PORT = 8883
USERNAME = "W-bot"
PASSWORD = "W-bot123!"
TOPIC_PREFIX = "sten-sax-pase/"

playerID = None
latest_playerID = None
expectReturn = False
askForInput = False

def waiting(wait_message):
    global expectReturn
    while not expectReturn:
        print(f"\r{wait_message}", end="", flush=True)
        for _ in range(5):
            sleep(0.5)
            if expectReturn: 
                break
            print(".", end="", flush=True)

def on_connect(client, userdata, flags, rc):    
    print(f"Connected successfully to Sten Sax Påse! (result code {str(rc)})")
    client.subscribe(f"{TOPIC_PREFIX}message")

def on_message(client, userdata, message):
    global expectReturn, playerID, latest_playerID, askForInput

    payload_str = message.payload.decode('utf-8')
    payload_json = json.loads(payload_str)
    display_message = payload_json.get('message')
    latest_playerID = payload_json.get('playerID')
    expectReturn = payload_json.get('expectReturn')

    if playerID is None:
        print(f"\n{display_message}")
    elif display_message == None:
        pass
    elif latest_playerID is None:
        print(f"{display_message}")
    else:
        print("Waiting for other players...")
        expectReturn = False

    askForInput = expectReturn

def mqtt_listener(client):
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context=ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()

def user_input(client):
    global playerID, latest_playerID, askForInput
    
    while True:
        if askForInput is True:
            user_command = input("Enter a command: ")

            if playerID is None:
                playerID = latest_playerID
                print(f"Welcome Player {playerID}!")

            client.publish(f"{TOPIC_PREFIX}player{playerID}", json.dumps({"message": user_command}))
            askForInput = False

if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)

    listener_thread = threading.Thread(target=mqtt_listener, args=(client,))
    user_input_thread = threading.Thread(target=user_input, args=(client,))

    listener_thread.start()
    sleep(3) # Give some time for the connection to establish
    waiting("Waiting for host to start game")
    user_input_thread.start()

    listener_thread.join()
    user_input_thread.join()

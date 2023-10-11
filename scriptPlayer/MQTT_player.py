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

if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)

    listener_thread = threading.Thread(target=mqtt_listener, args=(client,))
    user_input_thread = threading.Thread(target=user_input, args=(client,))

    listener_thread.start()
    sleep(2.2) # Give some time for the connection to establish
    waiting("Waiting for host to start game")
    user_input_thread.start()

    listener_thread.join()
    user_input_thread.join()

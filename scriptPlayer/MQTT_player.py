import paho.mqtt.client as mqtt
import json
import ssl
import threading

BROKER_URL = "1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud"
PORT = 8883
USERNAME = "W-bot"
PASSWORD = "W-bot123!"
TOPIC_PREFIX = "sten-sax-pase/"

playerID = None
latest_playerID = None
rockPaperScissor = None
countDown = 3

def on_connect(client, userdata, flags, rc):    
    print(f"Connected with result code {str(rc)}")
    client.subscribe(f"{TOPIC_PREFIX}message")

def on_message(client, userdata, message):
    global latest_playerID
    global countDown

    payload_str = message.payload.decode('utf-8')
    payload_json = json.loads(payload_str)
    latest_playerID = payload_json.get('playerID')

    if playerID is None:
        print(f"\nNew playerID received: {latest_playerID}")
        # print("Enter a command: ", end="", flush=True)
    elif payload_json.get('playerID') is None:
        print(payload_json.get('message'))
        countDown -= 1
    else:
        print("Waiting for new players...")

    if  playerID is None or countDown < 0:
        print("Enter a command: ", end="", flush=True)

def mqtt_listener(client):
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(BROKER_URL, PORT)
    client.tls_set_context(context=ssl.SSLContext(ssl.PROTOCOL_TLSv1_2))
    client.loop_forever()

def user_input(client):
    global latest_playerID
    global playerID
    global rockPaperScissor

    while playerID is None:
        user_command = input("Enter a command: ")

        if user_command.lower() == 'exit':
            break
        elif latest_playerID is not None:
            client.publish(f"{TOPIC_PREFIX}player{latest_playerID}", json.dumps({"message": user_command}))
            playerID = latest_playerID
            print(f"Executing '{user_command}' for playerID: {playerID}")
        else:
            print("No playerID received yet")
    
    while rockPaperScissor is None:
        user_command = input("Enter a command: ")
        client.publish(f"{TOPIC_PREFIX}player{playerID}", json.dumps({"message": user_command}))
        print(f"Executing '{user_command}' for playerID: {playerID}")
        rockPaperScissor = user_command


if __name__ == "__main__":
    client = mqtt.Client()
    client.username_pw_set(username=USERNAME, password=PASSWORD)

    listener_thread = threading.Thread(target=mqtt_listener, args=(client,))
    user_input_thread = threading.Thread(target=user_input, args=(client,))

    listener_thread.start()
    user_input_thread.start()

    listener_thread.join()
    user_input_thread.join()

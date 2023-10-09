import time
import paho.mqtt.client as mqtt
import serial
from random import randint

""" 
TODO RE-WRITE IN JAVA
"""

"""
You need to set your own stuff here for it to work 
"""

#USB stuff
baud_rate = 9600
usbPort = "COM3"
serialbus = serial.Serial(usbPort, baud_rate, timeout = 1)



#MQTT stuff
mqttClient = "test.mosquitto.org"
mqttPort = 1883
mqttTopic = "nacka/mqttTest"
client = mqtt.Client()
client.connect(mqttClient, mqttPort)





def arduinoDecode(incomingByte):
    match incomingByte:
        
        case 49: #Ascii 1
            print("RECIEVED Rock")
            sendRandomToArduino() # Send over MQTT here
            mqttDummySend("ROCK")
            
        case 50: #Ascii 2
            print("RECIEVED Paper")
            sendRandomToArduino()
            mqttDummySend("PAPER")
            
        case 52: #Ascii 4
            print("RECIEVED Scissor")
            sendRandomToArduino()
            mqttDummySend("SCISSOR")
            
        case 0b01000100: #upper case D, last in needs player ID
            print("Sent player ID x01")
            serialbus.write(b'\x01') #player ID 1, should wait for response from MQTT here
            
            
def sendRandomToArduino():
    time.sleep(3)
    if randint(0, 1) == 1:
        print("SENT W")
        serialbus.write(b'W') 
    else:
        print("SENT L")
        serialbus.write(b'L')
        
def mqttDummySend(value):
    client.publish(value)


while True:
    if serialbus.in_waiting > 0:
        message = serialbus.readline()
        print("RECIEVED: " + str(message))
        for a_byte in message: #Go through byte by byte
            arduinoDecode(a_byte)

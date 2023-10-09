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
baud_rate = 9600
mqttClient = "test.mosquitto.org"
mqttPort = 1883
mqttTopic = "nacka/mqttTest"
usbPort = "COM3"


serialbus = serial.Serial(usbPort, baud_rate, timeout = 1)

client = mqtt.Client()
client.connect(mqttClient, mqttPort)

def wtfisarduinosending(incomingByte):
    match incomingByte:
        
        case 49: #Ascii 1
            print("RECIEVED Rock")
            sendRandomToArduino() # Send over MQTT here
            
        case 50: #Ascii 2
            print("RECIEVED Paper")
            sendRandomToArduino()
            
        case 52: #Ascii 4
            print("RECIEVED Scissor")
            sendRandomToArduino()
            
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


while True:
    if serialbus.in_waiting > 0:
        message = serialbus.readline()
        print("RECIEVED: " + str(message))
        for a_byte in message: #Go through byte by byte
            wtfisarduinosending(a_byte)

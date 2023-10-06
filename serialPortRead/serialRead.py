import paho.mqtt.client as mqtt
import json
import serial

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

while True:
    if serialbus.in_waiting > 0:
        message = serialbus.readline()
        try:
            message = int(message)
            match message:
                case 1:
                    client.publish(mqttTopic, 1)
                    print(1)

                case 2:
                    client.publish(mqttTopic, 2)
                    print(2)

                case 3:
                    client.publish(mqttTopic, 3)
                    print(3)

                case _:
                    print("Read a invalid value from the port!")
        except:
            pass
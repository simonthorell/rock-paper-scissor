/* 
This requires the LiquidCrystal and Keypad libraries included with the freenove ultimate starter kit download
You also need to set your own paths to build it

The Keypad is connected to pin 11 through 4
the LCD is connected to A4 and A5
 */

/* 
TODO 
Wait before a connection is established before promoting a ask for rock/paper/scissor

promt on LCD asking for rock/paper/scissor

wait for select confirmation before sending?
*/

#include <Keypad.h>
#include "aLCD.h"

const int baud_rate = 9600;
const char keys[4][4] = {
    { '1', '2', '3', 'A'},
    { '4', '5', '6', 'B'},
    { '7', '8', '9', 'C'},
    { '*', '0', '#', 'D'}
};
byte rowPins[4] = {11, 10, 9, 8};
byte colPins[4] = {7, 6, 5, 4};

Keypad myKeypad = Keypad(makeKeymap(keys), rowPins, colPins, 4, 4);
LiquidCrystal_I2C lcd(0, 0, 0);

//forward declarations
void getChoice(char c);

void setup(){
    lcd = aLCD::startLCD();
    Serial.begin(baud_rate);
}

void loop(){
    char keyPressed = myKeypad.getKey();
    if(keyPressed)
        getChoice(keyPressed);
}

/* 
Preparing a interrupt for displaying if you win or lose later
 */
void serialEvent(){
    char inChar;

    if(Serial.available()){
        inChar = Serial.read();
        Serial.println("Recieved data");

        if(inChar == 'W'){
            Serial.println("W");
            lcd.clear();
            lcd.print("You won!");
        }
        else if(inChar == 'L'){
            Serial.println("L");
            lcd.clear();
            lcd.print("You lost!");
        }
    }
}

/* 
Char byte values
1 = 49
2 = 50
3 = 51
 */
void getChoice(char c){
    lcd.clear();
    switch(c)
    {
        case 49:
            Serial.println(1);
            lcd.print("Rock");
        break;
        
        case 50:
            Serial.println(2);
            lcd.print("Paper");
        break;

        case 51:
            Serial.println(3);
            lcd.print("Scissor");
        break;

        default:
            Serial.println(4);
            lcd.print("Press 1, 2 or 3!");
        break;
    }
}
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
    { '*', '0', '#', 'D'}};
byte rowPins[4] = {11, 10, 9, 8};
byte colPins[4] = {7, 6, 5, 4};

uint8_t playerID = 0;
uint8_t waiting = 0;
char pressedKey;

Keypad myKeypad = Keypad(makeKeymap(keys), rowPins, colPins, 4, 4);
LiquidCrystal_I2C lcd(0, 0, 0);

//forward declarations, do you even need these for the compiler here?
void getKeypadPress(char c);
void waitForID();
void printSelection(char* str);


void setup(){
    lcd = aLCD::startLCD();
    Serial.begin(baud_rate);
}

void loop(){
    if(playerID == 0 ? false : true){
        pressedKey = myKeypad.getKey();
        if(pressedKey)
            getKeypadPress(pressedKey);
    }
    else{
        waitForID();
    }
}

/* 
Get stuck in loop while we wait for a playerID over USB, we escape the loop by the interrupt function
*/
void waitForID(){
    Serial.println("NO PLAYER ID");
    lcd.clear();
    lcd.print("Connecting");
    for(int i = 0; i <= waiting % 3; i++)
        lcd.print(".");
    delay(500);
    waiting++;
}

/* 
Interrupt for serialEvents to read data and process it before jumping back to the previous program state
Char byte values
87 = W
76 = L
 */
void serialEvent(){
    char inChar;
    Serial.println("Serial event interrupt");

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
        else{
            lcd.clear();
            playerID = inChar;
            lcd.print("PlayerID: ");
            lcd.print(playerID);
            lcd.setCursor(0, 1);
            lcd.print("READY TO PLAY!");
        }
    }
}

/* 
Char byte values
1 = 49
2 = 50
3 = 51
 */
void getKeypadPress(char c){
    //lcd.clear();
    switch(c){
        case 49:
            Serial.println(1);
            printSelection("Rock");
        break;
        
        case 50:
            Serial.println(2);
            printSelection("Paper");
        break;

        case 51:
            Serial.println(3);
            printSelection("Scissor");
        break;

        default:
            lcd.clear();
            lcd.print("Press 1, 2 or 3!");
            lcd.setCursor(0, 1);
            lcd.print(c);
        break;
    }
}

void printSelection(char* str)
{
    lcd.clear();
    lcd.print("Selected: ");
/*     int i = 0;
    while(*(str + i) != 0){
        lcd.print(*(str + i));
        i++;
    }  */      
    lcd.print(str);
}
/* 
This requires the LiquidCrystal and Keypad libraries included with the freenove ultimate starter kit download
You also need to set your own paths to build it

The Keypad is connected to pin 11 through 4
the LCD is connected to A4 and A5
 */

/* 
Fix pressing star doesnt instantly resend the same after W/L
 */

#include <Keypad.h>
#include "aLCD.h"

#define rockBitFlag 0b001
#define paperBitFlag 0b010
#define scissorBitFlag 0b100

const int baud_rate = 9600;
const char keys[4][4] = {
    { '1', '2', '3', 'A'},
    { '4', '5', '6', 'B'},
    { '7', '8', '9', 'C'},
    { '*', '0', '#', 'D'}};
byte rowPins[4] = {11, 10, 9, 8};
byte colPins[4] = {7, 6, 5, 4};
const char *rock = "Rock";
const char *paper = "Paper";
const char *scissor = "Scissor";

uint8_t playerID = 0;
uint8_t waiting = 0;
char pressedKey;
byte selected;
bool waitForResult = false;

Keypad myKeypad = Keypad(makeKeymap(keys), rowPins, colPins, 4, 4);
LiquidCrystal_I2C lcd(0, 0, 0);

//forward declarations, do you even need these for the compiler here?
void getKeypadPress(char c);
void waitForID();
void printSelection(const char* str);
const char * getSelection(byte flags);


void setup(){
    lcd = aLCD::startLCD();
    Serial.begin(baud_rate);
}

void loop(){
    if(playerID == 0 ? false : true && waitForResult == false){
        pressedKey = myKeypad.getKey();
        if(pressedKey)
            getKeypadPress(pressedKey);
    }
    else if(playerID == 0 ? false : true && waitForResult == true){
        lcd.setCursor(0, 1);
        lcd.print("Waiting for result");
        delay(500);
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
    delay(1000);
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

        if(inChar == 'W' && playerID != 0){
            Serial.println("W");
            lcd.clear();
            lcd.print("You won!");
            waitForResult = false;
        }
        else if(inChar == 'L' && playerID != 0){
            Serial.println("L");
            lcd.clear();
            lcd.print("You lost!");
            waitForResult = false;
        }
        else if(inCHar == 'T' && playerID != 0){
            Serial.println("T");
            lcd.clear();
            lcd.print("Tie!");
            waitForResult = false;
        }
        else if(playerID == 0){
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
* = 42
1 = 49
2 = 50
3 = 51
 */
void getKeypadPress(char c){
    switch(c){
        case 42:
            Serial.println(selected);
            lcd.clear();
            lcd.print("SENT: ");
            lcd.print(getSelection(selected));
            waitForResult = true;
        break;

        case 49:
            selected = rockBitFlag;
            printSelection(rock);
        break;
        
        case 50:
            selected = paperBitFlag;
            printSelection(paper);
        break;

        case 51:
            selected = scissorBitFlag;
            printSelection(scissor);
        break;

        default:
            lcd.clear();
            lcd.print("Press 1, 2 or 3!");
            lcd.setCursor(0, 1);
            lcd.print(c);
            lcd.print((int)c);
        break;
    }
}

void printSelection(const char* str)
{
    lcd.clear();
    lcd.print("Selected: ");
/*     int i = 0;
    while(*(str + i) != 0){
        lcd.print(*(str + i));
        i++;
    }  */      
    lcd.print(str);
    lcd.setCursor(0, 1);
    lcd.print("* to confirm");
}

const char * getSelection(byte flags){
    if(flags & rockBitFlag)
        return rock;
    if(flags & paperBitFlag)
        return paper;
    if(flags & scissorBitFlag)
        return scissor;
    return NULL;
}
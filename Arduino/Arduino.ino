/* 
This requires the LiquidCrystal and Keypad libraries included with the freenove ultimate starter kit download
You also need to set your own paths to build it

The Keypad is connected to pin 11 through 4
the LCD is connected to A4 and A5
 */

#include <Keypad.h>
#include "aLCD.h"

//Some defines, could probably change these to const but I like the readability
#define rockFlag 1
#define paperFlag 2
#define scissorFlag 4
#define screenWidth 16
#define writeToDisplayBitFlag 0x01
#define countdownBitFlag 0x02

//Keypad setup thingies
const int baud_rate = 9600;
const char keys[4][4] = {
    { '1', '2', '3', 'A'},
    { '4', '5', '6', 'B'},
    { '7', '8', '9', 'C'},
    { '*', '0', '#', 'D'}};
byte rowPins[4] = {11, 10, 9, 8};
byte colPins[4] = {7, 6, 5, 4};

//const strings
const char *rock = "Rock";
const char *paper = "Paper";
const char *scissor = "Scissor";
const char *countdown[] = {
    "3", "2", "1", "Go!" //3,2,1, rock paper sisscors was too long and cba to fix newline
}; 

//global variables
uint8_t playerID = 0;
uint8_t waitingDots = 0;
uint8_t cursorLoc = 0;
//Will overflow after 255 wins/losses/ties but who cares
uint8_t wins = 0;
uint8_t losses = 0;
uint8_t ties = 0;
char pressedKey = 0;
byte selected = 0;
bool waitForResult = false;
bool waitForCountdown = false;
bool serialIncomingChars = false;

//Keypad and LCD setup
Keypad myKeypad = Keypad(makeKeymap(keys), rowPins, colPins, 4, 4);
LiquidCrystal_I2C lcd(0, 0, 0);

//forward declarations
void getKeypadPress(char c);
void waitForID();
void printSelection(const char* str);
const char * getSelection(byte flags);
void displayCountdown();
void displayResultAndClear(char c);

//runs once
//Move initialization to here?
void setup(){
    lcd = aLCD::startLCD(); //init LCD
    Serial.begin(baud_rate); //init serial port
}

//primary loop
void loop(){
    if(playerID != 0){ //We have a ID
        if(!waitForResult && !waitForCountdown){ //Not waiting for anything, button time!
            pressedKey = myKeypad.getKey();
            if(pressedKey)
                getKeypadPress(pressedKey);
        }
    }
    else //we dont have ID, wait until we get one
        waitForID();
}

/* 
Get stuck in loop while we wait for a playerID over USB, we escape the loop by the interrupt function
*/
void waitForID(){
    Serial.println("NO PLAYER ID");
    lcd.clear();
    lcd.print("Connecting");
    //Fancy loop for differing amounts of dots
    for(int i = 0; i <= waitingDots % 3; i++) 
        lcd.print(".");
    delay(1000);
    waitingDots++;
}

/* 
Interrupt for serialEvents to read data and process it before jumping back to the previous program state
 */
void serialEvent(){
    char inChar;

    if(Serial.available()){
        inChar = Serial.read(); //read the incoming value

        if(serialIncomingChars == true){ //if we are in display mode
            if(inChar == NULL)          //Null terminated
            {
                serialIncomingChars = false; //leave display mode
            }
            else
            {
                //logic for moving the print cursor to the correct location
                //We lose the location on changing scope it seems like
                lcd.setCursor(cursorLoc % screenWidth, cursorLoc / screenWidth);
                lcd.print(inChar);
                cursorLoc++;
            }
        }
        else if(playerID != 0){ //make sure we have playID before accepting any of these 
            switch (inChar){
                case 'W': //BIG W
                    displayResultAndClear(inChar);
                break;

                case 'L': //loser loser
                    displayResultAndClear(inChar);
                break;

                case 'T': //Tie
                    displayResultAndClear(inChar);
                break;

                case writeToDisplayBitFlag: //Specific byte to raise flag that we are supposed to display incoming text
                    serialIncomingChars = true; //set flag that we are in display mode
                    cursorLoc = 0;
                    lcd.clear();
                break;

                case countdownBitFlag: //Same as before except to start the countdown
                    displayCountdown();
                break;
            }
        }
        //We dont have a playerID so accepting the first byte sent as our playerID
        //Make sure on the python script side that we are aware of this
        else if(playerID == 0){
            playerID = inChar; //Set playerID to the incoming byte

            //Some confirmation messages on the LCD
            lcd.clear();
            char buffer[16];
            sprintf(buffer, "PlayerID: %d", playerID);
            lcd.print(buffer);
            /* lcd.print("PlayerID: ");
            lcd.print(playerID); */
            lcd.setCursor(0, 1);
            lcd.print("Waiting for host");
            waitForCountdown = true; //Waiting for host to start game
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
            if(selected != 0){ //if something is selected
                //send the data over USB and display what it sent
                Serial.println(selected);
                lcd.clear();
                char buffer[16];
                sprintf(buffer, "Sent: %s", getSelection(selected));
                lcd.print(buffer);
                /* lcd.print("Sent: ");
                lcd.print(getSelection(selected)); */
                lcd.setCursor(0, 1);
                lcd.print("Waiting");
                waitForResult = true; //flag that we are waiting for result
            }
            else{ //nothing is selected
                //Cant send nothing so prompt to press 1,2,3
                lcd.clear();
                lcd.print("Press 1,2,3 to");
                lcd.setCursor(0, 1);
                lcd.print("select something");
            }
        break;

        case 49:
            selected = rockFlag;
            printSelection(rock);
        break;
        
        case 50:
            selected = paperFlag;
            printSelection(paper);
        break;

        case 51:
            selected = scissorFlag;
            printSelection(scissor);
        break;

        default:
            //Didnt press 1,2,3, prompt to press one of those
            lcd.clear();
            lcd.print("Press 1, 2 or 3!");
        break;
    }
}

//Just a function to display to remove repeating code
void printSelection(const char* str)
{
    lcd.clear();
    /*lcd.print("Selected: ");
    int i = 0;
    while(*(str + i) != 0){
        lcd.print(*(str + i));
        i++;
    }  */      
    lcd.print(str);
    lcd.setCursor(0, 1);
    lcd.print("* to confirm");
}

//return the correct string based on selection
const char * getSelection(byte flags){
    if(flags & rockFlag)
        return rock;
    if(flags & paperFlag)
        return paper;
    if(flags & scissorFlag)
        return scissor;
    return NULL;
}

//the countdown before game beings
void displayCountdown(){
    for(int i = 0; i < 4; i++){ //Iterate over the countdown char ptr array
        lcd.clear();
        lcd.print(countdown[i]);
        delay(1000); 
    }
    lcd.setCursor(0, 1);
    lcd.print("Press 123!");

    waitForCountdown = false; //remove waiting flag
}

//resetting most variables and stuff
void displayResultAndClear(char c){
    lcd.clear();
    switch(c){
        case 'W':
            lcd.print("You won!");
            wins++;
        break;

        case 'L':
            lcd.print("You lost!");
            losses++;
        break;

        case 'T':
            lcd.print("Tie!");
            ties++;
        break;
    }
    delay(2000);
    char buffer[16];
    sprintf(buffer, "%dW %dL %dT", wins, losses, ties);
    lcd.clear();
    lcd.print(buffer);
    //reset some variables and await the countdown to begin again
    waitForResult = false;
    waitForCountdown = true;
    selected = 0;

    delay(1000); //wait 1 second before displaying last part
    lcd.setCursor(0, 1);
    lcd.print("Waiting for host");
}
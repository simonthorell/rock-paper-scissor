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

void setup()
{
    lcd = aLCD::startLCD();
    Serial.begin(baud_rate);
}

void loop()
{
    char keyPressed = myKeypad.getKey();
    if(keyPressed)
        getChoice(keyPressed);
}

/* 
Char byte values
1 = 49
2 = 50
3 = 51
 */
void getChoice(char c)
{
    lcd.clear();
    switch(c)
    {
        case 49:
            Serial.println(1);
            lcd.print("Sten");
        break;
        
        case 50:
            Serial.println(2);
            lcd.print("Sax");
        break;

        case 51:
            Serial.println(3);
            lcd.print("Påse");
        break;

        default:
            Serial.println("DEFAULT!");
            lcd.print("Tryck på 123!");
        break;
    }
}
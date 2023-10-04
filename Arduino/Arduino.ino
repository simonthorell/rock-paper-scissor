#include <Keypad.h>
#include "aLCD.h"

const int baud_rate = 9600;
int i = 97;

//needs big scope
LiquidCrystal_I2C lcd(0, 0, 0);

//forward declarations
void lcdDraw(LiquidCrystal_I2C *lcd, int i);

void setup()
{
    lcd = aLCD::startLCD();
    Serial.begin(baud_rate);
}

void loop()
{
    lcdDraw(&lcd, i);
    delay(1000);
    Serial.println(i);
    i++;
}

void lcdDraw(LiquidCrystal_I2C *lcd, int i)
{
    lcd->clear();
    char a = (char)i;
    lcd->setCursor(0, 0);
    lcd->print(a);
    Serial.println(a);
}   
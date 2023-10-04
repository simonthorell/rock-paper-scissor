/* 
For a bit easier LCD handling
 */
#pragma once
#ifndef aLCD_h
#define aLCD_h

#include <LiquidCrystal_I2C.h>

namespace aLCD
{
    bool i2CAddrTest(uint8_t addr)
    {
        Wire.begin();
        Wire.beginTransmission(addr);
        if (Wire.endTransmission() == 0)
        {
            return true;
        }
        return false;
    }

    LiquidCrystal_I2C startLCD()
    {
        LiquidCrystal_I2C lcd(0x27, 16, 2); // address, rows, cols
        if (!i2CAddrTest(0x27))
        {
            lcd = LiquidCrystal_I2C(0x3F, 16, 2);
        }
        lcd.init();
        lcd.backlight();
        return lcd;
    }
}

#endif
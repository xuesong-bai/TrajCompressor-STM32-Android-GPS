#ifndef __INIT_H__
#define __INIT_H__
//---------Other-------------
#include <stdint.h>
#include "stm32f10x.h"
#include "stm32f10x_iwdg.h"
#include <stdio.h>
#include <math.h>
#include <string.h>
#include "stm32f10x_i2c.h"
//---------Driver-------------
#include "delay.h"
#include "sys.h"
#include "usart.h"
#include "OSInclude.h"
//-------Hardware-------------
#include "led.h"
#include "key.h"
#include "lcd.h"
#include "usart2.h"
#include "usart3.h"
#include "wdg.h"
#include "gps.h"
#include "GPS_Ctrl.h"
#include "BL_GPRS_CTRL.h"



u8 ctrl_Init(void);

extern void LED0_on(void);
extern void LED0_off(void);
extern void LED1_on(void);
extern void LED1_off(void);

#endif


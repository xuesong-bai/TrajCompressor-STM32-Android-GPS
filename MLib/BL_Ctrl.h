#ifndef __BL_CTRL__
#define __BL_CTRL__

#include "delay.h" 			 
#include "usart.h" 	
#include "hc05.h" 
#include "OSInclude.h"
#include "LCD.h"
#include "wdg.h"
#include "GPS_Ctrl.h"
#include "string.h"

u8 BL_init(void);
void BL_SEND_task(void *pvParameters);
void BL_RECV_task(void *pvParameters);

#endif


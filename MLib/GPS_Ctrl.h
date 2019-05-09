#ifndef __GPS_CTRL__
#define __GPS_CTRL__

#include "gps.h"
#include "wdg.h"
#include "OSInclude.h"
#include "lcd.h"
#include "delay.h"
#include "usart2.h"

#include "time_shift.h"

void GPS_task(void *pvParameters);
void Gps_Msg_Show(void);
void gps_Init(void);
void time_transfer(void);


extern int status_gps;
extern _xtime UTC_time;
extern nmea_msg gpsx;
extern unsigned int Unix_time;

#endif

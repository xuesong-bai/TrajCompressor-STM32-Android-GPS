#include "gps.h"
#include "wdg.h"
#include "OSInclude.h"
#include "lcd.h"
#include "delay.h"

void gps_task(void *pvParameters);
void Gps_Msg_Show(void);
void gps_Init(void);


extern int status_gps;

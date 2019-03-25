#ifndef _SCHEDULER_H_
#define _SCHEDULER_H_

#include "init.h"
#include "GPS_Ctrl.h"

#define TRUE	1
#define FLASE 0


void start_task(void *pvParameters);

void led_task(void *pvParameters);		


#endif

#ifndef __SCHEDULER__
#define __SCHEDULER__

#include "init.h"
#include "GPS_Ctrl.h"
#include "BL_Ctrl.h"

#define TRUE	1
#define FLASE 0


void start_task(void *pvParameters);

void led_task(void *pvParameters);


#endif

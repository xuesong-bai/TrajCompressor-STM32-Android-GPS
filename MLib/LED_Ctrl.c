#include "LED_Ctrl.h"


void led_task(void *pvParameters)
{
    portTickType CurrentControlTick = 0;
    const TickType_t TimeIncrement = pdMS_TO_TICKS(500);
    for(;;)
    {
//		CurrentControlTick = xTaskGetTickCount();

        if(status_gps == 0)
        {
            LED0_off();
            LED1 =~ LED1;
        }
        else
        {
            LED0_on();
            LED1_off();
        }
//		vTaskDelay(500);
        vTaskDelayUntil(&CurrentControlTick, TimeIncrement);
    }
}

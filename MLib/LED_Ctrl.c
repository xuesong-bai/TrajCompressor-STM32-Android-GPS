#include "LED_Ctrl.h"


void led_task(void *pvParameters)
{
	for(;;)
	{
		
		
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
		vTaskDelay(500);
	}
}

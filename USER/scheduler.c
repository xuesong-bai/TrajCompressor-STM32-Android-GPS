#include "scheduler.h"


#define GPS_TASK_PRIO 2
#define GPS_STK_SIZE 50
TaskHandle_t GPSTask_Handler;
void GPS_task(void *p_arg);

#define LED_TASK_PRIO 4
#define LED_STK_SIZE 50
TaskHandle_t LEDTask_Handler;
void led_task(void *p_arg);

void start_task(void *pvParameters)
{
	taskENTER_CRITICAL();
	xTaskCreate((TaskFunction_t )gps_task,
							(const char*    )"gps_task",
							(uint16_t       )GPS_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )GPS_TASK_PRIO,
							(TaskHandle_t*  )&GPSTask_Handler);
							
	xTaskCreate((TaskFunction_t )led_task,
							(const char*    )"led_task",
							(uint16_t       )LED_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )LED_TASK_PRIO,
							(TaskHandle_t*  )&LEDTask_Handler);
	vTaskDelete(NULL);
	taskEXIT_CRITICAL();
}




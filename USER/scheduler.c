#include "scheduler.h"


#define GPS_TASK_PRIO 2
#define GPS_STK_SIZE 50
TaskHandle_t GPSTask_Handler;
void GPS_task(void *p_arg);

#define LED1_TASK_PRIO 3
#define LED1_STK_SIZE 50
TaskHandle_t LED1Task_Handler;
void led1_task(void *p_arg);

void start_task(void *pvParameters)
{
	taskENTER_CRITICAL();
	xTaskCreate((TaskFunction_t )gps_task,
							(const char*    )"gps_task",
							(uint16_t       )GPS_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )GPS_TASK_PRIO,
							(TaskHandle_t*  )&GPSTask_Handler);
							
	xTaskCreate((TaskFunction_t )led1_task,
							(const char*    )"led1_task",
							(uint16_t       )LED1_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )LED1_TASK_PRIO,
							(TaskHandle_t*  )&LED1Task_Handler);
	vTaskDelete(NULL);
	taskEXIT_CRITICAL();
}



void led1_task(void *pvParameters)
{
	while(1)
	{
		LED1 =~ LED1;
		vTaskDelay(500);
	}
}

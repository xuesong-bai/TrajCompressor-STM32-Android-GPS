#include "scheduler.h"


#define LED0_TASK_PRIO 2
#define LED0_STK_SIZE 50
TaskHandle_t LED0Task_Handler;
void led0_task(void *p_arg);

#define LED1_TASK_PRIO 3
#define LED1_STK_SIZE 50
TaskHandle_t LED1Task_Handler;
void led1_task(void *p_arg);

void start_task(void *pvParameters)
{
	taskENTER_CRITICAL();
	xTaskCreate((TaskFunction_t )led0_task,
							(const char*    )"led0_task",
							(uint16_t       )LED0_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )LED0_TASK_PRIO,
							(TaskHandle_t*  )&LED0Task_Handler);
							
	xTaskCreate((TaskFunction_t )led1_task,
							(const char*    )"led1_task",
							(uint16_t       )LED1_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )LED1_TASK_PRIO,
							(TaskHandle_t*  )&LED1Task_Handler);
	vTaskDelete(NULL);
	taskEXIT_CRITICAL();
}

void led0_task(void *pvParameters)
{
	while(1)
	{
		LED0 =~ LED0;
		vTaskDelay(500);
	}
}

void led1_task(void *pvParameters)
{
	while(1)
	{
		LED1 = 0;
		vTaskDelay(200);
		LED1 = 1;
		vTaskDelay(800);
	}
}

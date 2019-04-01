#include "scheduler.h"


#define GPS_TASK_PRIO 2
#define GPS_STK_SIZE 50
TaskHandle_t GPSTask_Handler;

#define BL_SEND_TASK_PRIO 3
#define BL_SEND_STK_SIZE 50
TaskHandle_t BL_SEND_Task_Handler;

#define BL_REC_TASK_PRIO 4
#define BL_REC_STK_SIZE 50
TaskHandle_t BL_REC_Task_Handler;

#define LED_TASK_PRIO 5
#define LED_STK_SIZE 50
TaskHandle_t LEDTask_Handler;

void start_task(void *pvParameters)
{
	taskENTER_CRITICAL();
	xTaskCreate((TaskFunction_t )gps_task,
							(const char*    )"gps_task",
							(uint16_t       )GPS_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )GPS_TASK_PRIO,
							(TaskHandle_t*  )&GPSTask_Handler);
							
	xTaskCreate((TaskFunction_t )BL_GPRS_SEND_task,
							(const char*    )"BL_GPRS_SEND_task",
							(uint16_t       )BL_SEND_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )BL_SEND_TASK_PRIO,
							(TaskHandle_t*  )&BL_SEND_Task_Handler);
							
	xTaskCreate((TaskFunction_t )BL_GPRS_REC_task,
							(const char*    )"BL_GPRS_REC_task",
							(uint16_t       )BL_REC_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )BL_REC_TASK_PRIO,
							(TaskHandle_t*  )&BL_REC_Task_Handler);
							
	xTaskCreate((TaskFunction_t )led_task,
							(const char*    )"led_task",
							(uint16_t       )LED_STK_SIZE,
							(void*          )NULL,
							(UBaseType_t    )LED_TASK_PRIO,
							(TaskHandle_t*  )&LEDTask_Handler);
	vTaskDelete(NULL);
	taskEXIT_CRITICAL();
}




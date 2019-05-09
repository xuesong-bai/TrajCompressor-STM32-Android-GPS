#include "scheduler.h"

extern TaskHandle_t StartTask_Handler;


#define GPS_TASK_PRIO 3
#define GPS_STK_SIZE 512
TaskHandle_t GPSTask_Handler;

#define GPRS_TASK_PRIO 5
#define GPRS_STK_SIZE 512
TaskHandle_t GPRS_Task_Handler;

#define BL_SEND_TASK_PRIO 4
#define BL_SEND_STK_SIZE 512
TaskHandle_t BL_SEND_Task_Handler;

#define BL_RECV_TASK_PRIO 4
#define BL_RECV_STK_SIZE 512
TaskHandle_t BL_RECV_Task_Handler;

#define LED_TASK_PRIO 2
#define LED_STK_SIZE 128
TaskHandle_t LEDTask_Handler;

void start_task(void *pvParameters)
{
    taskENTER_CRITICAL();

    xTaskCreate((TaskFunction_t )GPS_task,
                (const char*    )"GPS_task",
                (uint16_t       )GPS_STK_SIZE,
                (void*          )NULL,
                (UBaseType_t    )GPS_TASK_PRIO,
                (TaskHandle_t*  )&GPSTask_Handler);

//    xTaskCreate((TaskFunction_t )GPRS_task,
//                (const char*    )"GPRS_task",
//                (uint16_t       )GPRS_STK_SIZE,
//                (void*          )NULL,
//                (UBaseType_t    )GPRS_TASK_PRIO,
//                (TaskHandle_t*  )&GPRS_Task_Handler);

    xTaskCreate((TaskFunction_t )BL_SEND_task,
                (const char*    )"BL_SEND_task",
                (uint16_t       )BL_SEND_STK_SIZE,
                (void*          )NULL,
                (UBaseType_t    )BL_SEND_TASK_PRIO,
                (TaskHandle_t*  )&BL_SEND_Task_Handler);
								
    xTaskCreate((TaskFunction_t )BL_RECV_task,
                (const char*    )"BL_RECV_task",
                (uint16_t       )BL_RECV_STK_SIZE,
                (void*          )NULL,
                (UBaseType_t    )BL_RECV_TASK_PRIO,
                (TaskHandle_t*  )&BL_RECV_Task_Handler);

    xTaskCreate((TaskFunction_t )led_task,
                (const char*    )"led_task",
                (uint16_t       )LED_STK_SIZE,
                (void*          )NULL,
                (UBaseType_t    )LED_TASK_PRIO,
                (TaskHandle_t*  )&LEDTask_Handler);
//	vTaskDelete(NULL);
    vTaskDelete(StartTask_Handler);
    taskEXIT_CRITICAL();
//	IWDG_Feed();
}




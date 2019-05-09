#ifndef __GPRS_CTRL__
#define __GPRS_CTRL__

#include "sys.h"
#include "wdg.h"
#include "OSInclude.h"
#include "lcd.h"
#include "delay.h"
#include "usart3.h"
#include "timer.h"
#include "key.h"
#include "string.h"
#include "GPS_Ctrl.h"
#include "beep.h"

void GPRS_task(void *pvParameters);
void GPRS_REC_task(void *pvParameters);
u8 GPRS_Init(void);
void GPRS_Msg_Show(void);
//void sim_at_response(u8 mode);
u8 connect_BL(void);
u8 sim800c_send_cmd(u8 *cmd,u8 *ack,u16 waittime);
u8* sim800c_check_cmd(u8 *str);
u8 sim800c_wait_request(u8 *request ,u16 waittime);
u8 sim800c_gsm_info(void);


#endif

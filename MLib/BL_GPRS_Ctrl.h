#ifndef __BL_GPRS_CTRL__
#define __BL_GPRS_CTRL__

#include "sys.h"
#include "wdg.h"
#include "OSInclude.h"
#include "lcd.h"
#include "delay.h"
#include "usart3.h"
#include "timer.h"
#include "key.h"
#include "string.h"

void BL_GPRS_SEND_task(void *pvParameters);
void BL_GPRS_REC_task(void *pvParameters);
u8 BL_GPRS_Init(void);
void BL_GPRS_Msg_Show(void);
//void sim_at_response(u8 mode);
u8 connect_BL(void);
u8 sim800c_send_cmd(u8 *cmd,u8 *ack,u16 waittime);
u8* sim800c_check_cmd(u8 *str);
u8 sim800c_wait_request(u8 *request ,u16 waittime);
u8 sim800c_gsm_info(void);


#endif

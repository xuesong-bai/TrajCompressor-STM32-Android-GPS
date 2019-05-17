#include "BL_Ctrl.h"


__align(4) u8 blbuff[50];
u8 BL_time[100];
u8 BL_road[200];
u8 recv_flag;

u8 BL_init()
{
	u8 status = 0;
	recv_flag = 0;
	POINT_COLOR=RED;
	LCD_ShowString(30,540,300,16,16,"Bluetooth Data:");
	POINT_COLOR=BLUE;
 	while(HC05_Init()) 		//ATK-HC05
	{
		LCD_ShowString(30,560,300,16,16,"ATK-HC05 Error!"); 
		delay_ms(500);
		LCD_ShowString(30,560,300,16,16,"Please Check!!!"); 
		delay_ms(100);
	}	 										   	   
	
	return status;
}


void BL_SEND_task(void *pvParameters)
{
	portTickType CurrentControlTick = 0;
  const TickType_t TimeIncrement = pdMS_TO_TICKS(6000);
	u8 sendbuf[300];
	float tp1, tp2, tp3, tp4;
  for(;;)
  {
		tp1 = gpsx.longitude;
		tp2 = gpsx.latitude;
		tp3 = gpsx.speed;
		tp4 = gpsx.direction/100000;
		sprintf((char*)sendbuf,"T:%i,Lo:%.2f,La:%.2f,SP:%0.2f,DR:%0.2f\r\n\32",Unix_time, tp1/100000, tp2/100000,tp3/100000,tp4/100000);
		LCD_ShowString(30,560,300,16,16,sendbuf); 
//		u1_printf("T:%i,Lo:%.5f,La:%.5f\r\n",Unix_time, tp1/=100000, tp2/=100000);
		u1_printf("%i,%.2f,%.2f,%0.2f,%i",Unix_time, tp1/=100000, tp2/=100000,tp3/=100000,(int) tp4);
		
		vTaskDelayUntil(&CurrentControlTick, TimeIncrement);
  }
}

void BL_RECV_task(void *pvParameters)
{
	portTickType CurrentControlTick = 0;
  const TickType_t TimeIncrement = pdMS_TO_TICKS(100);
	u8 *p1;
//	u8 reclen=0;  	
  for(;;)
  {
		if(USART_RX_STA&0X8000)	
		{
// 			reclen=USART_RX_STA&0X7FFF;
//		  USART_RX_BUF[reclen]=0;
//			if(reclen==9||reclen==8) 
//			{
//				if(strcmp((const char*)USART_RX_BUF,"+LED1 ON")==0)LED1=0;	
//				if(strcmp((const char*)USART_RX_BUF,"+LED1 OFF")==0)LED1=1;
//			}
			USART_RX_BUF[USART_RX_STA&0X7FFF]=0;//Ìí¼Ó½áÊø·û
			sprintf((char *)blbuff,"%s",USART_RX_BUF);
			p1 =(u8*)strstr((const char*)USART_RX_BUF,",");
			if(p1!=NULL)
			{
				if(recv_flag == 0)
				{
					recv_flag = 1;
					sprintf((char *)BL_road,"%s",(u8*)(p1+1));
					*p1 = 0;
					sprintf((char *)BL_time,"%s",USART_RX_BUF);
				}
				else
				{
					sprintf((char *)BL_road,"%s;%s",BL_road, (u8*)(p1+1));
				}
				LCD_Fill(30,600,330,620,WHITE);
				LCD_ShowString(30,600,300,16,16,BL_road);
				LCD_Fill(30,620,330,640,WHITE);
				LCD_ShowString(30,620,300,16,16,BL_time);
			}
			POINT_COLOR=BLUE;
			LCD_Fill(30,580,330,600,WHITE);
      LCD_ShowString(30,580,300,16,16,blbuff);
 			USART_RX_STA=0;
		}	 															     				   


		vTaskDelayUntil(&CurrentControlTick, TimeIncrement);
  }
}


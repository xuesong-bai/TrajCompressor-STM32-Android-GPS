#include "init.h"


u8 ctrl_Init(void)
{
	int status = 0;
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);  //设置中断优先级分组为组2：2位抢占优先级，2位响应优先级
	delay_init();	    	 //延时函数初始化	  
	uart_init(115200);	 //串口初始化为115200
	usmart_dev.init(72);		//初始化USMART		
	LED_Init();         //初始化与LED连接的硬件接口
	LCD_Init();			   		//初始化LCD   
	KEY_Init();					//初始化按键
	usart3_init(38400);		//初始化串口3 
	uart_init(38400);	 //串口初始化为38400
	LED0_on();
	LED1_on();
	
	
	LCD_ShowString(30,20,200,16,16,"ALIENTEK STM32F1 ^_^");	  
	LCD_ShowString(30,40,200,16,16,"S1216F8 GPS TEST");	
	LCD_ShowString(30,60,200,16,16,"ATOM@ALIENTEK");
	LCD_ShowString(30,80,200,16,16,"KEY0:Upload NMEA Data SW");   	 										   	   
  LCD_ShowString(30,100,200,16,16,"NMEA Data Upload:ON"); 
	

	
	/*--------------------GPS-------------------*/
	gps_Init();
	
	
	/*--------------Watch Dog-------------------*/
	IWDG_Init(4,2875);    //与分频数为64,重载值为625,溢出时间为1s	  
	IWDG_Feed();
	/*------------GPRS + Bluetooth--------------*/
	
	
	
	/*-------------------END--------------------*/
	status = 1;
	LED0_off();
	LED1_off();
	return status;
}

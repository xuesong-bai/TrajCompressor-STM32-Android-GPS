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
	LED0_on();
	LED1_on();
	
	
	LCD_ShowString(30,20,200,16,16,"ALIENTEK STM32F1 ^_^");	  
	LCD_ShowString(30,40,200,16,16,"S1216F8 GPS TEST");	
	LCD_ShowString(30,60,200,16,16,"ATOM@ALIENTEK");
	LCD_ShowString(30,80,200,16,16,"KEY0:Upload NMEA Data SW");   	 										   	   
  LCD_ShowString(30,100,200,16,16,"NMEA Data Upload:ON"); 
	
	/*--------------Watch Dog-------------------*/
	IWDG_Init(4,2875);    //与分频数为64,重载值为625,溢出时间为1s	  
	
	/*--------------------GPS-------------------*/
	if(SkyTra_Cfg_Rate(5)!=0)	//设置定位信息更新速度为5Hz,顺便判断GPS模块是否在位. 
	{
   	LCD_ShowString(30,120,200,16,16,"SkyTraF8-BD Setting...");
		do
		{
			usart3_init(9600);			//初始化串口3波特率为9600
	  	SkyTra_Cfg_Prt(3);			//重新设置模块的波特率为38400
			usart3_init(38400);			//初始化串口3波特率为38400
      SkyTra_Cfg_Tp(100000);	//脉冲宽度为100ms
		}while(SkyTra_Cfg_Rate(5)!=0);//配置SkyTraF8-BD的更新速率为5Hz
	  LCD_ShowString(30,120,200,16,16,"SkyTraF8-BD Set Done!!");
		delay_ms(10);
		LCD_Fill(30,120,30+200,120+16,WHITE);//清除显示 
	}
	
	IWDG_Feed();
	/*------------GPRS + Bluetooth--------------*/
	
	
	
	/*-------------------END--------------------*/
	status = 1;
	LED0_off();
	LED1_off();
	return status;
}

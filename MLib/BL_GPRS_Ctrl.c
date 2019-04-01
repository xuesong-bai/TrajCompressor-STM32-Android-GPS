#include "BL_GPRS_Ctrl.h"


u8 Scan_Wtime = 0;//保存蓝牙扫描需要的时间 
u8 BT_Scan_mode=0; //蓝牙扫描设备模式标志

__align(4) u8 dtbuff[50];


void BL_GPRS_SEND_task(void *pvParameters)
{
	portTickType CurrentControlTick = 0;
	//u16 i, rxlen;
	u16 t = 0;
	//u8 key;
	//int connect;
	
	for(;;)
	{
		CurrentControlTick = xTaskGetTickCount();
		t += 1;
		//sim_at_response(1);	//检查GSM模块发送过来的数据
		if(t>100)
		{
			t = 0;
		}

		//vTaskDelay(10);
		vTaskDelayUntil(&CurrentControlTick, 100 / portTICK_RATE_MS);
	}

}
void BL_GPRS_REC_task(void *pvParameters)
{
	portTickType CurrentControlTick = 0;
	//u16 i, rxlen;
	u16 t = 0;
	//u8 key;
	//int connect;
	
	for(;;)
	{
		CurrentControlTick = xTaskGetTickCount();
		t += 1;
		//sim_at_response(1);	//检查GSM模块发送过来的数据
		if(t>100)
		{
			t = 0;
		}

		//vTaskDelay(10);
		vTaskDelayUntil(&CurrentControlTick, 100 / portTICK_RATE_MS);
	}

}


u8 connect_BL()
{
	u8 status = 0;
	u8 res;
	if(sim800c_send_cmd("ATE1","OK",200))//打开回显失败
	 {
		  //printf("打开回显失败");
		 status = 1;
		  return status;
	 }
	 delay_ms(10);
	 if(sim800c_send_cmd("AT+BTPOWER=1","AT",300))//打开蓝牙电源 不判断OK，因为电源原本开启再发送打开的话会返回error
     {
		  sim800c_send_cmd("ATE0","OK",200);//关闭回显功能
		 status = 1;
		  return status;
	 }			
	 sim800c_send_cmd("AT+BTUNPAIR=0","AT",100);//删除配对信息				 
	 do
	{		
		LCD_ShowString(30,480,300,16,16,"Waiting for CON request");	   
			   res = sim800c_wait_request("+BTPAIRING:",600);             //等待手机端蓝牙连接请求 6s
			   if(res==1)                                                 //手机端连接请求
			   {
				 delay_ms(10);
				 sim800c_send_cmd("AT+BTPAIR=1,1","BTPAIR:",500);         //响应连接
			   }
			   else if(res==2) return 0;                                  //按键返回上一级
			   LCD_ShowString(30,480,300,16,16,"                         ");
			   delay_ms(50);
			}while(strstr((const char*)USART3_RX_BUF,"+BTPAIR: 1")==NULL);//判断是否匹配成功
			USART3_RX_STA=0;
			LCD_ShowString(30,480,300,16,16,"Device Connected...");
			LCD_ShowString(30,500,300,16,16,"Waiting for SSP CON");
			do
	    {
			res = sim800c_wait_request("SPP",120);            //等待手机端SPP连接请求
			if(res==2)return 0;                               //按键返回上一级
			else if(res==1) break;                            //SPP连接成功
	    }while(1);
					if(!sim800c_send_cmd("AT+BTACPT=1","+BTCONNECT:",300))//应答手机端spp连接请求 3S
	    {
		   LCD_ShowString(30,500,300,16,16,"SSP connected...");
	    }
	    else  
	    {
		   LCD_ShowString(30,500,300,16,16,"SSP Failed...");
		 status = 2;
		  return status;
	    }
	return status;
}

u8 BL_GPRS_Init()
{
	u8 status = 0;
	u8 key=0; 
	u8 res;
	LCD_ShowString(30,100,300,16,16,"GPRS-BlueTooth Setting...");
	while(sim800c_send_cmd("AT","OK",100))
	{
		delay_ms(400);
	}
	LCD_ShowString(30,100,300,16,16,"GPRS-BlueTooth Set Done!!");
	key+=sim800c_send_cmd("ATE0","OK",200);//不回显
	USART3_RX_STA=0;
	
	delay_ms(50);
	BL_GPRS_Msg_Show();
	do
	{
		res = connect_BL();
		sprintf((char *)dtbuff, "BL result: %i", res);
		LCD_ShowString(30,100,300,16,16,dtbuff);
	}while(res==2);
	if(res==1)
	{
		status = 1;
	}
	return status;
}
void BL_GPRS_Msg_Show()
{
	u8 *p1, *p2;
	u8 res;
	POINT_COLOR=RED;
	LCD_ShowString(30,380,300,16,16,"BlueTooth + GPRS Data:");
	POINT_COLOR=BLUE;
	USART3_RX_STA=0;
	res = sim800c_send_cmd("AT+CGMI","OK",200);
	if(res==0)	//查询制造商名称
	{ 
		p1=(u8*)strstr((const char*)(USART3_RX_BUF+2),"\r\n");
		p1[0]=0;//加入结束符
		sprintf((char *)dtbuff,"Manufac:%s",USART3_RX_BUF+2);
		//Show_Str(30,400,300,16,dtbuff,16,0);
		LCD_ShowString(30,400,300,16,16,dtbuff);
		USART3_RX_STA=0;		
	} 
	res = sim800c_send_cmd("AT+CGMM","OK",200);
	if(res==0)//查询模块名字
	{ 
		p1=(u8*)strstr((const char*)(USART3_RX_BUF+2),"\r\n"); 
		p1[0]=0;//加入结束符
		sprintf((char *)dtbuff,"Model:%s",USART3_RX_BUF+2);
		//Show_Str(30,420,300,16,dtbuff,16,0);
		LCD_ShowString(30,420,300,16,16,dtbuff);
		USART3_RX_STA=0;		
	} 
	res = sim800c_send_cmd("AT+CGSN","OK",200);
	if(res==0)//查询产品序列号
	{ 
		p1=(u8*)strstr((const char*)(USART3_RX_BUF+2),"\r\n");//查找回车
		p1[0]=0;//加入结束符 
		sprintf((char *)dtbuff,"Serial:%s",USART3_RX_BUF+2);
		//Show_Str(30,440,300,16,dtbuff,16,0);
		LCD_ShowString(30,440,300,16,16,dtbuff);
		USART3_RX_STA=0;		
	}
	res = sim800c_send_cmd("AT+CNUM","+CNUM",200);
	if(res==0)//查询本机号码
	{
		p1=(u8*)strstr((const char*)(USART3_RX_BUF),",");
		p2=(u8*)strstr((const char*)(p1+2),"\"");
		p2[0]=0;//加入结束符
		p1=(u8*)strstr((const char*)(USART3_RX_BUF),",");
		sprintf((char *)dtbuff,"Phone:%s",p1+2);
		//Show_Str(30,460,300,16,dtbuff,16,0);
		LCD_ShowString(30,460,300,16,16,dtbuff);
		USART3_RX_STA=0;		
	}
}
//usmart支持部分
//将收到的AT指令应答数据返回给电脑串口
//mode:0,不清零USART3_RX_STA;
//     1,清零USART3_RX_STA;
//void sim_at_response(u8 mode)
//{
//	if(USART3_RX_STA&0X8000)		//接收到一次数据了
//	{ 
//		USART3_RX_BUF[USART3_RX_STA&0X7FFF]=0;//添加结束符
//		printf("%s",USART3_RX_BUF);	//发送到串口
//		if(mode)USART3_RX_STA=0;
//	} 
//}



//SIM800C发送命令
//cmd:发送的命令字符串(不需要添加回车了),当cmd<0XFF的时候,发送数字(比如发送0X1A),大于的时候发送字符串.
//ack:期待的应答结果,如果为空,则表示不需要等待应答
//waittime:等待时间(单位:10ms)
//返回值:0,发送成功(得到了期待的应答结果)
//       1,发送失败
u8 sim800c_send_cmd(u8 *cmd,u8 *ack,u16 waittime)
{
	u8 res=0; 
	USART3_RX_STA=0;
	if((u32)cmd<=0XFF)
	{
		while((USART3->SR&0X40)==0);//等待上一次数据发送完成  
		USART3->DR=(u32)cmd;
	}else u3_printf("%s\r\n",cmd);//发送命令
	
	if(waittime==1100)//11s后读回串口数据(蓝牙扫描模式)
	{
		 Scan_Wtime = 11;//需要定时的时间
		 TIM7_SetARR(9999);//产生1S定时中断
		 
	}
	if(ack&&waittime)		//需要等待应答
	{
	   while(--waittime)	//等待倒计时
	   { 
		  if(BT_Scan_mode)//蓝牙扫描模式
		  {  
			  res=KEY_Scan(0);//返回上一级
			  if(res==WKUP_PRES)return 2;
		  }
		  delay_ms(10);
		  if(USART3_RX_STA&0X8000)//接收到期待的应答结果
		  {
			  if(sim800c_check_cmd(ack))break;//得到有效数据 
			  USART3_RX_STA=0;
		  } 
	   }
	   if(waittime==0)res=1; 
	}
	return res;
} 
u8* sim800c_check_cmd(u8 *str)
{
	char *strx=0;
	if(USART3_RX_STA&0X8000)		//接收到一次数据了
	{ 
		USART3_RX_BUF[USART3_RX_STA&0X7FFF]=0;//添加结束符
		strx=strstr((const char*)USART3_RX_BUF,(const char*)str);
	} 
	return (u8*)strx;
}

//接收SIM800C返回数据（蓝牙测试模式下使用）
//request:期待接收命令字符串
//waittimg:等待时间(单位：10ms)
//返回值:0,发送成功(得到了期待的应答结果)
//       1,发送失败
u8 sim800c_wait_request(u8 *request ,u16 waittime)
{
	 u8 res = 1;
	 u8 key;
	 if(request && waittime)
	 {
	    while(--waittime)
		{   
		   key=KEY_Scan(0);
		   if(key==WKUP_PRES) return 2;//返回上一级
		   delay_ms(10);
		   if(USART3_RX_STA &0x8000)//接收到期待的应答结果
		   {
			  if(sim800c_check_cmd(request)) break;//得到有效数据
			  USART3_RX_STA=0;
		   }
		}
		if(waittime==0)res=0;
	 }
	 return res;
}

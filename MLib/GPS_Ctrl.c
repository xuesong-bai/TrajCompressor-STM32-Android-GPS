#include "GPS_Ctrl.h"

u8 TX_BUF[USART2_MAX_RECV_LEN];
nmea_msg gpsx; 											//GPS信息
__align(4) u8 dtbuf[50];   								//打印缓存器
unsigned int Unix_time;
_xtime UTC_time;
const u8*fixmode_tbl[4]= {"Fail","Fail"," 2D "," 3D "};	//fix mode字符串

int status_gps = 0;

//显示GPS定位信息
void Gps_Msg_Show(void)
{
    float tp;
    POINT_COLOR=RED;
    LCD_ShowString(30,120,200,16,16,"GPS Data:");
    POINT_COLOR=BLUE;
    tp=gpsx.longitude;
    sprintf((char *)dtbuf,"Longitude:%.5f %1c   ",tp/=100000,gpsx.ewhemi);	//得到经度字符串
    LCD_ShowString(30,140,200,16,16,dtbuf);
    tp=gpsx.latitude;
    sprintf((char *)dtbuf,"Latitude:%.5f %1c   ",tp/=100000,gpsx.nshemi);	//得到纬度字符串
    LCD_ShowString(30,160,200,16,16,dtbuf);
    tp=gpsx.altitude;
    sprintf((char *)dtbuf,"Altitude:%.1fm     ",tp/=10);	    			//得到高度字符串
    LCD_ShowString(30,180,200,16,16,dtbuf);
    tp=gpsx.speed;
    sprintf((char *)dtbuf,"Speed:%.3fkm/h     ",tp/=1000);		    		//得到速度字符串
    LCD_ShowString(30,200,200,16,16,dtbuf);
		tp=gpsx.direction;
		sprintf((char *)dtbuf,"Direction:%.1f   ",tp/=1000);	//显示方向
    LCD_ShowString(30,220,200,16,16,dtbuf);
    if(gpsx.fixmode<=3)														//定位状态
    {
        sprintf((char *)dtbuf,"Fix Mode:%s",fixmode_tbl[gpsx.fixmode]);
        LCD_ShowString(30,240,200,16,16,dtbuf);
    }
    sprintf((char *)dtbuf,"GPS+BD Valid satellite:%02d",gpsx.posslnum);	 		//用于定位的GPS卫星数
    LCD_ShowString(30,260,200,16,16,dtbuf);
    sprintf((char *)dtbuf,"GPS Visible satellite:%02d",gpsx.svnum%100);	 		//可见GPS卫星数
    LCD_ShowString(30,280,200,16,16,dtbuf);

    sprintf((char *)dtbuf,"BD Visible satellite:%02d",gpsx.beidou_svnum%100);	 		//可见北斗卫星数
    LCD_ShowString(30,300,200,16,16,dtbuf);
		
    sprintf((char *)dtbuf,"UTC Date:%04d/%02d/%02d   ",gpsx.utc.year,gpsx.utc.month,gpsx.utc.date);	//显示UTC日期
    LCD_ShowString(30,320,200,16,16,dtbuf);
    sprintf((char *)dtbuf,"UTC Time:%02d:%02d:%02d   ",gpsx.utc.hour,gpsx.utc.min,gpsx.utc.sec);	//显示UTC时间
    LCD_ShowString(30,340,200,16,16,dtbuf);

		sprintf((char *)dtbuf,"UNIX Time:%d   ", Unix_time);	//显示UTC时间
    LCD_ShowString(30,360,200,16,16,dtbuf);
}




void GPS_task(void *pvParameters)
{
    portTickType CurrentControlTick = 0;
    const TickType_t TimeIncrement = pdMS_TO_TICKS(50);
    u16 i, rxlen;
    u16 t = 0;


    for(;;)
    {
//		CurrentControlTick = xTaskGetTickCount();
        t += 1;
        if(USART2_RX_STA&(1<<15))		//接收到一次数据了
        {
            t = 0;
						IWDG_Feed();
            status_gps = 0;
            rxlen=USART2_RX_STA&0X7FFF;	//得到数据长度
            for(i=0; i<rxlen; i++)TX_BUF[i]=USART2_RX_BUF[i];
            USART2_RX_STA=0;		   	//启动下一次接收
            TX_BUF[i]=0;			//自动添加结束符
            GPS_Analysis(&gpsx,(u8*)TX_BUF);//分析字符串
            time_transfer();
            Gps_Msg_Show();				//显示信息
        }
        if(t>1000)
        {
            status_gps = 1;
        }
        //vTaskDelay(10);
        vTaskDelayUntil(&CurrentControlTick, TimeIncrement);
    }
}

void gps_Init(void)
{
    u8 flag, flag1;
    POINT_COLOR=BLACK;
    flag = SkyTra_Cfg_Rate(5);
    if(flag!=0)	//设置定位信息更新速度为5Hz,顺便判断GPS模块是否在位.
    {
        LCD_ShowString(30,80,200,16,16,"SkyTraF8-BD Setting...");
        do
        {
            usart2_init(9600);			//初始化串口3波特率为9600
            flag1 = SkyTra_Cfg_Prt(3);			//重新设置模块的波特率为38400
            usart2_init(38400);			//初始化串口3波特率为38400
            SkyTra_Cfg_Tp(100000);	//脉冲宽度为100ms
            flag = SkyTra_Cfg_Rate(5);
        } while(flag!=0&&flag1!=0);//配置SkyTraF8-BD的更新速率为5Hz
        //LCD_Fill(30,120,30+200,120+16,WHITE);//清除显示
    }
//	if(SkyTra_Cfg_Rate(5)!=0)	//设置定位信息更新速度为5Hz,顺便判断GPS模块是否在位.
//	{
//   	LCD_ShowString(30,120,200,16,16,"SkyTraF8-BD Setting...");
//		do
//		{
//			uart_init(9600);			//初始化串口3波特率为9600
//	  	SkyTra_Cfg_Prt(5);			//重新设置模块的波特率为38400
//			uart_init(115200);			//初始化串口3波特率为38400
//      flag = SkyTra_Cfg_Tp(100000);	//脉冲宽度为100ms
//		}while(SkyTra_Cfg_Rate(5)!=0&&flag!=0);//配置SkyTraF8-BD的更新速率为5Hz
//	}
    LCD_ShowString(30,80,200,16,16,"SkyTraF8-BD Set Done!!");
}
void time_transfer()
{
    UTC_time.year = gpsx.utc.year;
    UTC_time.month = gpsx.utc.month;
    UTC_time.day = gpsx.utc.date;
    UTC_time.hour = gpsx.utc.hour;
    UTC_time.minute = gpsx.utc.min;
    UTC_time.second = gpsx.utc.sec;

    Unix_time = xDate2Seconds(&UTC_time);
}


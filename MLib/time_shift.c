#include "time_shift.h"


unsigned int  xDate2Seconds(_xtime *time)
{
  static unsigned int  month[12]={
    /*01?*/xDAY*(0),
    /*02?*/xDAY*(31),
    /*03?*/xDAY*(31+28),
    /*04?*/xDAY*(31+28+31),
    /*05?*/xDAY*(31+28+31+30),
    /*06?*/xDAY*(31+28+31+30+31),
    /*07?*/xDAY*(31+28+31+30+31+30),
    /*08?*/xDAY*(31+28+31+30+31+30+31),
    /*09?*/xDAY*(31+28+31+30+31+30+31+31),
    /*10?*/xDAY*(31+28+31+30+31+30+31+31+30),
    /*11?*/xDAY*(31+28+31+30+31+30+31+31+30+31),
    /*12?*/xDAY*(31+28+31+30+31+30+31+31+30+31+30)
  };
  unsigned int  seconds = 0;
  unsigned int  year = 0;
  year = time->year-1970;       //???2100??????
  seconds = xYEAR*year + xDAY*((year+1)/4);  //????????
  seconds += month[time->month-1];      //???????????
  if( (time->month > 2) && (((year+2)%4)==0) )//2008????
    seconds += xDAY;            //???1???
  seconds += xDAY*(time->day-1);         //?????????
  seconds += xHOUR*time->hour;           //??????????
  seconds += xMINUTE*time->minute;       //??????????
  seconds += time->second;               //??????<br> 
	seconds -= 8 * xHOUR;
  return seconds;
}

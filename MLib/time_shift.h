#ifndef __TIME_SHIFT__
#define __TIME_SHIFT__

#include "math.h"

typedef struct t_xtime {
  int year; int month;  int day;  
  int hour; int minute;  int second;
} _xtime ;
 
#define xMINUTE   (60             ) //1????
#define xHOUR      (60*xMINUTE) //1?????
#define xDAY        (24*xHOUR   ) //1????
#define xYEAR       (365*xDAY   ) //1????


unsigned int  xDate2Seconds(_xtime *time);

#endif 


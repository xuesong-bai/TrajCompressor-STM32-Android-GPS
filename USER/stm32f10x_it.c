/**
  ******************************************************************************
  * @file    GPIO/IOToggle/stm32f10x_it.c
  * @author  MCD Application Team
  * @version V3.5.0
  * @date    08-April-2011
  * @brief   Main Interrupt Service Routines.
  *          This file provides template for all exceptions handler and peripherals
  *          interrupt service routine.
  ******************************************************************************
  * @attention
  *
  * THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
  * WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
  * TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
  * DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS ARISING
  * FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS OF THE
  * CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.
  *
  * <h2><center>&copy; COPYRIGHT 2011 STMicroelectronics</center></h2>
  ******************************************************************************
  */

/* Includes ------------------------------------------------------------------*/
#include "stm32f10x_it.h"



void NMI_Handler(void)
{
}

void HardFault_Handler(void)
{

    if (CoreDebug->DHCSR & 1)
    {
        __breakpoint(0);
    }
//	NVIC_SystemReset();
    /* Go to infinite loop when Hard Fault exception occurs */
    while (1)
    {
    }
}

void MemManage_Handler(void)
{
    /* Go to infinite loop when Memory Manage exception occurs */
    while (1)
    {
    }
}


void BusFault_Handler(void)
{
    /* Go to infinite loop when Bus Fault exception occurs */
    while (1)
    {
    }
}

void UsageFault_Handler(void)
{
    /* Go to infinite loop when Usage Fault exception occurs */
    while (1)
    {
    }
}

//void SVC_Handler(void)
//{
//}

void DebugMon_Handler(void)
{
}

//void PendSV_Handler(void)
//{
//}

//void SysTick_Handler(void)
//{
//}


//static __INLINE void NVIC_SystemReset(void)
//{
//  SCB->AIRCR  = ((0x5FA << SCB_AIRCR_VECTKEY_Pos)      |
//                 (SCB->AIRCR & SCB_AIRCR_PRIGROUP_Msk) |
//                 SCB_AIRCR_SYSRESETREQ_Msk);                   /* Keep priority group unchanged */
//  __DSB();                                                     /* Ensure completion of memory access */
//  while(1);                                                    /* wait until reset */
//}
/******************************************************************************/
/*                 STM32F10x Peripherals Interrupt Handlers                   */
/*  Add here the Interrupt Handler for the used peripheral(s) (PPP), for the  */
/*  available peripheral interrupt handler's name please refer to the startup */
/*  file (startup_stm32f10x_xx.s).                                            */
/******************************************************************************/

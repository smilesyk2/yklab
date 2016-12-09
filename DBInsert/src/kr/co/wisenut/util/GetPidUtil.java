/*
 * @(#)GetPidUtil.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;

/**
 *
 * GetPidUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetPidUtil {

    /**
     *
     * @return PID NUMBER
     * @throws GetPidUtilException Error info
     */
    public static int getPid() throws GetPidUtilException {
        int pid=-1;
        try{
            pid = (new GetPid()).getPid();
        }catch(UnsatisfiedLinkError e){
            throw new GetPidUtilException();
        }
        return pid;
    }

    public static void main(String args[]){
        try{
            System.out.println("Current pid: " +(new GetPidUtil()).getPid());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

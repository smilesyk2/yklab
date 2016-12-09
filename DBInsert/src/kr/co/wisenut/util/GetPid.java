/*
 * @(#)GetPid.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;

/**
 *
 * GetPid
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public final class GetPid {
    public native int getPid();

    /**
     * CHECK!! Only Windows OS 
     * GetPid  -Djava.library.path=$LIBRARY OR set path=$LIBRARY
     * getpid.dll
     *
     * ex) -Djava.library.path=c:\sf-1\lib
     */
    static {
        try {
            System.loadLibrary("getpid");
        } catch(UnsatisfiedLinkError e){
            throw e;
        }
    }

}

/*
 * @(#)ILogger.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.logger;
/**
 *
 * ILogger
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface ILogger {
	
	/**
	 * 로그 패스가 /log/bridge/yyyy/mm/yymmdd_info.log 형태로 생성하는 타입
	 * sf-1 5.0 이전 버전과 동일한 로그 형태
	 */
	public static final String SF1_VERSION_TYPE_4 = "4";
	/**
	 * 로그 패스가 /log/bridge/brg-{source}.info.yyyymmdd.log.log 형태로 생성하는 타입
	 * sf-1 5.0 과 동일한 로그 형태
	 */	
	public static final String SF1_VERSION_TYPE_5 = "5";
	
    public static final int CRIT = Integer.MIN_VALUE;
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int INFO = 3;
    public static final int DEBUG = 4;
    public static final String STDOUT = "SDTOUT";
    public static final String ERROUT = "ERROUT";
    public static final String DAILY = "DAILY";
    
    public void log(String message);

    public void log(Exception ex);

    public void log(String message, int verbosity);

    public void log(Exception exception, String msg);

    public void log(String message, Throwable throwable);

    public void log(String message, Throwable throwable, int verbosity);

    public void error(String message);

    public void error(Exception ex);

    public void verbose(String message);

    public void finalize();
}

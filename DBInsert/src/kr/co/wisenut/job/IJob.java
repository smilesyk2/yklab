/*
 * @(#)IJob.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job;

import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.config.Config;

/**
 *
 * IJob
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface IJob {
    public static final int EXCEL = 0;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;

    public static final String STOPPED = "STOPPED";
    public static final String STARTING = "STARTING";
    public static final String RUNNING = "RUNNING";
    public static final String STOPPING = "STOPPING";


    public IJob getInstance();

    public String getState();

    public boolean run() throws StringException;

    public void stop() throws StringException;

    public void setConfig(Config config) throws ConfigException;

    public Config getConfig();

    public void log(String msg);

    public void log(String msg, int level);

    public void log(Exception ex);

    public void error(String msg);

    public void error(Exception ex);
}

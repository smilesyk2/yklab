/*
 * @(#)JobFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job;

import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.config.Config;

/**
 *
 * JobFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class JobFactory {
    private static IJob m_job;

    public static IJob getInstance(Config config, int mode) throws StringException   {

    	if(mode == IJob.EXCEL){
    		m_job = new JobExcute(config,IJob.EXCEL);
    	}else if(mode == IJob.INSERT){
    		m_job = new JobExcute(config,IJob.INSERT);
    	}else if(mode == IJob.UPDATE){
    		m_job = new JobExcute(config,IJob.UPDATE);
    	}
        return m_job;
    }
}

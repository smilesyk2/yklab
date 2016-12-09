/*
 * @(#)Job.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.config.Config;
import kr.co.wisenut.config.catalogInfo.Mapping;
import kr.co.wisenut.config.datasource.DataSource;
import kr.co.wisenut.config.source.Source;
import kr.co.wisenut.job.db.DBJob;
import kr.co.wisenut.job.excel.ExcelJob;
import kr.co.wisenut.logger.Log2;

/**
 *
 * Job
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public abstract class Job implements IJob{
    protected int m_mode = -1;
    protected Config m_config;
    protected IJob m_job;
    protected Source m_source;
    protected Mapping m_collection;
    protected DBJob m_dbjob;
    protected ExcelJob m_exjob;

    public Job(Config config, int mode) throws StringException {
        m_config = config;
        m_mode = mode;
        m_job = this;
        m_source = config.getSource();
        m_collection = config.getCollection();
        m_dbjob = new DBJob((DataSource)config.getDataSource().get(config.getSource().getTargetDSN()));

        if(mode == IJob.EXCEL){
        	if(!config.getSource().getExcelPath().equals("")){
        		m_exjob = new ExcelJob(m_source.getExcelPath(), m_source.getSheetIdx());
        	}else{
        		throw new StringException("[Error] [Job] [Missing or Not exist <Source> - <Excel> - path ]");
        	}
        }else if(mode == IJob.INSERT){
        	if(!config.getSource().getExcelPath().equals("")){
        		m_exjob = new ExcelJob(m_source.getExcelPath(), m_source.getSheetIdx());
        	}else{
        		throw new StringException("[Error] [Job] [Missing or Not exist <Source> - <Excel> - path ]");
        	}
        }else if(mode == IJob.UPDATE){
        	if(!config.getSource().getExcelPath().equals("")){
        		m_exjob = new ExcelJob(m_source.getExcelPath(), m_source.getSheetIdx());
        	}else{
        		throw new StringException("[Error] [Job] [Missing or Not exist <Source> - <Excel> - path ]");
        	}
        }

    }

    public IJob getInstance(){
        return m_job;
    }

    public String getState() {
        return null;
    }

    public abstract boolean run() throws StringException;

    //public abstract void destroy();

    public void stop() throws StringException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setConfig(Config config) throws ConfigException {
        m_config = config;
    }

    public Config getConfig() {
        return m_config;
    }

    public void log(String msg){
        Log2.out(msg);
    }

    public void log(String msg, int level){
        Log2.debug(msg, level);
    }

    public void log(Exception ex){
        Log2.error(ex);
    }

    public void error(String msg){
        Log2.error(msg);
    }

    public void error(Exception ex){
        Log2.error(ex);
    }

    public void debug(String msg, int level){
        Log2.debug(msg, level);
    }

    public void debug(String msg) {
        Log2.debug(msg, 4);
    }
}

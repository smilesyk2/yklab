/*
 * @(#)Logger.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.logger;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import kr.co.wisenut.util.DateUtil;
import kr.co.wisenut.util.FileUtil;

/**
 *
 * Logger
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Logger implements ILogger {
    private boolean isStdout = false;
    private boolean isDebug = false;
    private boolean isVerbose = false;
    protected String m_srcid;
    protected String m_logBase;
    private PrintWriter m_log;
    private PrintWriter m_err;
    private String m_logtype = DAILY;
    protected final static Object lock = new Object();
    protected int verbosity = INFO;
    private String m_module = "DBInsert-";


    public Logger(String logBase, String logType, boolean debug, int verbosity, String modName) {
        setLogBase(logBase);
        setHowtoLog(logType);
        setLogSrcId("");
        setModuleName(modName);
        setDebug(debug);
        setPrintWriter();
        setVerbosity(verbosity);
    }

	private void setModuleName(String module) {
        m_module = module;
    }

    private void setLogBase(String logBasePath) {
        m_logBase = logBasePath;
    }


    private void setPrintWriter(){
        String logDate ;
        String logFileName ;
        String errFileName ;

        if(m_logtype.equals(DAILY)){ logDate =  DateUtil.getCurrentDate();
        } else {
            return;
        }

        try {
            FileUtil.makeDir(m_logBase);
        } catch (Exception e) {
            e.printStackTrace();
        }


		logFileName = m_logBase + FileUtil.fileseperator + m_module + m_srcid+".info." + logDate + ".log";
		errFileName = m_logBase + FileUtil.fileseperator + m_module + m_srcid+".error." + logDate + ".log";

        makeLogFile(logFileName, errFileName);
    }

    private void makeLogFile(String logFileName, String errFileName){
        if(m_log == null || ! new File(logFileName).exists()){
            try {
                m_log  = new PrintWriter(new BufferedWriter(new FileWriter(logFileName, true)), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(m_err == null || ! new File(errFileName).exists()){
            try {
                m_err  = new PrintWriter(new BufferedWriter(new FileWriter(errFileName, true)), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLogSrcId(String SrcId){
        m_srcid = SrcId;
    }

    private void setHowtoLog(String logType) {
        if(logType.equals("stdout")) {
            m_logtype = STDOUT;
        } else if(logType.equals("day")) {
            m_logtype = DAILY;
        }
    }

    private void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    private void setDebug(boolean debug) {
        isDebug = debug;
        if(isDebug) {
            setVerbosity(DEBUG);
        }
    }

    private String getTitle(int level) {
        String title = "";
        switch(level) {
            case CRIT:
                title = "[Crit] ";
                break;
            case ERROR:
                title = "[Error] ";
                break;
            case WARNING:
                title = "[Warning] ";
                break;
            case INFO:
                title = "[Info] ";
                break;
            case DEBUG:
                title = "[Debug] ";
                break;
            default:
                break;
        }
        return title;
    }

    public void log(String msg){
        if( !isStdout && m_log != null ) {
            synchronized (lock) {
                setPrintWriter();
                m_log.println("["+DateUtil.getTimeStamp()+"]"+" ["+DateUtil.getCurrSysTime()+"] " + msg);
                m_log.flush();
            }
        }
        System.out.println(msg);
    }

	public void trace(String msg) {
        if( !isStdout && m_log != null ) {
            synchronized (lock) {
                setPrintWriter();
                m_log.println("["+DateUtil.getTimeStamp()+"]"+" ["+DateUtil.getCurrSysTime()+"] " + msg);
                m_log.flush();
            }
        }
        //System.out.println(msg);
    }

    public void error(String msg) {
        if( !isStdout && m_err != null ) {
            synchronized (lock) {
                setPrintWriter();
                m_err.println("["+DateUtil.getTimeStamp()+"]"+" ["+DateUtil.getCurrSysTime()+"] [error] " + msg);
                m_err.flush();
            }
        }
        System.out.println("[error] " + msg);
    }

    public void log(Exception ex) {
        error(ex.toString());
    }

    public void error(Exception ex) {
        error(ex.toString());
    }

    public void log(Exception exception, String msg) {
        error(msg+"\n"+ exception.toString());
    }

    public void log(String msg, Throwable throwable) {
        CharArrayWriter buf = new CharArrayWriter();
        error(buf.toString());
    }

    public void log(String message, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(getTitle(verbosity)+message);
        }
    }

    public void log(String message, Throwable throwable, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message, throwable);
        }
    }

    public void finalize() {
        try {
            if (m_log != null) {
                m_log.flush();
            }
            if (m_err != null) {
                m_err.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verbose(String message){
        if(isVerbose){
            System.out.println(message);
        }
    }

    public void verbose(String title, String message){
        verbose("[" + title + "] " + message);
    }


}

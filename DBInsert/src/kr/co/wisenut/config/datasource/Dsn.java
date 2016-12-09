/*
 * @(#)Dsn.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.config.datasource;

import java.util.Properties;

/**
 *
 * Dsn
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Dsn {
    private String driver;
    private String url;
    private Properties prps;
    private Class dbms;

    public Class getDbms() {
        return dbms;
    }

    public void setDbms(Class dbms) {
        this.dbms = dbms;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Properties getPrps() {
        return prps;
    }

    public void setPrps(Properties prps) {
        this.prps = prps;
    }
}

/*
 * @(#)InfoSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.db;


/**
 *
 * InfoSet
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DBInfoSet {
    private String columnName = "";

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

}

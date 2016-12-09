/*
 * @(#)Source.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.config.source;


/**
 *
 * Source
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Source {
    private String targetDBSrc = "";
    private String logDBSrc = "";

    private String excelPath = "";
    private String tableName = "";
    private String dbKey = "";
    private int sheetIdx;
    private int firstRow;
    private int firstColumn;

	public String getDbKey() {
		return dbKey;
	}

	public void setDbKey(String dbKey) {
		this.dbKey = dbKey;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getSheetIdx() {
		return sheetIdx;
	}

	public void setSheetIdx(int sheetIdx) {
		this.sheetIdx = sheetIdx;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getFirstColumn() {
		return firstColumn;
	}

	public void setFirstColumn(int firstColumn) {
		this.firstColumn = firstColumn;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String getTargetDSN() {

        return targetDBSrc;
    }

    public void setTargetDSN(String targetDBSrc) {

        this.targetDBSrc = targetDBSrc;
    }

    public String getLogDSN() {
        return logDBSrc;
    }

    public void setLogDSN(String logDBSrc) {
        this.logDBSrc = logDBSrc;
    }

}

/*
 * @(#)InfoSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.excel;


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
public class ExcelInfoSet {
    private String cellData = "";


    public String getCellData() {
		return cellData;
	}

	public void setCellData(String cellData) {
		this.cellData = cellData;
	}
}

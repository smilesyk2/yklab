/*
 * @(#)Mapping.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.excel;

import java.util.ArrayList;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.util.FormatColumn;

/**
 *
 * Mapping field infomation view
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class ExcelMapping {
    private ArrayList<ExcelInfoSet> ex_catalog = null;

    /**
     * Catalog Info Mapping Field Set Function
     * @param catalog InfoSet[]
     */
    public void setExcelCatalog(ArrayList<ExcelInfoSet> ex_catalog){
        this.ex_catalog = ex_catalog;
    }
    /**
     * Catalog Info Mapping Field Return Function
     * @return InfoSet Class Array
     */
    public ArrayList<ExcelInfoSet> getExcelCatalog(){
        return ex_catalog;
    }
    /**
     *  Catalog Info Size Return Function
     * @return m_catalog.length
     */
    public int size(){
        if(ex_catalog == null){
            return 0;
        } else {
            return ex_catalog.size();
        }
    }
}
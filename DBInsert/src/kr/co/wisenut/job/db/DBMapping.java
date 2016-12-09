/*
 * @(#)Mapping.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.db;

import java.util.ArrayList;


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
public class DBMapping {
    private ArrayList<DBInfoSet> db_catalog = null;

    /**
     * Catalog Info Mapping Field Set Function
     * @param catalog InfoSet[]
     */
    public void setDBCatalog(ArrayList<DBInfoSet> db_catalog){
        this.db_catalog = db_catalog;
    }
    /**
     * Catalog Info Mapping Field Return Function
     * @return InfoSet Class Array
     */
    public ArrayList<DBInfoSet> getDBCatalog(){
        return db_catalog;
    }
    /**
     *  Catalog Info Size Return Function
     * @return m_catalog.length
     */
    public int size(){
        if(db_catalog == null){
            return 0;
        } else {
            return db_catalog.size();
        }
    }

}
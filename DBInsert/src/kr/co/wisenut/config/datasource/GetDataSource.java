/*
 * @(#)GetDataSource.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.config.datasource;

import java.util.HashMap;
import java.util.List;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.util.XmlUtil;

import org.jdom.Element;

/**
 *
 * GetDataSource
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetDataSource extends XmlUtil {
    private Element rootElement;

    public GetDataSource(String path) throws ConfigException {
        super(path);
        rootElement = getRootElement();
    }

    public HashMap getDataSource() throws ConfigException, StringException {
        List lds = rootElement.getChildren("Database");
        int size = lds.size();
        HashMap mapDB = new HashMap(size);
        String[] duplecate = new String[size];
        String id = "";
        DataSource ds = null;
        String[] driver = null;
        for(int i=0; i<size;i++){
            ds = new DataSource();
            Element element = (Element) lds.get(i);
            id = element.getChildText("Id");

            //Check Duplicated DataSource ID
            for(int j=0; j<size;j++){
                //debug(duplecate[j]);
                if(duplecate[j] != null && duplecate[j].equals(id)){
                    throw new ConfigException(": Duplicated DataSource ID. " +
                            "Please check the <DataSource> - <Database> - <Id> in datasource.xml.");
                }
            }
            duplecate[i] = id;
            ds.setId(id);
            ds.setChar_set(getElementChildText(element, "CharSet", ""));
            ds.setVender(getDBVender(getElementChildText(element, "Vendor")));
            ds.setSid(getElementChildText(element, "SID", ""));
            ds.setDataBaseName(getElementChildText(element, "DatabaseName"));
            ds.setUser(getElementChildText(element, "User"));
            ds.setPwd(getElementChildText(element, "Password"));
            ds.setPort(getElementChildText(element, "PortNumber"));
            ds.setServerName(getElementChildText(element, "ServerName"));
            ds.setJdbcUrl(getElementChildText(element, "Url", ""));

            List listDriver = element.getChildren("Driver");
            int dSize = listDriver.size();
            driver  = new String[dSize];
            for (int j = 0; j < dSize; j++) {
                Element eleDriver = (Element) listDriver.get(j);
                driver[j] = eleDriver.getText() ;
                ds.setDriver(driver);
            }

            ds.setClassname(getElementChildText(element, "ClassName"));
            mapDB.put(id, ds);
        }
        return mapDB;
    }

    protected int getDBVender(String vender) throws StringException {
        int _ivender = -1;
        vender = vender.toLowerCase();
        if(vender.equals("oracle")){
          //  _ivender = DBVender.ORACLE;
        	_ivender = 0;
        }/* else if(vender.equals("oracle_oci")) {
            _ivender = DBVender.ORACLE_OCI;
        } else if(vender.equals("mssql")) {  //MSSQL JTDS JDBC Driver
            _ivender = DBVender.MSSQL;
        } else if(vender.equals("mssql_jtds")) { //MSSQL JTDS JDBC Driver
            _ivender = DBVender.MSSQL;
        } else if(vender.equals("mssql2005")) { //MSSQL 2005 Microsoft JDBC Driver
            _ivender = DBVender.MSSQL2005;
        } else if(vender.equals("mssql_microsoft")) { //MSSQL 2005 Microsoft JDBC Driver
            _ivender = DBVender.MSSQL2005;
        } else if(vender.equals("mysql")) {
            _ivender = DBVender.MYSQL;
        } else if(vender.equals("informix")) {
            _ivender = DBVender.INFORMIX;
        } else if(vender.equals("db2")) {
            _ivender = DBVender.DB2;
        }else if(vender.equals("as400")) {
            _ivender = DBVender.AS400;
        } else if(vender.equals("sybase")) {  //Sybase JTDS JDBC Driver
            _ivender = DBVender.SYBASE;
        } else if(vender.equals("sybase_ase")) { //Sybase Adaptive Server Enterprise JDBC Driver
            _ivender = DBVender.SYBASE_ASE;
        } else if(vender.equals("sybase_jtds")) {  //Sybase JTDS JDBC Driver
            _ivender = DBVender.SYBASE;
        } else if(vender.equals("access")) {
            _ivender = DBVender.ACCESS;
        } else if(vender.equals("unisql")) {
            _ivender = DBVender.UNISQL;
        } else if(vender.equals("kdbapp")) {
            _ivender = DBVender.KDB_APP;
        } else if(vender.equals("postgre")) {
            _ivender = DBVender.POSTGRE;
        } else if(vender.equals("derby")) {
            _ivender = DBVender.DERBY;
        } else if(vender.equals("symfoware")) {
            _ivender = DBVender.SYMFOWARE;
        } else if(vender.equals("cubrid")) {
            _ivender = DBVender.CUBRID;
        } else if(vender.equals("tibero")){
            _ivender = DBVender.TIBERO;
        } else if(vender.equals("altibase")){
            _ivender = DBVender.ALTIBASE;
        } */else {
            throw new StringException("Please check the DBVender in xml.");
        }
        return _ivender;
    }
}

/*
 * @(#)GetSource.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.config.source;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.config.catalogInfo.GetCatalogInfo;
import kr.co.wisenut.config.catalogInfo.Mapping;
import kr.co.wisenut.util.XmlUtil;

import org.jdom.Element;

/**
 *
 * GetSource
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetSource extends XmlUtil{
    private String srcid = null;
    private Element element = null;

    public GetSource(String path, String srcid) throws ConfigException {
        super(path);
        HashMap m_source_map = getElementHashMap("Source");
        this.srcid = srcid;
        element = (Element) m_source_map.get(srcid);
        if(element == null) {
            throw new ConfigException(": Missing <Source id=\""+srcid+"\"> " +
                    "setting in configuration file." );
        }
    }

    /**
     * GetCatalogInfo Return function
     * @return  Mapping Class
     * @throws ConfigException error
     */
    public Mapping getCollection() throws ConfigException {
        return new GetCatalogInfo(element).getCatalogInfo();
    }
    
    /**
     * GetStatusTableInfo Return function
     * @return  Mapping Class
     * @throws ConfigException error
     */

    public Source getSource() throws ConfigException {
        Source source = new Source();
        if(element == null){
            throw new ConfigException(": Missing " +
                    "<Source id="+srcid+"> setting in configuration file." +
                    "Could not parse Source Config.");
        }

        // <DSN type="target" dsn="dbsrcid" encrypt="y"/>
        source.setTargetDSN(getElementListValue(element.getChildren("DSN"), "target"));
        source.setLogDSN(getElementListValue(element.getChildren("DSN"), "log"));

        //excel table name 및 path 저장
        source.setExcelPath(getElementValue(element.getChild("Excel"),"path",""));
        source.setTableName(getElementValue(element.getChild("Excel"),"tablename",""));
        source.setSheetIdx(Integer.parseInt(getElementValue(element.getChild("Excel"),"sheetIdx","")));
        source.setFirstRow(Integer.parseInt(getElementValue(element.getChild("Excel"),"firstRow","")));
        source.setFirstColumn(Integer.parseInt(getElementValue(element.getChild("Excel"),"firstColumn","")));
        source.setDbKey(getElementValue(element.getChild("CatalogInfo"),"dbkey",""));

        return source;
    }




	/**
     * Get DNS ID
     * @param list Element list
     * @param mode database type
     * @return  dsn id
     * @throws ConfigException error info
     */
    protected String getElementListValue(List list,  String mode) throws ConfigException {
        String retStr = "";
        Element element ;

        for(int i=0; i< list.size(); i++){
            element = (Element) list.get(i);
            String value = getElementValue(element, "type");
            if(!value.equals("") && value.equals(mode)){
                if(mode.equals("target")) {
                    retStr = getElementValue(element, "dsn");
                }else {
                    retStr = getElementValue(element, "dsn", "");
                }
                break;
            }
        }
        return retStr;
    }

}

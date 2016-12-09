package kr.co.wisenut.config.catalogInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.logger.Log2;

import org.jdom.Element;

/**
 *
 * GetCatalogInfo
 *
 * XML <Source> - <CatalogInfo>의 값들을 set
 *
 * @author  이준명
 *
 */
public class GetCatalogInfo {
    private Element element = null;
    /**
     * GetCatalogInfo constructor
     * @param element xml
     */
    public GetCatalogInfo(Element element) {
        this.element = element;
    }
    /**
     * Get Mapping node infomation method
     * @return Mapping object
     * @throws ConfigException error info
     */
    public Mapping getCatalogInfo() throws ConfigException {
        Log2.debug("[GetCatalogInfo] [Read CatalogInfo]", 4);
        ArrayList<InfoSet> catalog = null;
        Mapping mapping = new Mapping();
        if(element == null){
            throw new ConfigException(": Missing <Source> - <CatalogInfo> setting in configuration file.");
        }

        List list = element.getChildren("CatalogInfo");
        Iterator irc = list.iterator();
        while (irc.hasNext()) {
            Element elementCatalog = (Element) irc.next();
            List initmapping = elementCatalog.getChildren("Mapping");

            int size = initmapping.size();
            catalog = new ArrayList<InfoSet>();
            for (int k = 0; k < size; k++) {
            	InfoSet info = new InfoSet();
                Element nextmpping = (Element) initmapping.get(k);

                if(getElementValue(nextmpping, "fieldname")!=null){                	
                	info.setFieldName(getElementValue(nextmpping, "fieldname").trim());
                }
                if(getElementValue(nextmpping, "subquery")!=null){
                	info.setSubquery(getElementValue(nextmpping, "subquery").trim());
                }
                if(getElementValue(nextmpping, "autoIncrement")!=null){
                	info.setAutoIncrement(getElementValue(nextmpping, "autoIncrement").trim());
                }
                // subquery설정에서 skip 값이 설정되어 있지 않으면 N 으로 설정.
                if(getElementValue(nextmpping, "value")!=null){
                	info.setValue(getElementValue(nextmpping, "value").trim());
                }
                if(getElementValue(nextmpping, "skip")!=null){
                	info.setSkip(getElementValue(nextmpping, "skip").trim());
                }
                if(getElementValue(nextmpping, "type")!=null){
                	info.setType(getElementValue(nextmpping, "type").trim());
                }
                catalog.add(info);
                //catalog[k] = new InfoSet();
                //catalog[k].setFieldName(getElementValue(nextmpping, "fieldname"));
            }
        }
        mapping.setCatalog(catalog);
        return mapping;
    }
    
    /**
     * Element의 Value Return Function
     * @param element Element
     * @param name lement name
     * @return element
     * @throws ConfigException error info
     */
    protected String getElementValue(Element element, String name) throws ConfigException {
        if(element.getAttribute(name) != null){
            return element.getAttribute(name).getValue().trim();
        } else {
        	//throw new ConfigException(": Missing <"+element.getName()+" /> "+name+" setting in configuration file.");
        	return null;
        }
    }

    /**
     * Element의 Value Return Function
     * @param element Element
     * @param name element name
     * @param def initialize value
     * @return  element xml
     * @throws ConfigException error info
     */
    protected String getElementValue(Element element, String name, String def) throws ConfigException {
        if(element.getAttribute(name) != null){
            return element.getAttribute(name).getValue().trim();
        } else {
            return def;
        }
    }

    /**
     * Element의 List Return Function
     * @param element Element
     * @param childName element name
     * @return  List
     */
    protected List getChildrenElementList(Element element, String childName){
        if(element != null && element.getChildren(childName) != null){
            return element.getChildren(childName);
        } else {
            return null;
        }
    }
}

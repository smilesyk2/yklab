package kr.co.wisenut.config.catalogInfo;

import java.util.ArrayList;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.util.FormatColumn;

/**
 *
 * Mapping
 *
 * <Source> - <CatalogInfo> - <Mapping> - fieldName을 저장하는 infoset을 배열로 저장
 *
 * @author  이준명
 *
 */
public class Mapping {
    private ArrayList<InfoSet> m_catalog = null;

    /**
     * Catalog Info Mapping Field Set Function
     * @param catalog InfoSet[]
     */
    public void setCatalog(ArrayList<InfoSet> catalog){
        m_catalog = catalog;
    }
    /**
     * Catalog Info Mapping Field Return Function
     * @return InfoSet Class Array
     */
    public ArrayList<InfoSet> getCatalog(){
        return m_catalog;
    }
    /**
     *  Catalog Info Size Return Function
     * @return m_catalog.length
     */
    public int size(){
        if(m_catalog == null){
            return 0;
        } else {
        	int catalogSize = 0;
        	for(InfoSet e : m_catalog){
            	// subquery에 매핑할 값을 넣을 부분이 없으면 catalog 크기에서 제외.
                if( e.getValue()!=null || e.getAutoIncrement()!=null || e.getSkip()!=null ){         	
                	continue;
                }
                
                catalogSize++;
            }
        	
        	return catalogSize;
        }
    }

    /**
     *  Catalog Infomation function
     * @throws ConfigException error
     */
    public void viewInfo() throws ConfigException {
        FormatColumn sutil = new FormatColumn();
        int size = m_catalog.size();
        String customClass = "";
        String line = "\n------------------------------------------------------------------------------------------------\n";
        String msg = line;
        msg += sutil.formatColumn("Number", 8);
        msg += sutil.formatColumn("XML Field", 25);
        msg += sutil.formatColumn("Value", 10);
        msg += line;
        //for(int i=0;i<size;i++){

        for(int idx = 0; idx < m_catalog.size(); idx++){
        	InfoSet info = m_catalog.get(idx);
        	msg += sutil.formatColumn("  "+ Integer.toString(idx) , 8);
            msg += sutil.formatColumn(info.getFieldName(), 25);
            msg += sutil.formatColumn(info.getValue(), 10);
            
            if(idx<size-1) {
            	msg += "\n";
            }
        }


        debug(msg+line);
        if(!customClass.equals("")) {
            debug("[Mapping ] [ICustom Class Filed Info" + customClass+"]");
        }
    }

    private void debug(String msg){
        System.out.println(msg);
    }

}
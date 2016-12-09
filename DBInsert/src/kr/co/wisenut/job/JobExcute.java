/*
 * @(#)JobStcDyn.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.config.Config;
import kr.co.wisenut.config.catalogInfo.InfoSet;
import kr.co.wisenut.job.db.DBInfoSet;
import kr.co.wisenut.job.excel.ExcelInfoSet;
import kr.co.wisenut.logger.Log2;

/**
 *
 * JobStcDyn
 *
 * Copyright 2000-2012 WISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 19 Jun 2012
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,5. 2012/06/19 Bridge Release
 *
 */

public class JobExcute extends Job{

	private String tableName = "";
	private String mode = "";
	private String dbKey = "";

    public JobExcute(Config config, int mode) throws StringException  {
        super(config, mode);

        if(mode == 0){ 		 this.mode = "excel"; }
        else if(mode == 1){ this.mode = "insert"; }
        else if(mode == 2){ this.mode = "update"; }

        this.tableName = config.getSource().getTableName();
        this.dbKey = config.getSource().getDbKey();

        Log2.out("[Info] [JobExcute] ["+this.mode+" mode]");

    }

    public boolean run() throws StringException {

    	long sqlCnt = 0;
    	boolean sqlChk = true;
    	
    	int firstRow = m_config.getSource().getFirstRow();
    	int firstColumn = m_config.getSource().getFirstColumn();

        try {


        	if(mode.equals("excel") || mode.equals("insert") || mode.equals("update")){
        		//해당 테이블 필드 정보를 list
        		//ArrayList<DBInfoSet> db_catalog = m_dbjob.excuteSelect(tableName).getDBCatalog();
        		ArrayList<InfoSet> catalog = m_collection.getCatalog();
        		//ArrayList<ExcelInfoSet> ex_catalog = m_exjob.getCellData().getExcelCatalog();

        		m_exjob.addSheetData(firstRow, firstColumn);

        		Log2.debug("[Debug] [JobExecute] m_collection.size() : " + m_collection.size());
        		Log2.debug("[Debug] [JobExecute] m_exjob.getColumnCount() : " + m_exjob.getColumnCount());
        		if( m_collection.size() == m_exjob.getColumnCount()){
        			m_collection.viewInfo();
        			//DbXmlCheck(db_catalog,catalog);
        			int sheetDataSize = m_exjob.getSheetSize();

        			Log2.out("[Info] [JobExcute] [DB MergeQuery Start]");

        			for(int sIdx = 0; sIdx < sheetDataSize; sIdx++){
        				ArrayList<ExcelInfoSet> ex_catalog = m_exjob.getCellData(sIdx).getExcelCatalog();
        				LinkedHashMap<String,String[]> valueMap = new LinkedHashMap<String,String[]>();

        				String iParam = "";
        				String iName = "";
        				String uParam = "";

        				int excelColIdx = 0;
        				
	        			for(int idx=0; idx<catalog.size(); idx ++){
	        				String value = catalog.get(idx).getValue();
	        				String fieldName = catalog.get(idx).getFieldName();
	        				String autoIncrement = catalog.get(idx).getAutoIncrement();
	        				String subquery = catalog.get(idx).getSubquery();
	        				String type = catalog.get(idx).getType();
	        				
	        				iParam += "?,";
	        				iName += fieldName+",";
	        				// update 구문에는 dbkey에 해당하는 필드를 넣지 않는다.
	        				if(!fieldName.equals(dbKey)){
	        					uParam += fieldName+"=?,";
	        				}
        				
	        				if(value!=null){
        						// 고정값을 지닌 필드에 대한 값이 이미 설정되어 있으면
        						valueMap.put(fieldName, new String[]{value, type});
	        					continue;
	        				}
	        				if(autoIncrement!=null && autoIncrement.toUpperCase().equals("Y")){
	        					valueMap.put(fieldName, new String[]{"!!!", type});
	        					continue;
	        				}
	        				
	        				// subquery가 존재하면 excelValue를 subquery의 %s에 넣고 SQL을 생성.
	        				if(subquery !=null && !subquery.isEmpty()){
	        					String subqueryResult = "";
	        					String excelValue = "";
	        					if(subquery.contains("%s")){	        						
	        						excelValue = ex_catalog.get(excelColIdx++).getCellData().trim();
	        						subquery = subquery.replaceAll("%s", excelValue);
	        					}
	        					subqueryResult = m_dbjob.executeSelect(subquery);
	        					valueMap.put(fieldName, new String[]{subqueryResult.trim(), type});
	        				}else{
	        					String excelValue = ex_catalog.get(excelColIdx++).getCellData();
	        					
	        					valueMap.put(fieldName, new String[]{excelValue.trim(), type});
	        				}
	        				
	        			}

	        			iName = iName.replaceAll(",$", "");
	        			uParam = uParam.replaceAll(",$", "");
	        			iParam = iParam.replaceAll(",$", "");

	        			if (sqlCnt % 100 == 0) {
	                        if (sqlCnt % 1000 == 0 || sqlCnt == 0) {
	                            System.out.print("[" + sqlCnt + "]");
	                        } else {
	                            System.out.print(".");
	                        }
	                    }

	        			if("insert".equals(mode)){
	        				sqlChk = m_dbjob.excuteInsert(tableName, dbKey,uParam,iParam,iName,valueMap,sqlCnt,sqlChk);
	        			}else if("update".equals(mode)){	        				
	        				sqlChk = m_dbjob.excuteUpdate(tableName, dbKey,uParam,iParam,iName,valueMap,sqlCnt,sqlChk);
	        			}
	        			sqlCnt++;
        			}


        			m_dbjob.closeDB();
        			Log2.out("\n[Info] [JobExcute] [DB Input Insert & Update : "+sqlCnt+"]");


        		}else{
        			throw new StringException("[Error] [JobExcute] [DB Colunm & XML Field does not match]");
        		}



        	}else if(mode.equals("test1")){

        	}else if(mode.equals("test2")){

        	}





        }catch(Exception e){
        	e.printStackTrace();
        }

        return true;
    }

    public void DbXmlCheck(ArrayList<DBInfoSet> list1, ArrayList<InfoSet> list2) throws StringException{
    	try{
    		for(int idx=0;idx<list2.size();idx++){
        		if(!list1.get(idx).getColumnName().equals(list2.get(idx).getFieldName())){
        			throw new StringException("[Error] [JobExcute] [ ("+idx+") DB Column : "+list1.get(idx).getColumnName()+" & XML Field : "+list2.get(idx).getFieldName()+" Not equals]");
        		}
        	}
    	}catch(IndexOutOfBoundsException e){
    		Log2.error("[Error] [JobExcute] [DB Coulnm & XML Field Count not match]");
    	}

    }

}       // END Class
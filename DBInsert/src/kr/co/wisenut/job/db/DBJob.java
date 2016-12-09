/*
 * @(#)DBJob.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.db;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import oracle.sql.DATE;
import kr.co.wisenut.Exception.DBException;
import kr.co.wisenut.config.datasource.DataSource;
import kr.co.wisenut.logger.Log2;
import kr.co.wisenut.util.StringUtil;

/**
 *
 * DBJob
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DBJob{

	private Connection conn = null;
	private ResultSet rs = null;
	private PreparedStatement pstmt = null;

	private String url = "";
	private String user = "";
	private String passwd = "";

	private DBQueryConstants constant = null;
	
	private int connectionResetCount;


	public DBJob(DataSource datasource){

		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			this.url = "jdbc:oracle:thin:@"+datasource.getServerName()+":"+datasource.getPort()+
					":"+datasource.getSid();
			this.user = datasource.getUser();
			this.passwd = datasource.getPwd();
			this.constant = new DBQueryConstants();
			this.connectionResetCount = 0;
		}catch (Exception e) {
			Log2.error("[DBJob] [Class] ["+e.getMessage()+"] ");
		}
	}

	public DBMapping excuteSelect(String tableName) throws DBException{
		ArrayList<DBInfoSet> db_catalog = null;
		DBMapping db_mapping = new DBMapping();
		try {
			Log2.out("[Info] [DBJob] [DB Colunm Select Start]");
		
			if(connectionResetCount%100==0){				
				conn = DriverManager.getConnection(url,user,passwd);
				Log2.out("[Info] [DBJob] [DB Connection Success]");
			}

			pstmt = conn.prepareStatement(constant.selectColumnQuery());
			pstmt.setString(1, tableName);
			rs = pstmt.executeQuery();

			if(rs != null){
				db_catalog = new ArrayList<DBInfoSet>();
				while(rs.next()){
					DBInfoSet db_infoSet = new DBInfoSet();

					db_infoSet.setColumnName(rs.getString(1).trim());
					db_catalog.add(db_infoSet);
				}


				db_mapping.setDBCatalog(db_catalog);

			}else{
				throw new DBException("[Error] [DBJob] [resultSet is NULL]");
			}

			rs.close();
			pstmt.close();
			
			if(connectionResetCount%100==99){				
				conn.close();
			}
			
			Log2.out("[Info] [DBJob] [DB Colunm Select End]");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			connectionResetCount++;
		}
		return db_mapping;
	}
	
	public String executeSelect(String sql){
		String result = "";
		try {
			Log2.out("[Info] [DBJob] [DB Column Select Start]");
			
			if(connectionResetCount%100==0){				
				conn = DriverManager.getConnection(url,user,passwd);
				Log2.out("[Info] [DBJob] [DB Connection Success]");
			}		
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				result = rs.getString(1);
			}

			Log2.out("[Info] [DBJob] [DB Colunm Select End]");
			
			if(connectionResetCount%100==99){				
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			connectionResetCount++;
		}
		
		return result;
	}

	public boolean excuteInsert(String tableName,String dbKey,
								  String uParam,String iParam, String iName,
								  LinkedHashMap<String,String[]> valueMap,
								  long sqlCnt, boolean sqlChk) throws DBException, ClassNotFoundException{

		
		Log2.debug("[Debug] [DBJob] ["+constant.insertQuery(tableName, iName, iParam)+"]");
		try {
			
			if(connectionResetCount%100==0){				
				conn = DriverManager.getConnection(url,user,passwd);
				Log2.out("[Info] [DBJob] [DB Connection Success]");
			}	
			
			pstmt = conn.prepareStatement(constant.insertQuery(tableName, iName, iParam));
			
			Iterator<String> iter = valueMap.keySet().iterator();

			int idx = 1;
			while(iter.hasNext()){
				String columnName = iter.next();
				String[] valueTypePair = valueMap.get(columnName);
								
				// insert 구문 세팅
				// valueMap의 dbKey의 값도 insert는 포함되어야 하니 idx+2가아니라 1이다.
				setPreparedStatement(idx, valueTypePair);
				idx++;
			}

			pstmt.executeUpdate();
			pstmt.close();

			if(connectionResetCount%100==99){				
				conn.close();
			}
		} catch (SQLException e) {
			Log2.error("[DBJob] [setResultSet SQL Query : "+constant.insertQuery(tableName, iName, iParam)+"]");
            Log2.error("[DBJob] [setResultSet SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBJob] [setResultSet SQLException "+e.getMessage()+"] " + StringUtil.getStackTrace(e) );
            //throw new DBException(": setResultSet SQL Error " +e);
        } catch (ParseException e) {
            Log2.error("[DBJob] [ParseException] " + StringUtil.getStackTrace(e) );
		} finally{
			connectionResetCount++;
		}
		return sqlChk;
	}
	
	public boolean excuteUpdate(String tableName,String dbKey,
			  String uParam,String iParam, String iName,
			  LinkedHashMap<String,String[]> valueMap,
			  long sqlCnt, boolean sqlChk) throws DBException, ClassNotFoundException{


		Log2.debug("[Debug] [DBJob] ["+constant.updateQuery(tableName, dbKey, uParam)+"]");
		try {
		
			if(connectionResetCount%100==0){				
				conn = DriverManager.getConnection(url,user,passwd);
				Log2.out("[Info] [DBJob] [DB Connection Success]");
			}	
			
			pstmt = conn.prepareStatement(constant.updateQuery(tableName, dbKey, uParam));
			
			setPreparedStatement(1, valueMap.get(dbKey));
			
			Iterator<String> iter = valueMap.keySet().iterator();
			
			int idx = 1;
			while(iter.hasNext()){
				String columnName = iter.next();
				String[] valueTypePair = valueMap.get(columnName);
				
				// update 구문 세팅
				if(!columnName.toUpperCase().equals(dbKey.toUpperCase())){
					setPreparedStatement(idx, valueTypePair);
				}
			
				// insert 구문 세팅
				// valueMap의 dbKey의 값도 insert는 포함되어야 하니 idx+2가아니라 1이다.
				setPreparedStatement(idx+valueMap.size(), valueTypePair);
				idx++;
			}
			
			pstmt.executeUpdate();
			pstmt.close();
			
			if(connectionResetCount%100==99){				
				conn.close();
			}
		} catch (SQLException e) {
			Log2.error("[DBJob] [setResultSet SQL Query : "+constant.updateQuery(tableName, dbKey, uParam)+"]");
			Log2.error("[DBJob] [setResultSet SQL Error Code : "+e.getErrorCode()+" ]");
			Log2.error("[DBJob] [setResultSet SQLException "+e.getMessage()+"] " + StringUtil.getStackTrace(e) );
			//throw new DBException(": setResultSet SQL Error " +e);
		} catch (ParseException e) {
			Log2.error("[DBJob] [ParseException] " + StringUtil.getStackTrace(e) );
		} finally{
			connectionResetCount++;
		}
		
		return sqlChk;
	}

	public void closeDB() throws SQLException{
		if(conn != null) conn.close();
		if(pstmt != null) pstmt.close();
		if(rs != null) rs.close();
	}
	
	public void setPreparedStatement(int idx, String[] valueTypePair) throws SQLException, ParseException{
		if(valueTypePair[1].equals("String")){
			pstmt.setString(idx, valueTypePair[0]); 
		}else if(valueTypePair[1].equals("Number")){
			if(!"".equals(valueTypePair[0])){
				pstmt.setInt(idx, Integer.valueOf(valueTypePair[0])); 			
			}else{
				pstmt.setInt(idx, -1);
			}
		}else if(valueTypePair[1].equals("Float")){
			pstmt.setFloat(idx, Float.valueOf(valueTypePair[0]));
		}else if(valueTypePair[1].equals("CLOB")){
			pstmt.setClob(idx, new javax.sql.rowset.serial.SerialClob(valueTypePair[0].toCharArray()));
		}else if(valueTypePair[1].equals("Date")){
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	        Date parsed = format.parse(valueTypePair[0]);
	        java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());
			pstmt.setDate(idx, sqlDate);
		}
	}
}

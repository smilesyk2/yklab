package kr.co.wisenut.job.db;

public class DBQueryConstants {

	public DBQueryConstants(){

	}

	public String selectColumnQuery(){
		return "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME=? ORDER BY COLUMN_ID ASC";
	}

	public String insertQuery(String tableName, String insertName, String insertParam){

		String insertQuery = "";
		/*mergeQuery  = "MERGE INTO "+tableName+" ";
		mergeQuery += "USING DUAL ";
		mergeQuery += "ON ("+dbKey+" = ?) ";
		mergeQuery += "WHEN MATCHED THEN ";
		mergeQuery += "UPDATE SET "+update+" ";
		mergeQuery += "WHEN NOT MATCHED THEN ";
		mergeQuery += "INSERT("+insertName+") ";
		mergeQuery += "VALUES("+insertParam+")";*/
		
		insertQuery += "insert into " + tableName + " (";
		insertQuery += insertName;
		insertQuery += ") values (";
		insertQuery += insertParam;
		insertQuery += ")";

		return insertQuery;
	}
	
	public String updateQuery(String tableName,String dbKey, String update){
		String updateQuery = "";
		
		updateQuery += "update " + tableName;
		updateQuery += " set " + update;
		updateQuery += " where " + dbKey + " = ?";
		
		return updateQuery;
	}

}

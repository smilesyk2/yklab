package com.wisenut.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wisenut.common.WNProperties;
import com.wisenut.util.StringUtil;

import QueryAPI530.Search;

public class WiseSearchWorker {
	final static Logger logger = LogManager.getLogger(WiseSearchWorker.class);
	
	public static final String SPACE = " ";
	public static final int AND_OPERATOR = 1;
	
	public Properties prop;
	public Search search;
	public String searchIP;
	public int searchPort;
	public int searchTimeout;
	
	public String sort;
	public String ranking;
	public String highlight;
	public String queryAnalyzer;
	
	public String collectionId;
	public String searchFields;
	public String documentFields;
	public String dateField;
	public String[] collectionIdArr;
	public String[] searchFieldsArr;
	public String[] documentFieldsArr;
	public String[] dateFieldArr;
	
	
	private HashMap<String,ArrayList<HashMap<String,String>>> resultMap; // 연관 기사 리스트
	private HashMap<String,ArrayList<String>> docidMap; // 연관 기사 DOCID 리스트
	
	public boolean debug = true;
	
	public WiseSearchWorker() throws Exception{
		WNProperties wnprop = WNProperties.getInstance("/wisenut.properties");
		
		searchIP = wnprop.getProperty("search.ip");
		searchPort = Integer.parseInt(wnprop.getProperty("search.port"));
		searchTimeout = Integer.parseInt(wnprop.getProperty("search.timeout"));
		
		search = new Search();
		
		sort = wnprop.getProperty("search.sort");
		ranking = wnprop.getProperty("search.ranking");
		highlight = wnprop.getProperty("search.highlight");
		queryAnalyzer = wnprop.getProperty("search.queryanalyzer");
		
		collectionId = wnprop.getProperty("search.collection.id");
		if(null != collectionId && collectionId.contains(",")){
			collectionIdArr = collectionId.split(",");
		}
		searchFields = wnprop.getProperty("search.collection.searchfields");
		if(null != searchFields && searchFields.contains("/")){
			searchFieldsArr = searchFields.split("/");
		}
		documentFields = wnprop.getProperty("search.collection.documentfields");
		if(null != documentFields && documentFields.contains("/")){
			documentFieldsArr = documentFields.split("/");
		}
		dateField = wnprop.getProperty("search.collection.datefield");
		if(null != dateField && dateField.contains("/")){
			dateFieldArr = dateField.split("/");
		}
	}
	
	public void setSearchCondition(String query, int listNo, String startDate, String endDate, boolean docidSearch){
		search = new Search();
		
		int pageNum = 0;
		
		int ret = 0;
		
		ret = search.w3SetCodePage("UTF-8");
		ret = search.w3SetQueryLog(1);
		
		if(docidSearch){
			ret = search.w3SetCommonQuery("", 0);
		}else{
			ret = search.w3SetCommonQuery(query, 0);
		}
		
		for(int colIdx=0; colIdx<collectionIdArr.length; colIdx++){
			logger.debug(" - collection : " + collectionIdArr[colIdx]);			
			ret = search.w3AddCollection(collectionIdArr[colIdx]);
			
			String[] rankingArr = ranking.split(",");
			logger.debug(" - ranking : "+rankingArr[0]+", "+rankingArr[1]+", " + rankingArr[2]);
			ret = search.w3SetRanking(collectionIdArr[colIdx], rankingArr[0], rankingArr[1], Integer.parseInt(rankingArr[2]));
			
			String[] hlArr = highlight.split(",");
			logger.debug(" - highlight : 1,1");
			ret = search.w3SetHighlight(collectionIdArr[colIdx], Integer.parseInt(hlArr[0]), Integer.parseInt(hlArr[1]));
			
			logger.debug(" - sort : " + sort);
			ret = search.w3SetSortField(collectionIdArr[colIdx], sort);
			
			String[] qaArr = queryAnalyzer.split(",");
			logger.debug(" - query analyzer : 1,1,1,1");
			ret = search.w3SetQueryAnalyzer(collectionIdArr[colIdx], Integer.parseInt(qaArr[0]), Integer.parseInt(qaArr[1]), Integer.parseInt(qaArr[2]), Integer.parseInt(qaArr[3]));
			
			StringBuffer prefixQuery = new StringBuffer();
			if(docidSearch){
				prefixQuery.append("<DOCID:contains:"+query+">");
			}
			
			if(docidSearch){				
				logger.debug(" - date range : ");
				ret = search.w3SetDateRange(collectionIdArr[colIdx], "1970/01/01", "2030/12/31");
			}
			
			logger.debug(" - prefixQuery : " + prefixQuery.toString());
			if(prefixQuery.length()>0){
				ret = search.w3SetPrefixQuery(collectionIdArr[colIdx], prefixQuery.toString(), AND_OPERATOR);
			}
			
			logger.debug(" - search fields : " + searchFieldsArr[colIdx]);
			ret = search.w3SetSearchField(collectionIdArr[colIdx], searchFieldsArr[colIdx]);
			
			logger.debug(" - document fields : " + documentFieldsArr[colIdx]);
			ret = search.w3SetDocumentField(collectionIdArr[colIdx], documentFieldsArr[colIdx]);
			
			logger.debug(" - page info : " + pageNum + ", " + listNo);
			ret = search.w3SetPageInfo(collectionIdArr[colIdx], pageNum, listNo);
			
			logger.debug(" - startDate : " + startDate);
			StringBuffer filterQuery = new StringBuffer();
			if(!"".equals(startDate)){
				filterQuery.append("<"+dateFieldArr[colIdx]+":gte:").append(startDate).append(">");
			}
			
			if(filterQuery.length()>0){
				filterQuery.append(SPACE);
			}
			
			logger.debug(" - endDate : " + endDate);
			if(!"".equals(endDate)){
				filterQuery.append("<"+dateFieldArr[colIdx]+":lte:").append(endDate).append(">");
			}
			
			logger.debug(" - filterQuery : " + filterQuery.toString());
			if(filterQuery.length()>0){
				ret = search.w3SetFilterQuery(collectionIdArr[colIdx], filterQuery.toString());
			}
			
		}
		
		logger.debug(" - search ip : " + searchIP);
		logger.debug(" - search port : " + searchPort);
		logger.debug(" - search timeout : " + searchTimeout);
		ret = search.w3ConnectServer(searchIP, searchPort, searchTimeout);
		
		ret = search.w3ReceiveSearchQueryResult(0);
		if(ret != 0) {
            logger.error(search.w3GetErrorInfo() + " (Error Code : " + search.w3GetError() + " )");
        }
	}
		
	public void search(String query, int listNo, String startDate, String endDate){
		resultMap = new HashMap<String,ArrayList<HashMap<String,String>>>();
		docidMap = new HashMap<String,ArrayList<String>>();
		
		// property에 지정한 모든 필드 검색
		setSearchCondition(query, listNo, startDate, endDate, false);
		
		int totalResultCount = 0;
		for(int colIdx=0; colIdx<collectionIdArr.length; colIdx++){
			totalResultCount += search.w3GetResultTotalCount(collectionIdArr[colIdx]);
			
			ArrayList<HashMap<String,String>> thisCollectionResultList = new ArrayList<HashMap<String,String>>();
			ArrayList<String> thisCollectionDocidList = new ArrayList<String>();
			
			int count = search.w3GetResultCount(collectionIdArr[colIdx]);
			for(int i=0; i<count; i++){
				HashMap<String,String> map = new HashMap<String, String>();
				
				for(String dfield : documentFieldsArr){
					// DOCID 결과값은 별도로 따로 저장.
					if(dfield.equals("DOCID")){
						thisCollectionDocidList.add(search.w3GetField(collectionIdArr[colIdx], dfield, i));
					}
					map.put(dfield, search.w3GetField(collectionIdArr[colIdx], dfield, i));
				}
							
				thisCollectionResultList.add(map);
			}
			
			resultMap.put(collectionIdArr[colIdx], thisCollectionResultList);
			docidMap.put(collectionIdArr[colIdx], thisCollectionDocidList);
		}
		
		logger.debug("############################################# ");
		logger.debug("### Query : " + query);
		logger.debug("### Total Result Count : " + totalResultCount);
		logger.debug("############################################# ");
		
		
		
		search.w3CloseServer();
	}
	
	public void docidSearch(String query, int listNo, String startDate, String endDate){
		resultMap = new HashMap<String,ArrayList<HashMap<String,String>>>();
		docidMap = new HashMap<String,ArrayList<String>>();
		
		// docid만 검색.
		setSearchCondition(query, listNo, startDate, endDate, true);
		
		int totalResultCount = 0;
		for(int colIdx=0; colIdx<collectionIdArr.length; colIdx++){
			totalResultCount += search.w3GetResultTotalCount(collectionIdArr[colIdx]);
			
			ArrayList<HashMap<String,String>> thisCollectionResultList = new ArrayList<HashMap<String,String>>();
			ArrayList<String> thisCollectionDocidList = new ArrayList<String>();
			
			int count = search.w3GetResultCount(collectionIdArr[colIdx]);

			String[] documentFieldsArr = documentFields.split(",");
			for(int i=0; i<count; i++){
				HashMap<String,String> map = new HashMap<String, String>();
				
				for(String dfield : documentFieldsArr){
					// DOCID 결과값은 별도로 따로 저장.
					if(dfield.equals("DOCID")){
						thisCollectionDocidList.add(search.w3GetField(collectionIdArr[colIdx], dfield, i));
					}
					map.put(dfield, search.w3GetField(collectionIdArr[colIdx], dfield, i));
				}
							
				thisCollectionResultList.add(map);
			}
			
			resultMap.put(collectionIdArr[colIdx], thisCollectionResultList);
			docidMap.put(collectionIdArr[colIdx], thisCollectionDocidList);
		}
		
		logger.debug("############################################# ");
		logger.debug("### Query : " + query);
		logger.debug("### Total Result Count : " + totalResultCount);
		logger.debug("############################################# ");
		
		
		
		search.w3CloseServer();
	}
	
	public String getDocidMapAsJson(){
		return StringUtil.objectToString(docidMap);
	}
	
	public HashMap<String,ArrayList<String>> getDocidMap(){
		return docidMap;
	}
	
	
	public String getResultMapAsJson(){
		return StringUtil.objectToString(resultMap);
	}
	
	public HashMap<String,ArrayList<HashMap<String,String>>> getResultMap(){
		return resultMap;
	}
}

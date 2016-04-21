package com.wisenut.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.wisenut.common.WNProperties;
import com.wisenut.util.StringUtil;

import QueryAPI530.Search;

public class WiseSearchWorker {
	public static final String SPACE = " ";
	
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
		searchFields = wnprop.getProperty("search.collection.searchfields");
		documentFields = wnprop.getProperty("search.collection.documentfields");
	}
	
	public String search(String query, int listNo){
		return search(query, listNo, "", ""); 
	}
	
	public String search(String query, int listNo, String startDate, String endDate){
		ArrayList<HashMap<String,String>> articleList = new ArrayList<HashMap<String,String>>();
		
		search = new Search();
		
		int pageNum = 0;
		
		int ret = 0;
		
		ret = search.w3SetCodePage("UTF-8");
		ret = search.w3SetQueryLog(1);
		ret = search.w3SetCommonQuery(query, 0);
		
		String[] collectionArr = collectionId.split(",");
		for(String col : collectionArr){
			if(debug) System.out.println(" - collection : " + col);			
			ret = search.w3AddCollection(col);
			
			String[] rankingArr = ranking.split(",");
			if(debug) System.out.println(" - ranking : "+rankingArr[0]+", "+rankingArr[1]+", " + rankingArr[2]);
			ret = search.w3SetRanking(col, rankingArr[0], rankingArr[1], Integer.parseInt(rankingArr[2]));
			
			String[] hlArr = highlight.split(",");
			if(debug) System.out.println(" - highlight : 1,1");
			ret = search.w3SetHighlight(col, Integer.parseInt(hlArr[0]), Integer.parseInt(hlArr[1]));
			
			if(debug) System.out.println(" - sort : " + sort);
			ret = search.w3SetSortField(col, sort);
			
			String[] qaArr = queryAnalyzer.split(",");
			if(debug) System.out.println(" - query analyzer : 1,1,1,1");
			ret = search.w3SetQueryAnalyzer(col, Integer.parseInt(qaArr[0]), Integer.parseInt(qaArr[1]), Integer.parseInt(qaArr[2]), Integer.parseInt(qaArr[3]));
			
			if(debug) System.out.println(" - search fields : " + searchFields);
			ret = search.w3SetSearchField(col, searchFields);
			
			if(debug) System.out.println(" - document fields : " + documentFields);
			ret = search.w3SetDocumentField(col, documentFields);
			
			if(debug) System.out.println(" - page info : " + pageNum + ", " + listNo);
			ret = search.w3SetPageInfo(col, pageNum, listNo);
			
			if(debug) System.out.println(" - startDate : " + startDate);
			StringBuffer filterQuery = new StringBuffer();
			if(!"".equals(startDate)){
				filterQuery.append("<CRT_DTIME:gte:").append(startDate).append(">");
			}
			
			if(filterQuery.length()>0){
				filterQuery.append(SPACE);
			}
			
			if(debug) System.out.println(" - endDate : " + endDate);
			if(!"".equals(endDate)){
				filterQuery.append("<CRT_DTIME:lte:").append(endDate).append(">");
			}
			
			if(debug) System.out.println(" - filterQuery : " + filterQuery.toString());
			if(filterQuery.length()>0){
				ret = search.w3SetFilterQuery(collectionId, filterQuery.toString());
			}
			
		}
		
		if(debug) System.out.println(" - search ip : " + searchIP);
		if(debug) System.out.println(" - search port : " + searchPort);
		if(debug) System.out.println(" - search timeout : " + searchTimeout);
		ret = search.w3ConnectServer(searchIP, searchPort, searchTimeout);
		
		ret = search.w3ReceiveSearchQueryResult(0);
		if(ret != 0) {
            System.out.println(search.w3GetErrorInfo() + " (Error Code : " + search.w3GetError() + " )");
            return null;
        }
		
		int totalResultCount = 0;
		for(String col : collectionArr){
			totalResultCount += search.w3GetResultTotalCount(col);
		}
		
		System.out.println("############################################# ");
		System.out.println("### Query : " + query);
		System.out.println("### Total Result Count : " + totalResultCount);
		System.out.println("############################################# ");
		
		int count = search.w3GetResultCount(collectionId);
		
		String[] documentFieldsArr = documentFields.split(",");
		for(int i=0; i<count; i++){
			HashMap<String,String> articleResultMap = new HashMap<String, String>();
			
			for(String dfield : documentFieldsArr){
				articleResultMap.put(dfield, search.w3GetField(collectionId, dfield, i));
			}
						
			articleList.add(articleResultMap);
		}
		
		return StringUtil.objectToString(articleList);
	} 
	
}

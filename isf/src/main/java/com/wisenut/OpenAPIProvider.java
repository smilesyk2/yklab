package com.wisenut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wisenut.common.WNConstants;
import com.wisenut.common.WNProperties;
import com.wisenut.model.WNResultData;
import com.wisenut.tea20.types.Pair;
import com.wisenut.util.StringUtil;
import com.wisenut.worker.FacebookWorker;
import com.wisenut.worker.NaverWorker;
import com.wisenut.worker.TwitterWorker;
import com.wisenut.worker.WiseSearchWorker;
import com.wisenut.worker.WiseTeaWorker;
import com.wisenut.worker.YoutubeWorker;

public class OpenAPIProvider {
	
	final static Logger logger = LogManager.getLogger(OpenAPIProvider.class);
	private String[] collections;
	
	public OpenAPIProvider() throws Exception{
		WNProperties wnprop = new WNProperties("/wisenut.properties");
		String strCollections = wnprop.getProperty("search.collection.id");
		if( null != strCollections && strCollections.indexOf(",")!=-1 ){
			collections = strCollections.split(",");
		}
	}
	
	public String getMainKeywordsInfo(String article, int start, int pageno) throws Exception{
		WiseTeaWorker teaWorker = new WiseTeaWorker();
		
		return teaWorker.getMainKeywordsInfo(article, start, pageno);
	}
		
	// 2-1. Model. 날짜 조건 없음. 전체 컬렉션 대상.
	public String getRecommendedContentsInfo(String article, int start, int pageno) throws Exception{
		return getRecommendedContentsInfo(article, "all", start, pageno, "", "");
	}
	
	// 2-2. Model. 날짜 조건 없음. 특정 컬렉션 대상.
	public String getRecommendedContentsInfo(String article, String targetCollection, int start, int pageno) throws Exception{
		return getRecommendedContentsInfo(article, targetCollection, start, pageno, "", "");
	}
	
	// 2-3. Model. 날짜 조건 없음. 특정 컬렉션 대상.
	public String getRecommendedContentsInfo(String article, int start, int pageno, String startDate, String endDate) throws Exception{
		return getRecommendedContentsInfo(article, "all", start, pageno, "", "");
	}
	
	// 2-4. Model
	public String getRecommendedContentsInfo(String article, String targetCollection, int start, int pageno, String startDate, String endDate) throws Exception{
		HashMap<String, ArrayList<HashMap<String,String>>> resultMap = new HashMap<String, ArrayList<HashMap<String,String>>>();
		
		WiseTeaWorker teaWorker = new WiseTeaWorker();
		WiseSearchWorker searchWorker = new WiseSearchWorker();
        
        
        // 기사 길이가 200자가 넘는 경우는 Model을, 아닌 경우 SF-1+Model 결과를 조합
        if(article.length()>200){
        	logger.debug("article length is more than 200 characters.");
        	
        	// similarDoc만 사용
        	List<Pair<Double>> docidList = teaWorker.getRecommendedContentsPair(article, pageno);
        	
        	logger.debug("docidList size : " + docidList.size());
        	
        	if(targetCollection.equals("all")){
            	for(String col : collections){
            		ArrayList<HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
                	for(Pair<Double> p : docidList){
            			searchWorker.docidSearch(p.key(), pageno, startDate, endDate);
            			
            			// DOCID Search에 대한 결과는 한 개이므로 첫번째 결과만 가져와서 add.
            			if(searchWorker.getResultMap().get(col).size()>0){				
            				resultList.add(searchWorker.getResultMap().get(col).get(0));
            			}
            		}
                	
                	resultMap.put(col, resultList);
            	}
            }else{
            	ArrayList<HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
            	for(Pair<Double> p : docidList){
        			searchWorker.docidSearch(p.key(), pageno, startDate, endDate);
        			
        			// DOCID Search에 대한 결과는 한 개이므로 첫번째 결과만 가져와서 add.
        			if(searchWorker.getResultMap().get(targetCollection).size()>0){				
        				resultList.add(searchWorker.getResultMap().get(targetCollection).get(0));
        			}
        		}
            	
            	resultMap.put(targetCollection, resultList);
            }
        }else{
        	logger.debug("article length is less than and equal to 200 characters.");
        	
        	List<Pair<Integer>> keywordList = teaWorker.getMainKeywordsPair(article, start, pageno);
        	
        	StringBuffer query = new StringBuffer();
            
            for (int i = 0; i < keywordList.size(); i++) {
             	Pair<Integer> item = keywordList.get(i);
     			if (null == item) {
     				continue;
     			}
     			
     			if( query.length() != 0 )
     				query.append("|");
     			
     			query.append(item.key());
     		}
            
            logger.debug("query : " + query.toString());
            
            searchWorker.search(query.toString(), pageno, startDate, endDate);
            
            if(targetCollection.equals("all")){
            	for(String col : collections){
            		// 검색한 결과와 tea의 similarDoc 결과를 조합. 검색 결과 중 기사(article)의 결과만 리스트로 제공
                    List<Pair<Double>> docidList = teaWorker.getRecommendedContentsPair(article, searchWorker.getDocidMap().get(col), pageno);
                    ArrayList<HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
                    
            		for(Pair<Double> p : docidList){
            			searchWorker.docidSearch(p.key(), pageno, startDate, endDate);
            			
            			// DOCID Search에 대한 결과는 한 개이므로 첫번째 결과만 가져와서 add.
            			if(searchWorker.getResultMap().get(col).size()>0){				
            				resultList.add(searchWorker.getResultMap().get(col).get(0));
            			}
            		}
            		resultMap.put(col, resultList);
            	}
            }else{
            	// 검색한 결과와 tea의 similarDoc 결과를 조합. 검색 결과 중 기사(article)의 결과만 리스트로 제공
                List<Pair<Double>> docidList = teaWorker.getRecommendedContentsPair(article, searchWorker.getDocidMap().get(targetCollection), pageno);
                ArrayList<HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
                
        		for(Pair<Double> p : docidList){
        			searchWorker.docidSearch(p.key(), pageno, startDate, endDate);
        			
        			
        			// DOCID Search에 대한 결과는 한 개이므로 첫번째 결과만 가져와서 add.
        			if(searchWorker.getResultMap().get(targetCollection).size()>0){				
        				resultList.add(searchWorker.getResultMap().get(targetCollection).get(0));
        			}
        		}
        		resultMap.put(targetCollection, resultList);
            }
        }
        
		return StringUtil.objectToString(resultMap);
	}
	
	
	public String getOpenAPIResult(String provider, String query, int startPos, int pageNo){
		WNResultData data = new WNResultData("op");
		data.setProvider(provider);
		
		if(provider.equals("twitter")){
			TwitterWorker tWorker = new TwitterWorker();
			tWorker.search(query, startPos, pageNo, WNConstants.METAINFO_BY_PROVIDER[WNConstants.TWITTER_ID][WNConstants.SORT_BY_RANK], data);
		}else if(provider.equals("naver")){
			NaverWorker nWorker = new NaverWorker();
			nWorker.search(query, startPos, pageNo, WNConstants.METAINFO_BY_PROVIDER[WNConstants.NAVER_ID][WNConstants.SORT_BY_RANK], data);
		}else if(provider.equals("facebook")){
			FacebookWorker fWorker = new FacebookWorker("group");
			fWorker.search(query, data);
		}else if(provider.equals("youtube")){
			YoutubeWorker yWorker = new YoutubeWorker();
			yWorker.search(query, startPos, pageNo, WNConstants.METAINFO_BY_PROVIDER[WNConstants.YOUTUBE_ID][WNConstants.SORT_BY_RANK], data);
		}
		return data.toString();
	}
	
	public static void main(String[] args){
		System.out.println("##########################################################");
		System.out.println("               WISENUT OPEN API PROVIDER");
		System.out.println("##########################################################");
		OpenAPIProvider provider = null;
		
		
		try {
			provider = new OpenAPIProvider();
			
			String strResultData = "";
			
			System.out.println("================================================================================================================== 외부 API");
			System.out.println("##### 외부 API 검색 결과");
			strResultData = provider.getOpenAPIResult("twitter", "teamcoco", 1, 10);
			System.out.println(strResultData);
			
			System.out.println("================================================================================================================== 내부 API");
			//String article = "겨울채비 하세요... 이번 주 맑고 포근 윤우현 기자 whyoon@jbnews.com 11월4째주 충북지방은 대체로 고기압의 영향을 받아 맑겠으며, 기온도 평년보다 조금 높아 포근한 한 주가 될 것으로 전망된다.청주기상대는 20일 충북지방은 고기압의 영향을 받아 맑은 날씨를 보이겠으며, 아침최저 청주2도, 충주 영하1도 등 영하2~영상2도의 분포를 보이겠고, 낮 최고기온은 청주 16도, 충주 14도 등 14~16도로 포근할 것이라고 예보했다. 한편 11월 셋째 휴일인 19일 충북지역은 구름이 낀 흐린 날씨를 보인 가운데 유명산 등에는 다소 한산한 모습을 보였다. 가을 단풍이 모두 떨어진 월악산국립공원 입장객은 2천명으로 지난 주의 절반에 그쳤고 속리산에는 4천명의 등산객이 산행을 즐겼으나 평소보다 적은 수준이었다. 대통령 옛 별장인 청원 청남대에도 휴일 평균 관람객의 50%를 밑도는 2천명만 입장해 초겨울 대청호의 풍광을 감상했다. 청주 상당산성, 청원 문의문화재 단지 등 도내 주요 유원지에서는 두툼한 옷을 입은 가족단위 행락객이 등산, 산책, 외식 등을 하며 휴일의 여유를 즐겼다. 청주 도심 극장가는 대입 수능을 마친 고3학생 등으로 북적거렸고 도시와 농촌가정에서는 김장김치를 담그는 손길이 분주했다."; 
			String article = "청주 도심 극장가는 대입 수능을 마친 고3학생 등으로 북적거렸고 도시와 농촌가정에서는 김장김치를 담그는 손길이 분주했다.";
			System.out.println("###### Input Article : "+ article);
			System.out.println("###### ( article length is "+ article.length() + ".)");
			
			strResultData = provider.getMainKeywordsInfo(article, 0, 10);
			System.out.println("#1. 키워드(해쉬태그) 추출 : " + strResultData);
			
			strResultData = provider.getRecommendedContentsInfo(article, 0, 10);
			System.out.println("#2-1. 연관기사 추천. 전체 컬렉션. 날짜 조건 X : " + strResultData);
			
			strResultData = provider.getRecommendedContentsInfo(article, "article", 0, 10);
			System.out.println("#2-2. 연관기사 추천. 특정 컬렉션. 날짜 조건 X : " + strResultData);
			
			strResultData = provider.getRecommendedContentsInfo(article, 0, 10, "20150101", "20151231");
			System.out.println("#2-3. 연관기사 추천. 전체 컬렉션. 날짜 조건 O : " + strResultData);
			
			strResultData = provider.getRecommendedContentsInfo(article, "video", 0, 10, "20150101", "20151231");
			System.out.println("#2-4. 연관기사 추천. 특정 컬렉션. 날짜 조건 O : " + strResultData);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

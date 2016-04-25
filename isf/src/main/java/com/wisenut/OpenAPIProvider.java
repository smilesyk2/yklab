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
			String article = "오는 16일, 세월호 참사 1주기를 맞습니다.      세월호 참사 진상규명은 제자리 걸음이고 유가족들의 속은 하루하루 타들어 가고 있습니다.      광주에서도 세월호 희생자 304명을 기리는 추모 행사가 시작됐습니다.      김기중 기자가 취재했습니다.      세월호 침몰 때 아들을 잃은 아버지가 무거운 침묵 속에 걷고 절하기를 반복합니다.      고 이승현 군을 보낸 지 그새 1년, 아무것도 달라진 게 없어 답답한 마음에 진도에서 서울까지 45일째 걷고 있습니다.   이호진 / 고 이승현 군 아버지 뭐가 하나 달라진 게 없어요. (세월호 1주기가) 아무런 의미가 없어요. 고통이 더 심해지면 심해졌지...   인양하라! 인양하라!    본격적인 추모 행사도 시작됐습니다.    세월호 참사 광주시민대책위는 오늘 기자회견을 열고 진상규명을 막고 있는 세월호 특별법 시행령을 폐기하라고 촉구했습니다.   나간채 / 세월호 참사 광주시민대책위 조사 대상인 관련 부서 공무원이 조사의 주체가 되는 시행령을 만들어 특별법을 무력화시키려고 합니다.      광주 금남로에는 다시 세월호 참사 1주기 합동분향소가 설치됐습니다.      더 이상 주저하지 말고 세월호 인양 계획을 세워야 한다는 1인 시위도 오는 17일까지 이어집니다.   정영일 / 광주시민단체협의회 상임대표 비용의 문제가 아니라 (세월호 인양은) 대한민국의 안전을 세운다는 측면에서 봐야 된다고 생각합니다.      오는 16일까지 광주 금남로와 진도 팽목항 일대에서 세월호 참사 1주기 추모 행사가 잇따라 열려 세월호 진상 규명을 촉구하는 여론은 더욱 거세질 전망입니다. KBS 뉴스 김기중입니다.끝. 담양군 대전면 오늘 {1,004713} 이호진 / 고 이승현 군 아버지 나간채 / 세월호 참사 광주시민대책위 세월호 참사 1주기 추모기간 선포 기자회견 오늘, 광주시 금남로 {2,000309} 나간채 / 세월호 참사 광주시민대책위 조사 대상인 관련 부서 공무원이 조사의 주체가 되는 시행령을 만들어 특별법을 무력화시키려고 합니다. {1,005433}정영일 / 광주시민단체협의회 상임대표 비용의 문제가 아니라 (세월호 인양은) 대한민국의 안전을 세운다는 측면에서 봐야 된다고 생각합니다. 촬영기자 : 이승준 영상편집 : 이두형 "; 
			//String article = "청주 도심 극장가는 대입 수능을 마친 고3학생 등으로 북적거렸고 도시와 농촌가정에서는 김장김치를 담그는 손길이 분주했다.";
			System.out.println("###### Input Article : "+ article);
			System.out.println("###### ( article length is "+ article.length() + ".)");
			
			strResultData = provider.getMainKeywordsInfo(article, 0, 10);
			System.out.println("#1. 키워드(해쉬태그) 추출 : " + strResultData);
			
			//strResultData = provider.getRecommendedContentsInfo(article, 0, 10);
			//System.out.println("#2-1. 연관기사 추천. 전체 컬렉션. 날짜 조건 X : " + strResultData);
			
			strResultData = provider.getRecommendedContentsInfo(article, "article", 0, 10);
			System.out.println("#2-2. 연관기사 추천. 특정 컬렉션. 날짜 조건 X : " + strResultData);
			
			//strResultData = provider.getRecommendedContentsInfo(article, 0, 10, "20150101", "20151231");
			//System.out.println("#2-3. 연관기사 추천. 전체 컬렉션. 날짜 조건 O : " + strResultData);
			
			strResultData = provider.getRecommendedContentsInfo(article, "article", 0, 10, "20150401", "20150430");
			System.out.println("#2-4. 연관기사 추천. 특정 컬렉션. 날짜 조건 O : " + strResultData);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

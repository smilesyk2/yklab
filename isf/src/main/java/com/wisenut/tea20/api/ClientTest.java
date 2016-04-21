package com.wisenut.tea20.api;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import QueryAPI530.Search;

import com.wisenut.tea20.api.TeaClient;
import com.wisenut.tea20.tools.Tools;
import com.wisenut.tea20.types.DocumentInfo;
import com.wisenut.tea20.types.Pair;

public class ClientTest {

    static TeaClient teaClient;
    
    public static final String TEA_IP = "211.39.140.51";
    public static final int TEA_PORT = 11000;
    
	public static final String SEARCH_IP = "211.39.140.51";
	public static final int SEARCH_PORT = 7000;
	public static final int SEARCH_TIMEOUT = 20000;
	
    public static void main(String[] args) {
    	
    	String content;	
    
    	
    	/*
    	String fileName = "AtoJ_test.txt";
    	try {

    		
    		
    		BufferedReader in = new BufferedReader(new FileReader(fileName)) ;
    		FileOutputStream output = new FileOutputStream("result.txt") ;
    		
    		
    		while ( ( content = in.readLine() ) != null )
    		{*/
    
    			
    	//content = "겨울채비 하세요... 이번 주 맑고 포근 윤우현 기자 whyoon@jbnews.com 11월4째주 충북지방은 대체로 고기압의 영향을 받아 맑겠으며, 기온도 평년보다 조금 높아 포근한 한 주가 될 것으로 전망된다.청주기상대는 20일 충북지방은 고기압의 영향을 받아 맑은 날씨를 보이겠으며, 아침최저 청주2도, 충주 영하1도 등 영하2~영상2도의 분포를 보이겠고, 낮 최고기온은 청주 16도, 충주 14도 등 14~16도로 포근할 것이라고 예보했다. 한편 11월 셋째 휴일인 19일 충북지역은 구름이 낀 흐린 날씨를 보인 가운데 유명산 등에는 다소 한산한 모습을 보였다. 가을 단풍이 모두 떨어진 월악산국립공원 입장객은 2천명으로 지난 주의 절반에 그쳤고 속리산에는 4천명의 등산객이 산행을 즐겼으나 평소보다 적은 수준이었다. 대통령 옛 별장인 청원 청남대에도 휴일 평균 관람객의 50%를 밑도는 2천명만 입장해 초겨울 대청호의 풍광을 감상했다. 청주 상당산성, 청원 문의문화재 단지 등 도내 주요 유원지에서는 두툼한 옷을 입은 가족단위 행락객이 등산, 산책, 외식 등을 하며 휴일의 여유를 즐겼다. 청주 도심 극장가는 대입 수능을 마친 고3학생 등으로 북적거렸고 도시와 농촌가정에서는 김장김치를 담그는 손길이 분주했다.";
    	content = "MVP 류현진 신인왕도 석권 프로야구 사상 첫 동시수상 '기염' 중부매일 jb@jbnews.com ‘괴물 루키’ 유현진(19·한화)이 한국 프로야구 출범(1982년) 이후 24년 만에 처음으로 최우수선수(MVP)와 신인왕을 석권했다．유현진은 2일 오후 서울 중구 태평로 1가 서울프라자호텔 그랜드볼룸에서 열린 2006 프로야구 정규시즌 MVP 투표에서 전체 92표 중 47표를 획득， 35표를 얻은 ‘토종 거포’ 이대호(24·롯데)와 10표의 ‘특급 소방수’ 오승환(24·삼성)을 제치고 최고의 선수로 우뚝 서 2천만원 상당의 순금 트로피를 받았다． 유현진은 또 생애 한 번 뿐인 신인왕 투표에서도 82표를 얻어 8표에 그친 ‘황금팔’ 한기주(KIA)를 압도적인 표 차로 따돌리고 최우수신인 타이틀을 차지했다． 국내 프로야구에서 MVP와 신인상을 동시에 수상하기는 유현진이 사상 처음이다． 유현진은 올 해 프로에 입문해 다승(18승)과 방어율(2．23)， 탈삼진(204개) 각 1위에 오르며 선동열 삼성 감독 이후 15년 만에 투수 트리플 크라운 위업을 이뤘다． ‘국보급 투수’ 명성을 얻은 선동열 감독은 해태(현 KIA) 시절이던 지난 1986년과 1989∼91년 등 혼자 4차례 투수 3관왕이 됐다． 또 김진우(KIA)가 2002년 세웠던 한 시즌 신인 최다 탈삼진기록(177개)을 갈아 치우고 신인 최다승 부문에서도 김건우(MBC 1986년)와 어깨를 나란히 하는 등 프로야구 신인 기록을 새롭게 썼다． 유현진은 올 해 이렇게 많은 상을 받게 해주신 모든 분들께 감사를 드린다． 오는 12월 도하 아시안게임 국가대표로 그리고 내년 시즌 더 좋은 모습을 보일 수 있도록 노력하겠다고 소감을 밝혔다． 반면 이대호는 리딩히터(타율 0．336)에 오르며 홈런(26개)과 타점(88개)， 장타율(0．571) 각 1위 등 공격 4관왕으로 지난 1984년 삼성 소속이던 이만수 SK 수석코치 이후 22년 만의 타격 트리플 크라운을 달성했지만 팀의 포스트시즌 진출 실패 등으로 강한 인상을 남기지 못해 수상 좌절의 아쉬움을 남겼다． 삼성 마무리 오승환(24·삼성)도 47세이브를 수확해 일본프로야구의 이와세 히토키(주니치 46세이브)를 넘어 아시아 신기록을 세웠지만 무서운 새내기 유현진을 넘지 못했다． 투·타 부문별 시상에서는 투수 3관왕 유현진과 타격 4관왕 이대호 외에 국내 홀드 부문 신기록(32홀드)을 세운 권오준(삼성)과 승률왕(0．778) 전준호(현대)， 도루왕(51개) 이종욱(두산)， 안타왕(154개) 이용규(KIA)， 득점왕(89개) 박한이(삼성)가 각각 타이틀 수상의 기쁨을 누렸다． / 연합뉴스";
    	//extract keywords from Document
         
        List<Pair<Integer>> keywordList = extractKeywords( content );
       
        
         //test code ( extract keywords from Document )
         
 		for (int i = 0; i < keywordList.size(); i++) {
 			Pair<Integer> item = keywordList.get(i);
 			if (null == item) {
 				continue;
 			}
 			System.out.println( item.key() + "^" + item.value() );
 		}
 		
        
         //make query for SF1 
         
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
        
        
        // get document list for query by SF1          
    	SF1Test test = new SF1Test();
		ArrayList<String> resultList = test.search(query.toString(), 100, 1);
		System.out.println( "get sf1 search result" ); 
		for(String docid: resultList){	
			System.out.println( docid );
		}		
        
        //get similar document list by model  		
		List<Pair<Double>> similarDocumentList = getSimilarDoc( content );     
        System.out.println( "get similar doc of whole documents" );
    	for (int i = 0; i < similarDocumentList.size(); i++) {
 			Pair<Double> item = similarDocumentList.get(i);
 			if (null == item) {
 				continue;
 			}
 			System.out.println( item.key() + "^" + item.value() );
 		}        	
    	
    	 //get similar document list by model + SF1
		similarDocumentList = getSimilarDoc( content, resultList ); 
		System.out.println( "get similar doc of sf1 results" );
		for (int i = 0; i < similarDocumentList.size(); i++) {
 			Pair<Double> item = similarDocumentList.get(i);
 			if (null == item) {
 				continue;
 			}
 			System.out.println( item.key() + "^" + item.value() );
 		}
    		/*}
    		} catch (IOException e) {
        		System.err.println(e) ;
        		System.exit(1); 
        	}*/
    }
    
    public static List<Pair<Integer>> extractKeywords( String content ) {
    	 
    	teaClient = new TeaClient(TEA_IP, TEA_PORT);       
        String query = "CONTENT" + "$!$" + content;
         
        return teaClient.extractKeywordsForPlainText("sample_terms", query, "TERMS" );
    }
    
    public static List<Pair<Double>> getSimilarDoc( String content ) {
   	 
    	teaClient = new TeaClient(TEA_IP, TEA_PORT);
    	// not yet
    	String query = "CONTENT" + "$!$" + content;
         
        return teaClient.getSimilarDoc( "sample_terms", query, "10");
    }
    
    public static List<Pair<Double>> getSimilarDoc( String content, ArrayList<String> resultList ) {
      	 
    	teaClient = new TeaClient(TEA_IP, TEA_PORT);
    	// not yet
    	String query = "CONTENT" + "$!$" + content;
         
        return teaClient.getSimilarDocSf1( "sample_terms", query, "10", resultList);
    }
    
    
   
    public ArrayList<String> search(String query, int listNo, int isDebug){
		boolean debug = false;
		if(isDebug != 0) debug = true;
		
		ArrayList<String> docidList = new ArrayList<String>();
		
		Search search = new Search();
		
		String collection = "article";
		String sort = "RANK/DESC,UID/DESC";
		
		int pageNum = 0;
		
		String documentFields = "DOCID";
		String searchFields = "Subject,Contents";
		
		int ret = 0;
		
		ret = search.w3SetCodePage("UTF-8");
		ret = search.w3SetQueryLog(1);
		ret = search.w3SetCommonQuery(query, 0);
		
		String[] collectionArr = collection.split(",");
		for(String col : collectionArr){
			if(debug) System.out.println(" - collection : " + col);			
			ret = search.w3AddCollection(col);
			
			if(debug) System.out.println(" - ranking : basic, rpf, 10000");
			ret = search.w3SetRanking(col, "basic", "rpf", 10000);
			
			if(debug) System.out.println(" - highlight : 1,1");
			ret = search.w3SetHighlight(col, 1, 1);
			
			if(debug) System.out.println(" - sort : " + sort);
			ret = search.w3SetSortField(col, sort);
			
			if(debug) System.out.println(" - query analyzer : 1,1,1,1");
			ret = search.w3SetQueryAnalyzer(col, 1, 1, 1, 1);
			
			if(debug) System.out.println(" - search fields : " + searchFields);
			ret = search.w3SetSearchField(col, searchFields);
			
			if(debug) System.out.println(" - document fields : " + documentFields);
			ret = search.w3SetDocumentField(col, documentFields);
			
			if(debug) System.out.println(" - page info : " + pageNum + ", " + listNo);
			ret = search.w3SetPageInfo(col, pageNum, listNo);
		}
		
		if(debug) System.out.println(" - search ip : " + SEARCH_IP);
		if(debug) System.out.println(" - search port : " + SEARCH_PORT);
		if(debug) System.out.println(" - search timeout : " + SEARCH_TIMEOUT);
		ret = search.w3ConnectServer(SEARCH_IP, SEARCH_PORT, SEARCH_TIMEOUT);
		
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
		int count = search.w3GetResultCount(collection);
		for(int i=0; i<count; i++){
			String docid = search.w3GetField(collection, "DOCID", i);
			docidList.add(docid);
		}
		
		return docidList;
	}
}

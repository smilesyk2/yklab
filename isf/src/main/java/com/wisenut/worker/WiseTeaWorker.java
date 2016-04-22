package com.wisenut.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wisenut.common.WNProperties;
import com.wisenut.model.MainKeywordsInfo;
import com.wisenut.tea20.api.TeaClient;
import com.wisenut.tea20.types.Pair;
import com.wisenut.util.StringUtil;

public class WiseTeaWorker {
	final static Logger logger = LogManager.getLogger(WiseTeaWorker.class);
	
	public Properties prop;
	public TeaClient teaClient;
	public String teaIP;
	public int teaPort;
	public String collectionId;
	public String targetField;
	public String searchField;
	
	public WiseTeaWorker() throws Exception{
		WNProperties wnprop = WNProperties.getInstance("/wisenut.properties");
		
		teaIP = wnprop.getProperty("tea.ip");
		logger.debug("tea ip : " + teaIP);
		
		teaPort = Integer.parseInt(wnprop.getProperty("tea.port"));
		logger.debug("tea port : " + teaPort);
		
		teaClient = new TeaClient(teaIP, teaPort);
		
		collectionId = wnprop.getProperty("tea.collection.id");
		logger.debug("collection id : " + collectionId);
		
		targetField = wnprop.getProperty("tea.collection.target");
		logger.debug("target field : " + targetField);
		
		searchField = wnprop.getProperty("tea.collection.searchfield");
		logger.debug("search field : " + searchField);
	}
	
	public List<Pair<Integer>> getMainKeywordsPair(String article, int start, int pageNo){
		article = searchField + "$!$" + article;
		List<Pair<Integer>> keywordList = teaClient.extractKeywordsForPlainText(collectionId, article, targetField);
		
		logger.debug("keywordList size : " + keywordList.size());
		
		return keywordList;
	}
	
	public String getMainKeywordsInfo(String article, int start, int pageNo){
		article = searchField + "$!$" + article;
		List<Pair<Integer>> keywordList = teaClient.extractKeywordsForPlainText(collectionId, article, targetField);
		ArrayList<MainKeywordsInfo> mkiList = new ArrayList<MainKeywordsInfo>(); 
		
		for(Pair<Integer> p : keywordList){
			MainKeywordsInfo mki = new MainKeywordsInfo();
			mki.setKeyword(p.key());
			mki.setScore(p.value());
			
			mkiList.add(mki);
		}
		
		logger.debug("mkiList size : " + mkiList.size());
		
		return StringUtil.objectToString(mkiList);
	}
	
	public List<Pair<Double>> getRecommendedContentsPair(String article, int pageno){
		article = searchField + "$!$" + article;
		List<Pair<Double>> documentList = teaClient.getSimilarDoc( collectionId, article, String.valueOf(pageno));
		
		logger.debug("documentList size : " + documentList.size());
		
		return documentList;
	}
	
	public List<Pair<Double>> getRecommendedContentsPair(String article, ArrayList<String> searchResultList, int pageno){
		article = searchField + "$!$" + article;
		List<Pair<Double>> documentList = teaClient.getSimilarDocSf1( collectionId, article, String.valueOf(pageno), searchResultList);
		
		logger.debug("documentList size : " + documentList.size());
		
		return documentList;
	}
	
	public static void main(String[] args){
		WiseTeaWorker teaWorker;
		String article = "겨울채비 하세요... 이번 주 맑고 포근 윤우현 기자 whyoon@jbnews.com 11월4째주 충북지방은 대체로 고기압의 영향을 받아 맑겠으며, 기온도 평년보다 조금 높아 포근한 한 주가 될 것으로 전망된다.청주기상대는 20일 충북지방은 고기압의 영향을 받아 맑은 날씨를 보이겠으며, 아침최저 청주2도, 충주 영하1도 등 영하2~영상2도의 분포를 보이겠고, 낮 최고기온은 청주 16도, 충주 14도 등 14~16도로 포근할 것이라고 예보했다. 한편 11월 셋째 휴일인 19일 충북지역은 구름이 낀 흐린 날씨를 보인 가운데 유명산 등에는 다소 한산한 모습을 보였다. 가을 단풍이 모두 떨어진 월악산국립공원 입장객은 2천명으로 지난 주의 절반에 그쳤고 속리산에는 4천명의 등산객이 산행을 즐겼으나 평소보다 적은 수준이었다. 대통령 옛 별장인 청원 청남대에도 휴일 평균 관람객의 50%를 밑도는 2천명만 입장해 초겨울 대청호의 풍광을 감상했다. 청주 상당산성, 청원 문의문화재 단지 등 도내 주요 유원지에서는 두툼한 옷을 입은 가족단위 행락객이 등산, 산책, 외식 등을 하며 휴일의 여유를 즐겼다. 청주 도심 극장가는 대입 수능을 마친 고3학생 등으로 북적거렸고 도시와 농촌가정에서는 김장김치를 담그는 손길이 분주했다.";
		try{
			teaWorker = new WiseTeaWorker();
			System.out.println(teaWorker.getMainKeywordsInfo(article, 0, 10));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}

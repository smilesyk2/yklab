package com.wisenut;

import com.wisenut.common.WNConstants;
import com.wisenut.model.WNResultData;
import com.wisenut.worker.DaumWorker;
import com.wisenut.worker.FacebookWorker;
import com.wisenut.worker.NaverWorker;
import com.wisenut.worker.TwitterWorker;
import com.wisenut.worker.YoutubeWorker;

public class OpenAPIProvider {
	
	public WNResultData getOpenAPIResult(String provider, String query, int startPos, int pageNo, String sort){
		WNResultData data = new WNResultData();
		data.setProvider(provider);
		
		if(provider.equals("twitter")){
			TwitterWorker tWorker = new TwitterWorker();
			tWorker.search(query, startPos, pageNo, sort, data);
		}else if(provider.equals("naver")){
			NaverWorker nWorker = new NaverWorker();
			nWorker.search(query, startPos, pageNo, sort, data);
		}else if(provider.equals("daum")){
			DaumWorker dWorker = new DaumWorker();
			dWorker.search(query, startPos, pageNo, sort, data);
		}else if(provider.equals("facebook")){
			FacebookWorker fWorker = new FacebookWorker("group");
			fWorker.search(query, data);
		}else if(provider.equals("youtube")){
			YoutubeWorker yWorker = new YoutubeWorker();
			yWorker.search(query, startPos, pageNo, sort, data);
		}
		return data;
	}
	
	public static void main(String[] args){
		OpenAPIProvider provider = new OpenAPIProvider();
		WNResultData resultData = provider.getOpenAPIResult("twitter", "스바세행사", 1, 10, WNConstants.METAINFO_BY_PROVIDER[WNConstants.TWITTER_ID][WNConstants.SORT_BY_RANK]);
		
		System.out.println(resultData.toString());
	}
}

package com.wisenut.worker;


import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.wisenut.model.OpenAPIResult;
import com.wisenut.model.WNResultData;
import com.wisenut.util.StringUtil;

public class TwitterWorker {
	private Twitter twitter;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");	
	
	public TwitterWorker(){
		twitter = TwitterFactory.getSingleton();
	}
		
	public void search(String query, int startPos, int pageNo, String sort, WNResultData data){
		try {
			Query twitterQuery = new Query(query);
			twitterQuery.setCount(pageNo);
			
			QueryResult result = twitter.search(twitterQuery);
			
			
			Map<String, RateLimitStatus> statusMap = twitter.getRateLimitStatus();
			/*Iterator<String> iter = statusMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				System.out.println("#### " + key +" :::::" + statusMap.get(key).getLimit());
			}*/
			System.out.println("### /search/tweets ::: " + statusMap.get("/search/tweets").getRemaining() + "/" +statusMap.get("/search/tweets").getLimit());
			
			data.setProvider("twitter");
			data.setCurrentCount(result.getTweets().size());
			data.setStartPos(startPos);
			
			int pos = 0;
			do{
				if(pos+1 == startPos ){
					for (Status status : result.getTweets()) {
						OpenAPIResult thisResult = new OpenAPIResult();
						thisResult.setTitle("");
						thisResult.setContents(StringUtil.removeSpecialCharacter(status.getText()));
						thisResult.setCreateDate(sdf.format(status.getCreatedAt()));
						thisResult.setAuthor(status.getUser().getScreenName());
						thisResult.setLink("https://twitter.com/"+status.getUser().getScreenName()+"/status/"+status.getId());
						thisResult.setThumbnailUrl("");
						
						data.addItem(thisResult);
					}
					break;
				}
				pos++;
			}while(result.hasNext());
			
			data.setTotalCount(0);
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

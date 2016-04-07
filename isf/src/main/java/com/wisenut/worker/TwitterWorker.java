package com.wisenut.worker;


import java.text.SimpleDateFormat;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

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
			
			
			data.setProvider("twitter");
			data.setCurrentCount(result.getTweets().size());
			data.setStartPos(startPos);
			
			int pos = 0;
			do{
				if(pos+1 == startPos ){
					for (Status status : result.getTweets()) {
						 data.addItem("", StringUtil.removeSpecialCharacter(status.getText()), sdf.format(status.getCreatedAt()), status.getUser().getScreenName(), "");
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

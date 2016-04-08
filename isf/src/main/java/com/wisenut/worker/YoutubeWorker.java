/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wisenut.worker;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.wisenut.model.WNResultData;
import com.wisenut.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Prints a list of videos based on a search term.
 *
 * @author Jeremy Walker
 */
public class YoutubeWorker {

  /** Global instance properties filename. */
  private static String PROPERTIES_FILENAME = "youtube.properties"; // 주석 테스트

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
  private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

  /** Global instance of Youtube object to make all API requests. */
  private YouTube youtube;
  private Properties properties;
  private DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmss");


  public YoutubeWorker(){
	  properties = new Properties();
	  try {
		  InputStream in = YoutubeWorker.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
		  properties.load(in);
	  } catch (IOException e) {
		  System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
				  + " : " + e.getMessage());
		  System.exit(1);
	  }
	  
	  youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
	        public void initialize(HttpRequest request) throws IOException {}
	      }).setApplicationName("youtube-cmdline-search-sample").build();
  }
  
  public void search(String query, int startPos, int pageNo, String sort, WNResultData data){
	  try {
		  YouTube.Search.List search = youtube.search().list("id,snippet");
		  
		  String apiKey = properties.getProperty("youtube.apikey");
		  long l_pageNo = Long.valueOf(pageNo);
		  
		  search.setKey(apiKey);
		  search.setQ(query);
		  search.setType("video");
		  search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/publishedAt,snippet/description,snippet/channelTitle,snippet/channelId)");		  
		  search.setMaxResults(l_pageNo);
		  
		  SearchListResponse searchResponse = search.execute();
		  List<SearchResult> searchResultList = searchResponse.getItems();
		  if (searchResultList != null) {
			  setResultData(searchResultList.iterator(), data);
		  }
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
  }
  
  private void setResultData(Iterator<SearchResult> iteratorSearchResults, WNResultData data) {
	  data.setProvider("youtube");

	  int numberOfResult = 0;
	  while (iteratorSearchResults.hasNext()) {
		  SearchResult singleVideo = iteratorSearchResults.next();
		  ResourceId rId = singleVideo.getId();
		  
		  // Double checks the kind is video.
		  if (rId.getKind().equals("youtube#video")) {
			  numberOfResult++;
			  
			  Thumbnail thumbnail = (Thumbnail)singleVideo.getSnippet().getThumbnails().get("default");
			  
			  data.addItem(StringUtil.removeSpecialCharacter(singleVideo.getSnippet().getTitle()),
					  StringUtil.removeSpecialCharacter(singleVideo.getSnippet().getDescription()),
					  dtf.print(DateTime.parse(singleVideo.getSnippet().getPublishedAt().toString())),
					  singleVideo.getSnippet().getChannelTitle(),
					  "https://youtu.be/" + rId.getVideoId(),
					  thumbnail.getUrl());
		  }
	  }
	  
	  data.setTotalCount(numberOfResult);
  }
  
  public static void main(String[] args){
	  YoutubeWorker yWorker = new YoutubeWorker();
	  WNResultData data = new WNResultData();
	  yWorker.search("conan", 1, 10, "accu", data);
  }
}
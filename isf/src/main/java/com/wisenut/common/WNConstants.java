package com.wisenut.common;

public class WNConstants {
	public static final int NAVER_ID = 0;
	public static final int DAUM_ID = 1;
	public static final int TWITTER_ID = 2;
	public static final int FACEBOOK_ID = 3;
	public static final int YOUTUBE_ID = 4;
	
	public static final int PROVIDER_NAME = 0;
	public static final int PROPERTY_NAME = 1;
	public static final int SORT_BY_RANK = 2;
	public static final int SORT_BY_DATE = 3;
	//public static final int SORT_ASC = 3;
	//public static final int SORT_DSC = 4;
	
	public static final String[][] METAINFO_BY_PROVIDER = {
		{"Naver", "openapi.xml", "sim", "date"},
		{"Daum", "openapi.xml", "accu", "date"},
		{"Twitter", "twitter4j.properties", "popular", "recent"},
		{"Facebook", "facebook4j.properties", "", ""},
		{"Youtube", "youtube.properties", "", ""},
	};
}

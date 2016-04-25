package com.wisenut.worker;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.wisenut.model.OpenAPIResult;
import com.wisenut.model.WNResultData;
import com.wisenut.util.StringUtil;

import facebook4j.Event;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Group;
import facebook4j.Location;
import facebook4j.Page;
import facebook4j.Place;
import facebook4j.ResponseList;
import facebook4j.User;

public class FacebookWorker {
	private Facebook facebook;
	private String searchType;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");	
	
	public FacebookWorker(String _searchType){
		facebook = new FacebookFactory().getInstance();
		searchType = _searchType;
	}
		
	public void search(String query, WNResultData data){
		try {
			ResponseList results = null;
			
			if(searchType.equals("event")){
				results = facebook.searchEvents(query);
			}else if(searchType.equals("location")){
				results = facebook.searchLocations(query);
			}else if(searchType.equals("page")){
				results = facebook.searchPages(query);
			}else if(searchType.equals("group")){
				results = facebook.searchGroups(query);
			}else if(searchType.equals("place")){
				results = facebook.searchPlaces(query);
			}else if(searchType.equals("user")){
				results = facebook.searchUsers(query);
			}
			
			data.setProvider("facebook");
			data.setTotalCount(results.size());
			
			for(Object result : results){
				OpenAPIResult thisResultObj = new OpenAPIResult();
				String createDate = getCreateDate(result)!=null ? sdf.format(getCreateDate(result)):"";
				 
				thisResultObj.setTitle(getTitle(result));
				thisResultObj.setContents(StringUtil.removeSpecialCharacter(getContents(result)));
				thisResultObj.setCreateDate(createDate);
				thisResultObj.setAuthor(getAuthor(result));
				thisResultObj.setLink(getLink(result));
				thisResultObj.setThumbnailUrl("");
								
				data.addItem(thisResultObj.toString());
			}
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getTitle(Object result){
		String title = "";
		if(searchType.equals("event")){
			title = ((Event)result).getName();
		}else if(searchType.equals("location")){
			title = ((Location)result).getPlace()!=null ? ((Location)result).getPlace().getName():"";
		}else if(searchType.equals("page")){
			title = ((Page)result).getName();
		}else if(searchType.equals("group")){
			title = ((Group)result).getName();
		}else if(searchType.equals("place")){
			title = ((Place)result).getName();
		}else if(searchType.equals("user")){
			title = ((User)result).getName();
		}
		
		return title;
	}
	
	public String getContents(Object result){
		String contents = "";
		
		if(searchType.equals("event")){
			contents = ((Event)result).getDescription();
		}else if(searchType.equals("location")){
			contents = "";
		}else if(searchType.equals("page")){
			contents = ((Page)result).getAbout();
		}else if(searchType.equals("group")){
			contents = ((Group)result).getDescription();
		}else if(searchType.equals("place")){
			contents = "";
		}else if(searchType.equals("user")){
			contents = "";
		}
		
		return contents;
	}
	
	public Date getCreateDate(Object result){
		Date createDate = new Date();
		
		if(searchType.equals("event")){
			createDate = ((Event)result).getStartTime();
		}else if(searchType.equals("location")){
			createDate = ((Location)result).getCreatedTime();
		}else if(searchType.equals("page")){
			createDate = ((Page)result).getCreatedTime();
		}else if(searchType.equals("group")){
			createDate = ((Group)result).getUpdatedTime();
		}else if(searchType.equals("place")){
			createDate = null;
		}else if(searchType.equals("user")){
			createDate = ((User)result).getUpdatedTime();
		}
		
		return createDate;
	}
	
	public String getAuthor(Object result){
		String author = "";
		
		if(searchType.equals("event")){
			author = ((Event)result).getOwner()!=null ? ((Event)result).getOwner().getName():"";
		}else if(searchType.equals("location")){
			author = "";
		}else if(searchType.equals("page")){
			author = ((Page)result).getUsername();
		}else if(searchType.equals("group")){
			author = ((Group)result).getOwner()!=null ? ((Group)result).getOwner().getName():"";
		}else if(searchType.equals("place")){
			author = "";
		}else if(searchType.equals("user")){
			author = "";
		}
		
		return author;
	}
	
	public String getLink(Object result){
		String link = "";
		
		if(searchType.equals("event")){
			link = ((Event)result).getTicketURI()!=null ? ((Event)result).getTicketURI().getPath():"";
		}else if(searchType.equals("location")){
			link = "";
		}else if(searchType.equals("page")){
			link = ((Page)result).getLink()!=null ? ((Page)result).getLink().getPath():"";
		}else if(searchType.equals("group")){
			link = "";
		}else if(searchType.equals("place")){
			link = "";
		}else if(searchType.equals("user")){
			link = ((User)result).getLink()!=null ? ((User)result).getLink().getPath():"";
		}
		
		return link;
	}
}

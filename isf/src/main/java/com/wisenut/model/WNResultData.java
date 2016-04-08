package com.wisenut.model;

import java.util.ArrayList;

public class WNResultData {
	private static final String JSON_COMMA = ",";
	private static final String JSON_COLON = ":";
	private static final String JSON_QUOT ="\"";
	
	private String provider;
	private String sort;
	private int totalCount;
	private int currentCount;
	private int startPos;
	private ArrayList<WNResultItem> itemList;
	
	public WNResultData(){
		itemList = new ArrayList<WNResultItem>();
	}
	
	public String toString(){
		StringBuffer sbResult = new StringBuffer();
		sbResult.append("{");
		sbResult.append(JSON_QUOT + "Result" + JSON_QUOT + JSON_COLON);
		
		sbResult.append(	"{");
		sbResult.append(	JSON_QUOT + "provider" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.provider + JSON_QUOT);
		sbResult.append(	JSON_COMMA);
		sbResult.append(	JSON_QUOT + "totalCount" + JSON_QUOT + JSON_COLON).append(this.totalCount);
		sbResult.append(	JSON_COMMA);
		sbResult.append(	JSON_QUOT + "currentCount" + JSON_QUOT + JSON_COLON).append(this.currentCount);
		sbResult.append(	JSON_COMMA);
		sbResult.append(	JSON_QUOT + "startPos" + JSON_QUOT + JSON_COLON).append(this.startPos);
		sbResult.append(	JSON_COMMA);
		sbResult.append(	JSON_QUOT + "itemList" + JSON_QUOT + JSON_COLON);
		sbResult.append(		"[");
		for(int i=0; i<itemList.size(); i++){
			sbResult.append(	"{");
			sbResult.append(	JSON_QUOT + "title" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.itemList.get(i).getTitle() + JSON_QUOT);
			sbResult.append(	JSON_COMMA);
			sbResult.append(	JSON_QUOT + "contents" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.itemList.get(i).getContents() + JSON_QUOT);
			sbResult.append(	JSON_COMMA);
			sbResult.append(	JSON_QUOT + "createDate" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.itemList.get(i).getCreateDate() + JSON_QUOT);
			sbResult.append(	JSON_COMMA);
			sbResult.append(	JSON_QUOT + "author" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.itemList.get(i).getAuthor() + JSON_QUOT);
			sbResult.append(	JSON_COMMA);
			sbResult.append(	JSON_QUOT + "link" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.itemList.get(i).getLink() + JSON_QUOT);
			sbResult.append(	JSON_COMMA);
			sbResult.append(	JSON_QUOT + "thumbnail" + JSON_QUOT + JSON_COLON).append(JSON_QUOT + this.itemList.get(i).getThumbnailUrl() + JSON_QUOT);
			sbResult.append(	"}");
			
			if( i != itemList.size()-1 ){
				sbResult.append(JSON_COMMA);
			}
		}
		sbResult.append(		"]");
		sbResult.append(	"}");
		sbResult.append("}");
		
		return sbResult.toString();
	}
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getCurrentCount() {
		return currentCount;
	}
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}
	public int getStartPos() {
		return startPos;
	}
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	public ArrayList<WNResultItem> getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList<WNResultItem> itemList) {
		this.itemList = itemList;
	}
	public void addItem(String title, String contents, String createDate, String author, String link, String thumbnailUrl){
		WNResultItem item = new WNResultItem();
		item.setTitle(title);
		item.setContents(contents);
		item.setCreateDate(createDate);
		item.setAuthor(author);
		item.setLink(link);
		item.setThumbnailUrl(thumbnailUrl);
		
		itemList.add(item);
	}
}

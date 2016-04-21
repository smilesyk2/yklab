package com.wisenut.tea20.types;

import java.util.ArrayList;
import java.util.List;


public class RelativeKeywordInfo {

	private String mainKeyword;
	private String relativeKeyword;
	private boolean isModified = false;
	private String positiveKeyword;
	private String negativeKeyword;
	private boolean isExcepted = false;
	private List<String> collectionIdList;

	private boolean isLocked = false;
	private String keywordHistType;
	private String createDate;
	private String modifyDate;
	private String historyKeyword;

	public enum HistoryType {
		CREATE, MODIFY, DELETE
	}

	public RelativeKeywordInfo(String mainKeyword, String relativeKeyword,
			String positiveKeyword, String negativeKeyword, String historyKeyword,
			boolean isModified, boolean isExcepted, boolean isLocked,
	        String createDate, String modifyDate, String keywordHistType
	) {
		this.mainKeyword = mainKeyword;
		this.relativeKeyword = relativeKeyword;
		this.positiveKeyword = positiveKeyword;
		this.negativeKeyword = negativeKeyword;
		this.historyKeyword = historyKeyword;
		this.isModified = isModified;
		this.isExcepted = isExcepted;
		this.isLocked = isLocked;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.keywordHistType = keywordHistType;
	}
	
	public RelativeKeywordInfo(String mainKeyword, String relativeKeyword,
			String positiveKeyword, String negativeKeyword, String historyKeyword,
	        String createDate, String modifyDate, String keywordHistType
	) {
		this(mainKeyword, relativeKeyword, positiveKeyword, negativeKeyword, historyKeyword
				, false, false, false
		        , createDate, modifyDate, keywordHistType);
	}

	public String getMainKeyword() {
		return mainKeyword;
	}

	public void setMainKeyword(String mainKeyword) {
		this.mainKeyword = mainKeyword;
	}

	public String getRelativeKeyword() {
		return relativeKeyword;
	}

	public void setRelativeKeyword(String relativeKeyword) {
		this.relativeKeyword = relativeKeyword;
	}
	
	public boolean isModified() {
		return  this.isModified;
	}
	
	public void setIsModified(boolean isModified) {
		this.isModified = isModified;
	}
	
	public String getPositiveKeyword() {
		return this.positiveKeyword;
	}
	
	public void setPositiveKeyword(String positiveKeyword) {
		this.positiveKeyword = positiveKeyword;
	}
	
	public String getNegativeKeyword() {
		return this.negativeKeyword;
	}
	
	public void setNegativeKeyword(String negativeKeyword) {
		this.negativeKeyword = negativeKeyword;
	}
	
	public boolean isExcepted() {
		return this.isExcepted;
	}
	
	public void setIsExcepted(boolean isExcepted) {
		this.isExcepted = isExcepted;
	}
	
	public void addCollectionId(String collectionId) {
		if (this.collectionIdList == null) {
			collectionIdList = new ArrayList<String>();
		}
		
		collectionIdList.add(collectionId);
	}
	
	public List<String> getCollectionIds() {
		return collectionIdList;
	}
	
	public int getCollectionIdCount() {
		return collectionIdList != null ? collectionIdList.size() : 0;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getKeywordHistType() {
		return keywordHistType;
	}

	public void setKeywordHistType(String keywordHistType) {
		this.keywordHistType = keywordHistType;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getHistoryKeyword() {
		return historyKeyword;
	}

	public void setHistoryKeyword(String historyKeyword) {
		this.historyKeyword = historyKeyword;
	}
}

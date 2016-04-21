package com.wisenut.tea20.types;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO for Document Info.
 * 
 * Description for a document with detailed analysis info.
 * @author hkseo@wisenut.co.kr
 */
public final class DocumentInfo
{
	String id_; 
	String title_; 
	String content_;
	String date_;
	String [] keywords_;
	List< Pair<Integer> > topics_;
	List< Pair<String> > keywordTopicPairs_;
	List< Pair<String> > customFieldValues_;
	
	public DocumentInfo()
	{
		id_ = "";
		title_ = "";
		content_ = "";
		date_ = "";
		keywords_ = new String [0];
		topics_ = new ArrayList< Pair<Integer> > ();
		keywordTopicPairs_ = new ArrayList< Pair<String> > ();
		customFieldValues_ = new ArrayList< Pair<String> > ();
	}
	
	public DocumentInfo(String id, String title, String content, String date, 
			String [] keywords, List< Pair<Integer> > topics, List< Pair<String> > keywordTopicPairs, List <Pair<String> > custom)
	{
		id_ = id;
		title_ = title;
		content_ = content;
		date_ = date;
		keywords_ = keywords;
		topics_ = topics;
		keywordTopicPairs_ = keywordTopicPairs;
		customFieldValues_ = custom;
	}

	/**
	 * Get document ID
	 * @return ID
	 */
	public String getId()
	{
		return id_;
	}

	/**
	 * Get title
	 * @return title
	 */
	public String getTitle()
	{
		return title_;
	}

	/**
	 * Get text content of the document
	 * @return text content
	 */
	public String getContent()
	{
		return content_;
	}
	
	/**
	 * Get date as YYYYMMDD form
	 * @return data string (YYYYMMDD)
	 */
	public String getDate()
	{
		return date_;
	}

	/**
	 * Get keywords extracted
	 * @return keywords as String Array
	 */
	public String [] getKeywords()
	{
		return keywords_;
	}

	/**
	 * Get topics assigned to this document
	 * @return topics as (topic_id, weight) pairs
	 */
	public List< Pair<Integer> > getTopics()
	{
		return topics_;
	}

	/**
	 * Get keyword-topic assignments pairs
	 * @return assignment as (keyword, topic_id) pairs
	 */
	public List< Pair<String> > getKeywordTopicPairs()
	{
		return keywordTopicPairs_;
	}
	
	/**
	 * Get custom fields and associated values
	 * @return (field_name, value) pairs
	 */
	public List< Pair<String> > getCustomFieldValues()
	{
		return customFieldValues_;
	}
	
}

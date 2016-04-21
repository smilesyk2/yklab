package com.wisenut.tea20.types;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DTO for Metadata Group item.
 * 
 * @author hkseo@wisenut.co.kr
 */
public class MetadataGroup
{
	private String id_ = "";
	private String date_ = "";
	
	private Map<String, Pair<String> > fieldValues_ = null;
	
	public MetadataGroup(String id)
	{
		id_ = id;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		date_ = formatter.format(new Date());
		
		fieldValues_ = new HashMap<String, Pair<String> > ();
	}
	
	public MetadataGroup(String id, String dateString)
	{
		id_ = id;
		if (null == dateString || 0 == dateString.length())
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			date_ = formatter.format(new Date());
		}
		else
		{
			date_ = dateString;
		}
		
		fieldValues_ = new HashMap<String, Pair<String> > ();
	}
	
	/**
	 * Get ID of the metadata group item
	 * @return ID
	 */
	public String getId()
	{
		return id_;
	}
	
	/**
	 * Get data as String
	 * @return date (YYYYMMDD)
	 */
	public String getDate()
	{
		return date_;
	}
	
	/**
	 * Set (field, value) pair in the metadata group item 
	 * @param field name of field
	 * @param value value for field
	 */
	public void setValue(String field, String value)
	{
		if (null != fieldValues_.get(field))
		{
			fieldValues_.remove(field);
		}
		fieldValues_.put(field, new Pair<String> (field, value));
	}
	
	/**
	 * Get value of given field
	 * @param field field to retrieve
	 * @return value for field
	 */
	public String getValue(String field)
	{
		Pair<String> fieldValue = fieldValues_.get(field);
		
		return (null == fieldValue ? "" : fieldValue.value());
	}
	
	/**
	 * Get entire field-value pairs
	 * @return (field, value) pairs in the metadata group item
	 */
	public List< Pair<String> > getFieldValues()
	{
		List< Pair<String> > toReturn = new ArrayList< Pair<String> > ();
		
		Iterator< Pair<String> > entries = fieldValues_.values().iterator();
		
		while (entries.hasNext())
		{
			toReturn.add(entries.next());
		}
		
		return toReturn;
	}
}

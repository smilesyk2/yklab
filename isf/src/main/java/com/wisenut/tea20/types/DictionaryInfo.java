package com.wisenut.tea20.types;

/**
 * DTO for Dictionary Info.
 * 
 * Description for a inner dictionary with some useful metadata.
 * @author hkseo@wisenut.co.kr
 * @deprecated
 */
public final class DictionaryInfo
{
	private String id_;
	public String getId()
	{
		return id_;
	}

	public DictionaryType getType()
	{
		return type_;
	}

	public String getFileName()
	{
		return fileName_;
	}
	private DictionaryType type_;
	private String fileName_;
	
	/**
	 * Constructor.
	 * 
	 * @param id dictionary ID
	 * @param fileName file name of dictionary
	 * @param isBlackWord if true, it's blackword(aka stopword)
	 */
	public DictionaryInfo(String id, String fileName, boolean isBlackWord)
	{
		id_ = id;
		fileName_ = fileName;

		if (isBlackWord)
		{
			type_ = DictionaryType.BLACKWORD;
		}
		else
		{
			type_ = DictionaryType.WHITEWORD;
		}
	}
	
	public enum DictionaryType 
	{
		BLACKWORD,
		WHITEWORD
	}
}


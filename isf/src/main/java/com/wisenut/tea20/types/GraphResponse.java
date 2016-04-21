package com.wisenut.tea20.types;

import java.util.*;

/**
 * Wrapper for Graph API Response.
 * 
 * (inner class: under construction and not opened)
 * 
 * @author hkseo@wisenut.co.kr
 *
 */
public class GraphResponse
{
	private String message_ = "";
	private String code_ = "";
	private boolean isSuccessful_ = true;
	
	private Map<String, String> params_ = null;
	
	private String responseString_ = "";
	
	public GraphResponse()
	{
		params_ = new HashMap<String, String> ();
	}
	
	public void setParam(String key, String value)
	{
		params_.put(key, value);
	}
	
	public String getParam(String key)
	{
		String toReturn = params_.get(key);
		if (null == toReturn)
			toReturn = "";
		return toReturn;
	}

	public String getMessage()
	{
		return message_;
	}

	public void setMessage(String message)
	{
		this.message_ = message;
	}

	public String getCode()
	{
		return code_;
	}

	public void setCode(String code)
	{
		this.code_ = code;
	}

	public boolean isSuccessful()
	{
		return isSuccessful_;
	}

	public void setSuccessful(boolean isSuccessful)
	{
		this.isSuccessful_ = isSuccessful;
	}
	
	public void setResponseString(String str)
	{
		this.responseString_ = str;
	}
	
	public String getResponseString()
	{
		return responseString_;
	}
}

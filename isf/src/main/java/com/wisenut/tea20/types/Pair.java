package com.wisenut.tea20.types;

/**
 * Pair Container.
 * 
 * Defines Pair container like that in C++. Used to store (Str, value) elements. 
 * 
 * @author hkseo@wisenut.co.kr
 *
 * @param <E> template variable for assigning type of value
 */
final public class Pair<E>
{
	/**
	 * Constructor. (It's immutable - only way to set pair values)
	 * @param key 
	 * @param value
	 */
	public Pair(String key, E value)
	{
		this.key_ = key;
		this.value_ = value;
	}
	
	public Pair()
	{
	}
	
	private String key_ = "";
	private E value_ ;
	
	/**
	 * Get key value.
	 * @return key
	 */
	public String key()
	{
		return key_;
	}
	
	/**
	 * Get value.
	 * @return value
	 */
	public E value()
	{
		return value_;
	}
}

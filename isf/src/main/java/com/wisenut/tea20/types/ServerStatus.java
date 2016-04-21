package com.wisenut.tea20.types;

/**
 * DTO for Server Status.
 * 
 * @author hkseo@wisenut.co.kr
 */
final public class ServerStatus
{
	boolean alive_;
	String ip_;
	int port_;
	long startTime_;
	
	/**
	 * Constructor. 
	 * 
	 * @param ip IP of server
	 * @param port port of server
	 * @param startTime time when server started (can be converted into java.util.Date using new Date(value))
	 * @param alive true if server's alive (if it's offline, startTime's '0')
	 */
	public ServerStatus(String ip, int port, long startTime, boolean alive)
	{
		ip_ = ip;
		port_ = port;
		startTime_ = startTime;
		alive_ = alive;
	}

	/**
	 * Return whether server's alive
	 * @return true if alive
	 */
	public boolean isAlive()
	{
		return alive_;
	}

	/**
	 * Get IP of server
	 * @return IP string
	 */
	public String getIp()
	{
		return ip_;
	}

	/**
	 * Get port number of server
	 * @return port number
	 */
	public int getPort()
	{
		return port_;
	}

	/**
	 * Get time when server's started
	 * @return time in long value
	 */
	public long getStartTime()
	{
		return startTime_;
	}
}

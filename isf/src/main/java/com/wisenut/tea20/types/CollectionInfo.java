package com.wisenut.tea20.types;

/**
 * DTO for Collection Information.
 * 
 * <br>Major information is status of collection. This status're like following:
 * <ul>
 * <li>INIT: initial status</li>
 * <li>LOCK_TE: collection's locked due to keyword extraction</li>
 * <li>LOCK_RST: collection's locked due to topic extraction (aka roasting)</li>
 * <li>LOCK_CUSTOM: collection's locked due to custom field analysis</li>
 * <li>RELEASED: collection's now open to TE/RST/CUSTOM</li>
 * <li>UNDEFINED: undefined. default value</li>
 * </ul>
 * @author hkseo@wisenut.co.kr
 */
public class CollectionInfo
{
	String id_ = "";
	private boolean isTeCollection_ = false;
	private boolean isRoasterCollection_ = false;
	
	private long timeTeExtracted_ = 0;
	private long timeRoasterAnalyzed_ = 0;
	private long timeStarted_ = 0;
	
	private CollectionStatus status_ = CollectionStatus.UNDEFINED;

	private boolean needsAnalysis_ = false;
	private boolean isRealtime_ = false;
	private String taskId_ = "";
	private String charset_ = "";
	
	private String errCodeTe_ = "";
	private String errMessageTe_ = "";
	private String errCodeRoaster_ = "";
	private String errMessageRoaster_ = "";
	private String errCodeCustom_ = "";
	private String errMessageCustom_ = "";
	
	
	/**
	 * Default Constructor.
	 */
	public CollectionInfo()
	{
	}
	
	/**
	 * Constructor.
	 * @param id collection ID
	 * @param teColl whether it's collection for TE
	 * @param roasterColl whether it's collection for Roaster
	 * @param timeTeExtract time when TE extraction (feature extraction) finished (0 if not executed)
	 * @param timeRoasterAnalysis time when Roaster analysis (topic analysis) finished (0 if not executed)
	 * @param timeStarted time when current task started (when collection's locked. 0 if not locked)
	 * @param statusString description string for status (INIT|LOCK_TE|LOCK_RST|LOCK_CUSTOM|RELEASED)
	 * @param needsAnalysis if set to true, this collection's required topic analysis 
	 */
	public CollectionInfo(String id, boolean teColl, boolean roasterColl,
			long timeTeExtract, long timeRoasterAnalysis, long timeStarted, 
			String statusString, boolean needsAnalysis)
	{
		id_ = id;
		isTeCollection_ = teColl;
		isRoasterCollection_ = roasterColl;
		timeTeExtracted_ = timeTeExtract;
		timeRoasterAnalyzed_ = timeRoasterAnalysis;
		timeStarted_ = timeStarted;
		setStatus(statusString);
		needsAnalysis_ = needsAnalysis;
	}
	
	/**
	 * Get collection ID
	 * @return collection ID
	 */
	public String getId()
	{
		return id_;
	}
	
	/**
	 * Get whether keyword's extracted.
	 * @return true if extracted
	 */
	public boolean isTeExtracted()
	{
		if (0 != timeTeExtracted_)
			return true;
		else
			return false;
	}
	
	/**
	 * Get whether topics're analyzed.
	 * @return true if analyzed
	 */
	public boolean isRoasterAnalyzed()
	{
		if (0 != timeRoasterAnalyzed_)
			return true;
		else
			return false;
	}
	
	/**
	 * Informs whether this collection's locked. 
	 * ("locked" means that this collection's under task like keyword extraction, topic extraction, or custom field analysis) 
	 * @return true if this collection's locked
	 */
	public boolean isLocked()
	{
		if (CollectionStatus.LOCK_TE.equals(status_) 
				|| CollectionStatus.LOCK_RST.equals(status_)
				|| CollectionStatus.LOCK_CUSTOM.equals(status_))
			return true;
		else
			return false;
	}

	/**
	 * Set status with status value
	 * @param statusString status string to set
	 */
	void setStatus(String statusString)
	{
		if ("INIT".equals(statusString))
			status_ = CollectionStatus.INIT;
		else if ("LOCK_TE".equals(statusString))
			status_ = CollectionStatus.LOCK_TE;
		else if ("LOCK_RST".equals(statusString))
			status_ = CollectionStatus.LOCK_RST;
		else if ("LOCK_CUSTOM".equals(statusString))
			status_ = CollectionStatus.LOCK_CUSTOM;
		else if ("RELEASED".equals(statusString))
			status_ = CollectionStatus.RELEASED;
		else
			status_ = CollectionStatus.UNDEFINED;
	}
	
	/**
	 * Return status with String value. (one of INIT, LOCK_TE, LOCK_RST, LOCK_CUSTOM, RELEASED, UNDEFINED)
	 * @return status string
	 */
	public String getStatusString()
	{
		if (CollectionStatus.INIT.equals(status_))
			return "INIT";
		else if (CollectionStatus.LOCK_TE.equals(status_))
			return "LOCK_TE";
		else if (CollectionStatus.LOCK_RST.equals(status_))
			return "LOCK_RST";
		else if (CollectionStatus.LOCK_CUSTOM.equals(status_))
			return "LOCK_CUSTOM";
		else if (CollectionStatus.RELEASED.equals(status_))
			return "RELEASED";
		else
			return "UNDEFINED";
	}
	
	/**
	 * Get status of collection. 
	 * @return status Enumeration value (one of INIT, LOCK_TE, LOCK_RST, LOCK_CUSTOM, RELEASED, UNDEFINED)  
	 */
	CollectionStatus getStatus()
	{
		return status_;
	}

	/**
	 * Return whether it's collection for Keyword Extraction
	 * @return true if it's for keyword extraction 
	 */
	public boolean isTeCollection()
	{
		return isTeCollection_;
	}

	/**
	 * Return whether it's collection for Topic Analysis
	 * @return true if it's for topic analysis
	 */
	public boolean isRoasterCollection()
	{
		return isRoasterCollection_;
	}

	/**
	 * Get time when Keyword Extraction finished 
	 * @return time in long value
	 */
	public long getTimeTeExtracted()
	{
		return timeTeExtracted_;
	}

	/**
	 * Get time when Topic Analysis finished 
	 * @return time in long value
	 */
	public long getTimeRoasterAnalyzed()
	{
		return timeRoasterAnalyzed_;
	}

	/**
	 * Get time when current action (to the collection) started 
	 * @return time in long value
	 */	
	public long getTimeStarted()
	{
		return timeStarted_;
	}

	/**
	 * Return whether this collection needs re-analysis because some feedback keywords're provided
	 * @return true if needs analysis
	 */
	public boolean needsAnalysis()
	{
		return needsAnalysis_;
	}
	
	/**
	 * Set task ID currently running on this collection 
	 * @param id task ID to set 
	 */
	public void setTaskId(String id)
	{
		this.taskId_ = id;
	}
	
	/**
	 * Set task ID currently running on this collection 
	 * <ul>
	 * <li>return "" if no task's running for it. </li>
	 * <li>Only it'll return task ID in the status like: LOCK_TE, LOCK_RST, LOCK_CUSTOM </li>
	 * </li>  
	 * @return task ID 
	 */
	public String getTaskId()
	{
		return this.taskId_;
	}
	
	/**
	 * Set error code/message may be occurred during analysis procedure on this collection.
	 * ("" if no error occurred)
	 * @param codeTe error code: Keywords Extraction
	 * @param messageTe error message: Keywords Extraction
	 * @param codeRoaster error code: Topic Analysis
	 * @param messageRoaster error message: Topic Analysis
	 * @param codeCustom error code: Custom Field Analysis
	 * @param messageCustom error message: Custom Field Analysis
	 */
	public void setErrors(String codeTe, String messageTe, String codeRoaster, String messageRoaster,
			String codeCustom, String messageCustom)
	{
		if (null == codeTe)
			codeTe = "";
		if (null == messageTe)
			messageTe = "";
		if (null == codeRoaster)
			codeRoaster = "";
		if (null == messageRoaster)
			messageRoaster = "";
		if (null == codeCustom)
			codeCustom = "";
		if (null == messageCustom)
			messageCustom = "";
	
		this.errCodeTe_ = codeTe;
		this.errCodeRoaster_ = codeRoaster;
		this.errCodeCustom_ = codeCustom;
		
		this.errMessageTe_ = messageTe;
		this.errMessageRoaster_ = messageRoaster;
		this.errMessageCustom_ = messageCustom;
	}

	/**
	 * Get error code: keywords extraction
	 * @return error code
	 */
	public String getErrorCodeTe()
	{
		return errCodeTe_;
	}

	/**
	 * Get error message: keywords extraction
	 * @return error message
	 */
	public String getErrorMessageTe()
	{
		return errMessageTe_;
	}

	/**
	 * Get error code: topic analysis
	 * @return error code
	 */
	public String getErrorCodeRoaster()
	{
		return errCodeRoaster_;
	}

	/**
	 * Get error message: topic analysis
	 * @return error message
	 */
	public String getErrorMessageRoaster()
	{
		return errMessageRoaster_;
	}

	/**
	 * Get error code: custom field analysis
	 * @return error code
	 */
	public String getErrorCodeCustom()
	{
		return errCodeCustom_;
	}

	/**
	 * Get error message: custom field analysis
	 * @return error message
	 */
	public String getErrorMessageCustom()
	{
		return errMessageCustom_;
	}
	
	/**
	 * Set whether this collection supports real-time topic analysis
	 * @param isRealtime true if supports
	 */
	public void setRealtime(boolean isRealtime)
	{
		this.isRealtime_ = isRealtime;
	}
	
	/**
	 * Return whether this collection supports real-time topic analysis
	 * @return true if supports
	 */
	public boolean isRealtime()
	{
		return this.isRealtime_;
	}
	
	/**
	 * Set character set of the collection
	 * @param charset character set name (available: euc-kr, cp949, utf-8)
	 */
	public void setCharset(String charset)
	{
		if (null == charset)
			charset = "";
		this.charset_ = charset;
	}
	
	/**
	 * Get character set of the collection
	 * @return character set name
	 */
	public String getCharset()
	{
		return this.charset_;
	}
	
	
	/**
	 * Inner Enumeration Type for Collection Status 
	 * @author hkseo@wisenut.co.kr
	 *
	 */
	public enum CollectionStatus
	{
		INIT, LOCK_TE, LOCK_RST, LOCK_CUSTOM, RELEASED, UNDEFINED
	}
}

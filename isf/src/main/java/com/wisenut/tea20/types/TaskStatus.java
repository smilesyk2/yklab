package com.wisenut.tea20.types;

/**
 * DTO for Task Status.
 * 
 * @author hkseo@wisenut.co.kr
 */
public class TaskStatus {
	private EnumTaskStatus status = EnumTaskStatus.DEFAULT;
	private String taskType = "";
	private String requestId = "";
	private long startTime = 0;
	private long endTime = 0;
	private long estimatedTime = 0;
	private double percent = 0.0;
	private String errorCode = "";
	private String message = "";

	/** 
	 * Get status
	 * @return status as Enum
	 */
	public EnumTaskStatus getStatus() {
		return status;
	}

	/**
	 * Set status
	 * @param status Enum value to set
	 */
	public void setStatus(EnumTaskStatus status) {
		this.status = status;
	}

	/**
	 * Set status
	 * @param statusString status with String value
	 */
	public void setStatus(String statusString) {
		status = EnumTaskStatus.DEFAULT;
		if (null == statusString || 0 == statusString.trim().length()) {
			return;
		}

		String stat = statusString.trim();

		if ("WAITING".equals(stat)) {
			status = EnumTaskStatus.WAITING;
		} else if ("RUNNING".equals(stat)) {
			status = EnumTaskStatus.RUNNING;
		} else if ("STOPPED".equals(stat)) {
			status = EnumTaskStatus.STOPPED;
		} else if ("CANCELED".equals(stat)) {
			status = EnumTaskStatus.CANCELED;
		} else if ("FINISHED".equals(stat)) {
			status = EnumTaskStatus.FINISHED;
		} else if ("FAILED".equals(stat)) {
			status = EnumTaskStatus.FAILED;
		} else if ("NOT_EXIST".equals(stat)) {
			status = EnumTaskStatus.NOT_EXIST;
		}
	}

	/**
	 * Get status as String value
	 * @return status as String
	 */
	public String getStatusString() {
		if (status.equals(EnumTaskStatus.WAITING)) {
			return "WAITING";
		} else if (status.equals(EnumTaskStatus.RUNNING)) {
			return "RUNNING";
		} else if (status.equals(EnumTaskStatus.STOPPED)) {
			return "STOPPED";
		} else if (status.equals(EnumTaskStatus.CANCELED)) {
			return "CANCELED";
		} else if (status.equals(EnumTaskStatus.FINISHED)) {
			return "FINISHED";
		} else if (status.equals(EnumTaskStatus.FAILED)) {
			return "FAILED";
		} else if (status.equals(EnumTaskStatus.NOT_EXIST)) {
			return "NOT_EXIST";
		} else if (status.equals(EnumTaskStatus.DEFAULT)) {
			return "DEFAULT";
		} else {
			return "";
		}
	}

	/**
	 * Get type of task
	 * @return type value
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * Set type of task
	 * @param taskType type value to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * Get request ID (aka task ID)
	 * @return ID
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Set request ID (aka task ID)
	 * @param requestId id to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Get time when task's started
	 * @return time as long value
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Set time when task's started
	 * @param startTime time to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Get time when task's ended
	 * @return time as long value
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * Set time when task's ended
	 * @param endTime time to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * Get estimated time when task seems to be end 
	 * @return time as long value
	 */
	public long getEstimatedTime() {
		return estimatedTime;
	}

	/**
	 * Set estimated time when task seems to be end 
	 * @param estimatedTime time as long value
	 */
	public void setEstimatedTime(long estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	/**
	 * Get percentage of tasks's proceeded
	 * @return percentage as double value
	 */
	public double getPercent() {
		return percent;
	}

	/**
	 * Set percentage of tasks's proceeded
	 * @param percent percentage as double value
	 */
	public void setPercent(double percent) {
		this.percent = percent;
	}

	/**
	 * Get error code (during execution)
	 * @return code as String ("" if no error occurred)
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Set error code
	 * @param code code as String
	 */
	public void setErrorCode(String code) {
		this.errorCode = code;
	}

	/**
	 * Get message (during execution, usually error)
	 * @return message as String 
	 */

	public String getMessage() {
		return message;
	}

	/**
	 * Set message (during execution, usually error)
	 * @param message message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Return whether error occurred during execution (status: FAILED) 
	 * @return true if some error occurred
	 */
	public boolean hasError() {
		return EnumTaskStatus.FAILED.equals(status);
	}
}

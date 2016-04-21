package com.wisenut.tea20.types;

/**
 * Enum for Task Status.
 * 
 * Defines possible task status.
 * 
 * @author hkseo@wisenut.co.kr
 */
public enum EnumTaskStatus
{
	/** can run task */
	WAITING,
	/** task's running */
	RUNNING,
	/** task's stopped (temporal, can be resumed) */
	STOPPED,
	/** task's canceled (cannot be resumed) */
	CANCELED,
	/** task's finished successfully */
	FINISHED,
	/** task's failed with some error */
	FAILED,
	/** task's not exist */
	NOT_EXIST,
	/** default value */
	DEFAULT
}

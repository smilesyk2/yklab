/*
 * @(#)BridgeException.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.Exception;
/**
 *
 * BridgeException
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public final class StringException extends Exception {
    protected String message = null;
    protected Throwable throwable = null;
    
    public StringException() {
        this(null, null);
    }

    public StringException(String message) {
        this(message, null);
    }

    public StringException(Throwable throwable) {
        this(null, throwable);
    }

    public StringException(String message, Throwable throwable) {
        super();
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return (message);
    }

    public Throwable getThrowable() {
        return (throwable);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (message != null) {
            sb.append(message);
            if (throwable != null) {
                sb.append(":  ");
            }
        }

        if (throwable != null) {
            sb.append(throwable.toString());
        }
        return (sb.toString());
    }

    
}

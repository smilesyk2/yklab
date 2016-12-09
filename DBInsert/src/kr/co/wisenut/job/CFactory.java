/*
 * @(#)CFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job;

import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.job.custom.ICustom;

/**
 *
 * CFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class CFactory {
    /**
     * Custom class object return
     * @param className custom classs name
     * @return ICustom
     * @throws BridgeException error info
     */
    public static ICustom getInstance(String className) throws StringException {
        try {
            return (ICustom) ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
        } catch (InstantiationException e) {
            throw new StringException(e);
        } catch (IllegalAccessException e) {
            throw new StringException(e);
        } catch (ClassNotFoundException e) {
            throw new StringException(e);
        }
    }
}

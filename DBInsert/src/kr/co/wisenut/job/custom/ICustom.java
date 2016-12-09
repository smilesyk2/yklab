/*
 * @(#)ICustom.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.custom;

import kr.co.wisenut.Exception.CustomException;

/**
 *
 * ICustom
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface ICustom {
    /**
     *
     * @param str input data
     * @return   result data
     * @throws CustomException  error info
     */
    public String customData(String str) throws CustomException;
}

/*
 * @(#)FormatColumn.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;

import java.sql.Timestamp;
import java.math.BigDecimal;

/**
 *
 * FormatColumn
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FormatColumn {
    public String formatColumn(String title, int displaySize){
        return formatColumn(title, displaySize, "java.lang.String");
    }
    public String formatColumn(String title, int displaySize, String className) {
        int    length = displaySize;
        if (title != null) {
            if (className.equals("java.lang.String")) {
            } else if (className.equals("java.math.BigDecimal")) {
                length = 9;
            } else if (className.equals("java.sql.Timestamp")) {
                length = 21;
            }
        }
        return StringUtil.padRight(title,  ' ', length) + " ";
    }
}

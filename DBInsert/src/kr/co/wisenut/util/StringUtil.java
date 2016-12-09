/*
 * @(#)StringUtil.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * StringUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class StringUtil {
    public final static String newLine = System.getProperty("line.separator");
    private static long SERIAL = -1;
    
    public static String getStackTrace(Exception e){
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	
    	e.printStackTrace(pw);
    	return sw.toString();
    }

    /**
     * Create unique ID method
     * @return String
     */
    public static synchronized String getTimeBasedUniqueID() {
        if (SERIAL < 0) {
            SERIAL = System.currentTimeMillis();
        }
        String tmp = Long.toHexString(SERIAL);
        SERIAL++ ;
        int len = tmp.length();
        return tmp.substring(len-8, len).toUpperCase();
    }
    /**
     * Check string method
     * @param str
     * StringUtil.hasLength(null) = false
     * StringUtil.hasLength("") = false
     * StringUtil.hasLength(" ") = true
     * StringUtil.hasLength("Hello") = true
     * @return  empty String return false
     */
    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check string method
     * @param str
     * StringUtil.hasText(null) = false
     * StringUtil.hasText("") = false
     * StringUtil.hasText(" ") = false
     * StringUtil.hasText("12345") = true
     * StringUtil.hasText(" 12345 ") = true
     * @return  boolean
     */
    public static boolean hasText(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    /**
     *
     * @param s
     * @param count
     * @return String
     */
    public static String repeat(String s, int count) {
        if (s == null || count < 0)
            return null;
        else if (s.length() == 0 || count == 0)
            return "";

        StringBuffer   result = new StringBuffer(s.length() * count);

        for (int j = 0; j < count; ++j)
            result.append(s);

        return result.toString();
    }

    /**
     *
     * @param c
     * @param count
     * @return String
     */
    public static String repeat(char c, int count){
        if (count < 0)
            return null;
        else if (count == 0)
            return "";

        StringBuffer   result = new StringBuffer(count);

        for (int j = 0; j < count; ++j)
            result.append(c);

        return result.toString();
    }

    /**
     *
     * @param s
     * @param finalLength
     * @return String
     */
    public static String padLeft(String s, int finalLength){
        return padLeft(s, ' ', finalLength);
    }
    /**
     *
     * @param value
     * @param finalLength
     * @return String
     */
    public static String padLeft(int value, int finalLength){
        return padLeft("" + value, ' ', finalLength);
    }

    /**
     *
     * @param value
     * @param padChar
     * @param finalLength
     * @return String
     */
    public static String padLeft(int value, char padChar, int finalLength){
        return padLeft("" + value, padChar, finalLength);
    }

    /**
     *
     * @param s
     * @param padChar
     * @param finalLength
     * @return String
     */
    public static String padLeft(String s, char padChar, int finalLength){
        if (s == null)
            return null;
        else if (s.length() >= finalLength)
            return s;

        return repeat(padChar, finalLength - s.length()) + s;
    }

    /**
     *
     * @param s
     * @param finalLength
     * @return  String
     */
    public static String padRight(String s, int finalLength){
        return padLeft(s, ' ', finalLength);
    }

    /**
     *
     * @param value
     * @param finalLength
     * @return  String
     */
    public static String padRight(int value, int finalLength){
        return padLeft("" + value, ' ', finalLength);
    }

    /**
     *
     * @param value
     * @param padChar
     * @param finalLength
     * @return String
     */
    public static String padRight(int value, char padChar, int finalLength){
        return padLeft("" + value, padChar, finalLength);
    }

    /**
     *
     * @param s
     * @param padChar
     * @param finalLength
     * @return String
     */
    public static String padRight(String s, char padChar, int finalLength){
        if (s == null)
            return null;
        else if (s.length() >= finalLength)
            return s;

        return s + repeat(padChar, finalLength - s.length());
    }

    /**
     *
     * @param s
     * @param length
     * @return String
     */
    public static String right(String s, int length){
        if (s == null)
            return null;
        else if (length < 0 && s.length() <= -length)
            return "";
        else if (s.length() <= length)
            return s;

        if (length < 0)
            return s.substring(0, s.length() + length);
        else
            return s.substring(s.length() - length);
    }

    /**
     *
     * @param s
     * @return String
     */
    public static String toMixedCase(String s){
        StringBuffer   result = new StringBuffer();
        char           ch;
        boolean        lastWasUpper = false;
        boolean        isUpper;

        for (int j = 0; j < s.length(); ++j) {
            ch = s.charAt(j);
            isUpper = Character.isUpperCase(ch);
            if (lastWasUpper && isUpper)
                result.append(Character.toLowerCase(ch));
            else
                result.append(ch);
            lastWasUpper = isUpper;
        }

        return result.toString();
    }

    /**
     *
     * @param base
     * @param newItem
     * @param delimiter
     * @return String
     */
    public static String extendDelimited(String base, String newItem, String delimiter){
        if (base == null || base.equals(""))
            return newItem;
        else
            return base + delimiter + newItem;
    }

    /**
     * Trim Leading Whitespace method
     * @param str
     * @return String
     */
    public static String trimLeadingWhitespace(String str) {
        if (str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }

    /**
     *
     * @param s
     * @return String
     */
    public static String trimDuplecateSpace(String s){
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if(i < s.length()-1) {
                if( c == ' ' && s.charAt(i+1)==' '){
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    /**
     *
     * @param strNum
     * @param def
     * @return int
     */

    public static int parseInt(String strNum, int def){
        if(strNum == null) return def;
         if(strNum.indexOf('.')>0) {
            strNum = strNum.substring(0,strNum.indexOf('.'));
        }
        try{
            return Integer.parseInt(strNum);
        }catch(Exception e){
            return def;
        }
    }

    /**
     * Check Null method
     * @param temp
     * @return String
     */
    public static String checkNull(String temp) {
        if (temp != null) {
            temp = temp.trim();
        } else {
            temp = "";
        }
        return temp;
    }

    /**
     * Check Null method
     * @param temp
     * @return String[]
     */
    public static String[] checkNull(String[] temp){
        for(int i=0; i<temp.length; i++) {
            temp[i] = checkNull(temp[i]);
        }
        return temp;
    }

    /**
     * Check Null method
     * @param temp
     * @return String[][]
     */
    public static String[][] checkNull(String[][] temp) {
        for(int i=0; i<temp.length; i++) {
            temp[i][0] = checkNull(temp[i][0]);
            temp[i][1] = checkNull(temp[i][1]);
        }
        return temp;
    }

    /**
     * String format convert method
     * convertFormat("1", "00") return "01"
     * @param inputStr
     * @param format
     * @return String
     */
    public static String convertFormat(String inputStr, String format){
        long _input = Long.parseLong(inputStr);
        StringBuffer result = new StringBuffer();
        DecimalFormat df = new DecimalFormat(format);
        df.format( _input, result, new FieldPosition(1) );
        return result.toString();
    }

    /**
     *
     * @param input
     * @param maxLen
     * @return String
     */
    public static String convertInteger(String input, int maxLen) {
        int output = 0;
        int idx = maxLen;
        if(input.length() < maxLen) {
            idx = input.length();
        }
        try {
            output = Integer.parseInt( input.substring(0, idx) );
        }catch(Exception e) {}
        return Integer.toString(output);
    }

    /**
     *
     * @param inputStr
     * @return String
     */
    public static String spaceReplace(String inputStr) {
        String Temp[] = split(inputStr, " ");
        inputStr = Temp[0];
        for(int i=1; i<Temp.length; i++){
            inputStr = inputStr  + Temp[i] ;
        }
        return inputStr;
    }

    public static String[] split(String splittee, String splitChar){
        return split(splittee, splitChar, 0);
    }
    /**
     * String split method
     * @param splittee  input string
     * @param splitChar split text
     * @param limit split limit number
     * @return  String[]
     */
    public static String[] split(String splittee, String splitChar, int limit){
        String taRetVal[] ;
        StringTokenizer toTokenizer ;
        int tnTokenCnt;

        try {
            toTokenizer = new StringTokenizer(splittee, splitChar);
            tnTokenCnt = toTokenizer.countTokens();
            if(limit != 0 && tnTokenCnt > limit) tnTokenCnt = limit;
            taRetVal = new String[tnTokenCnt];

            for(int i=0; i<tnTokenCnt; i++) {
                if(toTokenizer.hasMoreTokens()){
                    taRetVal[i] = toTokenizer.nextToken();
                }
                if(limit != 0 && limit == (i+1)) break;
            }
        } catch (Exception e) {
            taRetVal = new String[0];
        }
        return taRetVal ;
    }
    
	public static String[] split(String value, String string, boolean trim, boolean ignoreBlank) {
		if (isNull(value)) {
			return new String[0];
		} else {

			String[] result = value.split(string);

			ArrayList tmp = new ArrayList();
			for (int i = 0; i < result.length; i++) {
				if (trim) {
					result[i] = result[i].trim();
				}
				if (ignoreBlank) {
					if (!result[i].equals("")) {
						tmp.add(result[i]);
					}
				} else {
					tmp.add(result[i]);
				}
			}

			return (String[]) tmp.toArray(new String[tmp.size()]);
		}

	}    
	
	/**
	 * String의 null을 체크하여 true/false리턴한다.
	 * @param string
	 * @return
	 */
	public static boolean isNull(String string) {

		return "".equals(checkNull(string));
	}	

    /**
     * String sort method.
     * @param source the source array
     * @return the sorted array (never null)
     */
    public static String[] sortStringArray(String[] source) {
        if (source == null) {
            return new String[0];
        }
        Arrays.sort(source);
        return source;
    }

    /**
     * Erase Duplicated method
     * @param srcArr
     * @return String[]
     */
    public static  String[] eraseDuplicatedValue(String[] srcArr)  {
        List  tempVector = new ArrayList();
        int loopCount = 0;

        for (loopCount = 0; loopCount < srcArr.length; loopCount++) {
            tempVector.add(srcArr[loopCount]);
        }

        Collections.sort(tempVector);

        for (loopCount = 0; loopCount < srcArr.length; loopCount++){
            srcArr[loopCount] = (String)(tempVector.get(loopCount));
        }

        tempVector.clear();

        tempVector.add(srcArr[0]);

        for (loopCount = 1; loopCount < srcArr.length; loopCount++){
            if (!srcArr[loopCount].equals(srcArr[loopCount-1])) {
                tempVector.add(srcArr[loopCount]);
            }
        }

        String[] resultStrArr = new String[tempVector.size()];

        for (loopCount = 0; loopCount < resultStrArr.length; loopCount++) {
            resultStrArr[loopCount] = (String)(tempVector.get(loopCount));
        }
        return resultStrArr;
    }

    /**
     *
     * @param str
     * @return String
     */
    public static String trimTrailingWhitespace(String str) {
        if (str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    /**
     *
     * @param str
     * @param prefix
     * @return boolean
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }

    /**
     *
     * @param str
     * @param sub
     * @return int
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0, pos = 0, idx = 0;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    /**
     *
     * @param inString
     * @param oldPattern
     * @param newPattern
     * @return String
     */
    public static String replace(String inString, String oldPattern, String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuffer sbuf = new StringBuffer();

        int pos = 0;
        int index = inString.indexOf(oldPattern);

        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));


        return sbuf.toString();
    }

    /**
     *
     * @param inString
     * @param pattern
     * @return String
     */
    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, " ");
    }

    /**
     *
     * @param inString
     * @param charsToDelete
     * @return String
     */
    public static String deleteAny(String inString, String charsToDelete) {
        if (inString == null || charsToDelete == null) {
            return inString;
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     *
     * @param qualifiedName
     * @return String
     */
    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    /**
     *
     * @param qualifiedName
     * @param separator
     * @return String
     */
    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    /**
     *
     * @param str
     * @return String
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     *
     * @param str
     * @return  String
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**
     *
     * @param str
     * @param capitalize
     * @return String
     */
    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        }
        else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }


    /**
     *
     * @param localeString
     * @return Locale
     */
    public static Locale parseLocaleString(String localeString) {
        String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
        String language = parts.length > 0 ? parts[0] : "";
        String country = parts.length > 1 ? parts[1] : "";
        String variant = parts.length > 2 ? parts[2] : "";
        return (language.length() > 0 ? new Locale(language, country, variant) : null);
    }


    /**
     *
     * @param arr
     * @param str
     * @return String[]
     */
    public static String[] addStringToArray(String[] arr, String str) {
        String[] newArr = new String[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = str;
        return newArr;
    }

    /**
     *
     * @param array
     * @param delimiter
     * @return  Properties
     */
    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    /**
     *
     * @param array
     * @param delimiter
     * @param charsToDelete
     * @return Properties
     */
    public static Properties splitArrayElementsIntoProperties(
            String[] array, String delimiter, String charsToDelete) {

        if (array == null || array.length == 0) {
            return null;
        }

        Properties result = new Properties();
        for (int i = 0; i < array.length; i++) {
            String element = array[i];
            if (charsToDelete != null) {
                element = deleteAny(array[i], charsToDelete);
            }
            String[] splittedElement = split(element, delimiter);
            if (splittedElement == null) {
                continue;
            }
            result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
        }
        return result;
    }

    /**
     *
     * @param str
     * @param delimiters
     * @return String[]
     */
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     *
     * @param str
     * @param delimiters
     * @param trimTokens
     * @param ignoreEmptyTokens
     * @return String[]
     */
    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    /**
     *
     * @param str
     * @param delimiter
     * @return String[]
     */
    public static String[] delimitedListToStringArray(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }

        List result = new ArrayList();
        int pos = 0;
        int delPos = 0;
        while ((delPos = str.indexOf(delimiter, pos)) != -1) {
            result.add(str.substring(pos, delPos));
            pos = delPos + delimiter.length();
        }
        if (str.length() > 0 && pos <= str.length()) {
            result.add(str.substring(pos));
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     *
     * @param str
     * @return String[]
     */
    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }

    /**
     *
     * @param str
     * @return Set
     */
    public static Set commaDelimitedListToSet(String str) {
        Set set = new TreeSet();
        String[] tokens = commaDelimitedListToStringArray(str);
        for (int i = 0; i < tokens.length; i++) {
            set.add(tokens[i]);
        }
        return set;
    }

    /**
     *
     * @param arr
     * @param delim
     * @return  String
     */
    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /**
     *
     * @param coll
     * @param delim
     * @param prefix
     * @param suffix
     * @return String
     */
    public static String collectionToDelimitedString(Collection coll, String delim, String prefix, String suffix) {
        if (coll == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        Iterator it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(prefix).append(it.next()).append(suffix);
            i++;
        }
        return sb.toString();
    }

    /**
     *
     * @param coll
     * @param delim
     * @return String
     */
    public static String collectionToDelimitedString(Collection coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    /**
     *
     * @param arr
     * @return String
     */
    public static String arrayToCommaDelimitedString(Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }

    public synchronized static String convert(String input, String encode) {
        int idx = encode.indexOf(",");
        int len = encode.length();
        if(idx > 0 && len > idx+1) {
            String srcEncode = encode.substring(0, idx);
            String targetEncode = encode.substring(idx+1, encode.length());
            try {
                input = convert(input, srcEncode, targetEncode);
            } catch (UnsupportedEncodingException e) {
                return input;
            }
        }
        return input;
    }

    /**
     *
     * @param input
     * @param srcEncode
     * @param targetEncode
     * @return
     * @throws UnsupportedEncodingException
     */
    public synchronized static String convert(String input, String srcEncode, String targetEncode)
            throws UnsupportedEncodingException {
        input = new String(input.getBytes(srcEncode), targetEncode);
        return input;
    }

    /**
     * Check string method
     * @param prmData
     * @return
     */
    public static boolean chkChar(String prmData) {
        String tsValidChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_!@@#$%^&*()-+=|~`<>?/.,[]{}:;\"' ";
        try{
            char chData[] = prmData.toCharArray();
            for (int i=0; i < prmData.trim().length(); i++){
                if ( tsValidChars.indexOf(""+chData[i]) == -1) {
                    if( chData[i] >= 0x0020 && chData[i] <= 0x007E ) {
                        return false;
                    }
                    if( chData[i] >= 0xFF61 && chData[i] <= 0xFF9F ) {
                        return false;
                    }
                    if( Character.isDefined(chData[i]) == false) {
                        return false;
                    }
                }
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

    /**
     * Check hangul
     * @param c
     * @return boolean
     */
    public static boolean isHangul(char c) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
        return unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
                || unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO
                || unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES;
    }

    public static  boolean isInteger(String strVal) {
        int tempVal = -1;
        try{
            tempVal = Integer.parseInt(strVal);
            if(tempVal < 0) {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    public static  boolean isLong(String strVal) {
        long tempVal = -1;
        try{
            tempVal = Long.parseLong(strVal);
            if(tempVal < 0) {
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
    /**
     * Erase SpecialChar method
     * @param prmData
     * @return string
     */
    public static String chkSpecialChar(String prmData) {
        String tsValidChars = "!@#$%^&*()-+=|~`<>?[]{}\"'";
        StringBuffer buffer = new StringBuffer(256);
        try{
            char chData[] = prmData.toCharArray();
            for (int i=0; i < prmData.trim().length(); i++){
                if ( tsValidChars.indexOf(""+chData[i]) > -1) {
                    chData[i] = ' ';
                }
                buffer.append(chData[i]) ;
            }
        }catch(Exception e){
            return "";
        }
        return buffer.toString();
    }

    /**
     * strToFormatedNumber
     * @param str
     * @return String
     */
    public static String strToFormatedNumber(String str){
        long value = Long.parseLong(str);
        NumberFormat FORMAT = NumberFormat.getInstance();
        FORMAT.setGroupingUsed(true);
        return FORMAT.format(value);
    }

    public static List stringListSort(List list) {
        class ComparatorThis implements Comparator{
            public int compare(Object o1, Object o2){
                String s1 = (String)o1;
                String s2 = (String)o2;
                if (s1 == null || s2 == null) {
                    return 0;
                }
                return s1.compareTo(s2);
            }
        };

        Comparator comparator = new ComparatorThis();
        Collections.sort(list, comparator);
        return list;
    }
    
	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * str 에 start tag 와 end tag 사이의 데이터를 반환
	 * ex) str이 <test>abcde</test> 이고, start가 <test>, end가 </test> 이면 abcde 를 반환.
	 * @param str
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getTagValue(String str, String start, String end) {
		int startIdx = str.indexOf(start);
		int endIdx = str.indexOf(end);
		
		String title = "";
		if (startIdx > -1 && endIdx > -1 && startIdx < endIdx) {
			title = str.substring(startIdx + start.length(), endIdx);
		}

		return title;
	}
	
	public static boolean replaceYn(String bool) throws IllegalArgumentException {
		if (bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("yes")) {
			return true;
		} else if (bool.equalsIgnoreCase("n") || bool.equalsIgnoreCase("no")) {
			return false;
		} else{
			throw new IllegalArgumentException("\"y\", \"yes\", \"n\" ,\"no\"");
		}
	}

	/**
	 * boolean 값을 y 또는 n 문자열로 반환
	 * @param bool
	 * @return
	 */
	public static String replaceYn(boolean bool) {
		String ret = "n";
		if (bool)
			ret = "y";
		return ret;
	}
	
	/**
	 * array 에 checkStr 이 있는지
	 * @param strArr
	 * @param checkStr
	 * @return
	 */
	public static boolean isExistArray(String[] strArr, String checkStr) {
        for(int i=0; i<strArr.length; i++){
        	
            if(strArr[i] != null && strArr[i].equals(checkStr)){
            	return true;
            }
        }
        return false;
	}
	
	public static boolean isExistArray(ArrayList strArr, String checkStr) {
		
		String[] arr = (String[])strArr.toArray(new String[strArr.size()]);
		boolean result = isExistArray(arr, checkStr);
        return result;
	}


    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return "";
        }
        
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                + separator.length());

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
	public static String replaceArrayToString(int[] data, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			sb.append(String.valueOf(data[i])).append(delim);
		}
		String result = sb.toString();
		if(data.length > 0) {
			sb.substring(0, result.length()-delim.length());
		}
		return result;
	}
	
	public static boolean matched(String inputTxt, String rex){
		if(inputTxt.equals("") || rex.equals("")) return false;
		Pattern p = Pattern.compile(rex);
		Matcher m = p.matcher(inputTxt);
		
		return m.find();
	}

}

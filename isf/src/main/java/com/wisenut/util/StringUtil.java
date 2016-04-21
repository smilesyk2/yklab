package com.wisenut.util;

import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtil {
	public static String removeSpecialCharacter(String str){
		if(str != null){
			str = str.replaceAll("\n", "");
			str = str.replaceAll("\r", "");
			str = str.replaceAll("\"", "");
			str = str.replaceAll("-", "'");
		}
		
		return str;
	}
	
	public static String objectToString(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("문자열을 입력하세요. ------>");
		String test = scanner.next();
		
		System.out.println("#### before : " + test);
		System.out.println("#### after : " + StringUtil.removeSpecialCharacter(test));
	}
}

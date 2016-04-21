package com.wisenut.tea20.types;

import java.util.HashMap;
import java.util.Map;
import com.wisenut.tea20.tools.*;

/**
 * Wrapper for Socket Message.
 * (inner class)
 * 
 * @author hkseo@wisenut.co.kr
 */
public class SocketMessage {
	public static final String CDATA_HEAD = "<![CDATA[";
	public static final String CDATA_TAIL = "]]>";

	private String[] fields = null;
	private Map<String, String> fieldValueMap = null;

	private String command = "";
	private String requestType = "";
	private String requestId = "";

	private PriorityType priorityType = PriorityType.NOT_DEFINED;
	private TransferType transferType = TransferType.NOT_DEFINED;

	private boolean isResponse = false;
	private String errorCode = "";
	private String errorMessage = "";

	public SocketMessage(boolean isResponse) {
		this.isResponse = isResponse;
		this.fields = new String[0];
		this.fieldValueMap = new HashMap<String, String>();
	}

	public SocketMessage(String[] params) {
		this.fields = params;
		this.fieldValueMap = new HashMap<String, String>();
		initFields();
	}

	public SocketMessage(String commandType, String reqType,
			PriorityType priorityType, TransferType transferType,
			String reqId, String[] fields) {
		isResponse = false;
		this.fieldValueMap = new HashMap<String, String>();
		setFields(fields);
		this.priorityType = priorityType;
		this.transferType = transferType;
		this.command = commandType;
		this.requestType = reqType;
		this.requestId = reqId;
	}

	public SocketMessage(String commandType, String reqType, String reqId,
			String[] fields, String errorCode, String errorMessage) {
		this.isResponse = true;
		this.fieldValueMap = new HashMap<String, String>();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		setFields(fields);
	}

	private void initFields() {
		if (null == fields) {
			return;
		}

		for (int i = 0; i < fields.length; i++) {
			if (fieldValueMap.containsKey(fields[i])) {
				fieldValueMap.remove(fields[i]);
			}
			fieldValueMap.put(fields[i], "");
		}
	}

	public void setFields(String[] fields) {
		this.fields = fields;
		initFields();
	}

	public String[] getFields() {
		return fields;
	}

	public void setValue(String field, String value) {
		if (null == field || null == value || null == fieldValueMap || null == fieldValueMap.get(field)) {
			return;
		}

		fieldValueMap.remove(field);
		fieldValueMap.put(field, value);
	}

	public String getValue(String field) {
		if (null == field || null == fieldValueMap || null == fieldValueMap.get(field)) {
			return "";
		}

		return fieldValueMap.get(field);
	}

	private String getModifiedString(String str) {
		String toReturn = str;

		toReturn = StringTool.replace(str, "&", "&amp;");
		toReturn = StringTool.replace(toReturn, "<", "&lt;");
		toReturn = StringTool.replace(toReturn, ">", "&gt;");

		return toReturn;
	}

	private String getXmlNodeString(String str) {
		if (null == str || 0 == str.length()) {
			return "";
		}

		StringBuffer toReturn = new StringBuffer();
		int idx = 0;
		int posHead = str.indexOf(CDATA_HEAD, idx);
		int posTail = 0;

		while (posHead >= idx) {
			posTail = str.indexOf(CDATA_TAIL, idx);
			// no CDATA end tag, so breaks
			if (posTail < 0) {
				break;
			}

			if (posHead != idx) {
				toReturn.append(getModifiedString(str.substring(idx, posHead)));
			}
			idx = posTail + CDATA_TAIL.length();
			toReturn.append(str.substring(posHead, idx));
			posHead = str.indexOf(CDATA_HEAD, idx);
		}

		if (idx < str.length()) {
			toReturn.append(getModifiedString(str.substring(idx)));
		}

		return toReturn.toString();
	}

	private void addNodeString(String field, String value, StringBuffer target) {
		if (null == target || null == value) {
			return;
		}

		if (null == value || 0 == value.length()) {
			target.append('<');
			target.append(field);
			target.append(" />\n");
		} else {
			target.append('<');
			target.append(field);
			target.append('>');
			target.append(getXmlNodeString(value));
			target.append("</");
			target.append(field);
			target.append(">\n");
		}
	}

	private void addNameValueString(String headerType, String field, String value, StringBuffer target) {
		if (null == target || null == value) {
			return;
		}

		if (null == value || 0 == value.length()) {
			target.append('<');
			target.append(headerType);
			target.append(" name='");
			target.append(field);
			target.append("' />\n");
		} else {
			target.append('<');
			target.append(headerType);
			target.append(" name='");
			target.append(field);
			target.append("'>");
			target.append(getXmlNodeString(value));
			target.append("</");
			target.append(headerType);
			target.append(">\n");
		}
	}

	public String toString() {
		StringBuffer toReturn = new StringBuffer("");
		if (isResponse) {
			toReturn.append("<response>\n");
		} else {
			toReturn.append("<request>\n");
		}

		toReturn.append('\t');
		addNodeString("command", command, toReturn);

		toReturn.append('\t');
		addNodeString("request_type", requestType, toReturn);

		toReturn.append('\t');
		addNodeString("request_id", requestId, toReturn);

		if (isResponse) {
			toReturn.append("\t<results>\n");
		} else {
			toReturn.append("\t<params>\n");
		}

		String header = "param";
		if (isResponse) {
			header = "result";
		}

		for (int i = 0; i < fields.length; i++) {
			toReturn.append("\t\t");
			addNameValueString(header, fields[i], fieldValueMap.get(fields[i]), toReturn);
		}

		if (isResponse) {
			toReturn.append("\t</results>\n");
		} else {
			toReturn.append("\t</params>\n");
		}

		if (isResponse) {
			toReturn.append("\t<error>\n");
			toReturn.append("\t\t");
			addNodeString("code", errorCode, toReturn);
			toReturn.append("\t\t");
			addNodeString("message", errorMessage, toReturn);
			toReturn.append("\t</error>\n");
			toReturn.append("</response>\n");
		} else {
			toReturn.append("</request>\n");
		}

		return toReturn.toString();
	}

	public void setPriorityType(String typeString) {
		if ("1".equals(typeString)) {
			priorityType = PriorityType.EMERGENCY;
		} else if ("2".equals(typeString)) {
			priorityType = PriorityType.NORMAL;
		} else if ("3".equals(typeString)) {
			priorityType = PriorityType.MANAGING_MESSAGE;
		}
	}

	public void setTransferType(String typeString) {
		if ("1".equals(typeString)) {
			transferType = TransferType.SINGLE_WAY;
		} else if ("2".equals(typeString)) {
			transferType = TransferType.BI_WAY;
		} else if ("3".equals(typeString)) {
			transferType = TransferType.SF_WAY;
		}
	}

	public String getPriorityTypeString() {
		switch (priorityType) {
		case EMERGENCY:
			return "1";
		case NORMAL:
			return "2";
		case MANAGING_MESSAGE:
			return "3";
		default:
			return "";
		}
	}

	public String getTransferTypeString() {
		switch (transferType) {
		case SINGLE_WAY:
			return "1";
		case BI_WAY:
			return "2";
		case SF_WAY:
			return "3";
		default:
			return "";
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String code) {
		this.errorCode = code;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}

	public enum PriorityType {
		EMERGENCY, NORMAL, MANAGING_MESSAGE, NOT_DEFINED
	}

	public enum TransferType {
		SINGLE_WAY, BI_WAY, SF_WAY, NOT_DEFINED
	}
}

package com.wisenut.tea20.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.wisenut.tea20.tools.*;
import com.wisenut.tea20.types.*;


/**
 * Java API Client. 
 * 
 * Implementation class for TEA v2.0 Java API Client. 
 * <br><br>
 * system properties for API:
 * <ul>
 * <li>tea2.log.console: enable/disable console log (default: y)</li>
 * <li>tea2.debug: enable/disable debug mode (default: n)</li>
 * <li>tea2.log.file: enable/disable file log (default: n)</li>
 * <li>tea2.log.path: path for file log (default: ./log)</li>
 * </ul>
 * @author hkseo@wisenut.co.kr
 */

public class TeaClient {
    /**
     * default IP for listener
     */
    public static final String DEFAULT_IP = "127.0.0.1";
    /**
     * default port number for listener
     */
    public static final int DEFAULT_PORT = 11000;
    public static final String COLLECTION_ALL = "A";
    public static final String COLLECTION_GROUPBY = "G";
    public static final String HISTORY_ALL = "A";
    public static final String MODIFY_ALL = "A";

    /**
     * delimiter among items in the list
     */
    private static String ITEM_DELIMITER = "|";
    /**
     * delimiter among values in the item
     */
    private static String VALUE_DELIMITER = ":";
    private static String VALUE_DELIMITER_TYPE2 = "^";
    /**
     * delimiter for values about frequency (value+(delimiter)+frequency)
     */
    private static String FREQ_DELIMITER = ":";
    /**
     * delimiter for values about weight (value+(delimiter)+weight)
     */
    private static String WEIGHT_DELIMITER = "^";
    
    /**
     * delimiter for values about field (value+(delimiter)+weight)
     */
    private static String FIELD_DELIMITER = "$!$";
    
    /**
     * delimiter among documents 
     */    
    private static String DOCUMENT_DELIMITER = ",";

    /**
     * IP for listener.
     */
    private String listenerIp = DEFAULT_IP;
    /**
     * port for listener.
     */
    private int listenerPort = DEFAULT_PORT;

    /**
     * recent error code
     */
    private String recentErrorCode = "";

    /**
     * recent error code from server
     */
    private String recentServerErrorCode = "";

    /**
     * recent error message
     */
    private String recentErrorMessage = "";

    /**
     * recent error message from server
     */
    private String recentServerErrorMessage = "";

    /**
     * recent response from server
     */
    private SocketMessage recentResponse = null;

    /**
     * flag for whether to display messages in the console
     */
    private boolean consoleLog = true;

    /**
     * initial interval for socket connection retry
     */
    private int initConnectionInterval = 0;

    /**
     * lower limit for socket connection retry
     */
    private int INIT_CONNECTION_INTERVAL = 10;

    /**
     * maximum interval for socket connection retry
     */
    private int maxConnectionInterval = 0;

    /**
     * upper limit for maxConnectionInterval_
     */
    private final int MAX_CONNECTION_INTERVAL = 2000;

    /**
     * maximum connection retries
     */
    private int maxConnectionRetry = 0;

    /**
     * upper limit for maxConnectionRetry_
     */
    private final int MAX_CONNECTION_RETRY = 2000;

    /**
     * maximum wait time for receiving results from listener
     */
    private int waitTimeout = 0;

    /**
     * upper limit for waitTimeout_
     */
    private final int MAX_WAIT_TIMEOUT = 120000;

    /**
     * upper limit for content size
     */
    private final int MAX_CONTENT_SIZE = 2 * 1024 * 1024;

    /**
     * Constructor.
     *
     * @param ip   IP for listener
     * @param port port for listener
     */

    public TeaClient(String ip, int port) {
        this.listenerIp = ip;
        this.listenerPort = port;
    }

    /**
     * Constructor.
     * - alternative constructor for detailed connection control
     *
     * @param ip       IP for listener
     * @param port     port for listener
     * @param interval maximum interval for connection retry (in ms, 1 .. 2000)
     * @param maxRetry maximum count for retry connection (1 .. 100)
     */
    public TeaClient(String ip, int port, int interval, int maxRetry) {
        if (interval > 0 && interval < MAX_CONNECTION_INTERVAL) {
            maxConnectionInterval = interval;
        }

        if (maxRetry > 0 && maxRetry < MAX_CONNECTION_RETRY) {
            maxConnectionRetry = maxRetry;
        }

        this.listenerIp = ip;
        this.listenerPort = port;
    }

    /**
     * Constructor.
     * - alternative constructor for full connection control
     *
     * @param ip           IP for listener
     * @param port         port for listener
     * @param initInterval initial interval for connection retry (in ms, 10 .. 2000)
     * @param maxInterval  maximum interval for connection retry (in ms, 10 .. 2000)
     * @param maxRetry     maximum count for retry connection (1 .. 100)
     * @param waitTimeout  timeout for waiting response from listener (1 .. 120000)
     */
    public TeaClient(String ip, int port, int initInterval, int maxInterval, int maxRetry, int waitTimeout) {
        if (initInterval > INIT_CONNECTION_INTERVAL && maxInterval < MAX_CONNECTION_INTERVAL) {
            initConnectionInterval = initInterval;
        }

        if (maxInterval > INIT_CONNECTION_INTERVAL && maxInterval < MAX_CONNECTION_INTERVAL) {
            maxConnectionInterval = maxInterval;
        }

        if (maxRetry > 0 && maxRetry < MAX_CONNECTION_RETRY) {
            maxConnectionRetry = maxRetry;
        }

        if (waitTimeout > 0 && waitTimeout < MAX_WAIT_TIMEOUT) {
            this.waitTimeout = waitTimeout;
        }

        this.listenerIp = ip;
        this.listenerPort = port;
    }

    /**
     * Method for actual socket communication. (internal use)
     *
     * @param request wrapper object for (request) socket message
     * @return wrapper object for response message
     */
    private SocketMessage handleMessage(SocketMessage request) {
        SocketMessage toReturn = null;
        MessageHandler handler = null;

        if (0 != maxConnectionInterval || 0 != maxConnectionRetry) {
            handler = new MessageHandler(listenerIp, listenerPort, initConnectionInterval,
                    maxConnectionInterval, maxConnectionRetry, waitTimeout);
        } else {
            handler = new MessageHandler(listenerIp, listenerPort);
        }
        toReturn = handler.getResponse(request);
        recentResponse = toReturn;

        if (!"".equals(toReturn.getErrorCode())) {
            setError(toReturn.getErrorCode(), toReturn.getErrorMessage());
            recentServerErrorCode = toReturn.getErrorCode();
            recentServerErrorMessage = toReturn.getErrorMessage();
        } else {
            setError("", "");
        }

        return toReturn;
    }

    /**
     * Check if response's successful. (internal use)
     *
     * @param response wrapper object for response socket message
     * @return true if successful
     */
    private boolean isSuccessful(SocketMessage response) {
        if ("success".equals(response.getValue("status"))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set recent error code / message (internal use)
     *
     * @param code    error code to set
     * @param message error message to set
     */
    private void setError(String code, String message) {
        recentErrorCode = code;
        recentErrorMessage = message;
        recentServerErrorCode = "";
        recentServerErrorMessage = "";

        if (consoleLog && !"".equals(message)) {
            System.out.println("[E!:" + code + "] " + message);
        }
    }

    /**
     * Modify error code / message using messages from system (internal use)
     *
     * @param code    error code to modify
     * @param message error message to modify
     */
    private void wrapError(String code, String message) {
        recentErrorCode = code;

		/* 
		 *	<error>
		 *		<code>TEAL_8453</code>
		 *		<message>[request_id=201203082] Failed to create a process module by wrong value of a parameter(=topic_id)</message>
		 *	</error>
		 *
		 *	<error>
		 *		<code>TEAL_4410</code>
		 *		<message>Topic ID <1000> does not exist.</message>
		 *	</error> 
		*/
        if ("TEAL_8453".equals(recentServerErrorCode)) {
            recentErrorCode = "APIL_0155";
            recentErrorMessage = message + ": argument's not valid (server-side): " + recentServerErrorMessage;
        } else if ("TEAL_4410".equals(recentServerErrorCode)) {
            recentErrorCode = "APIL_0161";
            recentErrorMessage = message + ": topic ID's not exist";
        } else {
            recentErrorMessage = message + " DUE TO [" + recentServerErrorCode + ": " + recentServerErrorMessage + "]";
        }

        recentServerErrorMessage = "";

        if (consoleLog) {
            System.out.println("[E!:" + recentErrorCode + "] " + recentErrorMessage);
        }
    }

    /**
     * Set whether to show (error) log message in console.
     *
     * @param isSet true for setting
     */
    public void setConsoleLog(boolean isSet) {
        consoleLog = isSet;
    }

    ////////////////////////////////////////////////////////
    /// General Methods
    ////////////////////////////////////////////////////////

    /**
     * Set delimiters. (" " is not allowed)
     *
     * @param item   delimiter between items (null: use default delimiter)
     * @param value  delimiter in a item between first sub-item and next one (null: use default delimiter)
     * @param freq   delimiter in a item between first sub-item and next frequency value (null: use default delimiter)
     * @param weight delimiter in a item between first sub-item and next weight value (null: use default delimiter)
     */
    public static void setDelimiters(String item, String value, String freq, String weight) {
        if (null == item || null == value || null == freq || null == weight || 1 != item.length() || 1 != value.length()
                || 1 != freq.length() || 1 != weight.length()) {
            System.err.println("[E!:SYSTEM] Some delimiters are invalid.");
            return;
        }

        if (" ".equals(item) || " ".equals(value) || " ".equals(freq) || " ".equals(weight)) {
            System.err.println("[E!:SYSTEM] Space character's not allowed as delimiters.");
            return;
        }

        if (item.equals(value) || item.equals(freq) || item.equals(weight)) {
            System.err.println("[E!:SYSTEM] Some delimiters are duplicated.");
            return;
        }

        ITEM_DELIMITER = item;
        VALUE_DELIMITER = value;
        FREQ_DELIMITER = freq;
        WEIGHT_DELIMITER = weight;
    }

    /**
     * Wait until given task's end. (interval of checking task status is given as parameter)
     *
     * @param taskId   task id to check
     * @param interval interval of checking status
     */
    public void waitUntilEnd(String taskId, long interval) {
        waitUntilEnd(taskId, interval, 0);
    }

    /**
     * Wait until given task's end. (interval of checking task status is given as parameter)
     *
     * @param taskId   task id to check
     * @param interval interval of checking status
     * @param timeout  limit of total waiting time. after timeout, this function ends waiting. (in case of 0, infinite waiting)
     */
    public void waitUntilEnd(String taskId, long interval, long timeout) {
        long startTime = System.currentTimeMillis();

        boolean finished = false;
        while (!finished) {
            try {
                Thread.sleep(interval);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TaskStatus stat = getStatus(taskId);

            if (EnumTaskStatus.FINISHED.equals(stat.getStatus())) {
                finished = true;
                break;
            } else if (EnumTaskStatus.CANCELED.equals(stat.getStatus()) || EnumTaskStatus.NOT_EXIST.equals(stat.getStatus())
                    || EnumTaskStatus.FAILED.equals(stat.getStatus())) {
                setError("APIL_0173", "task finished with errorous condition: task_id=" + taskId + " DUE TO [" + stat.getMessage() + "]");
                finished = true;
                break;
            }

            if (timeout != 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                if (!finished && elapsed > timeout) {
                    setError("APIL_0171", "timeout in waiting task: task_id=" + taskId);
                    finished = true;
                }
            }
        }
    }

    /**
     * Checks whether error was occurred.
     *
     * @return true if last API call had some error.
     */
    public boolean hasError() {
        return "".equals(recentErrorCode) ? false : true;
    }

    /**
     * Get the error code recently occurred.
     *
     * @return code
     */
    public String getErrorCode() {
        String toReturn = recentErrorCode;

        return toReturn;
    }

    /**
     * Get the server error code recently occurred.
     *
     * @return code
     */
    public String getServerErrorCode() {
        String toReturn = recentServerErrorCode;

        return toReturn;
    }

    /**
     * Get the error message recently occurred.
     *
     * @return message
     */
    public String getErrorMessage() {
        String toReturn = recentErrorMessage;

        return toReturn;
    }

    /**
     * Get response XML message (which's as is from the listener)
     *
     * @return XML message as String
     */
    public String getResponseXml() {
        if (null == recentResponse)
            return "";
        else
            return recentResponse.toString();
    }

    ////////////////////////////////////////////////////////
    /// System Handling Methods
    ////////////////////////////////////////////////////////

    /**
     * Test Connection.
     *
     * @return true if successful
     */
    public boolean testConnection() {
        boolean toReturn = true;

        String[] paramFields = {};
        SocketMessage request = new SocketMessage("admin", "ping", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        SocketMessage response = handleMessage(request);
        if (isSuccessful(response)) {
            toReturn = true;
        } else {
            toReturn = false;
        }

        return toReturn;
    }

    /**
     * Return current server status
     *
     * @return get list of server statuses
     */
    public ServerStatus[] getServerStatus() {
        ServerStatus[] toReturn = new ServerStatus[0];

        String[] paramFields = {"item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("admin", "server_status",
                SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0201", "couldn't get server status");
            } else {
                wrapError("APIL_0201", "couldn't get server status");
            }
            return toReturn;
        } else {
            String serversString = response.getValue("servers");
            StringTokenizer tokenizer = new StringTokenizer(serversString, ITEM_DELIMITER);
            toReturn = new ServerStatus[tokenizer.countTokens()];
            try {
                for (int i = 0; i < toReturn.length; i++) {
                    String itemString = tokenizer.nextToken();
                    StringTokenizer itemTokenizer = new StringTokenizer(itemString, VALUE_DELIMITER);
                    String ip = itemTokenizer.nextToken();
                    String portString = itemTokenizer.nextToken();
                    String aliveString = itemTokenizer.nextToken();
                    String timeString = itemTokenizer.nextToken();
                    int port = Integer.parseInt(portString.trim());
                    boolean alive = "y".equals(aliveString);
                    long time = Tools.parseTime(timeString.trim());
                    toReturn[i] = new ServerStatus(ip, port, time, alive);
                }
            } catch (Exception e) {
                setError("APIL_0202", "error in parsing status string: " + serversString);
                return new ServerStatus[0];
            }
        }

        return toReturn;
    }

    /**
     * Get HTTPD port number.
     *
     * @return port number
     */
    public int getHttpdPort() {
        int toReturn = 0;

        String[] paramFields = {};
        SocketMessage request = new SocketMessage("admin", "httpd_port",
                SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);

        SocketMessage response = handleMessage(request);
        if (isSuccessful(response)) {
            toReturn = Tools.parseInt(response.getValue("port"));
        } else {
            wrapError("APIL_0206", "couldn't get httpd port number");
        }

        return toReturn;
    }

    /**
     * Return running status of given request ID.
     *
     * @param requestId request ID
     * @return status object
     */
    public TaskStatus getStatus(String requestId) {
        TaskStatus toReturn = new TaskStatus();
        if (null == requestId || 0 == requestId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {};
        SocketMessage request = new SocketMessage("admin", "status",
                SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, requestId, paramFields);

        SocketMessage response = handleMessage(request);

        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0211", "couldn't get task status: task_id=" + requestId);
            } else {
                wrapError("APIL_0211", "couldn't get task status: task_id=" + requestId);
            }
            toReturn.setStatus(EnumTaskStatus.FAILED);
            toReturn.setErrorCode(getErrorCode());
            toReturn.setMessage(getErrorMessage());
            return toReturn;
        }

        toReturn.setRequestId(response.getRequestId());
        toReturn.setStatus(response.getValue("task_status").trim());
        toReturn.setTaskType(response.getValue("task_type").trim());
        toReturn.setPercent(Tools.parseDouble(response.getValue("percentage")));
        toReturn.setStartTime(Tools.parseTime(response.getValue("start_time")));
        toReturn.setEndTime(Tools.parseTime(response.getValue("end_time")));
        toReturn.setEstimatedTime(Tools.parseTime(response.getValue("estimated_time")));
        toReturn.setMessage(response.getValue("message"));
        toReturn.setErrorCode(response.getValue("error_code"));

        return toReturn;
    }

    /**
     * Return entire request IDs running in the server.
     *
     * @return list of request IDs in String Array
     */
    public String[] getRequestIds() {
        String[] toReturn = new String[0];

        String[] paramFields = {"item_delimiter"};
        SocketMessage request = new SocketMessage("admin", "req_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0221", "couldn't get Request IDs");
            } else {
                wrapError("APIL_0221", "couldn't get Request IDs");
            }
            return toReturn;
        } else {
            toReturn = StringTool.stringToArray(response.getValue("req_ids").trim(), ITEM_DELIMITER);
        }

        return toReturn;
    }

    /**
     * Stop task.
     *
     * @param requestId request ID to stop
     * @return true if this message's successfully delivered
     */
    public boolean stopTask(String requestId) {
        boolean toReturn = false;
        if (null == requestId || 0 == requestId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        SocketMessage request = new SocketMessage("admin", "stop", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, requestId,
                new String[0]);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0231", "couldn't stop task: task_id=" + requestId);
            } else {
                wrapError("APIL_0231", "couldn't stop task: task_id=" + requestId);
            }
            toReturn = false;
        } else {
            toReturn = true;
        }
        return toReturn;
    }

    /**
     * Cancel task.
     *
     * @param requestId request ID to cancel
     * @return true if this message's successfully delivered
     */
    public boolean cancelTask(String requestId) {
        boolean toReturn = false;
        if (null == requestId || 0 == requestId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        SocketMessage request = new SocketMessage("admin", "cancel", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, requestId,
                new String[0]);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0241", "couldn't calcel task: task_id=" + requestId);
            } else {
                wrapError("APIL_0241", "couldn't calcel task: task_id=" + requestId);
            }
            toReturn = false;
        } else {
            toReturn = true;
        }
        return toReturn;
    }

    /**
     * Resume stopped task.
     *
     * @param requestId request ID to resume
     * @return true if this message's successfully delivered
     */
    public boolean resumeTask(String requestId) {
        boolean toReturn = false;
        if (null == requestId || 0 == requestId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        SocketMessage request = new SocketMessage("admin", "resume", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, requestId,
                new String[0]);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0251", "couldn't resume task: task_id=" + requestId);
            } else {
                wrapError("APIL_0251", "couldn't resume task: task_id=" + requestId);
            }
            toReturn = false;
        } else {
            toReturn = true;
        }
        return toReturn;
    }

    ////////////////////////////////////////////////////////
    /// Keywords Extraction Methods
    ////////////////////////////////////////////////////////

    /**
     * Request keywords extraction. (bulk operation for the collection)
     *
     * @param collectionId target collection ID
     * @return Request ID (not "") if successful, "" if error occurred.
     */
    public String requestKeywordExtraction(String collectionId) {
        String toReturn = "";
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }
        String[] paramFields = {"collection_id"};
        SocketMessage request = new SocketMessage("extractor", "bulk", SocketMessage.PriorityType.NORMAL, SocketMessage.TransferType.SINGLE_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0261", "keyword extraction (bulk) wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0261", "keyword extraction (bulk) wasn't successful: coll_id=" + collectionId);
            }
            toReturn = "";
        } else {
            toReturn = response.getRequestId();
        }
        return toReturn;
    }

    /**
     * Request keywords extraction. (PlainText)
     *
     * @param collectionId	target collection ID
     * @param content	texts to extract keywords.
     * @param targetField	field name for keyword extraction in server's configuration
     * @return wrapper object for keyword-weight pairs
     */
    public List<Pair<Integer>> extractKeywordsForPlainText(String collectionId, String content, String targetField) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        if (null == collectionId || null == content || 0 == collectionId.length() || 0 == content.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (content.length() > MAX_CONTENT_SIZE) {
            setError("APIL_0153", "content size cannot exceed " + MAX_CONTENT_SIZE + " characters: " + content.length());
            return toReturn;
        }

        String[] paramFields = {"collection_id", "target_field", "content", "item_delimiter", "weight_delimiter", "field_delimiter" };
        SocketMessage request = new SocketMessage("extractor", "plainText", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("target_field", targetField);
        request.setValue("content", content);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("weight_delimiter", WEIGHT_DELIMITER);
        request.setValue("field_delimiter", FIELD_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0271", "keyword extraction (plainText) wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0271", "keyword extraction (plainText) wasn't successful: coll_id=" + collectionId);
            }
        } else {
            //public static List< Pair<String> > getPairListStr(String obj, String itemDelimiter, String valueDelimiter)
            String keywordsString = response.getValue("keywords").trim();
            toReturn = Tools.getPairListInt(keywordsString, ITEM_DELIMITER, WEIGHT_DELIMITER);
        }
        return toReturn;
    }
    
    /**
     * Request keywords extraction. (PlainText)
     *
     * @param collectionId	target collection ID
     * @param content	texts to extract keywords.
     * @param targetField	field name for keyword extraction in server's configuration
     * @return wrapper object for keyword-weight pairs
     */
    public List<Pair<Double>> getSimilarDoc(String collectionId, String content, String topN) {
        List<Pair<Double>> toReturn = new ArrayList<Pair<Double>>();
        if (null == collectionId || null == content || 0 == collectionId.length() || 0 == content.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (content.length() > MAX_CONTENT_SIZE) {
            setError("APIL_0153", "content size cannot exceed " + MAX_CONTENT_SIZE + " characters: " + content.length());
            return toReturn;
        }

        String[] paramFields = {"collection_id", "target_field", "content", "field_delimiter", "value_delimiter", "item_delimiter", "topn" };
        SocketMessage request = new SocketMessage("recommender", "get_similar_doc", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("target_field", "TERMS_KMA");
        request.setValue("content", content);
        request.setValue("field_delimiter", FIELD_DELIMITER);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("topn", topN);
        
        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0271", "get similar Documents wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0271", "get similar Documents (realtime) wasn't successful: coll_id=" + collectionId);
            }
        } else {
            //public static List< Pair<String> > getPairListStr(String obj, String itemDelimiter, String valueDelimiter)
            String keywordsString = response.getValue("similar_doc").trim();
            toReturn = Tools.getPairListDouble(keywordsString, ITEM_DELIMITER, VALUE_DELIMITER);
        }
        return toReturn;
    }
    
    public List<Pair<Double>> getSimilarDocSf1(String collectionId, String content, String topN, ArrayList<String> resultList ) {
    	
    	String docidSf1 = "";
    	  	
        Iterator<String> docList = resultList.iterator();
        while (docList.hasNext()) {
        	docidSf1 += docList.next() + ITEM_DELIMITER;
        }
    	
    	
        List<Pair<Double>> toReturn = new ArrayList<Pair<Double>>();
        if (null == collectionId || null == content || 0 == collectionId.length() || 0 == content.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (content.length() > MAX_CONTENT_SIZE) {
            setError("APIL_0153", "content size cannot exceed " + MAX_CONTENT_SIZE + " characters: " + content.length());
            return toReturn;
        }

        String[] paramFields = {"collection_id", "target_field", "content", "field_delimiter", "value_delimiter", "item_delimiter", "topn", "docid_sf1" };
        SocketMessage request = new SocketMessage("recommender", "get_similar_doc_sf1", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("target_field", "TERMS_KMA");
        request.setValue("content", content);
        request.setValue("field_delimiter", FIELD_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("topn", topN);
        request.setValue("docid_sf1", docidSf1);
                
        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0271", "get similar Documents wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0271", "get similar Documents (realtime) wasn't successful: coll_id=" + collectionId);
            }
        } else {
            //public static List< Pair<String> > getPairListStr(String obj, String itemDelimiter, String valueDelimiter)
            String keywordsString = response.getValue("similar_doc").trim();
            toReturn = Tools.getPairListDouble(keywordsString, ITEM_DELIMITER, VALUE_DELIMITER);
        }
        return toReturn;
    }
    
    /**
     * Request keywords extraction. (realtime)
     *
     * @param collectionId target collection ID
     * @param content      texts to extract keywords. (SCD formats or raw text, according to the setting of the collection)
     * @return wrapper object for keyword-weight pairs
     */
    public List<Pair<Integer>> extractKeywords(String collectionId, String content) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        if (null == collectionId || null == content || 0 == collectionId.length() || 0 == content.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (content.length() > MAX_CONTENT_SIZE) {
            setError("APIL_0153", "content size cannot exceed " + MAX_CONTENT_SIZE + " characters: " + content.length());
            return toReturn;
        }

        String[] paramFields = {"collection_id", "content", "item_delimiter", "weight_delimiter"};
        SocketMessage request = new SocketMessage("extractor", "realtime", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("content", content);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("weight_delimiter", WEIGHT_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0271", "keyword extraction (realtime) wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0271", "keyword extraction (realtime) wasn't successful: coll_id=" + collectionId);
            }
        } else {
            //public static List< Pair<String> > getPairListStr(String obj, String itemDelimiter, String valueDelimiter)
            String keywordsString = response.getValue("keywords").trim();
            toReturn = Tools.getPairListInt(keywordsString, ITEM_DELIMITER, WEIGHT_DELIMITER);
        }
        return toReturn;
    }

    ////////////////////////////////////////////////////////
    /// Topic Analysis Methods
    ////////////////////////////////////////////////////////

    /**
     * TEA Listener 하위 모든 TEA Server에서 관리하고 있는 Collection ID 목록을 조회함.
     *
     * @return string array type collection id list
     */
    public String[] getCollectionIds() {
        return getCollectionIds("");
    }

    /**
     * 인자값으로 지정한 TEA Server에 해당하는 Collection ID 목록을 조회함.
     *
     * @param teaServerId
     * @return string array type collection id list
     */
    public String[] getCollectionIds(String teaServerId) {
        String[] toReturn = new String[0];

        String[] paramFields;
        if (teaServerId != null && teaServerId.equals("")) {
            paramFields = new String[]{"item_delimiter"};
        } else {
            paramFields = new String[]{"item_delimiter", "server_id"};
        }

        SocketMessage request = new SocketMessage("admin", "coll_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        for (String param : paramFields) {
            if (param.equals("item_delimiter")) {
                request.setValue("item_delimiter", ITEM_DELIMITER);
            } else if (param.equals("server_id")) {
                request.setValue("server_id", teaServerId);
            }
        }

        SocketMessage response = handleMessage(request);

        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0281", "couldn't get collection IDs");
            } else {
                wrapError("APIL_0281", "couldn't get collection IDs");
            }
            return toReturn;
        } else {
            toReturn = StringTool.stringToArray(response.getValue("coll_ids").trim(), ITEM_DELIMITER);
        }

        return toReturn;
    }

    /**
     * Get some information for given collection.
     *
     * @param collectionId collection ID to get info
     * @return wrapper object for collection status
     */
    public CollectionInfo getCollectionInfo(String collectionId) {
        CollectionInfo toReturn = new CollectionInfo();
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id"};
        SocketMessage request = new SocketMessage("admin", "coll_info", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0285", "couldn't get collection info: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0285", "couldn't get collection info: coll_id=" + collectionId);
            }
        } else {
            boolean isTeColl = "y".equals(response.getValue("is_te_collection"));
            boolean isRoasterColl = "y".equals(response.getValue("is_roaster_collection"));
            long timeTeExtract = Tools.parseTime(response.getValue("time_te_extracted"));
            long timeRoasterAnalysis = Tools.parseTime(response.getValue("time_roaster_analyzed"));
            long timeStarted = Tools.parseTime(response.getValue("start_time"));
            String statusString = response.getValue("collection_status");
            boolean needsAnalysis = "y".equals(response.getValue("needs_analysis"));

            String eCodeTe = response.getValue("te_error_code");
            String eMessageTe = response.getValue("te_error_message");
            String eCodeRoaster = response.getValue("roaster_error_code");
            String eMessageRoaster = response.getValue("roaster_error_message");
            String eCodeCustom = response.getValue("custom_error_code");
            String eMessageCustom = response.getValue("custom_error_message");

            String charset = response.getValue("charset");
            String taskId = "0".equals(response.getValue("locker")) ? "" : response.getValue("locker");
            boolean isRealtime = "y".equals(response.getValue("realtime"));

            toReturn = new CollectionInfo(collectionId, isTeColl, isRoasterColl, timeTeExtract, timeRoasterAnalysis, timeStarted,
                    statusString, needsAnalysis);

            toReturn.setErrors(eCodeTe, eMessageTe, eCodeRoaster, eMessageRoaster, eCodeCustom, eMessageCustom);
            toReturn.setCharset(charset);
            toReturn.setTaskId(taskId);
            toReturn.setRealtime(isRealtime);
        }
        return toReturn;
    }

    //	/**
    //	 * Create collection for topic analysis.
    //	 * @param collectionId collection ID to create
    //	 * @return true if successful
    //	 */
    //	public boolean createCollection(String collectionId)
    //	{
    //		boolean toReturn = false;
    //		if (null == collectionId || 0 == collectionId.length())
    //		{
    //			setError("APIL_0100", "argument's not valid.");
    //			return toReturn;
    //		}
    //
    //		if (DUMMY)
    //		{
    //			return true;
    //		}
    //
    //		String [] paramFields = {"collection_id"};
    //		SocketMessage request = new SocketMessage("roaster", "coll_create", PriorityType.EMERGENCY, TransferType.BI_WAY, "", paramFields);
    //		request.setValue("collection_id", collectionId);
    //
    //		SocketMessage response = handleMessage(request);
    //		if (!isSuccessful(response))
    //		{
    //			if ("".equals(response.getErrorCode()))
    //			{
    //				setError("APIL_0291", "couldn't create collection: " + collectionId);
    //			}
    //			toReturn = false;
    //		}
    //		else
    //		{
    //			toReturn = true;
    //		}
    //		return toReturn;
    //	}

    //	/**
    //	 * Request loading documents in the collection.
    //	 * @param collectionId collection ID to load documents
    //	 * @return Request ID (not "") if successful, "" if error occurred.
    //	 */
    //	public String requestLoadCollection(String collectionId)
    //	{
    //		String toReturn = "";
    //		if (null == collectionId || 0 == collectionId.length())
    //		{
    //			setError("APIL_0100", "argument's not valid.");
    //			return toReturn;
    //		}
    //
    //		if (DUMMY)
    //		{
    //			return Tools.getUniqueId();
    //		}
    //
    //		String [] paramFields = {"collection_id"};
    //		SocketMessage request = new SocketMessage("retriever", "coll_load", PriorityType.NORMAL, TransferType.SINGLE_WAY, "", paramFields);
    //		request.setValue("collection_id", collectionId);
    //
    //		SocketMessage response = handleMessage(request);
    //		if (!isSuccessful(response))
    //		{
    //			if ("".equals(response.getErrorCode()))
    //			{
    //				setError("APIL_0301", "request for loading collection wasn't successful: " + collectionId);
    //			}
    //			toReturn = "";
    //		}
    //		else
    //		{
    //			toReturn = response.getRequestId();
    //		}
    //		return toReturn;
    //	}

    /**
     * Request analyzing topics.
     *
     * @param collectionId collection ID to analyze topics
     * @param reload       if it's set to true, request reloading and re-analysis of given collection
     * @return Request ID (not "") if successful, "" if error occurred.
     */
    public String requestTopicAnalysis(String collectionId, boolean reload) {
        String toReturn = "";
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "reload"};
        SocketMessage request = new SocketMessage("roaster", "topic_ext", SocketMessage.PriorityType.NORMAL, SocketMessage.TransferType.SINGLE_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("reload", reload ? "y" : "n");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0311", "request for analyzing topics wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0311", "request for analyzing topics wasn't successful: coll_id=" + collectionId);
            }
            toReturn = "";
        } else {
            toReturn = response.getRequestId();
        }
        return toReturn;
    }

    /**
     * Request analyzing given custom field with given analysis settings.
     *
     * @param collectionId collection ID to analyze topics
     * @return Request ID (not "") if successful, "" if error occurred.
     */
    public String requestCustomFieldAnalysis(String collectionId, String[] analysisIds) {
        String toReturn = "";
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String delimiter = ",";
        String[] paramFields = {"collection_id", "analysis_id"};
        SocketMessage request = new SocketMessage("teasifter", "custom", SocketMessage.PriorityType.NORMAL, SocketMessage.TransferType.SINGLE_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        if (null == analysisIds)
            analysisIds = new String[0];
        request.setValue("analysis_id", StringTool.arrayToString(analysisIds, delimiter));

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0321", "request for analyzing custom field wasn't successful: coll_id=" + collectionId
                        + "/analysis_ids=" + StringTool.arrayToString(analysisIds, delimiter));
            } else {
                wrapError("APIL_0321", "request for analyzing custom field wasn't successful: coll_id=" + collectionId
                        + "/analysis_ids=" + StringTool.arrayToString(analysisIds, delimiter));
            }
            toReturn = "";
        } else {
            toReturn = response.getRequestId();
        }
        return toReturn;

    }

    /**
     * Request analyzing given custom field with given analysis settings.
     *
     * @param collectionId target collection ID
     * @param analysisId   actual ID for custom field analysis setting
     * @return Request ID (not "") if successful, "" if error occurred.
     */
    public String requestCustomFieldAnalysis(String collectionId, String analysisId) {
        String toReturn = "";
        if (null == collectionId || null == analysisId || 0 == collectionId.length() || 0 == analysisId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "analysis_id"};
        SocketMessage request = new SocketMessage("teasifter", "custom", SocketMessage.PriorityType.NORMAL, SocketMessage.TransferType.SINGLE_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("analysis_id", analysisId);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0321", "request for analyzing custom field wasn't successful: coll_id=" + collectionId
                        + "/analysis_id=" + analysisId);
            } else {
                wrapError("APIL_0321", "request for analyzing custom field wasn't successful: coll_id=" + collectionId
                        + "/analysis_id=" + analysisId);
            }
            toReturn = "";
        } else {
            toReturn = response.getRequestId();
        }
        return toReturn;
    }

    /**
     * Request extracting keywords and analyzing topics simultaneously against given collection.
     *
     * @param collectionId collection ID to extract / analyze
     * @param reload       if it's set to true, request reloading and re-analysis of given collection
     * @return Request ID (not "") if successful, "" if error occurred.
     */
    public String requestTotalAnalysis(String collectionId, boolean reload) {
        String toReturn = "";
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "reload"};
        SocketMessage request = new SocketMessage("total", "total_ext", SocketMessage.PriorityType.NORMAL, SocketMessage.TransferType.SINGLE_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("reload", reload ? "y" : "n");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0331", "request for total analysis wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0331", "request for total analysis wasn't successful: coll_id=" + collectionId);
            }
            toReturn = "";
        } else {
            toReturn = response.getRequestId();
        }
        return toReturn;
    }

    /**
     * Request topic analysis of given document. (realtime)
     *
     * @param collectionId      collection ID
     * @param keywords          keywords for input (not null!)
     * @param keywordTopicPairs returning object for (keyword, topic_id) pairs for topic assignment output (must be empty object to get results, no result if it's null)
     * @return wrapper object for (topic, weight) pairs (weight: 1 to 10000)
     */
    public List<Pair<Integer>> analyzeTopic(String collectionId, String[] keywords, List<Pair<String>> keywordTopicPairs) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        if (null == collectionId || null == keywords || 0 == collectionId.length() || 0 == keywords.length) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (null != keywordTopicPairs)
            keywordTopicPairs.clear();

        String[] paramFields = {"collection_id", "keywords", "item_delimiter", "weight_delimiter", "value_delimiter",
                "kw_topic_map"};
        SocketMessage request = new SocketMessage("roaster", "topic_ext_doc", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        String keywordsStr = StringTool.arrayToString(keywords, ITEM_DELIMITER);
        request.setValue("keywords", keywordsStr);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("weight_delimiter", WEIGHT_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("kw_topic_map", null == keywordTopicPairs ? "n" : "y");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0341", "topic analysis (realtime) wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0341", "topic analysis (realtime) wasn't successful: coll_id=" + collectionId);
            }
        } else {
            String topicsString = response.getValue("topics").trim();
            toReturn = Tools.getPairListInt(topicsString, ITEM_DELIMITER, WEIGHT_DELIMITER);

            if (null != keywordTopicPairs) {
                String kwTopicsString = response.getValue("keyword_topics").trim();
                Tools.setPairListStr(kwTopicsString, ITEM_DELIMITER, VALUE_DELIMITER, keywordTopicPairs);
            }
        }
        return toReturn;
    }

    /**
     * Request keyword extraction and topic analysis of given document simultaneously. (realtime)
     *
     * @param collectionId      collection ID
     * @param content           text content to analyze
     * @param keywords          returning object for keyword extraction results (must be an empty object to get results, returns no result if it's null)
     * @param keywordTopicPairs returning object for (keyword, topic_id) pairs for topic assignment output (must be empty object to get results, no result if it's null)
     * @return wrapper object for (topic, weight) pairs (weight: 1 to 10000)
     */
    public List<Pair<Integer>> analyzeTotally(String collectionId, String content, List<String> keywords,
                                              List<Pair<String>> keywordTopicPairs) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        if (null == collectionId || null == content || 0 == collectionId.length() || 0 == content.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (content.length() > MAX_CONTENT_SIZE) {
            setError("APIL_0153", "content size cannot exceed " + MAX_CONTENT_SIZE + " characters: " + content.length());
            return toReturn;
        }

        if (null != keywords)
            keywords.clear();
        if (null != keywordTopicPairs)
            keywordTopicPairs.clear();

        String[] paramFields = {"collection_id", "content", "item_delimiter", "weight_delimiter", "value_delimiter",
                "kw_topic_map"};
        SocketMessage request = new SocketMessage("total", "total_ext_doc", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("content", content);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("weight_delimiter", WEIGHT_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("kw_topic_map", null == keywordTopicPairs ? "n" : "y");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0351", "total analysis (realtime) wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0351", "total analysis (realtime) wasn't successful: coll_id=" + collectionId);
            }
        } else {
            String topicsString = response.getValue("topics").trim();
            toReturn = Tools.getPairListInt(topicsString, ITEM_DELIMITER, WEIGHT_DELIMITER);

            if (null != keywords) {
                String keywordsString = response.getValue("keywords");
                String[] keywordsArray = StringTool.stringToArray(keywordsString, ITEM_DELIMITER);
                for (int i = 0; i < keywordsArray.length; i++)
                    keywords.add(keywordsArray[i]);
                //Tools.setPairListInt(keywordsString, ITEM_DELIMITER, WEIGHT_DELIMITER, keywords);
            }

            if (null != keywordTopicPairs) {
                String kwTopicsString = response.getValue("keyword_topics").trim();
                Tools.setPairListStr(kwTopicsString, ITEM_DELIMITER, VALUE_DELIMITER, keywordTopicPairs);
            }
        }
        return toReturn;
    }

    ////////////////////////////////////////////////////////
    /// Repository Retrieval Methods (Topic)
    ////////////////////////////////////////////////////////

    /**
     * Get (analyzed) documents information.
     *
     * @param collectionId collection id to find document
     * @param docId        document id (unique in the collection)
     * @param maxTextSize  (0: full, &lt;0:follow settings, other integer: up to that size)
     */
    public DocumentInfo getAnalyzedDocument(String collectionId, String docId, int maxTextSize) {
        DocumentInfo toReturn = new DocumentInfo();
        if (null == collectionId || null == docId || 0 == collectionId.length() || 0 == docId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "doc_id", "item_delimiter", "weight_delimiter", "value_delimiter",
                "max_text_size"};
        SocketMessage request = new SocketMessage("retriever", "analyzed_doc_info", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("doc_id", docId);
        request.setValue("max_text_size", Integer.toString(maxTextSize));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("weight_delimiter", WEIGHT_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0361", "retrieval of analyzed document wasn't successful: coll_id=" + collectionId + "/doc_id="
                        + docId);
            } else {
                wrapError("APIL_0361", "retrieval of analyzed document wasn't successful: coll_id=" + collectionId + "/doc_id="
                        + docId);
            }
        } else {
            String title = response.getValue("title");
            String content = response.getValue("content");
            String date = response.getValue("date");
            List<Pair<String>> keywordTopics = Tools.getPairListStr(response.getValue("keyword_topics"), ITEM_DELIMITER,
                    VALUE_DELIMITER);
            String[] keywords = StringTool.stringToArray(response.getValue("keywords"), ITEM_DELIMITER);
            List<Pair<Integer>> topics = Tools.getPairListInt(response.getValue("topics"), ITEM_DELIMITER, WEIGHT_DELIMITER);
            List<Pair<String>> customFieldValues = Tools.getPairListStr(response.getValue("custom"), ITEM_DELIMITER,
                    VALUE_DELIMITER);

            toReturn = new DocumentInfo(docId, title, content, date, keywords, topics, keywordTopics, customFieldValues);
        }

        return toReturn;
    }

    /**
     * Get (analyzed) documents information. (maximum text size follows the system setting)
     *
     * @param collectionId collection id to find document
     * @param docId        document id (unique in the collection)
     */
    public DocumentInfo getAnalyzedDocument(String collectionId, String docId) {
        return getAnalyzedDocument(collectionId, docId, -1);
    }

    /**
     * Get (analyzed) documents information.
     *
     * @param collectionId collection id to find document
     * @param docId        document id (unique in the collection)
     * @param maxTextSize  (-1:full, 0: None, other integer: up to that size)
     */
    public List<DocumentInfo> getAnalyzedDocumentWithTopic(String collectionId, int offset, int num_count, int maxTextSize) {
    	List<DocumentInfo> toReturn = new ArrayList<DocumentInfo>();
                
        if (null == collectionId || 0 == collectionId.length() ) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "offset", "num_count", "item_delimiter", "weight_delimiter", "value_delimiter", "document_delimiter",
                "max_text_size"};
        SocketMessage request = new SocketMessage("retriever", "analyzed_doc_info_with_topic", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("offset", String.valueOf(offset) );
        request.setValue("num_count", String.valueOf(num_count) );
        request.setValue("max_text_size", Integer.toString(maxTextSize));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("weight_delimiter", WEIGHT_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("document_delimiter", DOCUMENT_DELIMITER);

        SocketMessage response = handleMessage(request);
        System.out.print( response );
        
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0361", "retrieval of analyzed document wasn't successful: coll_id=" + collectionId + "/offset="
                        + offset + "/num_count=" + num_count);
            } else {
                wrapError("APIL_0361", "retrieval of analyzed document wasn't successful: coll_id=" + collectionId + "/offset="
                        + offset + "/num_count=" + num_count);
            }
        } else {    
        	//docId, date, keywords, topics, title, content
        	String[] docIdsArray = StringTool.stringToArray(response.getValue("docId"), DOCUMENT_DELIMITER);
        	String[] datesArray = StringTool.stringToArray(response.getValue("date"), DOCUMENT_DELIMITER);
        	String[] keywordsArray = StringTool.stringToArray(response.getValue("keywords"), DOCUMENT_DELIMITER);
        	String[] topicsArray = StringTool.stringToArray(response.getValue("topics"), DOCUMENT_DELIMITER);
        	String[] titlesArray = StringTool.stringToArray(response.getValue("title"), DOCUMENT_DELIMITER);
        	String[] contentsArray = StringTool.stringToArray(response.getValue("content"), DOCUMENT_DELIMITER);
        	
            int itemCount = docIdsArray.length;
            if (itemCount != datesArray.length || itemCount != keywordsArray.length || itemCount != topicsArray.length
                    || itemCount != titlesArray.length || itemCount != contentsArray.length ) {
                setError("APIL_0415", "error in parsing result message for topic info retrieval: coll_id=" + collectionId);
                return toReturn;
            }
        
            for (int i = 0; i < itemCount; i++) {
            	String docId = docIdsArray[i];
            	String date = datesArray[i];
            	String title = titlesArray[i];
            	String content = contentsArray[i];
            	System.out.println(topicsArray[i]);
            	String[] keywords = StringTool.stringToArray(keywordsArray[i], ITEM_DELIMITER);
            	List<Pair<String>> topics = Tools.getPairListStr(topicsArray[i], ITEM_DELIMITER, WEIGHT_DELIMITER);
            	List<Pair<Integer>> temp = new ArrayList<Pair<Integer>>();
            	
                DocumentInfo docInfo = new DocumentInfo(docId, title, content, date, keywords, temp, topics, topics);          
                toReturn.add( docInfo );
            }
        }

        return toReturn;
    }
    
    /**
     * Get list of documents (id, title) of given topic ID.
     *
     * @param collectionId   target collection ID
     * @param topicId        topic ID to retrieve
     * @param offset         start point to read items
     * @param numItems       maximum number of items to get
     * @param sortDescending if set to true, sort result in descending order of document ID
     * @return list of (document ID, title) pairs
     */
    public List<Pair<String>> getDocumentsWithTopic(String collectionId, String topicId, int offset, int numItems,
                                                    boolean sortDescending) {
        List<Pair<String>> toReturn = new ArrayList<Pair<String>>();
        if (null == collectionId || null == topicId || 0 == collectionId.length() || 0 == topicId.length() || offset < 0
                || numItems <= 0) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "topic_id", "offset", "num_count", "item_delimiter", "value_delimiter",
                "sort_descending"};
        SocketMessage request = new SocketMessage("retriever", "topic_doc_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numItems));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("sort_descending", sortDescending ? "y" : "n");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0371", "retrieval of documents with given topic ID wasn't successful: coll_id=" + collectionId
                        + "/topic_id=" + topicId);
            } else {
                wrapError("APIL_0371", "retrieval of documents with given topic ID wasn't successful: coll_id=" + collectionId
                        + "/topic_id=" + topicId);
            }
        } else {
            String docsString = response.getValue("docs");
            toReturn = Tools.getPairListStr(docsString, ITEM_DELIMITER, VALUE_DELIMITER);
        }

        return toReturn;
    }

    /**
     * Get number of documents of given topic ID.
     *
     * @param collectionId target collection ID
     * @param topicId      topic ID to retrieve
     * @return count of documents
     */
    public int getDocumentCountWithTopic(String collectionId, String topicId) {
        int toReturn = 0;

        if (null == collectionId || null == topicId || 0 == collectionId.length() || 0 == topicId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "topic_id"};
        SocketMessage request = new SocketMessage("retriever", "topic_doc_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0381", "retrieval of number of documents with given topic ID wasn't successful: coll_id="
                        + collectionId + "/topic_id=" + topicId);
            } else {
                wrapError("APIL_0381", "retrieval of number of documents with given topic ID wasn't successful: coll_id="
                        + collectionId + "/topic_id=" + topicId);
            }
        } else {
            toReturn = Tools.parseInt(response.getValue("count"));
        }

        return toReturn;
    }

    /**
     * Get list of document IDs of given topic ID and keyword
     *
     * @param collectionId   target collection ID
     * @param topicId        topic ID to retrieve
     * @param keyword        keyword to retrieve
     * @param offset         start point to read items
     * @param numItems       maximum number of items to get
     * @param sortDescending if set to true, sort result in descending order of document ID
     * @return list of (document ID, title) pairs
     * @deprecated
     */
    public List<Pair<String>> getDocumentsWithTopicAndKeyword(String collectionId, String topicId, String keyword, int offset,
                                                              int numItems, boolean sortDescending) {
        List<Pair<String>> toReturn = new ArrayList<Pair<String>>();
        if (null == collectionId || null == topicId || null == keyword || 0 == collectionId.length() || 0 == topicId.length()
                || 0 == keyword.length() || offset < 0 || numItems <= 0) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "topic_id", "keyword", "offset", "num_count", "item_delimiter",
                "value_delimiter", "sort_descending"};
        SocketMessage request = new SocketMessage("retriever", "topic_kw_doc_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);
        request.setValue("keyword", keyword);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numItems));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("sort_descending", sortDescending ? "y" : "n");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0391", "retrieval of documents with given topic ID and keyword wasn't successful: coll_id="
                        + collectionId + "/topic_id=" + topicId + "/keyword=" + keyword);
            } else {
                wrapError("APIL_0391", "retrieval of documents with given topic ID and keyword wasn't successful: coll_id="
                        + collectionId + "/topic_id=" + topicId + "/keyword=" + keyword);
            }

        } else {
            String docsString = response.getValue("docs");
            toReturn = Tools.getPairListStr(docsString, ITEM_DELIMITER, VALUE_DELIMITER);
        }
        return toReturn;
    }

    /**
     * Get number of documents of given topic ID and keyword
     *
     * @param collectionId target collection ID
     * @param topicId      topic ID to retrieve
     * @param keyword      keyword to retrieve
     * @return count of documents
     * @deprecated
     */
    public int getDocumentCountWithTopicAndKeyword(String collectionId, String topicId, String keyword) {
        int toReturn = 0;
        if (null == collectionId || null == topicId || null == keyword || 0 == collectionId.length() || 0 == topicId.length()
                || 0 == keyword.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "topic_id", "keyword"};
        SocketMessage request = new SocketMessage("retriever", "topic_kw_doc_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);
        request.setValue("keyword", keyword);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0401",
                        "retrieval of number of documents with given topic ID and keyword wasn't successful: coll_id="
                                + collectionId + "/topic_id=" + topicId + "/keyword=" + keyword);
            } else {
                wrapError("APIL_0401",
                        "retrieval of number of documents with given topic ID and keyword wasn't successful: coll_id="
                                + collectionId + "/topic_id=" + topicId + "/keyword=" + keyword);
            }
        } else {
            toReturn = Tools.parseInt(response.getValue("count"));
        }

        return toReturn;
    }

    /**
     * Get list of topics with some additional metadata.
     *
     * @param collectionId   collection ID
     * @param offset         start position of index to get topics
     * @param numCount       number of topics to get
     * @param maxKeywords    maximum number of (related) keywords per topic
     * @param sortField      field for sorting list. (Use one in SortField class: ID, LABEL, DOC_COUNT)
     * @param sortDescending if set to true, sort result in descending order
     * @return list of TopicInfo objects
     */
    public TopicInfo[] getTopicList(String collectionId, int offset, int numCount, int maxKeywords, String sortField,
                                    boolean sortDescending) {
        TopicInfo[] toReturn = new TopicInfo[0];
        if (!SortField.ID.equals(sortField) && !SortField.LABEL.equals(sortField) && !SortField.DOC_COUNT.equals(sortField)) {
            setError("APIL_0101", "sort field's not valid: "
                    + (null == sortField ? "(null)" : (sortField.length() == 0 ? "(empty)" : sortField))
                    + " (allowed: ID, LABEL, DOC_COUNT)");
        }

        if (null == collectionId || 0 == collectionId.length() || offset < 0 || numCount <= 0 || maxKeywords < -1) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "offset", "num_count", "item_delimiter", "value_delimiter", "max_num_keywords",
                "sort_field", "sort_descending"};
        SocketMessage request = new SocketMessage("retriever", "topic_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numCount));
        request.setValue("max_num_keywords", Integer.toString(maxKeywords));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("sort_field", sortField);
        request.setValue("sort_descending", sortDescending ? "y" : "n");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0411", "retrieval of topic information wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0411", "retrieval of topic information wasn't successful: coll_id=" + collectionId);
            }
        } else {
            List<Pair<String>> topicIdLabels = Tools.getPairListStr(response.getValue("topics"), ITEM_DELIMITER, VALUE_DELIMITER);
            String[] keywordsArray = StringTool.stringToArray(response.getValue("keywords"), ITEM_DELIMITER);
            String[] weightsArray = StringTool.stringToArray(response.getValue("weights"), ITEM_DELIMITER);
            String[] numDocsArray = StringTool.stringToArray(response.getValue("doc_counts"), ITEM_DELIMITER);
            String[] feedbacksArray = StringTool.stringToArray(response.getValue("feedback"), ITEM_DELIMITER);

            int itemCount = topicIdLabels.size();
            if (itemCount != keywordsArray.length || itemCount != weightsArray.length || itemCount != numDocsArray.length
                    || itemCount != feedbacksArray.length) {
                setError("APIL_0415", "error in parsing result message for topic info retrieval: coll_id=" + collectionId);
                return toReturn;
            }

            toReturn = new TopicInfo[itemCount];
            for (int i = 0; i < toReturn.length; i++) {
                List<Pair<Integer>> keywordWeightList = new ArrayList<Pair<Integer>>();

                String keywordsDesc = keywordsArray[i];
                String weightsDesc = weightsArray[i];

                if (keywordsDesc.startsWith(":"))
                    keywordsDesc = " " + keywordsDesc;

                if (keywordsDesc.endsWith(":"))
                    keywordsDesc = keywordsDesc + " ";

                while (keywordsDesc.contains("::")) {
                    keywordsDesc = StringTool.replace(keywordsDesc, "::", ": :");
                }

                while (weightsDesc.contains("::")) {
                    weightsDesc = StringTool.replace(weightsDesc, "::", ": :");
                }

                String[] keywords = StringTool.stringToArray(keywordsDesc, VALUE_DELIMITER);
                String[] weights = StringTool.stringToArray(weightsDesc, VALUE_DELIMITER);

                if (keywords.length == weights.length) {
                    for (int x = 0; x < keywords.length; x++) {
                        if (" ".equals(keywords[x]) || " ".equals(weights[x]) || "NULL".equals(keywords[x]))
                            continue;
                        keywordWeightList.add(new Pair<Integer>(keywords[x], new Integer(Tools.parseInt(weights[x]))));
                    }
                } else {

                }

                Pair<String> idLabel = topicIdLabels.get(i);
                toReturn[i] = new TopicInfo(idLabel.key(), idLabel.value(), keywordWeightList, Tools.parseInt(numDocsArray[i]),
                        "y".equals(feedbacksArray[i]));
            }
        }

        return toReturn;
    }

    /**
     * Get number of topics
     *
     * @param collectionId collection ID
     * @return number of topics
     */
    public int getTopicCount(String collectionId) {
        int toReturn = 0;
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id"};
        SocketMessage request = new SocketMessage("retriever", "topic_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0421", "retrieval of number of topics wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0421", "retrieval of number of topics wasn't successful: coll_id=" + collectionId);
            }
        } else {
            toReturn = Tools.parseInt(response.getValue("count"));
        }

        return toReturn;
    }
    
    /**
     * Get number of documents
     *
     * @param collectionId collection ID
     * @return number of documents
     */
    public int getDocumentCount(String collectionId) {
        int toReturn = 0;
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id"};
        SocketMessage request = new SocketMessage("retriever", "document_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0421", "retrieval of number of topics wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0421", "retrieval of number of topics wasn't successful: coll_id=" + collectionId);
            }
        } else {
            toReturn = Tools.parseInt(response.getValue("count"));
        }

        return toReturn;
    }

    public TopicInfo[] getTopicList(String collectionId, String[] topicIds, int maxKeywords) {
        TopicInfo[] toReturn = new TopicInfo[0];
        if (null == collectionId || 0 == collectionId.length() || null == topicIds || 0 == topicIds.length || maxKeywords < 0) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String ID_DELIMITER = ","; // 20120710: used comma due to misunderstanding in message spec, but for conformance

        String[] paramFields = {"collection_id", "ids", "item_delimiter", "value_delimiter", "max_num_keywords"};
        SocketMessage request = new SocketMessage("retriever", "topic_list_id", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("ids", StringTool.arrayToString(topicIds, ID_DELIMITER));
        request.setValue("max_num_keywords", Integer.toString(maxKeywords));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0431", "retrieval of topics with given IDs wasn't successful: coll_id=" + collectionId
                        + "/topic_id=" + StringTool.arrayToString(topicIds, ","));
            } else {
                wrapError("APIL_0431", "retrieval of topics with given IDs wasn't successful: coll_id=" + collectionId
                        + "/topic_id=" + StringTool.arrayToString(topicIds, ","));
            }
        } else {
            List<Pair<String>> topicIdLabels = Tools.getPairListStr(response.getValue("topics"), ITEM_DELIMITER, VALUE_DELIMITER);
            String[] keywordsArray = StringTool.stringToArray(response.getValue("keywords"), ITEM_DELIMITER);
            String[] weightsArray = StringTool.stringToArray(response.getValue("weights"), ITEM_DELIMITER);
            String[] numDocsArray = StringTool.stringToArray(response.getValue("doc_counts"), ITEM_DELIMITER);
            String[] feedbacksArray = StringTool.stringToArray(response.getValue("feedback"), ITEM_DELIMITER);

            int itemCount = topicIdLabels.size();
            if (itemCount != keywordsArray.length || itemCount != weightsArray.length || itemCount != numDocsArray.length
                    || itemCount != feedbacksArray.length) {
                setError("APIL_0435", "error in parsing result message for topic info retrieval with given IDs: coll_id="
                        + collectionId + "/topic_id=" + StringTool.arrayToString(topicIds, ","));
                return toReturn;
            }

            toReturn = new TopicInfo[itemCount];
            for (int i = 0; i < toReturn.length; i++) {
                List<Pair<Integer>> keywordWeightList = new ArrayList<Pair<Integer>>();

                String keywordsDesc = keywordsArray[i];
                String weightsDesc = weightsArray[i];

                if (keywordsDesc.startsWith(":"))
                    keywordsDesc = " " + keywordsDesc;

                if (keywordsDesc.endsWith(":"))
                    keywordsDesc = keywordsDesc + " ";

                while (keywordsDesc.contains("::")) {
                    keywordsDesc = StringTool.replace(keywordsDesc, "::", ": :");
                }

                while (weightsDesc.contains("::")) {
                    weightsDesc = StringTool.replace(weightsDesc, "::", ": :");
                }

                String[] keywords = StringTool.stringToArray(keywordsDesc, VALUE_DELIMITER);
                String[] weights = StringTool.stringToArray(weightsDesc, VALUE_DELIMITER);

                if (keywords.length == weights.length) {
                    for (int x = 0; x < keywords.length; x++) {
                        if (" ".equals(keywords[x]) || " ".equals(weights[x]) || "NULL".equals(keywords[x]))
                            continue;
                        keywordWeightList.add(new Pair<Integer>(keywords[x], new Integer(Tools.parseInt(weights[x]))));
                    }
                } else {

                }

                Pair<String> idLabel = topicIdLabels.get(i);
                toReturn[i] = new TopicInfo(idLabel.key(), idLabel.value(), keywordWeightList, Tools.parseInt(numDocsArray[i]),
                        "y".equals(feedbacksArray[i]));
            }
        }

        return toReturn;
    }

    /**
     * Get searched list of topics with additional metadata.
     *
     * @param collectionId   collection ID
     * @param query          query string against topic label or keyword
     * @param offset         start position of index to get topics
     * @param numCount       number of topics to get
     * @param maxKeywords    maximum number of (related) keywords per topic
     * @param sortField      field for sorting list. (Use one in SortField class: ID, LABEL, DOC_COUNT)
     * @param sortDescending if set to true, sort result in descending order
     * @return list of TopicInfo objects
     */
    public TopicInfo[] searchTopicList(String collectionId, String query, int offset, int numCount, int maxKeywords,
                                       String sortField, boolean sortDescending) {
        TopicInfo[] toReturn = new TopicInfo[0];
        if (!SortField.ID.equals(sortField) && !SortField.LABEL.equals(sortField) && !SortField.DOC_COUNT.equals(sortField)) {
            setError("APIL_0101", "sort field's not valid: "
                    + (null == sortField ? "(null)" : (sortField.length() == 0 ? "(empty)" : sortField))
                    + " (allowed: ID, LABEL, DOC_COUNT)");
        }

        if (null == collectionId || 0 == collectionId.length() || query == null || 0 == query.length() || offset < 0
                || numCount <= 0 || maxKeywords < -1) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "query", "offset", "num_count", "item_delimiter", "value_delimiter",
                "max_num_keywords", "sort_field", "sort_descending"};
        SocketMessage request = new SocketMessage("retriever", "topic_list_query", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("query", query);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numCount));
        request.setValue("max_num_keywords", Integer.toString(maxKeywords));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("sort_field", sortField);
        request.setValue("sort_descending", sortDescending ? "y" : "n");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0441", "searching topics with given query wasn't successful: coll_id=" + collectionId + "/query="
                        + query);
            } else {
                wrapError("APIL_0441", "searching topics with given query wasn't successful: coll_id=" + collectionId + "/query="
                        + query);
            }
        } else {
            List<Pair<String>> topicIdLabels = Tools.getPairListStr(response.getValue("topics"), ITEM_DELIMITER, VALUE_DELIMITER);
            String[] keywordsArray = StringTool.stringToArray(response.getValue("keywords"), ITEM_DELIMITER);
            String[] weightsArray = StringTool.stringToArray(response.getValue("weights"), ITEM_DELIMITER);
            String[] numDocsArray = StringTool.stringToArray(response.getValue("doc_counts"), ITEM_DELIMITER);
            String[] feedbacksArray = StringTool.stringToArray(response.getValue("feedback"), ITEM_DELIMITER);

            int itemCount = topicIdLabels.size();
            if (itemCount != keywordsArray.length || itemCount != weightsArray.length || itemCount != numDocsArray.length
                    || itemCount != feedbacksArray.length) {
                setError("APIL_0445", "error in parsing result message for topic info search: coll_id=" + collectionId
                        + "/query=" + query);
                return toReturn;
            }

            toReturn = new TopicInfo[itemCount];
            for (int i = 0; i < toReturn.length; i++) {
                List<Pair<Integer>> keywordWeightList = new ArrayList<Pair<Integer>>();

                String keywordsDesc = keywordsArray[i];
                String weightsDesc = weightsArray[i];

                if (keywordsDesc.startsWith(":"))
                    keywordsDesc = " " + keywordsDesc;

                if (keywordsDesc.endsWith(":"))
                    keywordsDesc = keywordsDesc + " ";

                while (keywordsDesc.contains("::")) {
                    keywordsDesc = StringTool.replace(keywordsDesc, "::", ": :");
                }

                while (weightsDesc.contains("::")) {
                    weightsDesc = StringTool.replace(weightsDesc, "::", ": :");
                }

                String[] keywords = StringTool.stringToArray(keywordsDesc, VALUE_DELIMITER);
                String[] weights = StringTool.stringToArray(weightsDesc, VALUE_DELIMITER);

                if (keywords.length == weights.length) {
                    for (int x = 0; x < keywords.length; x++) {
                        if (" ".equals(keywords[x]) || " ".equals(weights[x]) || "NULL".equals(keywords[x]))
                            continue;
                        keywordWeightList.add(new Pair<Integer>(keywords[x], new Integer(Tools.parseInt(weights[x]))));
                    }
                } else {

                }

                Pair<String> idLabel = topicIdLabels.get(i);
                toReturn[i] = new TopicInfo(idLabel.key(), idLabel.value(), keywordWeightList, Tools.parseInt(numDocsArray[i]),
                        "y".equals(feedbacksArray[i]));
            }
        }

        return toReturn;
    }

    /**
     * Get searched number of topics
     *
     * @param collectionId collection ID
     * @param query        query string against topic label or keyword
     * @return number of topics
     */
    public int searchTopicCount(String collectionId, String query) {
        int toReturn = 0;
        if (null == collectionId || 0 == collectionId.length() || query == null || 0 == query.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "query"};
        SocketMessage request = new SocketMessage("retriever", "topic_count_query", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("query", query);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0445", "getting number of topics with given query wasn't successful: coll_id=" + collectionId
                        + "/query=" + query);
            } else {
                wrapError("APIL_0445", "getting number of topics with given query wasn't successful: coll_id=" + collectionId
                        + "/query=" + query);
            }
        } else {
            toReturn = Tools.parseInt(response.getValue("count"));
        }
        return toReturn;
    }

    ////////////////////////////////////////////////////////
    /// Repository Retrieval Methods (Custom Field)
    ////////////////////////////////////////////////////////

    /**
     * Get list of custom field analysis IDs in the given collection.
     * (shows lists which are registered in the server configuration file)
     *
     * @param collectionId collection id to find custom field analysis IDs
     * @return list of custom field analysis IDs in the collection
     */
    public String[] getCustomFieldAnalysisIds(String collectionId) {
        String[] toReturn = new String[0];
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }
        String[] paramFields = {"collection_id", "item_delimiter"};
        SocketMessage request = new SocketMessage("teasifter", "custom_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0451", "getting list of custom field analyis setting IDs wasn't successful: coll_id="
                        + collectionId);
            } else {
                wrapError("APIL_0451", "getting list of custom field analyis settings IDs wasn't successful: coll_id="
                        + collectionId);
            }
        } else {
            String[] idsArray = StringTool.stringToArray(response.getValue("custom_ids"), ITEM_DELIMITER);

            if (null != idsArray)
                toReturn = idsArray;
        }
        return toReturn;
    }

    /**
     * Get list of custom fields in the given collection.
     *
     * @param collectionId collection id to find custom field IDs
     * @return list of custom fields in the collection
     */
    public String[] getCustomFields(String collectionId) {
        String[] toReturn = new String[0];
        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "item_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "custom_field_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0461", "getting list of custom fields wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0461", "getting list of custom fields wasn't successful: coll_id=" + collectionId);
            }
        } else {
            toReturn = StringTool.stringToArray(response.getValue("fields"), ITEM_DELIMITER);
        }
        return toReturn;
    }

    /**
     * Get list of items in the given custom field.
     *
     * @param collectionId collection id
     * @param customField  custom field name given
     * @param offset       start point to read items
     * @param numItems     (maximum) number of items to get
     * @return list of custom field items in the collection
     */
    public String[] getCustomFieldItems(String collectionId, String customField, int offset, int numItems) {
        String[] toReturn = new String[0];
        if (null == collectionId || null == customField || 0 == collectionId.length() || 0 == customField.length() || 0 > offset
                || 0 >= numItems) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "custom_field_name", "offset", "num_count", "item_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "custom_field_item_list", SocketMessage.PriorityType.EMERGENCY,
                SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("custom_field_name", customField);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numItems));
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0471", "getting list of custom field values wasn't successful: coll_id=" + collectionId);
            } else {
                wrapError("APIL_0471", "getting list of custom field values wasn't successful: coll_id=" + collectionId);
            }
        } else {
            toReturn = StringTool.stringToArray(response.getValue("values"), ITEM_DELIMITER);
        }
        return toReturn;
    }

    /**
     * Get count of items in the given custom field.
     *
     * @param collectionId collection id
     * @param customField  custom field name given
     * @return count of custom field items in the collection
     */
    public int getCustomFieldItemsCount(String collectionId, String customField) {
        int toReturn = 0;
        if (null == collectionId || null == customField || 0 == collectionId.length() || 0 == customField.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "custom_field_name", "item_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "custom_field_item_count", SocketMessage.PriorityType.EMERGENCY,
                SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("custom_field_name", customField);
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0481", "getting number of unique custom field values wasn't successful: coll_id=" + collectionId
                        + "/custom_field=" + customField);
            } else {
                wrapError("APIL_0481", "getting number of unique custom field values wasn't successful: coll_id=" + collectionId
                        + "/custom_field=" + customField);
            }

        } else {
            toReturn = Tools.parseInt(response.getValue("count"));
        }
        return toReturn;
    }

    /**
     * Get custom field trend (time series) information of given analysis ID, custom field value, and keyword constraint(or topic ID, or without constraint for document)
     *
     * @param collectionId collection ID
     * @param analysisId   custom field analysis ID
     * @param fieldValue   custom field value
     * @param query        keyword or topic ID for constraint (follows analysis setting: KEYWORD or TOPIC. "" in the case of DOCUMENT)
     * @param beginDate    begin date (form of YYYYmmdd)
     * @param endDate      end date (form of YYYYmmdd)
     * @return (date_label, freq) pairs. date_label: YYYYmmdd form
     */
    public List<Pair<Integer>> getCustomTrend(String collectionId, String analysisId, String fieldValue, String query,
                                              String beginDate, String endDate) {
        return getCustomTrend(collectionId, analysisId, fieldValue, query, beginDate, endDate, IntervalType.MONTH);
    }

    /**
     * Get custom field trend (time series) information of given analysis ID, custom field value, and keyword constraint(or topic ID, or without constraint for document)
     *
     * @param collectionId collection ID
     * @param analysisId   custom field analysis ID
     * @param fieldValue   custom field value
     * @param query        keyword or topic ID for constraint (follows analysis setting: KEYWORD or TOPIC. "" in the case of DOCUMENT)
     * @param beginDate    begin date (form of YYYYmmdd)
     * @param endDate      end date (form of YYYYmmdd)
     * @param interval     type of trend interval (DAY|MONTH|YEAR)
     * @return (date_label, freq) pairs. date_label: YYYYmmdd form
     */
    public List<Pair<Integer>> getCustomTrend(String collectionId, String analysisId, String fieldValue, String query,
                                              String beginDate, String endDate, String interval) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        // query can be length of 0 (in the case of "DOCUMENT" as Frequency Type), so it's not included in wrong argument condition
        if (null == collectionId || null == analysisId || null == fieldValue || null == query || null == beginDate
                || null == endDate || 0 == collectionId.length() || 0 == analysisId.length() || 0 == fieldValue.length()
                || 0 == beginDate.length() || 0 == endDate.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (!IntervalType.DAY.equals(interval) && !IntervalType.MONTH.equals(interval) && !IntervalType.YEAR.equals(interval)) {
            setError("APIL_0165", "interval type's not valid: " + interval);
            return toReturn;
        }

        String[] paramFields = {"collection_id", "analysis_id", "field_value", "query", "date_begin", "date_end", "interval",
                "item_delimiter", "freq_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "custom_time_series", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("analysis_id", analysisId);
        request.setValue("field_value", fieldValue);
        request.setValue("query", query);
        request.setValue("date_begin", beginDate);
        request.setValue("date_end", endDate);
        request.setValue("interval", interval);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("freq_delimiter", FREQ_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0491", "getting custom trend wasn't successful: coll_id=" + collectionId + "/analysis_id="
                        + analysisId + "/field_value=" + fieldValue + "/query=" + query);
            } else {
                wrapError("APIL_0491", "getting custom trend wasn't successful: coll_id=" + collectionId + "/analysis_id="
                        + analysisId + "/field_value=" + fieldValue + "/query=" + query);
            }
        } else {
            toReturn = Tools.getPairListInt(response.getValue("time_series"), ITEM_DELIMITER, FREQ_DELIMITER);
        }

        return toReturn;
    }

    /**
     * Get trend (time series) information of given analysis ID and keyword constraint(or topic ID, or without constraint for document)
     * (It's general case without specific custom field. But setting for custom field analysis is required)
     *
     * @param collectionId collection ID
     * @param analysisId   custom field analysis ID
     * @param query        keyword or topic ID for constraint (follows analysis setting: KEYWORD or TOPIC. "" in the case of DOCUMENT)
     * @param beginDate    begin date (form of YYYYmmdd)
     * @param endDate      end date (form of YYYYmmdd)
     * @return (date_label, freq) pairs. date_label: YYYYmmdd form
     */
    public List<Pair<Integer>> getTrend(String collectionId, String analysisId, String query, String beginDate, String endDate) {
        return getTrend(collectionId, analysisId, query, beginDate, endDate, IntervalType.MONTH);
    }

    /**
     * Get trend (time series) information of given analysis ID and keyword constraint(or topic ID, or without constraint for document)
     * (It's general case without specific custom field. But setting for custom field analysis is required)
     *
     * @param collectionId collection ID
     * @param analysisId   custom field analysis ID
     * @param query        keyword or topic ID for constraint (follows analysis setting: KEYWORD or TOPIC. "" in the case of DOCUMENT)
     * @param beginDate    begin date (form of YYYYmmdd)
     * @param endDate      end date (form of YYYYmmdd)
     * @param interval     type of trend interval (DAY|MONTH|YEAR)
     * @return (date_label, freq) pairs. date_label: YYYYmmdd form
     */
    public List<Pair<Integer>> getTrend(String collectionId, String analysisId, String query, String beginDate, String endDate,
                                        String interval) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        if (null == collectionId || null == analysisId || null == query || null == beginDate || null == endDate
                || 0 == collectionId.length() || 0 == analysisId.length() || 0 == beginDate.length() || 0 == endDate.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (!IntervalType.DAY.equals(interval) && !IntervalType.MONTH.equals(interval) && !IntervalType.YEAR.equals(interval)) {
            setError("APIL_0165", "interval type's not valid: " + interval);
            return toReturn;
        }

        String[] paramFields = {"collection_id", "analysis_id", "query", "date_begin", "date_end", "interval", "item_delimiter",
                "freq_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "time_series", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("analysis_id", analysisId);
        request.setValue("query", query);
        request.setValue("date_begin", beginDate);
        request.setValue("date_end", endDate);
        request.setValue("interval", interval);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("freq_delimiter", FREQ_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0501", "getting trend wasn't successful: coll_id=" + collectionId + "/analysis_id=" + analysisId
                        + "/query=" + query);
            } else {
                wrapError("APIL_0501", "getting trend wasn't successful: coll_id=" + collectionId + "/analysis_id=" + analysisId
                        + "/query=" + query);
            }
        } else {
            toReturn = Tools.getPairListInt(response.getValue("time_series"), ITEM_DELIMITER, FREQ_DELIMITER);
        }
        return toReturn;
    }

    /**
     * Get custom field information (keyword|topic distribution) of given analysis ID and custom field value.
     *
     * @param collectionId collection ID
     * @param analysisId   custom field analysis ID
     * @param fieldValue   custom field value as query(constraint)
     * @param maxCount     top N count for items.
     * @return (keyword | topic_id, freq) pairs.
     */
    public List<Pair<Integer>> getCustomFieldInfoWithCount(String collectionId, String analysisId, String fieldValue, int maxCount) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();

        if (null == collectionId || null == analysisId || null == fieldValue || 0 >= maxCount || 0 == collectionId.length()
                || 0 == analysisId.length() || 0 == fieldValue.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "analysis_id", "field_value", "max_count", "date_begin", "date_end",
                "item_delimiter", "freq_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "custom_item_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("analysis_id", analysisId);
        request.setValue("field_value", fieldValue);
        request.setValue("max_count", Integer.toString(maxCount));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("freq_delimiter", FREQ_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0511", "getting keyword/topic distribution with given query wasn't successful: coll_id="
                        + collectionId + "/analysis_id=" + analysisId + "/field_vaule=" + fieldValue);
            } else {
                wrapError("APIL_0511", "getting keyword/topic distribution with given query wasn't successful: coll_id="
                        + collectionId + "/analysis_id=" + analysisId + "/field_vaule=" + fieldValue);
            }
        } else {
            toReturn = Tools.getPairListInt(response.getValue("values"), ITEM_DELIMITER, FREQ_DELIMITER);
        }
        return toReturn;
    }

    /**
     * Get top N items in the given custom field sorted by frequencies. (custom field retrieval)
     *
     * @param collectionId collection id
     * @param analysisId   custom field analysis ID
     * @param query        value constraint (can be given value in TOPIC or KEYWORD, according to custom field analysis settings)
     * @param maxCount     maximum number of items to get
     * @return (custom_field_item, freq) pairs
     */
    public List<Pair<Integer>> getCustomFieldItemsWithCount(String collectionId, String analysisId, String query, int maxCount) {
        List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
        if (null == collectionId || null == analysisId || null == query || 0 >= maxCount || 0 == collectionId.length()
                || 0 == analysisId.length() || 0 == query.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "analysis_id", "query", "max_count", "date_begin", "date_end",
                "item_delimiter", "freq_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "custom_value_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("analysis_id", analysisId);
        request.setValue("query", query);
        request.setValue("max_count", Integer.toString(maxCount));
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("freq_delimiter", FREQ_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0521", "getting custom field item distribution with given query wasn't successful: coll_id="
                        + collectionId + "/analysis_id=" + analysisId + "/query=" + query);
            } else {
                wrapError("APIL_0521", "getting custom field item distribution with given query wasn't successful: coll_id="
                        + collectionId + "/analysis_id=" + analysisId + "/query=" + query);
            }
        } else {
            toReturn = Tools.getPairListInt(response.getValue("values"), ITEM_DELIMITER, FREQ_DELIMITER);
        }

        return toReturn;

    }

    /**
     * Get top N authors who produced documents about given topic.
     *
     * @param collectionId collection ID
     * @param topicId      topic ID to query
     * @param count        N value
     * @return top N authors (unique values)
     */
    public String[] getTopAuthors(String collectionId, String topicId, int count) {
        String[] toReturn = new String[0];

        if (null == collectionId || null == topicId || 0 >= count || 0 == collectionId.length() || 0 == topicId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        List<Pair<Integer>> items = getCustomFieldItemsWithCount(collectionId, TeaGlobal.AUTHOR_TOPIC, topicId, count);

        // reset error code & message, because it arouse confusion in PK usage
        recentErrorCode = "";
        recentErrorMessage = "";
        recentServerErrorCode = "";
        recentServerErrorMessage = "";

        toReturn = new String[items.size()];
        for (int i = 0; i < items.size(); i++)
            toReturn[i] = items.get(i).key();

        return toReturn;
    }

    ////////////////////////////////////////////////////////
    /// User Feedback Methods
    ////////////////////////////////////////////////////////

    /**
     * Modify topic label.
     *
     * @param collectionId collection ID to apply
     * @param topicId      topic ID to modify
     * @param newLabel     new topic label
     * @return true if accepted.
     */
    public boolean modifyTopicLabel(String collectionId, String topicId, String newLabel) {
        final int labelSizeLimit = 100;
        String charsNotAllowed = "<>\\|'\"" + ITEM_DELIMITER + VALUE_DELIMITER + FREQ_DELIMITER + WEIGHT_DELIMITER;

        boolean toReturn = false;
        if (null == collectionId || null == topicId || null == newLabel || 0 == collectionId.length() || 0 == topicId.length()
                || 0 == newLabel.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (newLabel.length() > labelSizeLimit) {
            setError("APIL_0532", "label size cannot exceed " + labelSizeLimit + " characters");
            return toReturn;
        }

        //prohibit most special characters
        for (int i = 0; i < newLabel.length(); i++) {
            if (charsNotAllowed.indexOf(newLabel.charAt(i)) >= 0) {
                setError("APIL_0533", "label contains character which is not allowed: " + newLabel.charAt(i));
                return toReturn;
            }
        }

        String[] paramFields = {"collection_id", "topic_id", "new_topic"};
        SocketMessage request = new SocketMessage("roaster", "modify_topic_label", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);
        request.setValue("new_topic", newLabel);

		/* 
		�߸�� topic_id �� ���� ����
		<error>
			<code>TEAL_8453</code>
			<message>[request_id=201203082] Failed to create a process module by wrong value of a parameter(=topic_id)</message>
		</error>
		
		�������� �ʴ� topic_id �� ���� ����
		<error>
			<code>TEAL_4410</code>
			<message>Topic ID <1000> does not exist.</message>
		</error> 
		*/

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // TEAL_8164: topic ID range
            // TEAL_8453: topic ID's illegal
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0531", "failed in modifying topic label: coll_id=" + collectionId + "/topic_id=" + topicId
                        + "/label=" + newLabel);
            } else if ("TEAL_8164".equals(response.getErrorCode()) || "TEAL_8453".equals(response.getErrorCode())) {
                setError("APIL_0535", "failed in modifying topic label (wrong topic ID): coll_id=" + collectionId + "/topic_id="
                        + topicId + "/label=" + newLabel);
            } else {
                wrapError("APIL_0531", "failed in modifying topic label: coll_id=" + collectionId + "/topic_id=" + topicId
                        + "/label=" + newLabel);
            }
        } else {
            toReturn = true;
        }

        return toReturn;
    }

    //	/**
    //	 * Add a new topic.
    //	 * @param collectionId collection ID to apply
    //	 * @param topicId topic ID to add
    //	 * @param label topic label
    //	 * @return true if successful
    //	 */
    //	public boolean addTopic(String collectionId, String topicId, String label)
    //	{
    //		boolean toReturn = false;
    //		if (null == collectionId || null == topicId || null == label
    //				|| 0 == collectionId.length() || 0 == topicId.length() || 0 == label.length())
    //		{
    //			setError("APIL_0100", "argument's not valid.");
    //			return toReturn;
    //		}
    //
    //		if (DUMMY)
    //		{
    //			return toReturn;
    //		}
    //		String [] paramFields = {"collection_id", "topic_id", "topic_label"};
    //		SocketMessage request = new SocketMessage("roaster", "topic_add", PriorityType.EMERGENCY, TransferType.BI_WAY, "", paramFields);
    //		request.setValue("collection_id", collectionId);
    //		request.setValue("topic_id", topicId);
    //		request.setValue("topic_label", label);
    //
    //		SocketMessage response = handleMessage(request);
    //		if (!isSuccessful(response))
    //		{
    //			if ("".equals(response.getErrorCode()))
    //			{
    //				setError("APIL_0541", "failed in adding a new topic: "
    //						+ collectionId + "/" + topicId + "/" + label);
    //			}
    //		}
    //		else
    //		{
    //			toReturn = true;
    //		}
    //
    //		return toReturn;
    //	}
    //
    //	/**
    //	 * Remove topic.
    //	 * @param collectionId collection ID to apply
    //	 * @param topicId topic ID to delete
    //	 * @return true if successful
    //	 */
    //	public boolean removeTopic(String collectionId, String topicId)
    //	{
    //		boolean toReturn = false;
    //		if (null == collectionId || null == topicId
    //				|| 0 == collectionId.length() || 0 == topicId.length())
    //		{
    //			setError("APIL_0100", "argument's not valid.");
    //			return toReturn;
    //		}
    //
    //		if (DUMMY)
    //		{
    //			return toReturn;
    //		}
    //		String [] paramFields = {"collection_id", "topic_id"};
    //		SocketMessage request = new SocketMessage("roaster", "topic_remove", PriorityType.EMERGENCY, TransferType.BI_WAY, "", paramFields);
    //		request.setValue("collection_id", collectionId);
    //		request.setValue("topic_id", topicId);
    //
    //		SocketMessage response = handleMessage(request);
    //		if (!isSuccessful(response))
    //		{
    //			if ("".equals(response.getErrorCode()))
    //			{
    //				setError("APIL_0551", "failed in removing a topic: " + collectionId + "/" + topicId);
    //			}
    //		}
    //		else
    //		{
    //			toReturn = true;
    //		}
    //
    //		return toReturn;
    //	}

    /**
     * Get feedback keywords list of given topic.
     *
     * @param collectionId collection ID to get feedbacks
     * @param topicId      topic ID to get feedbacks
     * @param isPositive   true if they're positive feedbacks
     * @return array of feedback keywords
     */
    public String[] getFeedbackKeywords(String collectionId, String topicId, boolean isPositive) {
        String[] toReturn = new String[0];
        if (null == collectionId || null == topicId || 0 == collectionId.length() || 0 == topicId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "topic_id", "is_positive", "item_delimiter"};
        SocketMessage request = new SocketMessage("roaster", "feedback_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);
        request.setValue("is_positive", isPositive ? "y" : "n");
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0561", "failed in getting feedback keywords: coll_id=" + collectionId + "/topic_id=" + topicId
                        + " (" + (isPositive ? "positive" : "negative") + ")");
            } else {
                wrapError("APIL_0561", "failed in getting feedback keywords: coll_id=" + collectionId + "/topic_id=" + topicId
                        + " (" + (isPositive ? "positive" : "negative") + ")");
            }
        } else {
            toReturn = StringTool.stringToArray(response.getValue("keywords"), ITEM_DELIMITER);
        }

        return toReturn;
    }

    /**
     * Set feedback keyword list of given topic.
     *
     * @param collectionId collection ID to set feedbacks
     * @param topicId      topic ID to set feedbacks
     * @param keywords     keywords to set as feedbacks (array of size 0 for reset keywords)
     * @param isPositive   isPositive true if feedbacks're positive feedbacks
     * @return true if successful.
     */
    public boolean setFeedbackKeywords(String collectionId, String topicId, String[] keywords, boolean isPositive) {
        final int maxFeedbackCount = 20;
        final int minWordSize = 2;
        final int maxWordSize = 50;

        boolean toReturn = false;
        if (null == collectionId || null == topicId || 0 == collectionId.length() || 0 == topicId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (keywords == null) {
            keywords = new String[]{};
        }

        //allowed word count: maxFeedbackCount
        if (keywords.length > maxFeedbackCount) {
            setError("APIL_0572", "feedbacks cannot exceed " + maxFeedbackCount + " keywords");
            return toReturn;
        }

        for (int i = 0; i < keywords.length; i++) {
            String word = keywords[i];
            if (word == null) {
                setError("APIL_0573", "out of feedback keyword length range (" + minWordSize + " ~ " + maxWordSize
                        + "): NULL string");
                return toReturn;
            } else if (word.length() < minWordSize || word.length() > maxWordSize) {
                setError("APIL_0573",
                        "out of feedback keyword length range (" + minWordSize + " ~ " + maxWordSize + "): " + word.length()
                                + " characters");
                return toReturn;
            }
        }

        String[] paramFields = {"collection_id", "topic_id", "keywords", "is_positive", "item_delimiter"};
        SocketMessage request = new SocketMessage("roaster", "feedback_set", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("topic_id", topicId);
        request.setValue("keywords", StringTool.arrayToString(keywords, ITEM_DELIMITER));
        request.setValue("is_positive", isPositive ? "y" : "n");
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            String termsNotRegistered = response.getValue("terms_not_registered");
            String termsFailed = response.getValue("terms_failed");

            StringBuffer resultFeedback = new StringBuffer();
            if (0 != termsNotRegistered.length()) {
                resultFeedback.append(" NOT_REGISTERED{");
                resultFeedback.append(termsNotRegistered);
                resultFeedback.append("}");
            }
            if (0 != termsFailed.length()) {
                resultFeedback.append(" FAILED{");
                resultFeedback.append(termsFailed);
                resultFeedback.append("}");
            }

            if ("".equals(response.getErrorCode())) {
                setError("APIL_0571", "failed in setting feedback keywords: coll_id=" + collectionId + "/topic_id=" + topicId
                        + " (" + (isPositive ? "positive" : "negative") + ")");
            } else {
                if (0 != resultFeedback.length())
                    wrapError("APIL_0575", "failed in setting some of feedback keywords: coll_id=" + collectionId + "/topic_id="
                            + topicId + " (" + (isPositive ? "positive" : "negative") + ")" + resultFeedback.toString());
                else
                    wrapError("APIL_0571", "failed in setting feedback keywords: coll_id=" + collectionId + "/topic_id="
                            + topicId + " (" + (isPositive ? "positive" : "negative") + ")");
            }
        } else {
            toReturn = true;
        }

        return toReturn;
    }

    ////////////////////////////////////////////////////////
    /// Dictionary Manipulation, Etc.
    ////////////////////////////////////////////////////////

	/*
	 * Get list of dictionaries
	 * @return array of wrappers for dictionary item
	 */
	/*public DictionaryInfo[] getDictionaryList()
	{
		DictionaryInfo[] toReturn = new DictionaryInfo [0];
		
		if (DUMMY)
		{
			toReturn = new DictionaryInfo[2];
			toReturn[0] = new DictionaryInfo("global_black", "blackwords.txt", true);
			toReturn[1] = new DictionaryInfo("global_white", "whitewords.txt", false);
			return toReturn;
		}
		
		return toReturn;
	}*/

    /**
     * Get content of dictionary
     *
     * @param dicId dictionary ID to get
     * @return content of dictionary
     */
    public String[] getDictionary(String dicId) {
        String[] toReturn = new String[0];
        if (null == dicId || 0 == dicId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (!TeaGlobal.BLACKWORD_ID.equals(dicId) && !TeaGlobal.WHITEWORD_ID.equals(dicId)) {
            setError("APIL_0151", "dictionary ID's not valid: dic_id=" + dicId + " (allowed: " + TeaGlobal.BLACKWORD_ID + ","
                    + TeaGlobal.WHITEWORD_ID + ")");
            return toReturn;
        }

        String[] paramFields = {"dic_id"};
        SocketMessage request = new SocketMessage("extractor", "dic_content", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("dic_id", dicId);
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0581", "failed in getting dictionary: dic_id=" + dicId);
            } else {
                wrapError("APIL_0581", "failed in getting dictionary: dic_id=" + dicId);
            }
        } else {
            toReturn = StringTool.stringToArray(response.getValue("content"), "\n");
        }

        return toReturn;
    }

    /**
     * Request distribution of dictionary.
     * request for entire servers to change contents of dictionary with given dictionary content.
     *
     * @param dicId dictionaryId
     * @param terms dictionary terms to be replaced
     * @return request ID (not "" if successful)
     */
    public String requestDistributeDictionary(String dicId, String[] terms) {
        String toReturn = "";
        if (null == dicId || null == terms || 0 == dicId.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        if (!TeaGlobal.BLACKWORD_ID.equals(dicId) && !TeaGlobal.WHITEWORD_ID.equals(dicId)) {
            setError("APIL_0151", "dictionary ID's not valid: dic_id=" + dicId + " (allowed: " + TeaGlobal.BLACKWORD_ID + ","
                    + TeaGlobal.WHITEWORD_ID + ")");
            return toReturn;
        }

        String[] paramFields = {"dic_id", "content"};
        SocketMessage request = new SocketMessage("extractor", "dic_dist", SocketMessage.PriorityType.NORMAL, SocketMessage.TransferType.SINGLE_WAY, "",
                paramFields);
        request.setValue("dic_id", dicId);
        StringBuffer toSend = new StringBuffer();
        toSend.append("<![CDATA[");
        for (int i = 0; i < terms.length; i++) {
            toSend.append(terms[i]);
            toSend.append('\n');
        }
        toSend.append("]]>");
        request.setValue("content", toSend.toString());

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0591", "failed in request for distribution of dictionary: dic_id=" + dicId);
            } else {
                wrapError("APIL_0591", "failed in request for distribution of dictionary: dic_id=" + dicId);
            }
        } else {
            toReturn = response.getRequestId();
        }

        return toReturn;
    }

    /**
     * Get a metadata group item with given ID.
     *
     * @param collectionId Collection ID
     * @param groupId      Group ID
     * @param id           id of given metadata group item
     * @return wrapper object for metadata group item
     */
    public MetadataGroup getMetadataGroup(String collectionId, String groupId, String id) {
        MetadataGroup toReturn = new MetadataGroup(id);
        if (null == collectionId || null == groupId || null == id || 0 == collectionId.length() || 0 == groupId.length()
                || 0 == id.length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "group_id", "id", "item_delimiter"};
        SocketMessage request = new SocketMessage("retriever", "get_metadata_grp", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY,
                "", paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("group_id", groupId);
        request.setValue("id", id);
        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0601", "failed in getting a metadata group item: coll_id=" + collectionId + "/group_id=" + groupId
                        + "/id=" + id);
            } else {
                wrapError("APIL_0601", "failed in getting a metadata group item: coll_id=" + collectionId + "/group_id="
                        + groupId + "/id=" + id);
            }
        } else {
            String date = response.getValue("date");
            toReturn = new MetadataGroup(id, date);

            String fieldsString = Tools.padEmptyItem(response.getValue("fields"), ITEM_DELIMITER);
            String valuesString = Tools.padEmptyItem(response.getValue("values"), ITEM_DELIMITER);

            String[] fields = StringTool.stringToArray(fieldsString, ITEM_DELIMITER);
            String[] values = StringTool.stringToArray(valuesString, ITEM_DELIMITER);

            if (fields.length != values.length) {
                setError("APIL_0602", "failed in parsing message in metadata group item retrieval: coll_id=" + collectionId
                        + "/group_id=" + groupId + "/id=" + id);
                return toReturn;
            }

            for (int i = 0; i < fields.length; i++) {
                if (" ".equals(fields[i]))
                    continue;
                else if (" ".equals(values[i]))
                    toReturn.setValue(fields[i], "");
                else
                    toReturn.setValue(fields[i], values[i]);
            }
        }
        return toReturn;
    }

    /**
     * Set a metadata group item with given content.
     *
     * @param collectionId Collection ID
     * @param groupId      Group ID
     * @param item         item content to set. (ID must be specified)
     * @return true if modification's successful
     */
    public boolean setMetadataGroup(String collectionId, String groupId, MetadataGroup item) {
        boolean toReturn = false;

        if (null == collectionId || null == groupId || null == item || null == item.getId() || 0 == collectionId.length()
                || 0 == groupId.length() || 0 == item.getId().length()) {
            setError("APIL_0100", "argument's not valid.");
            return toReturn;
        }

        String[] paramFields = {"collection_id", "group_id", "id", "date", "fields", "values", "item_delimiter"};
        SocketMessage request = new SocketMessage("roaster", "set_metadata_grp", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("collection_id", collectionId);
        request.setValue("group_id", groupId);
        request.setValue("id", item.getId());
        request.setValue("date", item.getDate());

        List<Pair<String>> fieldValues = item.getFieldValues();
        String[] fields = new String[fieldValues.size()];
        String[] values = new String[fieldValues.size()];

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fieldValues.get(i).key();
            values[i] = fieldValues.get(i).value();
        }
        request.setValue("fields", StringTool.arrayToString(fields, ITEM_DELIMITER));
        request.setValue("values", StringTool.arrayToString(values, ITEM_DELIMITER));

        request.setValue("item_delimiter", ITEM_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            if ("".equals(response.getErrorCode())) {
                setError("APIL_0611", "failed in setting a metadata group item: coll_id=" + collectionId + "/group_id=" + groupId
                        + "/id=" + item.getId());
            } else {
                wrapError("APIL_0611", "failed in setting a metadata group item: coll_id=" + collectionId + "/group_id="
                        + groupId + "/id=" + item.getId());
            }
        } else {
            toReturn = true;
        }

        return toReturn;
    }

    /**
     * Inner Class for Sort Field
     *
     * @author hkseo@wisenut.co.kr
     */
    public class SortField {
        public static final String ID = "ID";
        public static final String LABEL = "LABEL";
        public static final String DOC_COUNT = "DOC_COUNT";
    }

    /**
     * Inner Class for Interval Type in Custom Field Analysis Settings
     *
     * @author hkseo@wisenut.co.kr
     */
    public class IntervalType {
        public static final String DAY = "DAY";
        public static final String MONTH = "MONTH";
        public static final String YEAR = "YEAR";
    }

	/*
	 * 연관키워드 관리를 위한 TEA Client Java API
	 *   + 2013. 07. 11
	 */

    /**
     * 현재 리스너에서 관리되고 있는 TEA Server 목록을 반환
     *
     * @return
     */
    public List<Server> getTeaServers() {
        List<Server> serverList = new ArrayList<Server>();

        String[] paramFields = {"item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("admin", "server_id_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0711";
            String errorMessage = "Failed in getting server list. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            String serverIdsStr = response.getValue("server_ids");
            String[] serverIdsArray = serverIdsStr.split("[" + ITEM_DELIMITER + "]");
            for (String serverIdInfo : serverIdsArray) {
                String[] serverIdsInfoArray = null;
                if (serverIdInfo != null) {
                    serverIdsInfoArray = serverIdInfo.split("[" + VALUE_DELIMITER + "]");

                    if (serverIdsInfoArray.length == 3) {
                        serverList.add(new Server(serverIdsInfoArray[0], serverIdsInfoArray[1], Integer.parseInt(serverIdsInfoArray[2])));
                    }
                }
            }
        }

        return serverList;
    }

    /**
     * 연관키워드 전체 건수 조회 (컬렉션, 키워드 검색 포함)
     *
     * @param teaServerId
     * @param enable
     * @param query
     * @return
     */
    public int searchRelativeKeywordTotalCount(
            String teaServerId, KeywordEnables enable,
            String keywordViewType, String keywordHistType,
            String query) {
        int totalCount = 0;

        String[] paramFields = {"server_id", "collection_id", "query", "item_delimiter", "value_delimiter", "enable", "modified", "history"};
        SocketMessage request = new SocketMessage("rke_retriever", "relation_keyword_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("query", query);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("enable", enable.toString());

        request.setValue("modified", keywordViewType);
        if (!keywordHistType.equals(HISTORY_ALL)) {
            request.setValue("history", keywordHistType);
        }

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0712";
            String errorMessage = "Failed in relation keyword count inquiry. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            totalCount = Tools.parseInt(response.getValue("count"));
        }

        return totalCount;
    }

    /**
     * 연관키워드 목록 조회 (컬렉션, 키워드 검색 포함)
     *
     * @param teaServerId
     * @param enable
     * @param query
     * @param offset
     * @param numCount
     * @param sortField
     * @param sortDescending
     * @return
     */
    public RelativeKeywordInfo[] searchRelativeKeywordList(String teaServerId, KeywordEnables enable,
                                                           String keywordViewType, String keywordHistType,
                                                           String query, int offset, int numCount, String sortField, boolean sortDescending) {
        RelativeKeywordInfo[] list = new RelativeKeywordInfo[0];

        String[] paramFields0 = {"server_id", "collection_id", "query"
                , "item_delimiter", "value_delimiter"
                , "offset", "num_count", "enable"
                , "sort_field", "sort_descending"
                , "modified", "history"
        };

        String[] paramFields1 = {"server_id", "collection_id", "query"
                , "item_delimiter", "value_delimiter"
                , "offset", "num_count", "enable"
                , "sort_field", "sort_descending"
                , "modified", "history"
        }; // 서버에서 history="" 와 같이 처리 안되었다고 해서(한규열) 분리함

        String[] paramFields = new String[]{};

        if (keywordHistType.equals(HISTORY_ALL)) {
            paramFields = paramFields0;
        } else {
            paramFields = paramFields1;
        }
        SocketMessage request = new SocketMessage("rke_retriever", "relation_keyword_list", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("query", query);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numCount));
        request.setValue("enable", enable.toString());
        request.setValue("sort_field", "");
        request.setValue("sort_descending", sortDescending ? "y" : "n");
        request.setValue("modified", keywordViewType);
        if (!keywordHistType.equals(HISTORY_ALL)) {
            request.setValue("history", keywordHistType);
        }

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0713";
            String errorMessage = "Failed in relation keyword list inquiry. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            List<Pair<String>> relationList = Tools.getRelationPairList(response.getValue("relation"), ITEM_DELIMITER, VALUE_DELIMITER);
            String[] enableList = StringTool.stringToArray(response.getValue("enable"), ITEM_DELIMITER);
            String[] modifiedList = StringTool.stringToArray(response.getValue("modified"), ITEM_DELIMITER);
            String[] historyList = StringTool.stringToArray(response.getValue("history"), ITEM_DELIMITER);
            String[] modifiedDateList = StringTool.stringToArray(response.getValue("modified_date"), ITEM_DELIMITER);
            String[] registeredDateList = StringTool.stringToArray(response.getValue("registered_date"), ITEM_DELIMITER);
            String[] lockedList = StringTool.stringToArray(response.getValue("locked_info"), ITEM_DELIMITER);

            int relationCount = relationList.size();
            list = new RelativeKeywordInfo[relationCount];
            for (int i = 0; i < relationCount; i++) {
                boolean isModified = modifiedList[i].equals("1") ? true : false;
                boolean isExcepted = enableList[i].equals("1") ? true : false;
                boolean isLocked = lockedList[i].equals("1") ? true : false;

                String regDate = registeredDateList[i];
                String modDate = modifiedDateList[i];
                String history = historyList[i];

                if (regDate != null) {
                    regDate = regDate.trim();
                }
                if (modDate != null) {
                    modDate = modDate.trim();
                }
                if (history != null) {
                    history = history.trim();
                }
                list[i] = new RelativeKeywordInfo(
                        relationList.get(i).key(),
                        relationList.get(i).value(),
                        "", "", "",
                        isModified, isExcepted, isLocked,
                        regDate, modDate, history);
            }
        }

        return list;
    }

    /**
	 * 임의의 연관키워드 상세 정보 조회
	 *
	 * @param teaServerId
	 * @param mainKeyword
	 * @return
	 */
    public RelativeKeywordInfo getRelativeKeywordMetaInfo(String teaServerId, String mainKeyword) {
        RelativeKeywordInfo relativeKeywordInfo = null;

        String[] paramFields = {"server_id", "collection_id", "keyword", "item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("rke_retriever", "relation_keyword", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("keyword", mainKeyword);
        request.setValue("item_delimiter", ",");
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0714";
            String errorMessage = "Failed in relation keyword meta info inquiry. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            String keywords = response.getValue("keywords");
            String positive = response.getValue("positive");
            String negative = response.getValue("negative");
            String history = response.getValue("previous_keywords");
            String createDate = response.getValue("registered_time");
            String modifyDate = response.getValue("modified_time");
            String keywordHistType = response.getValue("history");
            relativeKeywordInfo = new RelativeKeywordInfo(mainKeyword, keywords, positive, negative, history, createDate, modifyDate, keywordHistType);
        }

        String[] collectionIds = getCollectionIds(teaServerId);
        for (String collectionId : collectionIds) {
            relativeKeywordInfo.addCollectionId(collectionId);
        }

        return relativeKeywordInfo;
    }

    /**
     * 임의의 대표 키워드에 해당하는 연관키워드 정보를 컬렉션별로 반환
     *
     * @param teaServerId
     * @param collectionId
     * @param mainKeyword
     * @return
     */
    public String getRelativeKeywordByCollection(String teaServerId, String collectionId, String mainKeyword) {
        String relativeKeywords = "";

        String[] paramFields = {"server_id", "collection_id", "keyword", "item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("rke_retriever", "relation_keyword", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("keyword", mainKeyword);
        request.setValue("item_delimiter", ",");
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0714";
            String errorMessage = "Failed in relation keyword meta info inquiry. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                if (response.getErrorCode().equals("TEAL_13403")) {
					/* 해당 컬렉션의 연관키워드 분석이 수행되지 않은 케이스 */
                    errorCode = "APIL_0718";
                    errorMessage = response.getErrorMessage() + " (collection id=" + collectionId + ")";
                }
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            relativeKeywords = response.getValue("keywords");
        }

        return relativeKeywords;
    }

    /**
     * 신규 연관키워드 정보를 등록.
     *
     * @param teaServerId
     * @param mainKeyword
     * @param relativeKeywords
     * @return
     */
    public boolean setRelativeKeywordMetaInfo(String teaServerId, String mainKeyword, String relativeKeywords) {
        boolean result = true;

        if (result) {
            if (mainKeyword == null || mainKeyword.equals("")) {
                result = false;
                setError("APIL_0723", "Null or empty keyword.");
            } else {
				/* 대표키워드의 길이 허용 범위 체크 */
                if (mainKeyword.length() < 2 || mainKeyword.length() > 50) {
                    result = false;
                    setError("APIL_0721", "Out of keyword length range. (2 ~ 50) {" + mainKeyword + "}");
                }

                if (result && containsSpecialChars(mainKeyword)) {
                    result = false;
                    setError("APIL_0724", "Can not allow special character in keyword. {" + mainKeyword + "}");
                }
            }
        }

        if (result) {
            if (relativeKeywords == null || relativeKeywords.equals("")) {
                result = false;
                setError("APIL_0723", "Null or empty keyword.");
            } else {
                String[] keywords = relativeKeywords.split(",");
                for (String relativeKeyword : keywords) {
					/* 연관키워드의 길이 허용 범위 체크 */
                    if (relativeKeyword.length() > 50) {
                        result = false;
                        setError("APIL_0721", "Out of keyword length range. (2 ~ 50) {" + relativeKeyword + "}");
                        break;
                    }
					
					/* 연관키워드에 특수문자 포함 여부 체크 */
                    if (containsSpecialChars(relativeKeyword)) {
                        result = false;
                        setError("APIL_0724", "Can not allow special character in keywords. {" + relativeKeyword + "}");
                        break;
                    }
                }
            }
        }
		
		/* 키워드 등록 중복 체크 */
        if (result) {
            if (isAlreadyExistKeyword(teaServerId, mainKeyword)) {
                result = false;
                setError("APIL_0722", "Already exist keyword. {" + mainKeyword + "}");
            } else {
                RelativeKeywordInfo relativeKeywordInfo = new RelativeKeywordInfo(mainKeyword, "", relativeKeywords, "", "", "", "", "");
                result = modifyRelativeKeywordMetaInfo(teaServerId, relativeKeywordInfo);
            }
        }

        return result;
    }

    /**
     * 입력한 키워드가 이미 등록되어 있는지 여부를 체크. (true:등록, false:미등록)
     *
     * @param teaServerId
     * @param mainKeyword
     * @return
     */
    private boolean isAlreadyExistKeyword(String teaServerId, String mainKeyword) {
        boolean result = false;

        String[] paramFields = {"server_id", "collection_id", "keyword", "item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("rke_retriever", "exist_relation_keyword", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("keyword", mainKeyword);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            result = false;
        } else {
            // 반환 결과 처리
            String isExist = response.getValue("exist");
            if (isExist == null) {
                result = false;
            } else {
                result = isExist.equals("y") ? true : false;
            }
        }

        return result;
    }

    /**
     * 임의의 연관키워드 정보 수정
     *
     * @param teaServerId
     * @param relativeKeywordInfo
     * @return
     */
    public boolean modifyRelativeKeywordMetaInfo(String teaServerId, RelativeKeywordInfo relativeKeywordInfo) {
        boolean result = true;

        String[] positiveKeywords = null;
        String[] negativeKeywords = null;
		
		/* Check constraint of positive keyword */
        if (result) {
            String positive = relativeKeywordInfo.getPositiveKeyword();
            if (positive != null && !positive.equals("")) {
                positiveKeywords = positive.split(",");

                if (positiveKeywords != null) {
					/* 긍정키워드 입력 갯수가 20개 초과인 경우 에러 */
                    if (positiveKeywords.length > 20) {
                        result = false;
                        setError("APIL_0702", "Feedbacks(positive) can not exceed N keywords. {Count:"
                                + positiveKeywords.length + "}");
                    } else {
                        for (String positiveKeyword : positiveKeywords) {
							/* 긍정키워드가 50자 이상인 경우 에러 */
                            if (positiveKeyword.length() > 50) {
                                result = false;
                                setError("APIL_0701", "Out of feedback(positive) keyword length range. (2 ~ 50)" +
                                        " {" + positiveKeyword + "}");
                                break;
                            }
							
							/* 각 긍정키워드 내에 특수문자(공백제외)가 포함되어 있는 경우 에러 */
                            if (containsSpecialChars(positiveKeyword)) {
                                result = false;
                                setError("APIL_0703", "Can not allow special character in keywords." +
                                        " {" + positiveKeyword + "}");
                                break;
                            }
                        }
                    }
                }
            } else {
                relativeKeywordInfo.setPositiveKeyword("");
            }
        }
		
		/* Check constraints of negative keyword */
        if (result) {
            String negative = relativeKeywordInfo.getNegativeKeyword();
            if (negative != null && !negative.equals("")) {
                negativeKeywords = negative.split(",");

                if (negativeKeywords != null) {
					/* 부정키워드 입력 갯수가 20개 초과인 경우 에러 */
                    if (negativeKeywords.length > 20) {
                        result = false;
                        setError("APIL_0702", "Feedbacks(negative) can not exceed N keywords. {Count:"
                                + negativeKeywords.length + "}");
                    } else {
                        for (String negativeKeyword : negativeKeywords) {
							/* 부정키워드가 50자 이상인 경우 에러 */
                            if (negativeKeyword.length() > 50) {
                                result = false;
                                setError("APIL_0701", "Out of feedback(negative) keyword length range. (2 ~ 50)" +
                                        " {" + negativeKeyword + "}");
                                break;
                            }
							
							/* 각 긍정키워드 내에 특수문자(공백제외)가 포함되어 있는 경우 에러 */
                            if (containsSpecialChars(negativeKeyword)) {
                                result = false;
                                setError("APIL_0703", "Can not allow special character in keywords." +
                                        " {" + negativeKeyword + "}");
                                break;
                            }
                        }
                    }
                }
            } else {
                relativeKeywordInfo.setNegativeKeyword("");
            }
        }
		
		/* 긍/부정키워드에 서로 중복되는 키워드 등록 여부 검사 */
        if (result) {
            if (positiveKeywords != null && negativeKeywords != null) {
                Map<String, Integer> duplicateCheckMap = new HashMap<String, Integer>();

                List<String> duplicateKeywords = new ArrayList<String>();

                for (String positiveKeyword : positiveKeywords) {
                    if (duplicateCheckMap.containsKey(positiveKeyword)) {
                        duplicateCheckMap.put(positiveKeyword, duplicateCheckMap.get(positiveKeyword) + 1);
                    } else {
                        duplicateCheckMap.put(positiveKeyword, 1);
                    }
                }

                for (String negativeKeyword : negativeKeywords) {
                    if (duplicateCheckMap.containsKey(negativeKeyword)) {
                        duplicateCheckMap.put(negativeKeyword, duplicateCheckMap.get(negativeKeyword) + 1);
                    } else {
                        duplicateCheckMap.put(negativeKeyword, 1);
                    }
                }
				
				/* 긍/부정키워드 사이에 중복되는 키워드가 존재하는 경우 에러 */
                Iterator<String> keyList = duplicateCheckMap.keySet().iterator();
                while (keyList.hasNext()) {
                    String keyword = keyList.next();
                    int count = duplicateCheckMap.get(keyword);

                    if (count > 1) {
                        duplicateKeywords.add(keyword);
                    }
                }

                if (duplicateKeywords.size() > 0) {
                    result = false;
                    setError("APIL_0704", "Can not regist duplicate keyword. {" + concat(duplicateKeywords, ",") + "}");
                }
            }
        }
		
		/* 연관키워드 엔트리 정보 업데이트 */
        if (result) {
            String[] paramFields = {"server_id", "collection_id", "keyword", "positive", "negative", "item_delimiter", "value_delimiter"};
            SocketMessage request = new SocketMessage("rke_retriever", "modify_relation_keyword", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
            request.setValue("server_id", teaServerId);
            request.setValue("collection_id", "");
            request.setValue("keyword", relativeKeywordInfo.getMainKeyword());
            request.setValue("positive", relativeKeywordInfo.getPositiveKeyword());
            request.setValue("negative", relativeKeywordInfo.getNegativeKeyword());
            request.setValue("item_delimiter", ITEM_DELIMITER);
            request.setValue("value_delimiter", VALUE_DELIMITER);

            SocketMessage response = handleMessage(request);
            if (!isSuccessful(response)) {
                // 예외 처리
                String errorCode = "APIL_0715";
                String errorMessage = "Failed in relation keyword regist or update. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
                if (response.getErrorCode().equals("")) {
                    setError(errorCode, errorMessage);
                } else {
                    wrapError(errorCode, errorMessage);
                }
            } else {
                result = true;
            }
        }

        return result;
    }

    /**
     * 연관키워드 제외 및 복구 처리
     *
     * @param teaServerId
     * @param relativeKeywordList
     * @param isExcept
     * @return
     */
    public boolean setRelativeKeywordExceptStatus(String teaServerId, String[] relativeKeywordList, boolean isExcept) {
        boolean result = true;

        String[] paramFields = {"server_id", "port", "collection_id", "item_delimiter", "value_delimiter", "keyword_list", "enable"};
        SocketMessage request = new SocketMessage("rke_retriever", "set_service_status", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("keyword_list", Tools.connectByDelimiter(relativeKeywordList, ","));
        request.setValue("enable", isExcept ? "0" : "1");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0716";
            String errorMessage = "Failed in relation keyword except stauts change. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 반환 결과 처리
        }

        return result;
    }

    /**
     * 연관키워드를 물리적으로 완전히 삭제
     *
     * @param teaServerId
     * @param relativeKeywordList
     * @return
     */
    public boolean deleteRelativeKeyword(String teaServerId, String[] relativeKeywordList) {
        boolean result = true;

        String[] paramFields = {"server_id", "collection_id", "item_delimiter", "value_delimiter", "keyword_list", "enable"};
        SocketMessage request = new SocketMessage("rke_retriever", "remove_relation_keyword",
                SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("keyword_list", Tools.connectByDelimiter(relativeKeywordList, ","));

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0717";
            String errorMessage = "Failed in relation keyword remove. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 반환 결과 처리
        }

        return result;
    }

    /**
     * 문자열로 부터 특수문자 포함여부를 체크
     *
     * @param src
     * @return 특수문자 포함된 경우 true, 그렇지 않은 경우 false
     * @Method containsSpecialChars
     */
    private boolean containsSpecialChars(String src) {
        boolean result = false;

        if (src != null && !src.equals("")) {
            char[] chars = src.toCharArray();

            for (int idx = 0; idx < chars.length; idx++) {
                // 공백은 체크하지 않음
                if ((int) chars[idx] == 32) {
                    continue;
                }

                // 숫자도 문자(영문/한글 등)도 아닌 경우를 체크
                if (!Character.isDigit(chars[idx]) && !Character.isLetter(chars[idx])) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 구분자 삽입된 문자열 연결 메소드
     *
     * @param source
     * @param delimiter
     * @return
     */
    private String concat(List<String> source, String delimiter) {
        StringBuffer buffer = new StringBuffer();

        if (source != null) {
            for (int i = 0; i < source.size(); i++) {
                buffer.append(source.get(i));

                if (i < (source.size() - 1)) {
                    buffer.append(delimiter);
                }
            }
        }

        return buffer.toString();
    }


    /**
     * TODO 테스트 안됨
     *
     * @param teaServerId
     * @param collectionId
     * @return
     */
    public int selectHistoryInfoTotalCount(String teaServerId, String collectionId) {
        int totalCount = 0;

        String[] paramFields = {"server_id", "collection_id", "item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("rke_retriever", "analysis_record_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0801";
            String errorMessage = "Failed in analysis history count query. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            totalCount = Tools.parseInt(response.getValue("count"));
        }

        return totalCount;
    }

    /**
     * 분석이력 목록 가져오기
     *
     * @param teaServerId
     * @param collectionId
     * @param offset
     * @param numCount
     * @return
     */
    public RelativeKeywordInfo[] selectHistoryInfoList(String teaServerId,
                                                       String collectionId,
                                                       int offset, int numCount) {
        RelativeKeywordInfo[] list = new RelativeKeywordInfo[0];

        String[] paramFields = {"server_id", "collection_id", "item_delimiter", "value_delimiter", "offset", "num_count"};
        SocketMessage request = new SocketMessage("rke_retriever", "analysis_record", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numCount));

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0802";
            String errorMessage = "Failed in analysis history list. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            List<Pair<String>> relationList = Tools.getRelationPairList(response.getValue("relation"), ITEM_DELIMITER, VALUE_DELIMITER);
            String[] enableList = StringTool.stringToArray(response.getValue("enable"), ITEM_DELIMITER);
            String[] modifiedList = StringTool.stringToArray(response.getValue("modified"), ITEM_DELIMITER);
            String[] lockedList = StringTool.stringToArray(response.getValue("locked"), ITEM_DELIMITER);

            int relationCount = relationList.size();
            list = new RelativeKeywordInfo[relationCount];
            for (int i = 0; i < relationCount; i++) {
                boolean isModified = modifiedList[i].equals("1") ? true : false;
                boolean isExcepted = enableList[i].equals("1") ? true : false;
                //TODO
                //boolean isLocked = lockedList[i].equals("1") ? true : false;
                boolean isLocked = true;
                list[i] = new RelativeKeywordInfo(relationList.get(i).key(), relationList.get(i).value(), "", "", "", isLocked, isModified, isExcepted, "", "", "");
            }
        }

        return list;
    }


    /**
     * 연관키워드 고정 및 해제 처리
     *
     * @param teaServerId
     * @param relativeKeywordList
     * @param isLock
     * @return
     */
    public boolean setRelativeKeywordLockStatus(String teaServerId, String[] relativeKeywordList, boolean isLock) {
        boolean result = true;

        String[] paramFields = {"server_id", "port", "collection_id", "item_delimiter", "value_delimiter", "keyword_list", "lock"};
        SocketMessage request = new SocketMessage("rke_retriever", "set_lock", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", "");
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("keyword_list", Tools.connectByDelimiter(relativeKeywordList, ","));
        request.setValue("lock", isLock ? "1" : "0");

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0803";
            String errorMessage = "Failed in relation keyword to lock status change. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 반환 결과 처리
            // return true
        }

        return result;
    }

    /**
     * 분석 이력 건수
     *
     * @return
     */
    public int getHistoryTotalCount(String teaServerId, String collectionId) {
        int totalCount = 0;

        String[] paramFields = {"server_id", "collection_id", "most_recent"};
        SocketMessage request = new SocketMessage("rke_retriever", "analysis_record_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        if (collectionId.equals(COLLECTION_ALL)) {
            request.setValue("collection_id", "");
            request.setValue("most_recent", "n");
        } else if (collectionId.equals(COLLECTION_GROUPBY)) {
            request.setValue("collection_id", "");
            request.setValue("most_recent", "y");
        } else {
            request.setValue("collection_id", collectionId);
            request.setValue("most_recent", "n");
        }

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0804";
            String errorMessage = "Failed in history count. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            totalCount = Tools.parseInt(response.getValue("count"));
        }

        return totalCount;
    }


    /**
     * 분석 이력 목록
     *
     * @param offset
     * @param numCount
     * @return
     */
    public HistoryInfo[] getHistoryList(String teaServerId, String collectionId
            , int offset, int numCount) {
        HistoryInfo[] list = new HistoryInfo[0];

        String[] paramFields = {"server_id", "collection_id"
                , "most_recent", "item_delimiter", "value_delimiter", "offset", "num_count"
                , "sort_field", "sort_descending"
        };

        SocketMessage request = new SocketMessage("rke_retriever", "analysis_record", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        if (collectionId.equals(COLLECTION_ALL)) {
            request.setValue("collection_id", "");
            request.setValue("most_recent", "n");
        } else if (collectionId.equals(COLLECTION_GROUPBY)) {
            request.setValue("collection_id", "");
            request.setValue("most_recent", "y");
        } else {
            request.setValue("collection_id", collectionId);
            request.setValue("most_recent", "n");
        }
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER_TYPE2);
        request.setValue("offset", Integer.toString(offset));
        request.setValue("num_count", Integer.toString(numCount));
        request.setValue("sort_field", "id");   // default
        request.setValue("sort_descending", "y"); // default


        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0805";
            String errorMessage = "Failed in history list. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 결과 반환 처리
            List<HistoryInfo> historyList = Tools.getHistList(
                    response.getValue("record")
                    , ITEM_DELIMITER
                    , VALUE_DELIMITER_TYPE2);
            list = historyList.toArray(list);
        }

        return list;
    }


    public int getScheduleTotalCount() {
        int totalCount = 0;

        String[] paramFields = {"server_id", "collection_id", "query", "item_delimiter", "value_delimiter"};
        // TODO request_type 정의해야함
        SocketMessage request = new SocketMessage("rke_retriever", "relative_keyword_count", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("collection_id", "");
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

//		SocketMessage response = handleMessage(request);
//		if (!isSuccessful(response)) {
//			// 예외 처리
//			String errorCode = "APIL_0804";
//			String errorMessage = "Failed in history count. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
//			if (response.getErrorCode().equals("")) {
//				setError(errorCode, errorMessage);
//			} else {
//				wrapError(errorCode, errorMessage);
//			}
//		} else {
//			// 결과 반환 처리
//			totalCount = Tools.parseInt(response.getValue("count"));
//		}
//
//		return totalCount;
        return 2;
    }


    public ScheduleStatus[] getScheduleList(String teaServerId, int offset, int numCount) {
        ScheduleStatus[] list = new ScheduleStatus[0];

        String[] collectionIds = getCollectionIds(teaServerId);

        int count = getHistoryTotalCount(teaServerId, COLLECTION_GROUPBY);
        HistoryInfo[] historyList = getHistoryList(teaServerId, COLLECTION_GROUPBY, 0, count);

        List<ScheduleStatus> scheduleList = new ArrayList<ScheduleStatus>();

        String[] paramFields = {"server_id", "item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("rke_retriever", "get_schedule_info", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0807";
            String errorMessage = "Failed in schedule info. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            String scheduleStr = response.getValue("schedule");
            String[] scheduleItems = scheduleStr.split("[" + ITEM_DELIMITER + "]");
            int i = 0;
            for (String item : scheduleItems) {
                i++;
                String[] scheduleValues = item.split("[" + VALUE_DELIMITER + "]");
                if (scheduleValues.length >= 4) {
                    ScheduleStatus view = new ScheduleStatus();
                    String collectionId = scheduleValues[0];
                    view.setNum(i);
                    view.setCollectionId(collectionId);
                    String endTimeAnalysis = "";
                    view.setEndTimeAnalysis(endTimeAnalysis);
                    for (HistoryInfo hist : historyList) {
                        if (collectionId.equals(hist.getCollectionId())) {
                            endTimeAnalysis = hist.getEndTimeAnalysis();
                            view.setEndTimeAnalysis(endTimeAnalysis);
                            break;
                        }
                    }
                    String enableStatus = scheduleValues[1].equals("y") ? "Y" : "N";
                    String runStatus = scheduleValues[2].equals("y") ? "Y" : "N";
                    view.setRunStatus(runStatus);
                    view.setEnableStatus(enableStatus);
                    scheduleList.add(view);
                }
            }
        }

        list = scheduleList.toArray(list);
        return list;
    }

    public boolean setScheduleStatus(String teaServerId, String collectionId, String enableStatus) {
        boolean result = true;

        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0806", "argument's not valid.");
            return false;
        }

        String[] paramFields = {"server_id", "collection_id", "item_delimiter", "value_delimiter", "enable"};
        SocketMessage request = new SocketMessage("rke_retriever", "activate_schedule", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("enable", enableStatus);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0807";
            String errorMessage = "Failed in setScheduleStatus change. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
            return false;
        } else {
            // 반환 결과 처리
        }

        return result;
    }

    public boolean runSchedule(String teaServerId, String collectionId) {
        boolean result = true;

        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0808", "argument's not valid.");
            return false;
        }

        String[] paramFields = {"server_id", "collection_id", "item_delimiter", "value_delimiter", "enable"};
        SocketMessage request = new SocketMessage("rke_retriever", "force_start_schedule", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "", paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0809";
            String errorMessage = "Failed in runSchedule change. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
            return false;
        } else {
            // 반환 결과 처리
        }
        return result;
    }


    public boolean saveSchedule(String teaServerId, ScheduleInfo scheduleInfo) {
        boolean result = true;

        String collectionId = scheduleInfo.getCollectionId();

        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0810", "argument's not valid.");
            return false;
        }

        String[] paramFields = {"server_id", "collection_id"
                , "item_delimiter", "value_delimiter", "type", "time"};
        SocketMessage request = new SocketMessage("rke_retriever", "set_schedule_info", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);
        request.setValue("type", scheduleInfo.getExecuteCycle());

        String s = scheduleInfo.makeScheduleString();
        request.setValue("time", scheduleInfo.makeScheduleString());

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0811";
            String errorMessage = "Failed in saveSchedule change. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 반환 결과 처리
        }
        return result;
    }


    public ScheduleInfo getSchedule(String teaServerId, String collectionId) {
        ScheduleInfo result = new ScheduleInfo();

        if (null == collectionId || 0 == collectionId.length()) {
            setError("APIL_0812", "argument's not valid.");
            return result;
        }

        String[] paramFields = {"server_id", "collection_id", "item_delimiter", "value_delimiter"};
        SocketMessage request = new SocketMessage("rke_retriever", "get_schedule_info", SocketMessage.PriorityType.EMERGENCY, SocketMessage.TransferType.BI_WAY, "",
                paramFields);
        request.setValue("server_id", teaServerId);
        request.setValue("collection_id", collectionId);
        request.setValue("item_delimiter", ITEM_DELIMITER);
        request.setValue("value_delimiter", VALUE_DELIMITER);

        SocketMessage response = handleMessage(request);
        if (!isSuccessful(response)) {
            // 예외 처리
            String errorCode = "APIL_0813";
            String errorMessage = "Failed in getSchedule change. (listener ip=" + this.listenerIp + ", listener port=" + this.listenerPort;
            if (response.getErrorCode().equals("")) {
                setError(errorCode, errorMessage);
            } else {
                wrapError(errorCode, errorMessage);
            }
        } else {
            // 반환 결과 처리
            String scheduleStr = response.getValue("schedule");
            String[] scheduleItems = scheduleStr.split("[" + ITEM_DELIMITER + "]");

            for (String item : scheduleItems) {
                String[] scheduleValues = item.split("[" + VALUE_DELIMITER + "]");
                if (scheduleValues.length >= 4) {
                    ScheduleInfo info = new ScheduleInfo();
                    String collection = scheduleValues[0];
                    info.setCollectionId(collection);
                    if (collectionId.equals(collection)) {
                        String timeStr = scheduleValues[3];
                        info.parseSchedule(timeStr);
                        return info;
                    }
                }
            }
        }
        return result;
    }
}


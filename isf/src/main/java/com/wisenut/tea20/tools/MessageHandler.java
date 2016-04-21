package com.wisenut.tea20.tools;

import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import com.wisenut.tea20.types.*;

/**
 * Socket Message Handler.
 * 
 * Sends request message to listener.
 * Receives response message from listener.
 *     
 * @author hkseo@wisenut.co.kr
 *
 */
public class MessageHandler {
	final int INIT_CONNECTION_INTERVAL = 500;
	final int MAX_CONNECTION_INTERVAL = 500;
	final int MAX_BUFFER_SIZE = 1024;
	final int OUT_MESSAGE_HEADER_SIZE = 12;
	final int IN_MESSAGE_HEADER_SIZE = 10;
	final int MAX_RETRY_TIME = 30;
	
	final int RECV_SOCK_TIMEOUT = 30000;
	final String MESSAGE_ENCODING = "UTF-8";
	
	private String ip;
	private int port;
	
	private Socket socket = null;
	private OutputStream oStream = null;
	private InputStream iStream = null;
	
	private int initRetryInterval = INIT_CONNECTION_INTERVAL;
	private int maxRetryInterval = MAX_CONNECTION_INTERVAL;
	private int maxConnectionRetry = MAX_RETRY_TIME;
	private int recvTimeout = RECV_SOCK_TIMEOUT;
	
	private String failedCause = "";
	
	public MessageHandler(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public MessageHandler(String ip, int port, int interval, int maxRetry) {
		this.ip = ip;
		this.port = port;

		if (0 < interval) {
			maxRetryInterval = interval;
		}

		if (0 < maxRetry) {
			maxConnectionRetry = maxRetry;
		}
	}
	
	public MessageHandler(String ip, int port, int initInterval, int interval, int maxRetry, int waitTimeout) {
		this.ip = ip;
		this.port = port;
		
		if (0 < initInterval) {
			initRetryInterval = initInterval;
		}

		if (0 < interval) {
			maxRetryInterval = interval;
		}

		if (0 < maxRetry) {
			maxConnectionRetry = maxRetry;
		}

		if (0 < waitTimeout) {
			this.recvTimeout = waitTimeout;
		}
	}
	
	private void createSocket() throws Exception {
		if (null != socket) {
			try {
				socket.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		socket = new Socket();
		socket.setSoTimeout(recvTimeout);
		socket.setReuseAddress(true);
		socket.setSoLinger(true, 0);
	}
	
	private boolean connect() {
	 	try {
    		createSocket();
    		 
    		SocketAddress adress = new InetSocketAddress(ip, port);
    		int triedCount = 0;
    		boolean connected = false;
    		
    		int retryInterval = initRetryInterval;
    		
    		while (!connected && triedCount < maxConnectionRetry) {
    			try {
    				socket.connect(adress, retryInterval);	
    				if (retryInterval < maxRetryInterval) {
    					retryInterval *= 2;
    					if (retryInterval > maxRetryInterval) {
    						retryInterval = maxRetryInterval;
    					}
    				}
    			} catch (Exception e) {
    				createSocket();
    			}
    			
    			connected = socket.isConnected();
    			if (connected) {
    				break;
    			} else {
    				triedCount++;
    			}
    		}
    		
    		if (triedCount > 0) {
    			System.out.println("\t- tried connection: " + triedCount + " times");
    		}
    		
    		if (!connected) {
    			failedCause = "connection failed after " + triedCount + " attempts.";
    			return false;
    		}
    		
    		oStream = socket.getOutputStream();
    		iStream = socket.getInputStream();
    		return true;
    	} catch (Exception e) {
    		failedCause = e.getMessage();
    		return false;
    	}
	}
	
	private void disconnect() {
		try {
			if (socket != null) {
				socket.close();
			}
			if ( oStream != null ) {
				oStream.close();
			}
			if ( iStream != null ) {
				iStream.close();
			}
		} catch (IOException e) {
			System.err.println("[E!][TEClient][exception in socket disconnection: "+e+"]");
		}
	}
	
	private void setOutMessageHeader(SocketMessage request, byte[] message) {
		message[0] = (byte)request.getPriorityTypeString().charAt(0);
		message[1] = (byte)request.getTransferTypeString().charAt(0);
	}
	
	public SocketMessage getResponse(SocketMessage request) {
		SocketMessage toReturn = new SocketMessage(true);
		
		String responseMessage = "";

		if (!connect()) {
			toReturn.setErrorCode("APIL_0001");
			toReturn.setErrorMessage("connection failed: " + failedCause);
			disconnect();
			return toReturn;
		}
		
		String xmlMessage = request.toString();
		
		//construct message
		byte[] message = new byte[0];

		try {
			byte[] converted = xmlMessage.getBytes(MESSAGE_ENCODING);
			message = new byte[converted.length + OUT_MESSAGE_HEADER_SIZE];
			for (int i = 0; i < converted.length; i++) {
				message[i + OUT_MESSAGE_HEADER_SIZE] = converted[i]; 
			}
			
			try {
				setOutMessageHeader(request, message);	
			} catch (Exception e) {
				toReturn.setErrorCode("APIL_0005");
				toReturn.setErrorMessage("failed in constructing request message header.");
				return toReturn;
			}
			
			String strSize = Integer.toString(converted.length);
			int nZero = OUT_MESSAGE_HEADER_SIZE - strSize.length();
			for (int i=2; i<nZero; i++) {
				message[i] = ' ';
			}
			for (int i=0; i<strSize.length(); i++) {
				message[i+nZero] = (byte)strSize.charAt(i);
			}
			
			if (send(message)) {
				responseMessage = receive();
				MessageParser parser = new MessageParser(responseMessage, true);
				toReturn = parser.parse();
			} else {
				toReturn.setErrorCode("APIL_0004");
				toReturn.setErrorMessage("failed in sending message: " + failedCause);
			}
		} catch(Exception e) {
			toReturn.setErrorCode("APIL_0010");
			toReturn.setErrorMessage("system error during messaging: " + e.getMessage());
		} finally {
			disconnect();
		}
		
		return toReturn;
	}
	
	private boolean send(byte[] message) throws Exception {
		try {
    		oStream.write(message);
    		oStream.flush();
    	} catch(IOException e) {
    		failedCause = e.getMessage();
    		return false;
    	}

		return true;
	}
	
	private String receive() throws Exception {
		try {
			byte [] bRead = new byte [IN_MESSAGE_HEADER_SIZE];
			byte [] message = null;
			int numRead = iStream.read(bRead, 0, IN_MESSAGE_HEADER_SIZE);
			int numExpected = 0;
			
			if (numRead == IN_MESSAGE_HEADER_SIZE) {
				String sizeStr = new String(bRead, "ASCII").trim();
				numExpected = Integer.parseInt(sizeStr);
				message = new byte [numExpected];
			} else {
				throw new Exception("wrong response message header.");
			}
			
			bRead = new byte [MAX_BUFFER_SIZE];
			int numTotalRead = 0;
			while (numTotalRead < numExpected) {
				numRead = iStream.read(bRead, 0, MAX_BUFFER_SIZE);
				if (numRead <= 0) {
					break;
				}
				
				for (int i=0; numTotalRead <numExpected && i < numRead; i++) {
					message[numTotalRead++] = bRead[i];
				}
			}

			if (numTotalRead != numExpected) {
				throw new Exception("mismatch in response message size (" 
						+ numTotalRead+ " bytes read, " + numExpected + " bytes expected)");
			}
			
			return new String(message, MESSAGE_ENCODING);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("failed in receiving socket messages: " + e.getMessage());
		}
	}
}

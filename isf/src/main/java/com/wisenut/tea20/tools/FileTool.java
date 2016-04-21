package com.wisenut.tea20.tools;


import java.io.*;
import java.util.*;

/**
 * File I/O Class.
 *
 * Provides simple file I/O functionalities.
 *
 * <ul>
 * 	<li> Static functions: reading text content from the file, writing text content to the file, etc.
 *  <li> Class itself: Implements simple file reader / writer 
 * </ul> 
 * @author hkseo@wisenut.co.kr
 *
 */
public class FileTool
{
	/**
	 * Stores text contents to a file.
	 * 
	 * @param content String content
	 * @param file target file path
	 * @param encoding encoding to use (default: ASCII if it's null)
	 */
	public static void writeToFile(String content, String file, String encoding)
	{
		if (null == encoding)
			encoding = "ASCII";
		
		try
		{
			StringReader reader = new StringReader(content);
			File contentFile = new File(file);
			OutputStream ostream = new FileOutputStream(contentFile, false);
			OutputStreamWriter writer = new OutputStreamWriter(ostream, encoding);
			
			int nRead = 0;
			char[] buffer = new char[1024];
			while ((nRead = reader.read(buffer, 0, 1024)) != -1)
			{
				writer.write(buffer, 0, nRead);
			}
			writer.flush();
			ostream.flush();
			writer.close();
			ostream.close();
			reader.close();	
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Gets text contents of a file. 
	 * 
	 * @param filePath source file path
	 * @param encoding encoding used (default: ASCII if it's null)
	 * @return content of the text
	 */
	public static String getContent(String filePath, String encoding)
	{
		if (null == filePath || 0 == filePath.length())
			return "";
		
		if (null == encoding || 0 == encoding.length())
			encoding = "ASCII";
		
		File source = new File(filePath);
		if (!source.exists())
			return "";
		
		StringBuffer toReturn = new StringBuffer();
		
		try
		{
			FileInputStream iStream = new FileInputStream(filePath);
			InputStreamReader reader = new InputStreamReader(iStream, encoding);
			BufferedReader bReader = new BufferedReader(reader);
			String lineRead;
			
			while((lineRead = bReader.readLine()) != null)
			{
				if (0 != lineRead.length())
					toReturn.append(lineRead);
				toReturn.append("\n");
			}
			
			bReader.close();
			reader.close();
			iStream.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return toReturn.toString();
	}
	
	/**
	 * Gets trimmed text contents of a file as a string array. 
	 * 
	 * @param filePath source file path
	 * @param encoding encoding used (default: ASCII if it's null)
	 * @return content of the text
	 */
	public static String [] getLines(String filePath, String encoding)
	{
		
		if (null == filePath || 0 == filePath.length())
			return new String[0];
		
		if (null == encoding || 0 == encoding.length())
			encoding = "ASCII";
		
		File source = new File(filePath);
		if (!source.exists())
			return new String[0];
		
		Vector<String> toReturn = new Vector<String>();
		
		try
		{
			FileInputStream iStream = new FileInputStream(filePath);
			InputStreamReader reader = new InputStreamReader(iStream, encoding);
			BufferedReader bReader = new BufferedReader(reader);
			String lineRead;
			
			while((lineRead = bReader.readLine()) != null)
				if (0 != lineRead.length())
					toReturn.add(lineRead.trim());
			
			bReader.close();
			reader.close();
			iStream.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return (String[])toReturn.toArray(new String[0]);
	}
	
	FileInputStream inputStream_ = null;
	InputStreamReader reader_ = null;
	BufferedReader bufferedReader_ = null;
	
	FileOutputStream outputStream_ = null;
	OutputStreamWriter writer_ = null;
	BufferedWriter bufferedWriter_ = null;
	
	boolean isWriter_ = false;
	
	/**
	 * Constructor.
	 * 
	 * @param filePath path for file
	 * @param encoding encoding of file
	 * @param isWriter decides whether this tool's for writer
	 */
	public FileTool(String filePath, String encoding, boolean isWriter)
	{
		isWriter_ = isWriter;
		try
		{
			if (isWriter_)
			{
				outputStream_ = new FileOutputStream(filePath);
				writer_ = new OutputStreamWriter(outputStream_, encoding);
				bufferedWriter_ = new BufferedWriter(writer_);
			}
			else
			{
				inputStream_ = new FileInputStream(filePath);
				reader_ = new InputStreamReader(inputStream_, encoding);
				bufferedReader_ = new BufferedReader(reader_);
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Read a line from the file.
	 * @return line read from file.
	 */
	public String readLine()
	{
		if (isWriter_ || null == bufferedReader_)
			return null;
		
		String toReturn = null;
		
		try
		{
			toReturn = bufferedReader_.readLine();
		}
		catch(Exception e)
		{
		}
		return toReturn;
	}
	
	/**
	 * Write a line to file.
	 * @param line a line to be written
	 */
	public void writeLine(String line)
	{
		if (!isWriter_ || null == bufferedWriter_)
			return;
		try
		{
			bufferedWriter_.write(line + "\n");
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 * Flush buffer contents to file. (only for writer mode)
	 */
	public void flush()
	{
		if (!isWriter_)
			return;
		
		try
		{
			if (null != bufferedWriter_)
				bufferedWriter_.flush();
			if (null != writer_)
				writer_.flush();
			if (null != outputStream_)
				outputStream_.flush();
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 * Close file handles, etc.
	 */
	public void closeTool()
	{
		try
		{
			if (isWriter_)
			{
				if (null != bufferedWriter_)
					bufferedWriter_.flush();
				if (null != writer_)
					writer_.flush();
				if (null != outputStream_)
					outputStream_.flush();
				
				if (null != bufferedWriter_)
					bufferedWriter_.close();
				if (null != writer_)
					writer_.close();
				if (null != outputStream_)
					outputStream_.close();
				
				bufferedWriter_ = null;
				writer_ = null;
				outputStream_ = null;
			}
			else
			{
				if (null != bufferedReader_)
					bufferedReader_.close();
				if (null != reader_)
					reader_.close();
				if (null != inputStream_)
					inputStream_.close();
				
				bufferedReader_ = null;
				reader_ = null;
				inputStream_ = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void finalize()
	{
		closeTool();
	}
}

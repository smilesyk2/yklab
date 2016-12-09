/*
 * @(#)FileUtil.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

import kr.co.wisenut.logger.Log2;
import kr.co.wisenut.util.IOUtil;

/**
 *
 * FileUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FileUtil {
    private final static int MAX_BUF = 1024 * 100;
    private static  String[] m_Arr_Ext =
            new String[] {"doc", "docx", "rtf", "mht", "pdf", "ppt", "pptx", "xls", "xlsx",
                    "hwp", "hwd", "hwx", "hwn", "htm", "html", "swf", "mdi", "sxw", "sxc", "sxi",
                    "xml", "txt", "gul", "jtd", "7z", "alz", "zip", "tar", "gz", "bz2", "lzx", "rar",
                    "wpd", "dwg", "msg", "eml", "chm", "mdb", "mp3", "ps2" };
    public static String fileseperator = System.getProperty("file.separator");

    /**
     * @return String
     */
    public static String getFileSeperator() {
        return fileseperator;
    }

    /**
     * @param path
     */
    public static synchronized void makeDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    /**
     * create directory method
     *
     * @param parent directory
     * @param child  directory
     */
    public static synchronized void makeDir(String parent, String child) {
        StringBuffer mkdir = new StringBuffer(parent);
        mkdir.append(fileseperator);
        mkdir.append(child);
        makeDir(mkdir.toString());
    }

	public static void makeDirForPath(String path) {
		File file = new File(path);
		File dir = new File(file.getParent());
		makeDir(dir.getPath());
	}

    /**
     * @param dir  directory
     * @return String
     */
    public static String lastSeparator(String dir) {
        if (dir.equals("")) return "";
        if (dir.lastIndexOf(fileseperator) != dir.length() - 1) {
            dir += fileseperator;

        }
        return dir;
    }

    /**
     * check file extension
     *
     * @param fileName
     * @return boolean
     */
    public static boolean isFileExt(String fileName) {
        boolean isExt = false;
        int fileLen = fileName.length();
        int idx = fileName.lastIndexOf(".");
        if (idx > -1 && (idx + 1) != fileLen) {
            isExt = true;
        }
        return isExt;
    }

    /**
     * @param fileName
     * @return String
     */
    public static String getFileExt(String fileName) {
        String ext = "";
        int fileLen = fileName.length();
        int idx = fileName.lastIndexOf(".");
        if (idx > -1 && (idx + 1) != fileLen) {
            ext = fileName.substring(idx + 1, fileLen);
        }
        if(ext.indexOf("?") != -1){
            idx = ext.indexOf("?");
            if(idx != -1) ext = ext.substring(0, idx);
        }
        return ext;
    }

    /**
     *
     *
     * @param file
     * @param regex
     * @param replacement
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void texFileReplace(File file, String regex, String replacement) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + file);
        }
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + file);
        }
        File tempFile = File.createTempFile("temp", "temp");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                line = StringUtil.replace(line, regex, replacement);
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            reader.close();
            file.delete();
            tempFile.renameTo(file);
        } catch (Exception e) {
            Log2.error("[File ] [Make File IOException " + e + " ]");
        }
    }

    /**
     * InputStream Object to file method
     *
     * @param prefix
     * @param FileName
     * @param is
     * @return String
     */

    public static synchronized String inputStreamToFile(String prefix, String FileName, InputStream is) {
        String blobName = lastSeparator(prefix) + FileName;
        try {
            if (is != null) {
                byte[] buffer = new byte[MAX_BUF];
                makeDir(prefix);
                FileOutputStream saver = new FileOutputStream(blobName);
                while (true) {
                    int bytesRead = is.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    saver.write(buffer, 0, bytesRead);
                }
                saver.close();
                is.close();
            } else {
                blobName = "";
            }
            return blobName;
        } catch (IOException e) {
            Log2.error("[File ] [Make File IOException " + e + " ]");
        }
        return blobName;
    }

    /**
     * 주어진 절대파일경로 문자열로부터 파일 구분자를 분리해서 리턴한다.
     * @param fullFileName 절대파일경로 문자열
     * @return 파일구분자 문자열
     */
    public static String getFileSeperatorByString(String fullFileName) {
        if (fullFileName == null)         return null;
        if (fullFileName.length() == 0)   return null;

        for (int i = 0; i < fullFileName.length(); i ++) {
            char oneChar = fullFileName.charAt(i);
            if (oneChar == '\\')    return "\\";
            if (oneChar == '/')     return "/";
        }
        return fileseperator;
    }

    /**
     * 주어진 절대파일경로 문자열로부터 파일의 부모 디렉토리를 분리해서 리턴한다.
     * @param fullFileName 절대파일경로 문자열
     * @return 부모디렉토리 절대경로 문자열
     */
    public static String getFileParentName(String fullFileName){
        if(fullFileName==null) return "";
        String fname = getFileName(fullFileName);
        return (fullFileName.substring(0,fullFileName.length()- fname.length() ) ).trim();
    }

    /**
     * @param fullFileName
     * @return  String
     */
    public static String getFileName(String fullFileName) {
        String filename = "";
        int len = fullFileName.length();
        //int Idx = fullFileName.lastIndexOf("/");
        int Idx = fullFileName.lastIndexOf(getFileSeperatorByString( fullFileName ) );

        if (Idx > -1 && (Idx + 1) != len)
            filename = fullFileName.substring(Idx + 1, len);

        if(filename.indexOf("?") != -1){
            int idx = filename.lastIndexOf("=");
            if(idx != -1) filename = filename.substring(idx+1, filename.length());
        }
        return filename;
    }

    /**
     * @param file file to read
     * @return String containing contents of file
     */
    public static String readFile(File file) throws IOException {
        FileReader filereader = null;
        String s = null;
        try {
            filereader = new FileReader(file);
            s = IOUtil.readReader(filereader);
        } finally {
            if (filereader != null)
                filereader.close();
        }
        return s;
    }


    /**
     *
     * @param f
     * @throws IOException
     */
    public static void lock(File f) throws IOException {
        File lock = lockfile(f);
        for (int i = 0; i < 60; ++i) {
            if (i == 59) {
                System.err.println("stealing lock on " + f);
                lock.delete();
                i = 0;
            }

            if (lock.createNewFile()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        PrintWriter out = new PrintWriter(new FileWriter(lock));
        out.println(System.currentTimeMillis());
        out.close();
    }


    public static void unlock(File f) {
        File lock = lockfile(f);
        lock.delete();
    }

    /**
     * @param f
     * @return File
     */
    private static File lockfile(File f) {
        return new File(f.getParent(), f.getName() + ".lock");
    }


    /**
     * OBSOLETE
     *
     * @deprecated readFile
     */
    public static String loadFile(File file) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException fnfe) {
            Log2.error(fnfe.toString());
            return null;
        }
        StringBuffer buf = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            buf.append(line);
            buf.append("\n");
        }
        if (in != null) in.close();
        return buf.toString();
    }

    /**
     * @param filename
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(String filename) throws IOException {
        return loadProperties(new File(filename));
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(File file) throws IOException {
        Properties properties = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            properties = new Properties();
            properties.load(in);
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
            }
        }
        return properties;
    }

    /**
     *
     * @param dir  directory to put the file in
     * @param name name of new file
     */
    public static void writeProperties(File dir, String name, Properties prop) throws IOException {
        File file;
        FileOutputStream out = null;
        try {
            file = new File(dir, name);
            out = new FileOutputStream(file);
            prop.store(out, "properties for " + name + " written " + new Date());
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     *
     * @param dir  directory to put the file in
     * @param name name of new file
     * @param data the data to write in the file
     */
    public static void writeFile(File dir, String name, InputStream data) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            File outFile = new File(dir, name);
            in = new BufferedInputStream(data);
            out = new BufferedOutputStream(new FileOutputStream(outFile));
            int ch;
            while ((ch = in.read()) != -1) {
                out.write(ch);
            }
        } catch (IOException ioe) {
            Log2.error("Error in writeFile: " + ioe);
            ioe.printStackTrace();
            throw ioe;
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (Exception e) {
            }
        }
    } // writeFile

    /**
     *
     * @param dir  directory to put the file in
     * @param name name of new file
     * @param data the data to write in the file
     */
    public static void writeString(File dir, String name, String data) throws IOException {
        writeString(new File(dir, name), data);
    }

    /**
     *
     * @param data the data to write in the file
     */
    public static void writeString(File file, String data) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(file));
            out.print(data);
        } finally {
            //	    try {
            if (out != null) out.close();
            //        } catch (IOException e) {}
        }
    }


    public static String fixPath(String path) throws IOException {
        Stack stack = getPathStack(path);

        StringBuffer fixed = new StringBuffer(path.length());
        Iterator i = stack.iterator();
        boolean first = true;
        while (i.hasNext()) {
            String s = (String) i.next();
            if (first)
                first = false;
            else
                fixed.append("/");
            fixed.append(s);
        }
        return fixed.toString();
    }


    public static Stack getPathStack(String path) throws IOException {
        StringTokenizer tok = new StringTokenizer(path, "/\\");
        Stack stack = new Stack();
        String s = "";
        try {
            while (tok.hasMoreTokens()) {
                s = tok.nextToken();
                if (s.equals(".")) {
                    continue;
                } else if (s.equals("..")) {
                    stack.pop();
                } else
                    stack.push(s);
            }
        } catch (EmptyStackException e) {
            throw new IOException("Bad path " + path + " - too many ..s");
        }
        return stack;
    }

    /**
     * 두개의 파일 차이점을 찾는다.
     *
     * @return true if the two files differ at any point
     */
    public static boolean isDiff(File a, File b) throws IOException {
        InputStream inA = null, inB = null;
        try {
            inA = new BufferedInputStream(new FileInputStream(a));
            inB = new BufferedInputStream(new FileInputStream(b));
            return IOUtil.isDiff(inA, inB);
        } finally {
            try {
                if (inA != null) inA.close();
            } finally {
                if (inB != null) inB.close();
            }
        }
    }

    public static void copyFile(File source, File target) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(target));
            int ch;
            while ((ch = in.read()) != -1) {
                out.write(ch);
            }
            out.flush();	// just in case
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException ioe) {
                }
            if (in != null)
                try {
                    in.close();
                } catch (IOException ioe) {
                }
        }
    }

    /**
     * @param file
     * @param mode
     * @throws IOException
     */
    public static void chmod(File file, String mode) throws IOException {
        Runtime.getRuntime().exec
                (new String[]
                        {"chmod", mode, file.getAbsolutePath()});
    }

    /**
     * @param file
     * @param data
     * @param encoding
     * @throws IOException
     */
    public static void writeStringToFile(File file,
                                         String data, String encoding) throws IOException {
        OutputStream out = new java.io.FileOutputStream(file);
        try {
            out.write(data.getBytes(encoding));
        } finally {
            IOUtil.closeQuietly(out);
        }
    }


    /**
     * force delete file or directory
     *
     * @param file file or directory to delete.
     * @throws IOException in case deletion is unsuccessful
     */
    public static void forceDeleteOnExit(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        } else {
            file.deleteOnExit();
        }
    }

    /**
     *
     * @param directory directory to delete.
     * @throws IOException in case deletion is unsuccessful
     */
    private static void deleteDirectoryOnExit(File directory)
            throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectoryOnExit(directory);
        directory.deleteOnExit();
    }


    /**
     *
     * @param directory directory to clean.
     * @throws IOException in case cleaning is unsuccessful
     */
    private static void cleanDirectoryOnExit(File directory)
            throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        IOException exception = null;

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDeleteOnExit(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }


    /**
     * @param directory
     * @return File Number in Dirctory
     */
    public static long sizeOfDirectory(File directory) {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        long size = 0;

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                size += sizeOfDirectory(file);
            } else {
                size += file.length();
            }
        }

        return size;
    }

    /**
     * rename file
     *
     * @param from
     * @param to
     * @throws IOException
     */
    public static void rename(File from, File to) throws IOException {
        if (to.exists() && !to.delete()) {
            throw new IOException("Failed to delete " + to + " while trying to rename " + from);
        }
        File parent = to.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent + " while trying to rename " + from);
        }
        if (!from.renameTo(to)) {
            copyFile(from, to);
            if (!from.delete()) {
                throw new IOException("Failed to delete " + from + " while trying to rename it.");
            }
        }
    }

    /**
     * file delete method
     *
     * @param file
     */
    public static void delete(File file) {
        if (file != null) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean isFiltered(String fileName) {
        boolean isRet = false;
        int idx = fileName.lastIndexOf(".");
        if(idx > 0 && idx < fileName.length()) {
            isRet = isFilteredExt( fileName.substring(idx+1, fileName.length()) );
        } else {
            isRet = isFilteredExt(fileName);
        }
        return isRet;
    }

    public static boolean isFilteredIndexOf(String fileName) {
        boolean isRet = false;
        fileName = fileName.toLowerCase();
        for(int i=0;i<m_Arr_Ext.length; i++) {
            if(fileName.indexOf(m_Arr_Ext[i]) >0) {
                isRet = true;
                break;
            }
        }
        if( !isRet) {
            Log2.debug("[Filter] [None Filtering Source File indexOf match("+fileName+")]", 4);
        }
        return isRet;
    }
    /**
     * check allow filter file
     * @param fileExt
     * @return bool
     */
    private static  boolean isFilteredExt(String fileExt) {
        boolean isRet = false;
        fileExt = fileExt.toLowerCase();
        for(int i=0;i<m_Arr_Ext.length; i++) {
            if(m_Arr_Ext[i].equals(fileExt)) {
                isRet = true;
                break;
            }
        }
        if( !isRet) {
            Log2.debug("[Filter] [None Filtering Source File Ext("+fileExt+")]", 4);
        }
        return isRet;
    }

    /**
     * 1. find filename and file extension
     * 2. delete file
     */
    public static boolean deleteFile(String path, String ext) {
    	boolean result = true;
        File file = new File(path);
        try {
            if (file.exists()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().endsWith(ext)) {
                        boolean currResult = files[i].delete();
                        if(result) {
                        	result = currResult;
                        }
                    }
                }
            }
            return result;
        } catch (NullPointerException e) {
            Log2.error("[deleteFile Method " + e.getMessage() + "]");
            return false;
        }
    }

    /**
     *
     * @param path
     * @param ext
     * @return boolean
     */
    public static  boolean isBdb(String path, String ext) {
        boolean isExist = false;
        File file = new File(path);
        try {
            if (file.exists()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if( files[i].getName().endsWith(ext) ) {
                        isExist = true;
                    }
                }
            }
        } catch (NullPointerException e) {
            Log2.error("[BDB File ] ["+e+"]");
            return false;
        }
        return isExist;
    }
   /**
     *
     * @param content
     * @param filename
     * @return boolean
     */
    public static synchronized boolean RandomAccessWrite(String content, String filename) {
        return RandomAccessWrite(content, filename, "");
    }

    /**
     *
     * @param content
     * @param filename
     * @param language
     * @return boolean
     */
    public static synchronized boolean RandomAccessWrite(String content, String filename, String language) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "rw");
            raf.seek(raf.length());
            if(language.toLowerCase().equals("utf-8")) {     // Write UTF-8 Encoding
                raf.write(content.getBytes("UTF-8"));
            } else {        // Write Default Encoding
                raf.write(content.getBytes());
            }
            raf.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static synchronized File getFile(String parent, String child) {
        File file;
        if(parent.equals("")) {
            file = new File(child);
        } else {
            file = new File(parent, child);
        }
        return file;
    }

    public static synchronized String getNoneExtFileName(String strFile) {
        String retFileName = strFile;
        String fileName = new File(strFile).getName();
        int idx = fileName.indexOf(".");
        if(idx != -1) {
            retFileName = strFile.substring(0, idx);
        }
        return retFileName;
    }

    static final int BUFF_SIZE = 100000;
    static final byte[] buffer = new byte[BUFF_SIZE];
    public static void copy(String from, String to) throws IOException {
        //if(new File(from).exists()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(from);
                out = new FileOutputStream(to);
                while (true) {
                    synchronized (buffer) {
                        int amountRead = in.read(buffer);
                        if (amountRead == -1) {
                            break;
                        }
                        out.write(buffer, 0, amountRead);
                    }
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
        //}
    }


    public static void setFilterExt(String[] fileExtList) {
        m_Arr_Ext = fileExtList;
    }

    /**
     * 디렉토리 경로가 파일구분자로 끝나지 않는다면, 파일구분자를  끝에 붙여준다.
     * @param path
     * @return
     */
    public static String checkDirPath(String path){
        if(path==null||path.trim().length()==0) return path;

        String result = new String(path);
        String DEF_FILE_SEPERATOR_WIN = "\\";
        String DEF_FILE_SEPERATOR_UNIX = "/";

        String seperator = DEF_FILE_SEPERATOR_WIN;
        if(path.endsWith(DEF_FILE_SEPERATOR_WIN) ) seperator = DEF_FILE_SEPERATOR_WIN;
        else if(path.endsWith(DEF_FILE_SEPERATOR_UNIX) ) seperator = DEF_FILE_SEPERATOR_UNIX;
        else if(path.startsWith(DEF_FILE_SEPERATOR_UNIX))
            seperator = DEF_FILE_SEPERATOR_UNIX;

        if(!path.endsWith(seperator)) result += seperator;
        return result;
    }

    /**
     * path 마지막에 file.separator 가 없으면 추가
     * @param path
     * @return
     */
	public static String appendLastSeparator(String path) {
		if (path.charAt(path.length() - 1) != '/' && path.charAt(path.length() - 1) != '\\') {
			return path + FileUtil.fileseperator;
		} else {
			return path;
		}
	}

	/**
	 * path 마지막에 file.separator 가 있으면 제거
	 * @param path
	 * @return
	 */
	public static String removeLastSeparator(String path) {
		if (path.charAt(path.length() - 1) == '/' || path.charAt(path.length() - 1) == '\\') {
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}
	}
}

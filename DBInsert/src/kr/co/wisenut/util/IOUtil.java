/*
 * @(#)IOUtil.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;

import java.io.*;

/**
 *
 * IOUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class IOUtil {

    /**
     * @param input A (possibly null) Reader
     */
    public static void closeQuietly( Reader input ){
        if( input == null ){
            return;
        }

        try{
            input.close();
        }catch( IOException ioe ){}
    }

    /**
     * @param output A (possibly null) Writer
     */
    public static void closeQuietly( Writer output )
    {
        if( output == null ){
            return;
        }

        try{
            output.close();
        } catch( IOException ioe ){}
    }

    /**
     * @param output A (possibly null) OutputStream
     */
    public static void closeQuietly( OutputStream output ){
        if( output == null ){
            return;
        }

        try{
            output.close();
            output.flush();
        } catch( IOException ioe ){}
    }

    /**
     * @param input A (possibly null) InputStream
     */
    public static void closeQuietly( InputStream input ){
        if( input == null ) {
            return;
        }

        try{
            input.close();
        }catch( IOException ioe ) {}
    }

    /**
     * @param input the InputStream to read from
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( InputStream input )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw );
        return sw.toString();
    }

    /**
     * @param input the InputStream to read from
     * @param encoding The name of a supported character encoding. See the
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( InputStream input,
                                   String encoding )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw, encoding );
        return sw.toString();
    }

    ///////////////////////////////////////////////////////////////
    // InputStream -> byte[]
    /**
     * @param input the InputStream to read from
     * @return the requested byte array
     * @throws IOException In case of an I/O problem
     */
    public static byte[] toByteArray( InputStream input )
            throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOCopy.copy( input, output );
        return output.toByteArray();
    }


    ///////////////////////////////////////////////////////////////
    // Derived copy methods
    // Reader -> *
    ///////////////////////////////////////////////////////////////
    // Reader -> String
    /**
     * @param input the Reader to read from
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( Reader input )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw );
        return sw.toString();
    }


    ///////////////////////////////////////////////////////////////
    // Reader -> byte[]
    /**
     * @param input the Reader to read from
     * @return the requested byte array
     * @throws IOException In case of an I/O problem
     */
    public static byte[] toByteArray( Reader input )
            throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOCopy.copy( input, output );
        return output.toByteArray();
    }


    ///////////////////////////////////////////////////////////////
    // Derived copy methods
    // String -> *
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // String -> byte[]
    /**
     * @param input the String to convert
     * @return the requested byte array
     * @throws IOException In case of an I/O problem
     */
    public static byte[] toByteArray( String input )
            throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOCopy.copy( input, output );
        return output.toByteArray();
    }


    ///////////////////////////////////////////////////////////////
    // Derived copy methods
    // byte[] -> *
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // byte[] -> String

    /**
     * @param input the byte array to read from
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( byte[] input )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw );
        return sw.toString();
    }


    /**
     * @param input the byte array to read from
     * @param encoding The name of a supported character encoding. See the
     * @return the requested String
     * @throws IOException In case of an I/O problem
     */
    public static String toString( byte[] input,
                                   String encoding )
            throws IOException{
        StringWriter sw = new StringWriter();
        IOCopy.copy( input, sw, encoding );
        return sw.toString();
    }


    /**
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the content of the streams are equal or they both don't exist, false otherwise
     * @throws IOException In case of an I/O problem
     */
    public static boolean contentEquals( InputStream input1,
                                         InputStream input2 )
            throws IOException{
        InputStream bufferedInput1 = new BufferedInputStream( input1 );
        InputStream bufferedInput2 = new BufferedInputStream( input2 );

        int ch = bufferedInput1.read();
        while( -1 != ch ){
            int ch2 = bufferedInput2.read();
            if( ch != ch2 ){
                return false;
            }
            ch = bufferedInput1.read();
        }

        int ch2 = bufferedInput2.read();
        if( -1 != ch2 ){
            return false;
        }else{
            return true;
        }
    }

    /**
     * @return true if the two streams differ at any point
     **/
    public static boolean isDiff(InputStream a, InputStream b) throws IOException{
        int x, y;

        do {
            x = a.read();
            y = b.read();
            if (x != y) return true;
        } while (x != -1);
        return false;
    }

    /**
     * @param input stream to read
     * @return String containing contents of file
     **/
    public static String readStream(InputStream input) throws IOException {
        return readReader(new InputStreamReader(input));
    }

    /**
     * @param input stream to read
     * @return String containing contents of file
     **/
    public static String readReader(Reader input) throws IOException {
        try {
            StringBuffer buf = new StringBuffer();
            BufferedReader in = new BufferedReader(input);
            int ch;
            while ((ch=in.read())!=-1)
                buf.append((char)ch);
            return buf.toString();
        } finally {
            input.close();
        }
    }

    /**
     *
     * @param e
     * @return
     */
    public static String StackTraceToString( Throwable e ){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream p = new PrintStream( b );
        e.printStackTrace( p );
        p.flush();
        p.close();
        String msg = b.toString();
        try {
            b.flush();
            b.close();
        } catch (IOException e1) {}
        return msg;
    }

    /**
     *
     * @param e
     * @return
     */
    public static String StackTraceToString( LinkageError e ){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream p = new PrintStream( b );
        e.printStackTrace( p );
        p.close();
        String msg = b.toString();
        try {
            b.flush();
            b.close();
        } catch (IOException e1) {}
        return msg;
    }
}

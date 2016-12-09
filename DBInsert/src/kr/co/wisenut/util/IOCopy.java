/*
 * @(#)IOCopy.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.util;

import java.io.*;
/**
 *
 * IOCopy
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class IOCopy {
     private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
     // ----------------------------------------------------------------
    // byte[] -> OutputStream
    // ----------------------------------------------------------------
    /**
     * @param input the byte array to read from
     * @param output the OutputStream to write to
     * @throws java.io.IOException In case of an I/O problem
     */
    public static void copy(byte[] input, OutputStream output)
            throws IOException {
        output.write(input);
    }

    // ----------------------------------------------------------------
    // byte[] -> Writer
    // ----------------------------------------------------------------
    /**
     * @param input the byte array to read from
     * @param output the Writer to write to
     * @throws IOException In case of an I/O problem
     */
    public static void copy(byte[] input, Writer output)
            throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output);
    }


    /**
     * @param input the byte array to read from
     * @param output the Writer to write to
     * @param encoding The name of a supported character encoding. See the
     * @throws IOException In case of an I/O problem
     */
    public static void copy(
            byte[] input,
            Writer output,
            String encoding)
            throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, encoding);
    }


    // ----------------------------------------------------------------
    // Core copy methods
    // ----------------------------------------------------------------
    /**
     * @param input the InputStream to read from
     * @param output the OutputStream to write to
     * @return the number of bytes copied
     * @throws IOException In case of an I/O problem
     */
    public static int copy(
            InputStream input,
            OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    // ----------------------------------------------------------------
    // Reader -> Writer
    // ----------------------------------------------------------------
    /**
     * @param input the Reader to read from
     * @param output the Writer to write to
     * @return the number of characters copied
     * @throws IOException In case of an I/O problem
     */
    public static int copy(
            Reader input,
            Writer output)
            throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        output.flush();
        output.close();
        return count;
    }

    // ----------------------------------------------------------------
    // InputStream -> Writer
    // ----------------------------------------------------------------

    /**
     * @param input the InputStream to read from
     * @param output the Writer to write to
     * @throws IOException In case of an I/O problem
     */
    public static void copy(
            InputStream input,
            Writer output)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    /**
     * @param input the InputStream to read from
     * @param output the Writer to write to
     * @param encoding The name of a supported character encoding. See the
     * @throws IOException In case of an I/O problem
     */
    public static void copy(
            InputStream input,
            Writer output,
            String encoding)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }


    // ----------------------------------------------------------------
    // Reader -> OutputStream
    // ----------------------------------------------------------------

    /**
     * @param input the Reader to read from
     * @param output the OutputStream to write to
     * @throws IOException In case of an I/O problem
     */
    public static void copy(
            Reader input,
            OutputStream output)
            throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out);
        // XXX Unless anyone is planning on rewriting OutputStreamWriter, we have to flush here.
        out.flush();
    }

    // ----------------------------------------------------------------
    // String -> OutputStream
    // ----------------------------------------------------------------

    /**
     * @param input the String to read from
     * @param output the OutputStream to write to
     * @throws IOException In case of an I/O problem
     */
    public static void copy(
            String input,
            OutputStream output)
            throws IOException {
        StringReader in = new StringReader(input);
        OutputStreamWriter out = new OutputStreamWriter(output);
        copy(in, out);
        out.flush();
    }

    // ----------------------------------------------------------------
    // String -> Writer
    // ----------------------------------------------------------------
    /**
     * @param input the String to read from
     * @param output the Writer to write to
     * @throws IOException In case of an I/O problem
     */
    public static void copy(String input, Writer output)
            throws IOException {
        output.write(input);
        output.flush();
        output.close();
    }
}

/*
* @(#)PidUtil.java   3.8.1 2009/03/11
*
*/
package kr.co.wisenut.util;

import kr.co.wisenut.util.DynRun;

import java.io.*;

/**
 *
 * PidUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class PidUtil {
    private String m_srcid;
    private String m_pid_path;

    private String DEF_PID_VAL_INIT="0";
    private String DEF_PID_VAL_ERROR="-1";

    public PidUtil(String srcid, String sf1_home) {
        m_srcid = srcid;
        sf1_home = sf1_home.trim();
        String baseDir = sf1_home;
        if( !sf1_home.endsWith(FileUtil.fileseperator) ){
            baseDir = sf1_home + FileUtil.fileseperator;
        }
        m_pid_path = baseDir + "pid" + FileUtil.fileseperator;
        FileUtil.makeDir(m_pid_path);
    }

    public void makePID() throws IOException {
        makePID(getName());
    }

    public String getPID() throws IOException{
        String pid = null;
        try {
            boolean isWindow = System.getProperty("os.name").startsWith("Windows");
            if((pid = getPIDByDynamicClassRun())==null){ // JAVA 로 PID 얻기
                if(isWindow)  pid = ""+GetPidUtil.getPid();   // window 라면 dll라이브러리로 PID 얻기
                else          pid = getPidByPerScript();      // 펄스크립트로 PID 얻기.
            }
        }catch(Exception e){
            System.out.println("[error] [Make PID] ["+e.getMessage()+"]");
        }
        if(pid==null)
            throw new IOException("Fail getPID. pid is null");
        return pid;

    }

    public void makePID(String name) throws IOException {
        String pid=null;
        try { pid = getPID();}catch(Exception e){
            pid = DEF_PID_VAL_INIT;
            System.out.println("[error] [Make PID] ["+e.getMessage()+"]");
        }

        // 파일 작성
        writePID(getFile(name), pid);
    }

    public void deletePID() { deletePID( getName() ); }

    public void deletePID(String name) {  FileUtil.delete(getFile(name)); }

    /**
     * 에러용 PID 파일을 남겨둔다.
     */
    public void leaveErrorPID() { leaveErrorPID(getName()); }

    /**
     * 에러용 PID 파일을 남겨둔다.
     * @param name 브릿지용 pid 파일명 전치사
     */
    public void leaveErrorPID(String name) {
        try {
            writePID( getFile(name), DEF_PID_VAL_ERROR);
        } catch(IOException e){
            System.out.println("[error] [Make PID] [PID of Process is make error PID]");
        }
    }

    public boolean existsPidFile() throws IOException{
        boolean isRet = false;
        //pid파일이 존재하는지 체크한다.
        //파일이 존재할 경우 pid 정보를 읽는다.
        //pid가 -1인 경우 실패한 경우이므로 pid파일이 존재하지 않는다는 결과를 반환한다.
        //pid가 -1이 아닌 경우 windows 2000이면 tlist로 해당 pid를 체크하고 windows 2000이상이면 tasklist로 체크한다.
        if(getFile(getName()).exists()) {
            String pid = FileUtil.readFile((getFile(getName())));
            if(!pid.equals("-1")) {
                String winOsName = System.getProperty("os.name");
                boolean isWindow = winOsName.startsWith("Windows");
                if(isWindow) {  //windows
                    if(winOsName.indexOf("2000") > -1){  //greater than windows 2000
                        if(hasBridgeProcess(pid, "tlist")) {
                            isRet = true;
                        }
                    }else{  //windows xp, windows windows 2003, windows 2008, etc ...
                        if(hasBridgeProcess(pid, "tasklist")) {
                            isRet = true;
                        }
                    }
                } else { //linux & unix
                    if(hasBridgeProcess(pid, "ps -p " + pid)) {
                        isRet = true;
                    }
                }
            }
        }
        return isRet;
    }

    /**
     * birdge 프로세스가 실행중인지 체크하는 메소드
     * @param pid  bridge process id
     * @param checkCMD check program
     * @return true/false.
     * @throws IOException runtime exception
     */
    private boolean hasBridgeProcess(String pid, String checkCMD) throws IOException{
        boolean isRet = false;
        Process p_start = Runtime.getRuntime().exec(checkCMD);
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p_start.getInputStream()));
        String output;
        while ( (output = stdout.readLine()) != null) {
            if(output.indexOf(pid) > -1 && ( output.startsWith("java") ||  output.indexOf("java") > -1)) {
                isRet = true;
                break;
            }
        }
        p_start.destroy();
        return isRet;
    }

    private File getFile(String name){
        return new File( m_pid_path + name+ ".pid" );
    }

    private String getName(){
        return "DBInsert-" + m_srcid;
    }

    private void writePID(File file, String pid) throws IOException {
        System.out.println("[info] [Make PID] [PID of Process is " + pid + "]");
        FileWriter fw = new FileWriter(file);
        fw.write(pid);
        fw.close();
    }

    /**
     * jvm 버전이 1.5 이상이라면 true 를 리턴한다.
     */
    private boolean isJdk15Up(){
        String version = System.getProperty("java.specification.version");
        float nVer = Float.parseFloat(version);
        return nVer >= 1.5;
    }

    /**
     * 윈도우가 아닌 os 라면 -  펄스크립트를 이용해서 PID 를 리턴한다.
     */
    private String getPidByPerScript() throws IOException {
        boolean isWindow = System.getProperty("os.name").startsWith("Windows");
        String pid = null;

        if ( isWindow )
            return pid;

        try {
            String[] cmd = new String[]{"perl", "-e", "print getppid(). \"\n\";"};
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            pid = br.readLine();
        }catch(IOException e){
            throw e;
        }

        return pid;
    }

    /**
     * jvm1.5 이상이라면 - java management 패키지를 이용해서 PID 를 리턴한다.
     * wise.util.jvm15-1.0.0.jar 가 필요한다.
     * @throws Exception 매소드 실행 실패시 예외 발생
     */
    private String getPIDByDynamicClassRun() throws Exception {
        String pid = null;

        if( !isJdk15Up() )
            return null;

        String classNameString = "com.wisenut.util.getpid.GetpidManagement";
        String methodNameString = "getPID";
        try {
            pid = ""+DynRun.run(classNameString, methodNameString);
        }catch(Exception e){
            throw new Exception( "Fail DynRun. Class:"+classNameString+", method:"+ methodNameString+", Message:"+e.getMessage());
        }
        return pid;
    }
}

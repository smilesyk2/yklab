/*
 * @(#)DBInsert.java   1.0.1 2016/04/11
 * 해당 DB에 데이터를 merge하는 소스
 * 현재는 excel만 구현 추가 구현은 test1, test2.. 부분을 바꾸면 된다.
 *
 */
package kr.co.wisenut;

import java.io.IOException;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.config.Config;
import kr.co.wisenut.config.RunTimeArgs;
import kr.co.wisenut.config.SetConfig;
import kr.co.wisenut.job.IJob;
import kr.co.wisenut.job.JobFactory;
import kr.co.wisenut.logger.Log2;
import kr.co.wisenut.util.ExistCodeConstants;
import kr.co.wisenut.util.IOUtil;
import kr.co.wisenut.util.PidUtil;
import kr.co.wisenut.util.StringUtil;


/**
 *
 * Main
 *
 * 해당 모듈의 실행파일
 *
 * @author  이준명
 * @version 1.0.1 2016/04/11
 *
 */
public class Main {


    public static void main(String[] args) {

    	//받은 인자값이 없으면 exit
        if(args.length ==0) {
            InfoMsg.header(0);
            InfoMsg.usage(0);
            System.exit(-1);
        }


        //처음 msg
        InfoMsg.header(0);

        RunTimeArgs rta = new RunTimeArgs();
        if( !rta.readargs(args) ) {
            System.exit(-1);
        }
        // System Exit Code
        int exit_code = ExistCodeConstants.EXIST_CODE_NORMAL;

        // Create Log Object
        try {
                Log2.setLogger(rta.getLogPath(), rta.getLog(), rta.isDebug(), rta.getLoglevel(), rta.getSrcid());
		} catch (Exception e) {
			System.out.println("[DBInsert] [Set Logger fail. "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
			System.exit(-1);
		}


        // Create PidUtil Object
        PidUtil pidUtil = new PidUtil(rta.getSrcid(), rta.getDir());
        try {
            if(pidUtil.existsPidFile()) {
                Log2.error("[DBInsert] [Crawling failed. Is already running in source.)]"+StringUtil.newLine) ;
                exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
                System.exit(exit_code);
            }
            pidUtil.makePID();
        } catch (IOException e) {
            Log2.error("[DBInsert] [Make PID file fail "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }

        Config config = null;
        double div = 0;
        long start = 0,end = 0;
        try {
            config = new SetConfig().getConfig(rta);
            IJob job = JobFactory.getInstance(config, rta.getMode());
            Log2.out("[Info] [DBInsert] [Source ID: "+rta.getSrcid()+" START]");
            start = System.currentTimeMillis() ;
            if (job.run()) {
                end = System.currentTimeMillis() ;
                div = ((double)(end-start)/1000) ;
                Log2.out("[Info] [DBInsert] [Source ID: "+rta.getSrcid()+ " run time: "+div+" sec]");
                Log2.out("[Info] [DBInsert] [END: Successful]"+StringUtil.newLine);
            } else {
                Log2.error("[DBInsert] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            }
            pidUtil.deletePID();	// Normal Exit PidUtil Object
        } catch (ConfigException e) {
            Log2.error("[DBInsert] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            Log2.error("[DBInsert] [ConfigException: "+IOUtil.StackTraceToString(e)+StringUtil.newLine+"]");
            pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
            exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
        }catch (StringException e) {
            Log2.error("[DBInsert] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            Log2.error("[DBInsert] [BridgeException "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
            exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
        }catch (Throwable e) {
            Log2.error("[DBInsert] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            Log2.error("[DBInsert] [Throwable message]" + "[" + IOUtil.StackTraceToString(e) + "]");
            pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
            exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
        } finally {
            System.out.println("[Info] [DBInsert] [Process: Finished]");
            System.out.println("[Info] [DBInsert] [Exist Code: "+exit_code+" (normal:"+ExistCodeConstants.EXIST_CODE_NORMAL+", abnormal:"+ExistCodeConstants.EXIST_CODE_ABNORMAL+")]");
            System.exit(exit_code);
        }
    }
}

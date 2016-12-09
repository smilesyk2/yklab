package kr.co.wisenut.config;

import java.io.File;

import kr.co.wisenut.InfoMsg;

/**
 *
 * RunTimeArgs
 *
 * 인자값으로 받은 정보를 set하는 클래스
 *
 * @author 이준명
 *
 */

public class RunTimeArgs {
    private String conf = "";
    private String srcid = "";
    private String log = "day";
    private String dir = "";
    private String logPath = "";

    private int loglevel = 1;
    private int mode = -1;
    private boolean debug = false;

    /**
     *Argument Class's Main Function
     * @param args runtime
     */
    public boolean readargs(String[] args) {
        boolean isConf = false;
        boolean isSrc = false;
        boolean isMode = false;
        boolean isDir = false;
        boolean isLogPath = false;
        boolean isRet = true;
        int length = args.length;
        if(length == 0){
            InfoMsg.usage(0);
            return false;
        }


        for (int i = 0; i < length; i++) {
            if (!args[i].startsWith("-")) {//check Argument  - text
                InfoMsg.usage(0);
                isRet = false;
            }
            if (args[i].equalsIgnoreCase("-help")) {
                InfoMsg.usage(0);
                isRet = false;
            }
            if (args[i].equalsIgnoreCase("-conf")) {
                if (i < length-1 && !args[i+1].startsWith("-")) {
                    isConf = true;
                    this.conf = args[i+1];
                    i++;
                }else{
                    isRet = false;
                    break;
                }
            } else if (args[i].equalsIgnoreCase("-srcid")) {
                if (i<length-1 && !args[i+1].startsWith("-")) {
                    isSrc = true;
                    this.srcid = args[i+1];
                    i++;
                }else{
                    isRet = false;
                    break;
                }
            } else if (args[i].equalsIgnoreCase("-dir")) {
                if (i<length-1 && !args[i+1].startsWith("-")) {
                    isDir = true;
					this.dir = args[i+1];

                    File file = new File(this.dir);
                    if(!file.isDirectory()){
                    	InfoMsg.usageDirectoryPath(this.dir);
                    	System.exit(-1);
                    }
                    i++;
                }else{
                    isRet = false;
                    break;
                }
            }else if (args[i].equalsIgnoreCase("-logpath")) {
            	 if (i<length-1 && !args[i+1].startsWith("-")) {
                     isLogPath = true;
                     this.logPath = args[i+1];
                     i++;
                 }else{
                     isRet = false;
                     break;
                 }
            } else if (args[i].equalsIgnoreCase("-mode")) {
                if (i<length-1 && !args[i+1].startsWith("-")) {
                    String tMode = args[i+1];
                    if (tMode.equalsIgnoreCase("excel")) {
                        isMode = true;
                        this.mode = 0;
                    } else if (tMode.equalsIgnoreCase("insert")) {
                        isMode = true;
                        this.mode = 1;
                    } else if (tMode.equalsIgnoreCase("update")) {
                        isMode = true;
                        this.mode = 2;
                    }else {
                        error("*** DB Insert RunTime Mode Error !! -mode "+tMode+" ***");
                        isRet = false;
                    }
                    i++;
                } else {
                    isRet = false;
                    break;
                }
            } else if (args[i].equalsIgnoreCase("-log")) {
                if(i+1 < length) {
                    if ( !args[i+1].startsWith("-") ) {
                        if( args[i+1].equalsIgnoreCase("stdout")){
                            this.log = "stdout";
                        } else if( args[i+1].equalsIgnoreCase("day")){
                            this.log = "day";
                        } else if( args[i+1].equalsIgnoreCase("week")){
                            this.log = "day";
                        } else {
                            this.log = "stdout";
                        }
                        i++;
                    }
                }
            }else if (args[i].equalsIgnoreCase("-debug")) {
                debug = true;
                loglevel = 1;
                if(i+1 < length) {
                    if ( !args[i+1].startsWith("-") ) {
                        try{
                            loglevel = Integer.parseInt(args[i+1]);
                        }catch(Exception e){}
                        i++;
                    }
                }
            }else {
                error("Unknown RunTime Args :"+args[i]);
                isRet = false;
            }
        }

        if(!isRet || !isConf || !isSrc || !isMode || !isDir || !isLogPath){
            error("*** Runtime Arg Error ***");
            isRet = false;
        }
        if (conf.equals("")) {
            error(">> Not Found -conf <config file path>");
            isRet = false;
        }
        if (srcid.equals("")) {
            error(">> Not Found -srcid <source id>");
            isRet = false;
        }
        if(dir.equals("")){
        	error(">> Not Found -dir ");
        }
        if(logPath.equals("")){
        	error(">> Not Found -logpath <Log> ");
        }
        if (mode == -1) {
            error(">> Not Found -mode <excel|test1|test2>");
            isRet = false;
        }
        return isRet;
    }

    public String getConf() {
        return conf;
    }

    public String getSrcid() {
        return srcid;
    }

    public String getLog() {
        return log;
    }

    public String getDir(){
    	return dir;
    }

    public int getLoglevel() {
        return loglevel;
    }
     public boolean isDebug() {
        return debug;
    }

    public int getMode() {
        return mode;
    }

    private void error(String err){
        System.out.println(err);
    }

    public String getLogPath() {
        return logPath;
    }
}


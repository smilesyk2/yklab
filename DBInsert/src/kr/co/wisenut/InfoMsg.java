package kr.co.wisenut;

import kr.co.wisenut.util.StringUtil;

/**
 *
 * InfoMsg
 *
 * 모듈 정보를 출력하는 클래스
 *
 * @author  이준명
 *
 */

public class InfoMsg {

    private final static String version = "1.0.1";
    private final static String build = "0001";
    private final static String buildName = "released";
    private final static String copyright = "Copyright 2016 WISEnut, Inc. All Rights Reserved.";
    private final static int EXCEL = 0;

    /**
     * Print Bridge message
     */
    public static void header(int nType) {
        String msgBridge = "";
        switch(nType) {
            case EXCEL: msgBridge = "Excel Database Insert"; break;
            default: msgBridge = ""; break;
        }

        System.out.println(new StringBuffer().append("\nDatabase Insert")
                .append(msgBridge).append(" v").append(version).append(" (Build ").append(build)
                .append(" ").append("- ").append(buildName).append("), ")
                );

        System.out.println(copyright);
    }

    public static String getVersion() {
    	return "v" + version + "-bld" + build + " " + buildName;
    }

    public static void usage(int nType) {
        String msgClassName = "";
        String strOption = "";
        switch(nType) {
            case EXCEL:
                msgClassName = "kr.co.wisenut.Main";
                strOption += option("-mode excel", "Excel file read & DB Insert");
                break;
            default: msgClassName = ""; break;
        }
        String usage = "\n";
        usage += "Usage : java -Dsf1_home=<SF1_HOME> -Dsf1.ver=<4 or 5> "+ msgClassName +"\n";
        usage += StringUtil.padLeft("-dir <base path> -conf <xml file path> -srcid <srcid> -mode <mode> -logpath <logpath> \n\n", ' ', 80);
        usage += "mode include:\n";
        usage += option("-mode excel", "Excel file read & DB Insert");
        usage += option("-mode test1", "test1");
        usage += option("-mode test2", "test2");
        usage += strOption;

        usage += "\n";
        usage += "Options include:\n";
        usage += option("-log <stdout|day>", "");
        usage += option("-debug <1~4>", "Debug Mode Run [default: -debug 2]");
        usage += desc("1 : ERROR 2: WARNING\n");
        usage += desc("3: INFO 4 : DEBUG\n");
        usage += option("-help", "help");
        System.out.println(usage);
    }


	public static void usageDirectoryPath(String path) {
        String usage = "*** Error No Directory ***\n";
        usage += "usage: make directory "+path+" \n";
        System.out.println(usage);
	}

    public static void ImvalidArg(String arg) {
        System.out.println("*** Invalid Run-time arguments check arguments" +
                " ***\n>> argument name: " + arg);
    }

    public static String option(String oName, String desc){
        String option = StringUtil.padRight("    "+oName, ' ', 20);
        option += desc+"\n";
        return option;
    }

    public static String desc(String desc){
        return StringUtil.padRight("", ' ', 20) + desc;
    }
}

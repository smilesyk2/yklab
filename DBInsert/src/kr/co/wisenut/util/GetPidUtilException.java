package kr.co.wisenut.util;

/**
 * Created by IntelliJ IDEA.
 * User: kybae
 * Date: 2008. 6. 18
 * Time: 오후 6:57:10
 * To change this template use File | Settings | File Templates.
 */
public class GetPidUtilException  extends Exception {
    protected String message = null;
    protected Throwable throwable = null;

    public GetPidUtilException() {
        this("GetPidUtil Class, getPid Method : Need Library File \"getpid.dll\" in java.library.path", null);
    }

    public GetPidUtilException(String message) {
        this(message, null);
    }

    public GetPidUtilException(Throwable throwable) {
        this(null, throwable);
    }

    public GetPidUtilException(String message, Throwable throwable) {
        super();
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return (message);
    }

    public Throwable getThrowable() {
        return (throwable);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (message != null) {
            sb.append(message);
            if (throwable != null) {
                sb.append(":  ");
            }
        }

        if (throwable != null) {
            sb.append(throwable.toString());
        }
        return (sb.toString());
    }
}

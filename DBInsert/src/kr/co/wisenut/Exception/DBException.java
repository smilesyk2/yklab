package kr.co.wisenut.Exception;
import java.sql.SQLException;

/**
 * Created by KoreaWISENut
 * User: KoreaWisenut
 * Date: 2005. 5. 16.
 */

public class DBException extends Exception{
    protected StringBuffer message = new StringBuffer();
    protected Throwable throwable = null;

    public DBException(){
    };

    public DBException(String exp){
        message.append(exp);
    }

    public DBException(Throwable throwable) {
        this("", throwable);
    }

    public DBException(String message, Throwable throwable) {
        super();
        this.message.append( message );
        this.throwable = throwable;
    }

    public synchronized void handlelSQLException(String query, SQLException se){
        message.append("SQLExeption\n");
        message.append("QUERY: " + query);
        message.append("SQL is in error. SQL was not done.");
    }

    public synchronized void handlelClassNotFoundException(String driver, ClassNotFoundException ne){
        message.append("ClassNotFoundException\n");
        message.append("JDBC DRIVER: " + driver);
        message.append("JDBC Driver can not be found.");
    }

    public String toString(){
        if(throwable != null) {
            message.append(throwable.toString());
        }
        return (message.toString());
    }
}

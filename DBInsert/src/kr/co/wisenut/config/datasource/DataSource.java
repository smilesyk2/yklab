package kr.co.wisenut.config.datasource;

/**
 *
 * DataSource
 *
 * <Config> - <DataSource> 의 값을 저장하는 클래스
 *
 * @author  이준명
 *
 */
public class DataSource {
    private String id = "";
    private int vender = -1;
    private String sid = "";
    private String dataBaseName = "";
    private String user = "";
    private String pwd = "";
    private String port = "";
    private String serverName = "";
    private String[] driver = null;
    private String classname = "";
    private String char_set = "";
    private String jdbcUrl = "";

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getChar_set() {
        return char_set;
    }

    public void setChar_set(String char_set) {
        this.char_set = char_set;
    }

    public int driverSize(){
        if(driver == null){
            return 0;
        } else {
            return driver.length;
        }
    }

    public String[] getDriver() {
        return driver;
    }

    public void setDriver(String[] driver) {
        this.driver = driver;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVender() {
        return vender;
    }

    public void setVender(int vender) {
        this.vender = vender;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}

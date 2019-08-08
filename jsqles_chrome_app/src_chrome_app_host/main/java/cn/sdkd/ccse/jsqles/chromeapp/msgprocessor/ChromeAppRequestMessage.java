package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

/**
 *
 * 请求消息
 * Created by sam on 2019/8/4.
 */
public class ChromeAppRequestMessage {
    public enum RequestType {
            query, execute, initdb, help, requireddbtree, dbtree
    }

    RequestType requestType;
    String dbname;
    String sqlText;
    String originMsgText;
    String requiredb;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getOriginMsgText() {
        return originMsgText;
    }

    public void setOriginMsgText(String originMsgText) {
        this.originMsgText = originMsgText;
    }

    public String getRequiredb() {
        return requiredb;
    }

    public void setRequiredb(String requiredb) {
        this.requiredb = requiredb;
    }
}

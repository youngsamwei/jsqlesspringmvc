package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;

/**
 * Created by sam on 2019/8/4.
 */
public class ChromeAppMsgProcessor {
    protected Logger logger = LogManager.getLogger(ChromeAppMsgProcessor.class);

    private ChromeAppRequestMessage caMsg;

    public ChromeAppMsgProcessor(ChromeAppRequestMessage msg){
        this.caMsg = msg;
    }
    public ChromeAppMsgProcessor(String msg){
        ObjectMapper objectMapper = new ObjectMapper();
        ChromeAppRequestMessage chromeAppRequestMessage = null;
        try {
            chromeAppRequestMessage = objectMapper.readValue(msg, ChromeAppRequestMessage.class);
        } catch (IOException e) {
//            System.err.println(e.getMessage());
            logger.error(e.getMessage());
        }
        /*处理msg，转换为chromeAppRequestMessage*/
        this.caMsg = chromeAppRequestMessage;

    }

    /*注意：chrome要求的json格式严格*/
    public String process() throws SQLException, ClassNotFoundException {

        String response = "";
        if (this.caMsg != null){
            if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.query){
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    Connection con = DriverManager.getConnection( "jdbc:sqlserver://localhost:1433;instance=sqlexpress;DatabaseName="
                            + caMsg.getDbname() + ";IntegratedSecurity=true;");
                    Statement stmt = con.createStatement();//创建Statement
                    ResultSet rs = stmt.executeQuery(caMsg.getSqlText());

                    response = "{ \"result\":[";
                    rs.next();
                    while (rs.next()) {
                        String l = "{";
                        /*mssql resultset的下标从1开始*/
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            l += "\"" + rs.getMetaData().getColumnName(i) + "\"";
                            l += ":";
                            l += "\"" + rs.getString(i) + "\"";
                            if (i < rs.getMetaData().getColumnCount()) {
                                l += ",";
                            }
                        }

                        l += "},";
                        response += l;
                    }
                    response = response.substring(0, response.length() - 1) + "]}";
                    rs.close();
                    stmt.close();
                    con.close();

            }
        }
        return response;
    }
}

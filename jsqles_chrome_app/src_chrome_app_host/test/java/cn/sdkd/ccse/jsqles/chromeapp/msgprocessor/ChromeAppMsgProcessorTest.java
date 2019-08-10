package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sam on 2019/8/4.
 */
public class ChromeAppMsgProcessorTest {
    protected Logger logger = LogManager.getLogger(ChromeAppMsgProcessorTest.class);

    @Test
    public void testProcess(){
        String msg = "{\"requestType\":\"query\",\"dbname\":\"testdb\",\"sqlText\":\"select * from student\"}";
        ChromeAppMsgProcessor chromeAppMsgProcessor = new ChromeAppMsgProcessor(msg);
        try {
            String response = chromeAppMsgProcessor.process();
            logger.info(response);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public  void testInitdb(){
        String msg = "{\"requestType\":\"initdb\",\"dbname\":\"testdb\",\"sqlText\":\"select * from student\"}";
        ChromeAppMsgProcessor chromeAppMsgProcessor = new ChromeAppMsgProcessor(msg);
        try {
            String response = chromeAppMsgProcessor.process();
            logger.info(response);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJsonHelpDB(){
        String driver_class = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /*如何连接上sqlexpress实例?*/
        String conn_str = "jdbc:sqlserver://localhost:1533;IntegratedSecurity=true;instance=./sqlexpress;useUnicode=true&characterEncoding=utf8;";

        try {
            Class.forName(driver_class);
            Connection con = DriverManager.getConnection(conn_str + "DatabaseName=testdb;" );
            ResultSet resultSet = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY).executeQuery("sp_helpdb testdb");
            JSONArray jsonArray = new JSONArray();
            while (resultSet.next()) {
                int total_rows = resultSet.getMetaData().getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 0; i < total_rows; i++) {
                    obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                            .toLowerCase(), resultSet.getObject(i + 1));

                }
                jsonArray.put(obj);
            }
            logger.info(jsonArray.toString());
            resultSet.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

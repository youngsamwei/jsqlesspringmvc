package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

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
        }
    }
}

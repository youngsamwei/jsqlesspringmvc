package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by sam on 2019/8/7.
 */
public class DBMetadataProcessTest {
    protected Logger logger = LogManager.getLogger(DBMetadataProcessor.class);

    @Test
    public void test(){
        DBMetadataProcessor dbMetadataProcessor = new DBMetadataProcessor();
        try {
            dbMetadataProcessor.init("testdb");

            String json = "{\"database\":[{\"name\":\"testdb\",\"tables\":[{\"schema_name\":\"dbo\",\"type\":\"user table\",\"name\":\"sc\",\"full_name\":\"dbo.sc\",\"object_id\":2121058592}]}]}";
            JSONObject jsonObject = dbMetadataProcessor.getRequiredDBTree(json);
            logger.info(jsonObject.toString());

            dbMetadataProcessor.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

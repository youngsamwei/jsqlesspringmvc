package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sam on 2019/8/7.
 */
public class DBMetadataProcessTest {
    protected Logger logger = LogManager.getLogger(DBMetadataProcessor.class);
    String driver_class = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /*如何连接上sqlexpress实例?*/
    String conn_str = "jdbc:sqlserver://localhost\\sqlexpress;IntegratedSecurity=true;";
    Connection con = null;

    @Test
    public void testGetRequiredDBTree(){
        DBMetadataProcessor dbMetadataProcessor = new DBMetadataProcessor(con);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetDBTree(){
        DBMetadataProcessor dbMetadataProcessor = new DBMetadataProcessor(con);
        try {
            dbMetadataProcessor.init("testdb");

            JSONObject jsonObject = dbMetadataProcessor.getDBTree("testdb");
            logger.info(jsonObject.toString());

            dbMetadataProcessor.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSQLVersion(){
         String driver_class = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /*如何连接上sqlexpress实例?*/
         String conn_str = "jdbc:sqlserver://localhost;IntegratedSecurity=true;instance=./sqlexpress;";
        String conn_str1 = "jdbc:sqlserver://localhost\\sqlexpress;IntegratedSecurity=true;";

        try {
            Class.forName(driver_class);
            Connection con = DriverManager.getConnection(conn_str1 + "DatabaseName=master;");

            ResultSet rs = con.createStatement().executeQuery("select @@version");
            if (rs.next()){
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

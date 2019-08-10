package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;

/**
 * Created by sam on 2019/8/4.
 */
public class ChromeAppMsgProcessor {
    protected Logger logger = LogManager.getLogger(ChromeAppMsgProcessor.class);
    String driver_class = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /*如何连接上sqlexpress实例? 参考：https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-2017*/
//    String conn_str = "jdbc:sqlserver://localhost\\SQLEXPRESS;IntegratedSecurity=true;";
    String conn_str = "jdbc:sqlserver://localhost;IntegratedSecurity=true;";

    private ChromeAppRequestMessage caMsg;

    public ChromeAppMsgProcessor(ChromeAppRequestMessage msg) {
        this.caMsg = msg;
    }

    public ChromeAppMsgProcessor(String msg) {
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


    /*使用json产生json串*/
    public String query(Connection con) throws SQLException, ClassNotFoundException {
        Statement stmt = con.createStatement();//创建Statement
        ResultSet rs = stmt.executeQuery(caMsg.getSqlText());

        JSONArray response = new JSONArray();
        while (rs.next()) {
            JSONObject rec = new JSONObject();

            /*mssql resultset的下标从1开始*/
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                rec.put(rs.getMetaData().getColumnName(i) , rs.getObject(i));
            }
            response.put(rec);

        }

        rs.close();
        stmt.close();
        return response.toString();
    }

    @Deprecated
    public String query2(Connection con) throws SQLException, ClassNotFoundException {
        String response = "{\"success\":false}";
        Statement stmt = con.createStatement();//创建Statement
        ResultSet rs = stmt.executeQuery(caMsg.getSqlText());

        response = "[";
        while (rs.next()) {
            String l = "{";
                        /*mssql resultset的下标从1开始*/
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                l += "\"" + rs.getMetaData().getColumnName(i) + "\"";
                l += ":";
                int dataType = rs.getMetaData().getColumnType(i);
                if (dataType == -7  //bit
                        || dataType == -6  //tinyint
                        || dataType == -5  //bigint
                        || dataType == 2   //numeric
                        || dataType == 3   //decimal
                        || dataType == 4   //integer
                        || dataType == 5   //smallint
                        || dataType == 6   //float
                        || dataType == 7   //real
                        || dataType == 8   /*double*/) {
                    l += rs.getString(i);
                } else {
                    l += "\"" + rs.getString(i) + "\"";
                }
                if (i < rs.getMetaData().getColumnCount()) {
                    l += ",";
                }
            }

            l += "},";
            response += l;
        }
        if (response.equalsIgnoreCase("[")){
            response +="]";
        }else {
            response = response.substring(0, response.length() - 1) + "]";
        }
        rs.close();
        stmt.close();
        return response;
    }

    public String execute(Connection con) throws ClassNotFoundException, SQLException {
        String response = "{\"success\":true}";
        logger.info(caMsg.getSqlText());
        con.createStatement().execute(caMsg.getSqlText());
        con.commit();
        return response;
    }

    /*逐句执行sql语句, sqltext是jsonarray，多条sql语句*/
    public String executeOnebyOne(Connection con) throws ClassNotFoundException, SQLException {
        String response = "{\"success\":true}";

        JSONArray sqls = new JSONArray(caMsg.getSqlText());
        for (int i = 0 ; i < sqls.length(); i ++){
            String sql = sqls.getString(i);
//            logger.info(sql);
            con.createStatement().execute(sql);
            con.commit();
        }

        return response;
    }

    private void dropdb(Connection con) throws SQLException {
        String sql = "drop database " + caMsg.getDbname();
        con.createStatement().execute(sql);
        con.commit();
    }

    private void killspid(Connection con) throws SQLException {
        notExistCreateKillspid(con);

        String sql = "exec killspid '" + caMsg.getDbname() + "'";
        con.createStatement().execute(sql);
        con.commit();
    }

    /*断开数据的所有连接，否则删除时报异常*/
    private void notExistCreateKillspid(Connection con) throws SQLException {
        String killspid = "killspid";
        String sql = " create proc "
                + killspid
                + " (@dbname varchar(20)) "
                + " as "
                + " begin "
                + " declare @sql nvarchar(500); "
                + " declare @spid int; "
                + " set @sql='declare getspid cursor for select spid ' "
                + " + 'from sysprocesses where dbid in (select dbid from sysdatabases where name=''' +@dbname+''' )'; "
                + " exec(@sql); " + " open getspid ;"
                + " fetch next from getspid into @spid; "
                + " while @@fetch_status = 0 " + " begin "
                + " IF  @spid <> @@SPID " + "   exec('kill '+@spid); "
                + " fetch next from getspid into @spid " + " end ;"
                + " close getspid; " + " deallocate getspid; " + " end; ";

        String c = "DECLARE @Sql NVARCHAR(max);SELECT @Sql=' KILL '+RTRIM(spid)+';' FROM sys.sysprocesses WHERE dbid=DB_ID('DB') AND spid<>@@SPID;" +
                " EXEC(@Sql)";
        String existprocsql = "select object_id('" + killspid + "') as name;";
        ResultSet rs = con.createStatement().executeQuery(existprocsql);

        boolean ifExist = false;
        if (rs.next()) {
            ifExist = rs.getString("name") != null;
        }
        rs.close();
        if (!ifExist) {
		/* 该存储过程不存在，则创建 */
            con.createStatement().execute(sql);
        }
    }

    private void createdb(Connection con) throws SQLException {
        String sql = "create database " + caMsg.getDbname();
        con.createStatement().execute(sql);
        con.commit();
    }

    public String initdb(Connection con) throws ClassNotFoundException, SQLException {
        String response = "{\"success\":true}";

        String dbname = caMsg.getDbname();
        JSONArray sqls = new JSONArray(caMsg.getSqlText());

//        logger.info(caMsg.getDbname());
        ResultSet rs = con.createStatement().executeQuery("select name from master.dbo.sysdatabases where [name]='"
                + dbname + "'");
        if (rs != null){
            if (rs.next()) {
                String name = rs.getString("name");
                killspid(con);
                dropdb(con);
//                logger.info("dropdb db.");
            }
            createdb(con);
//            logger.info("create db.");

            /*切换当前数据库*/
            con.createStatement().execute("use " + dbname);

//            logger.info(caMsg.getSqlText());
            /*逐条执行sqltext中的sql语句*/
            executeOnebyOne(con);
        }else{
            response = "{\"success\":false}";
        }
        rs.close();

        return response;
    }

    public String help(Connection con) {
        String response = "{\"success\":true}";

        return response;
    }

    /*注意：chrome要求的json格式严格*/
    public String process() throws Exception {

        Class.forName(driver_class);
        Connection con = null;

        String response = "{\"success\":false}";
        if (this.caMsg != null) {
            if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.query) {
                 con = DriverManager.getConnection(conn_str + "DatabaseName=" + caMsg.getDbname() + ";");
                response = query(con);
            } else if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.execute) {
                 con = DriverManager.getConnection(conn_str + "DatabaseName=" + caMsg.getDbname() + ";");
                response = execute(con);
            } else if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.initdb) {
                 con  = DriverManager.getConnection(conn_str + "DatabaseName=master;");
                response = initdb(con);
            } else if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.help) {
                 con = DriverManager.getConnection(conn_str + "DatabaseName=" + caMsg.getDbname() + ";");
                response = help(con);
            }  else if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.requireddbtree) {
                con  = DriverManager.getConnection(conn_str + "DatabaseName=master;");
                DBMetadataProcessor dbMetadataProcessor = new DBMetadataProcessor(con);
                dbMetadataProcessor.init(caMsg.getDbname());
                JSONObject tree = dbMetadataProcessor.getRequiredDBTree(caMsg.getRequiredb());
                response = tree.toString();
                dbMetadataProcessor.close();
            } else if (caMsg.getRequestType() == ChromeAppRequestMessage.RequestType.dbtree) {
                con  = DriverManager.getConnection(conn_str + "DatabaseName=master;");
                DBMetadataProcessor dbMetadataProcessor = new DBMetadataProcessor(con);
                dbMetadataProcessor.init(caMsg.getDbname());
                JSONObject tree = dbMetadataProcessor.getDBTree(caMsg.getDbname());
                response = tree.toString();
                dbMetadataProcessor.close();
            }else {
                response = "{\"success\":false}";
            }
        }

        if (con != null) {
            con.close();
        }
        return response;
    }
}

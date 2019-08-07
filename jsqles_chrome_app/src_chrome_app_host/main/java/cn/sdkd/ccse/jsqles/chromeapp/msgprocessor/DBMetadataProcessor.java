package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * 主要功能：从数据库获取数据库对象的数据以及元数据。

 * 数据库浏览器
 * 获取数据库的资源
 * 以树形结构展示给用户
 * 让教师用户选择作为实验题目的结果检验的对照。
 *
 * 在js中使用ado访问数据库仅在IE中支持。
 * 若提示：
 * ado 安全警告；
 * 此计算机上的安全设置禁止访问其它域的数据源
 *
 * 则需要修改浏览器配置要求：
 *
 * 将站点加入本地Intranet；
 * 设置本地Intranet的信任级别为低
 *
 *
 * 关于xtype
 *
C = CHECK 约束
D = 默认值或 DEFAULT 约束
F = FOREIGN KEY 约束
PK = PRIMARY KEY 约束（类型是 K）
UQ = UNIQUE 约束（类型是 K）

U = 用户表
V = 视图
P = 存储过程
TR = 触发器
FN = 标量函数
R = 规则

RF = 复制筛选存储过程
S = 系统表
TF = 表函数
IF = 内嵌表函数
X = 扩展存储过程
L = 日志
 */

/* 使用sqlserver sp_help等存储过程 实现
 * sqlserver 2008
 *
 * */

/**
 * 从mssqlserver数据库中元数据以及数据
 * <p>
 * Created by sam on 2019/8/7.
 */
public class DBMetadataProcessor {
    protected Logger logger = LogManager.getLogger(DBMetadataProcessor.class);

    private int dataMaxSize = 20;
    private String driver_class = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /*如何连接上sqlexpress实例?*/
    private String conn_str = "jdbc:sqlserver://localhost:1533;IntegratedSecurity=true;instance=./sqlexpress;";
    private Connection con = null;

    private Map<String, String> objectTypeMap;

    public void init(String dbname) throws ClassNotFoundException, SQLException {
        Class.forName(driver_class);
        con = DriverManager.getConnection(conn_str + "DatabaseName=" + dbname + ";");

        objectTypeMap = new HashMap<String, String>();

        objectTypeMap.put("U ", "tables");
        objectTypeMap.put("V ", "views");
        objectTypeMap.put("FN ", "functions");
        objectTypeMap.put("P ", "procedures");
        objectTypeMap.put("D ", "defaults");
        objectTypeMap.put("R ", "rules");

    }

    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public Map<String, String> getObjectTypeMap() {
        return objectTypeMap;
    }

    public JSONObject getRequiredDBTree(String jsonRequiredb) throws SQLException {
        JSONObject requiredb = new JSONObject(jsonRequiredb);
        JSONArray databases = (JSONArray) requiredb.get("database");
        JSONObject database = (JSONObject) databases.get(0);
        if (database == null) {
            return new JSONObject();
        }
        ;

        JSONObject dbtree = new JSONObject();
        dbtree.put("database", new JSONArray());
        JSONObject db = new JSONObject();
        dbtree.getJSONArray("database").put(db);
        db.put("name", database.getString("name"));
        getAllFiles(db);

        Iterator it = this.getObjectTypeMap().keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String type = this.getObjectTypeMap().get(key);

            if (database.has(type)) {
                JSONArray objs = database.getJSONArray(type);
                JSONArray type_objs = new JSONArray();
                db.put(type, type_objs);
                for (int i = 0; i < objs.length(); i++) {
                    JSONObject obj = objs.getJSONObject(i);
                    JSONObject objInfo = getObjectInfo(obj.getString("full_name"));

                    type_objs.put(objInfo);
                    if (type.equalsIgnoreCase("tables")) {
                        this.getTriggerInfo(objInfo);
                        this.queryData(objInfo, this.dataMaxSize);

					    /* 对于table，会发生 Error:There is no text for object */
                        // var text = this.sp_helptext(o.full_name);
                        // this.process_sphelptext(o, text);

                    } else if (type.equalsIgnoreCase("defaults") || type.equalsIgnoreCase("rules")) {
                        this.processHelpText(objInfo);
                    }
                }
            }
        }

        return dbtree;
    }

    public JSONObject processHelpText(JSONObject o) throws SQLException {
        String sql = "exec sp_helptext " + o.getString("full_name");
        ResultSet rs = con.createStatement().executeQuery(sql);
        String text = "";
        while (rs.next()) {
            text += rs.getString(1);
        }
        o.put("text", text);
        rs.close();
        return o;
    }

    public JSONObject queryData(JSONObject o, int topN) throws SQLException {
        String sql = "select top " + topN + " * from " + o.getString("full_name");
        ResultSet rs = con.createStatement().executeQuery(sql);
        JSONArray data = getPropertiesFromResultSet(rs);
        o.put("data", data);
        rs.close();
        return o;
    }

    public JSONObject processNextResultSet(JSONObject jsonObject, CallableStatement cstmt) throws SQLException {

        boolean oprFlg = cstmt.getMoreResults();
        while (oprFlg) {
            ResultSet rs = cstmt.getResultSet();
            JSONObject rec = new JSONObject();
            String fieldName = "";
            String fieldValue = "";
            if (rs.next()){
                fieldName = rs.getMetaData().getColumnName(1);
                fieldValue = rs.getString(1);
                getPropertyFromResultSet(rec, rs);
            }else{
                continue;
            }

            JSONArray props = this.getPropertiesFromResultSet(rs);
            JSONArray ps = new JSONArray();
            ps.put(rec);
            for (int i = 0; i < props.length(); i++) {
                JSONObject o = props.getJSONObject(i);
                ps.put(o);
            }
            props  = ps;

            if (fieldName.equalsIgnoreCase("Column_name")) {
                jsonObject.put("columns", props);
            } else if (fieldName.equalsIgnoreCase("Identity")) {
                if (!fieldValue.equalsIgnoreCase("No identity column defined.")) {
                    jsonObject.put("identities", props);
                }
            } else if (fieldName.equalsIgnoreCase("RowGuidCol")) {
                if (!fieldValue.equalsIgnoreCase("No rowguidcol column defined.")) {
                    jsonObject.put("rowguidcols", props);
                }
            } else if (fieldName.equalsIgnoreCase("Parameter_name")) {
                jsonObject.put("parameters", props);
            } else if (fieldName.equalsIgnoreCase("Data_located_on_filegroup")) {

            } else if (fieldName.equalsIgnoreCase("index_name")) {
                jsonObject.put("indexes", props);
            } else if (fieldName.equalsIgnoreCase("constraint_type")) {

                for (int i = 0; i < props.length(); i++) {
                    JSONObject o = props.getJSONObject(i);
                    if (o.getString("constraint_type").equalsIgnoreCase("FOREIGN KEY")) {
                        JSONObject ref = props.getJSONObject(i + 1);
                        o.put("references", ref.get("constraint_keys"));
                        props.remove(i + 1);
                    }
                }
                jsonObject.put("constraints", props);
            } else if (fieldName.equalsIgnoreCase("Table is referenced by foreigh key")) {

            } else if (fieldName.equalsIgnoreCase("Table is referenced by views")) {

            } else if (fieldName.equalsIgnoreCase("trigger_name")) {
                jsonObject.put("triggers", props);
            }


            oprFlg = cstmt.getMoreResults();
        }

        return jsonObject;
    }

    public JSONObject getObjectInfo(String fullName) throws SQLException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("full_name", fullName);
        CallableStatement cstmt = con.prepareCall("exec sp_help '" + fullName + "'");
        boolean oprFlg = cstmt.execute();
        if (oprFlg) {
            /* 处理第一个数据集和获取对象的基本信息 */
            ResultSet rs = cstmt.getResultSet();
            rs.next();
            jsonObject.put("name", rs.getObject(1));
            jsonObject.put("schema_name", rs.getObject(2));
            jsonObject.put("type", rs.getObject(3));
            rs.close();
        }

        processNextResultSet(jsonObject, cstmt);

        cstmt.close();
        return jsonObject;
    }

    /*获取触发器的属性*/
    public JSONObject getTriggerInfo(JSONObject jsonObject) throws SQLException {
        CallableStatement cstmt = con.prepareCall("exec sp_helptrigger '" + jsonObject.getString("full_name") + "'");
        boolean oprFlg = cstmt.execute();
        if (oprFlg) {
            ResultSet rs = cstmt.getResultSet();
            if (rs.next()) {
                jsonObject.put("trigger_name", rs.getString(1));
            }
            rs.close();
        }
        cstmt.close();
        return jsonObject;
    }

    /*返回两个结果集*/
    public JSONObject getAllFiles(JSONObject db) throws SQLException {

        CallableStatement cstmt = con.prepareCall("exec sp_helpdb " + db.getString("name"));
//        ResultSet dbrs = cstmt.executeQuery();
        boolean oprFlg = cstmt.execute();
        if (oprFlg) {
            ResultSet dbrs = cstmt.getResultSet();
            if (dbrs.next()) {
                getPropertyFromResultSet(db, dbrs);
            }
            dbrs.close();
        }
        oprFlg = cstmt.getMoreResults();
        if (oprFlg) {
            ResultSet filesrs = cstmt.getResultSet();
            JSONArray files = getPropertiesFromResultSet(filesrs);
            ;
            db.put("files", files);
            filesrs.close();
        }
        cstmt.close();

        return db;
    }

    /* 从多条结果的rs中获取对象数组 */
    public JSONArray getPropertiesFromResultSet(ResultSet rs) throws SQLException {
        JSONArray jsonArray = new JSONArray();

        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            getPropertyFromResultSet(jsonObject, rs);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /*从数据集rs中获取一行的数据*/
    public JSONObject getPropertyFromResultSet(JSONObject jsonObject, ResultSet rs) throws SQLException {

        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            int type = rs.getMetaData().getColumnType(i);
            switch (type) {
                case 93: /* 日期类型 */
                    java.sql.Date d = rs.getDate(i);
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:SSS");
                    String s = format1.format(d);  /* 毫秒如果末尾是俩零则会变成一个零， */
                    jsonObject.put(rs.getMetaData().getColumnName(i), s);

                default:
                    jsonObject.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
            }
        }
        return jsonObject;
    }
}

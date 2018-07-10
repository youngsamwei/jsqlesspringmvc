package com.wangzhixuan.test;

/**
 * Created by sam on 2018/7/9.
 */
import java.sql.*;

public class ConnManager {
    final static String cfn = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    final static String jtds = "net.sourceforge.jtds.jdbc.Driver";
    final static String url = "jdbc:jtds:sqlserver://localhost:1433/jsqles3";

    public static void main(String[] args) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet res = null;
        try {
            Class.forName(jtds);
            con = DriverManager.getConnection(url,"jsqles","Sise224");

            String sql = "select *from test";//查询test表
            statement = con.prepareStatement(sql);
            res = statement.executeQuery();
            while(res.next()){
                String title = res.getString("test_name");//获取test_name列的元素                                                                                                                                                    ;
                System.out.println("姓名："+title);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            try {
                if(res != null) res.close();
                if(statement != null) statement.close();
                if(con != null) con.close();
            } catch (Exception e2) {
                // TODO: handle exception
                e2.printStackTrace();
            }
        }
    }
}

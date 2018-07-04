package cn.sdkd.ccse.jsqles.service.impl.test;

import cn.sdkd.ccse.jsqles.service.impl.ExercisebookServiceImpl;

/**
 * Created by sam on 2018/3/23.
 */
public class ExercisebookServiceImplTest {
    ExercisebookServiceImpl exercisebookService;
    /**
     * 作答
     */
    static String answer =  "{'database':[{'name':'testdb','files':[{'name':'testdb','fileid':1,'filename':'D:/Program Files (x86)/Microsoft SQL Server/MSSQL10.SQLEXPRESS/MSSQL/DATA/testdb.mdf','filegroup':'PRIMARY','size':'2304 KB','maxsize':'Unlimited','growth':'1024 KB','usage':'data only'},{'name':'testdb_log','fileid':2,'filename':'D:/Program Files (x86)/Microsoft SQL Server/MSSQL10.SQLEXPRESS/MSSQL/DATA/testdb_log.LDF','filegroup':null,'size':'576 KB','maxsize':'2147483648 KB','growth':'10%','usage':'log only'}],'tables':[{'schema_name':'dbo','type':'user table','name':'student','full_name':'dbo.student','object_id':2137058649,'columns':[{'Column_name':'sno','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'no','TrimTrailingBlanks':'no','FixedLenNullInSource':'no','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sname','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'no','TrimTrailingBlanks':'no','FixedLenNullInSource':'no','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sage','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'ssex','Type':'char','Computed':'no','Length':6,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sdept','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'}],'indexes':[{'index_name':'PK__student__DDDF64467F60ED59','index_description':'clustered, unique, primary key located on PRIMARY','index_keys':'sno'}],'constraints':[{'constraint_type':'PRIMARY KEY (clustered)','constraint_name':'PK__student__DDDF64467F60ED59','delete_action':'(n/a)','update_action':'(n/a)','status_enabled':'(n/a)','status_for_replication':'(n/a)','constraint_keys':'sno'}],'data':[]}]}]}";
    /**
     * 作答的查询结果
     */
    static  String resultSet = null;
    /**
     * 问题的答案
     */
    static String quesEval = "{'database':[{'name':'testdb','tables':[{'schema_name':'dbo','typeabbr':'U ','type':'user table','name':'student','object_id':2137058649,'full_name':'dbo.student','columns':[{'Column_name':'sno','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'no','TrimTrailingBlanks':'no','FixedLenNullInSource':'no','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sname','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'no','TrimTrailingBlanks':'no','FixedLenNullInSource':'no','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sage','Type':'char','Computed':'no','Length':10,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'ssex','Type':'char','Computed':'no','Length':6,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'},{'Column_name':'sdept','Type':'char','Computed':'no','Length':8,'Prec':'     ','Scale':'     ','Nullable':'yes','TrimTrailingBlanks':'no','FixedLenNullInSource':'yes','Collation':'Chinese_PRC_CI_AS'}],'indexes':[{'index_name':'PK__student__DDDF6446014935CB','index_description':'clustered, unique, primary key located on PRIMARY','index_keys':'sno'}],'constraints':[{'constraint_type':'PRIMARY KEY (clustered)','constraint_name':'PK__student__DDDF6446014935CB','delete_action':'(n/a)','update_action':'(n/a)','status_enabled':'(n/a)','status_for_replication':'(n/a)','constraint_keys':'sno'}],'data':[]}]}]}";
    /**
     * 问题的查询结果
     */
    static String quesResult = null;

    public static void main(String[] args){
        ExercisebookServiceImpl exercisebookService = new ExercisebookServiceImpl();
        String r = exercisebookService.doVerify(answer, resultSet, quesEval, quesResult);

        System.out.println(r);
    }
}

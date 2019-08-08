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
var dbmetadata2 = {};
dbmetadata2.jsqlesChromeExtensionId = "oeejofojochggegmkbmjbjhiojakbcme";
dbmetadata2.defaultDBName = "TestDB";
dbmetadata2.dataMaxSize = 20;

dbmetadata2.getObjectTypeMap = function() {
	var map = {};
	map['U '] = 'tables';
	map['V '] = 'views';
	map['FN'] = 'functions';
	map['P '] = 'procedures';
	map['D '] = 'defaults';
	map['R '] = 'rules';

	/* map['FILES'] = 'files'; */
	return map;
}

dbmetadata2.initJsonArray = function(o, name) {
	if (!o[name]) {
		o[name] = [];
	}
}
/*
 * 获取所有的用户自定义数据类型 User_Type，storage_type,length, prec, scale, nullable,
 * default_name, rule_name, collation
 */
dbmetadata2.getAllUserTypes = function() {

	return {};
}

/*日期格式化*/
function formatterdate(val, row) {

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10
            ? "0" + n
            : n;
    }

    if (val != null) {
        var date = new Date(val);
        return date.getFullYear() + '-' + f(date.getMonth() + 1) + '-' + f(date.getDate());
    }
}

/*在chrome中采用扩展实现*/
dbmetadata2.query = function(sql, dbname, quesid, dbtree, postext, submit_callback) {
	if (sql) {

        var request = {requestType: "query", dbname:dbname, sqlText:sql};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(json_resultset) {
//            console.log("response: " + JSON.stringify(response));
            submit_callback(quesid, dbtree, postext, json_resultset);
            return ;
        });

	} else {
		return {}
	}
}

/*在chrome中采用扩展实现*/
dbmetadata2.query = function(sql, dbname, query_callback) {
	if (sql) {

        var request = {requestType: "query", dbname:dbname, sqlText:sql};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(json_resultset) {
            query_callback(json_resultset);
        });

	} else {
		return {}
	}
}

dbmetadata2.execute = function(sql) {
	if (sql) {
        var request = {requestType: "execute", dbname:this.defaultDBName, sqlText:sql};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(json_resultset) {
//            console.log("response: " + JSON.stringify(response));
            submit_callback(json_resultset);
            return ;
        });
	}
}

dbmetadata2.execute = function(sql, dbname, execute_callback) {
	if (sql) {
        var request = {requestType: "execute", dbname:dbname, sqlText:sql};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(json_resultset) {
            execute_callback(json_resultset);
        });
	}
}

dbmetadata2.initDB = function(quesPreq, sqls, initdb_callback) {
	var database = quesPreq.database[0];
	if (database && database.name) {
        var request = {requestType: "initdb", dbname:database.name, sqlText : JSON.stringify(sqls)};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(json_resultset) {
            initdb_callback(json_resultset);
        });
	}

}

dbmetadata2.getRequiredDBTree = function(requiredb, quesid, postext, resultset, submit_callback){
    var dbname = requiredb.database[0].name;
    var request = {requestType: "requireddbtree", dbname:dbname, requiredb:JSON.stringify(requiredb)};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(dbtree) {
            submit_callback(quesid, dbtree, postext, resultset);
        });
}

dbmetadata2.getDBTree = function(dbname, getdbtree_callback) {
        var request = {requestType: "dbtree", dbname:dbname, sqlText : ""};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(dbtree) {
            getdbtree_callback(dbtree);
        });
}
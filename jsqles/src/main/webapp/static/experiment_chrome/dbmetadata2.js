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

dbmetadata2.getObjectPropertyKeys = function() {
	var map = {};
	map['schema_name'] = 'schema_name';
	map['type'] = 'type';
	map['name'] = 'name';
	map['full_name'] = 'full_name';
	map['object_id'] = 'object_id';

	return map;
}

/*获取指定数据库的所有对象的属性*/
dbmetadata2.getDBTree = function(dbname) {
	var db = {};
	db.database = [];
	var database = this.getAllObjects(dbname);

	this.getAllFiles(database);
	db.database.push(database);
	return db;
}

dbmetadata2.getAllFiles = function(database) {
	dbmanager.initMasterConnection();

	var dbrs = dbmanager.sp_helpdb(database.name);
	var db = this.getPropertyFromResultSet(dbrs);
	/*Ext.apply(database, db);*/

	var filesrs = dbrs.NextRecordset();
	var files = this.getPropertiesFromResultSet(filesrs);
	database.files = files;

	dbmanager.closeMasterConnection();
}

/* 获取所有的对象 Name, owner, object type */
dbmetadata2.getAllObjects = function(dbname) {
	this.initConnection(dbname);
	var database = {};
	database.name = dbname;
	var sql = "select  s.name as schema_name, o.type as typeabbr, substring(v.name,5,31) as type, o.name, o.object_id  "
			+ " from sys.all_objects o, sys.schemas s , master.dbo.spt_values v"
			+ " where o.schema_id = s.schema_id and s.name not in ('sys', 'INFORMATION_SCHEMA') "
			+ " and o.type in ('u', 'v', 'fn', 'p', 'r', 'd') and o.type = substring(v.name,1,2) collate database_default  and v.type = 'O9T' "
			+ " order by schema_name, type , name asc  ";

	var objrs = this.objdbConn.Execute(sql);

	var map = this.getObjectTypeMap();
	while (!objrs.EOF) {
		var o = {};
		o.schema_name = objrs.Fields(0).Value;
		o.typeabbr = objrs.Fields(1).Value;
		o.type = objrs.Fields(2).Value;
		o.name = objrs.Fields(3).Value;
		o.object_id = objrs.Fields(4).Value;
		o.full_name = o.schema_name + "." + o.name;

		var sp_help_rs = this.sp_help(o.full_name);
		var nextRs = sp_help_rs.NextRecordset();
		this.process_sphelp_resultset(o, nextRs);

		this.initJsonArray(database, map[o.typeabbr]);
		database[map[o.typeabbr]].push(o);

		if (map[o.typeabbr] === 'tables') {
			var triggerRs = this.sp_helptrigger(o.full_name);
			this.process_sphelp_resultset(o, triggerRs);
			/* 查询前10条数据 */
			this.query_data(o, this.dataMaxSize);
			/* 加上触发器的文本 */
			// var text = this.sp_helptext(o.full_name);
			// this.process_sphelptext(o, text);
		}
		if ((map[o.typeabbr] === 'defaults') || (map[o.typeabbr] === 'rules')) {
			var text = this.sp_helptext(o.full_name);
			this.process_sphelptext(o, text);
		}
		objrs.moveNext();
	}

    /* 增加对自定义role的初始化*/
    this.getAllUserRoles(database);

	this.closeConnection();

	return database;
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

dbmetadata2.getAllUserRoles = function(database){
    var sql = "SELECT name, type, type_desc "
               + " FROM sys.database_principals "
               + " where name like 'role_%'";
    var objrs = this.objdbConn.Execute(sql);

    while (!objrs.EOF) {
        var o = {};
		o.role_name = objrs.Fields(0).Value;
		this.initJsonArray(database, "roles");
        database["roles"].push(o);

        objrs.moveNext();
    }

}

dbmetadata2.query_data = function(o, topN) {
	var sql = "select top " + topN + " * from " + o.full_name;
	var objrs = this.objdbConn.Execute(sql);
	o.data = this.getPropertiesFromResultSet(objrs);
}

dbmetadata2.process_sphelp_resultset = function(o, nextRs) {
	while (nextRs) {

		if (nextRs.State == 0) {
			break;
		}
		if (!nextRs.EOF) {
			var rsName = nextRs.Fields.item(0).Name;
			var rsValue = nextRs.Fields.item(0).Value;
			var props = this.getPropertiesFromResultSet(nextRs);
			switch (rsName) {
			case 'Column_name':
				o.columns = props;
				break;
			case 'Identity':
				if (!(rsValue === 'No identity column defined.')) {
					o.identities = props;
				}
				break;
			case 'RowGuidCol':
				if (!(rsValue === 'No rowguidcol column defined.')) {
					o.rowguidcols = props;
				}
				break;
			case 'Parameter_name':
				o.parameters = props;
				break;
			case 'Data_located_on_filegroup':
				break;
			case 'index_name':
				o.indexes = props;
				break;
			case 'constraint_type':
				this.post_process_foreignkey(props);
				o.constraints = props;
				break;
			case 'Table is referenced by foreigh key':
				break;
			case 'Table is referenced by views':
				break;
			case 'trigger_name':
				o.triggers = props;
				break;
			}

		}

		nextRs = nextRs.NextRecordset();
	}
	return o;
}

/* 从单条结果的rs中获取一个对象 */
/* 2018-07-18 在从sqlserver查询得到的datetime，转换后丢失时间*/
dbmetadata2.getPropertyFromResultSet = function(nextRs) {
	var o = {};
	for (var i = 0; i < nextRs.fields.count; i++) {
		var item = nextRs.fields.item(i);
		switch (item.Type) {
		case 135: /* 日期类型 */
			var d = new Date(item.Value);
			var f = formatterdate(d); /* 毫秒如果末尾是俩零则会变成一个零， */
			o[item.name] = f;
			break;
		default:
			o[item.name] = item.value;
		}

	}
	return o;
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
/* 从多条结果的rs中获取对象数组 */
dbmetadata2.getPropertiesFromResultSet = function(nextRs) {
	var objs = [];
	while (!nextRs.EOF) {
		var o = this.getPropertyFromResultSet(nextRs);
		objs.push(o);
		nextRs.moveNext();
	}
	return objs;
}

/* 返回json格式的查询结果. */
dbmetadata2.query = function(sql) {
	if (sql) {
		this.initConnection();
		var rs = this.objdbConn.Execute(sql);
		var json = this.getPropertiesFromResultSet(rs);
		this.closeConnection();
		return json;
	} else {
		return {}
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

dbmetadata2.initDB = function(quesPreq, initdb_callback) {
	var database = quesPreq.database[0];
	if (database && database.name) {
        var request = {requestType: "initdb", dbname:database.name};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(json_resultset) {
            initdb_callback(json_resultset);
        });
	}

}

dbmetadata2.post_process_foreignkey = function(cons) {
	for (var i = 0; i < cons.length; i++) {
		var con = cons[i];
		if (con.constraint_type === 'FOREIGN KEY') {
			var ref = cons[i + 1];
			con.references = ref.constraint_keys;
			/*cons.remove(ref);   extjs 的用法*/
			cons.splice(i + 1, 1); /*jquery 用法*/
		}
	}
}

dbmetadata2.sp_help = function(object_name) {
	var sql = "exec sp_help '" + object_name + "'";
	var objrs = this.objdbConn.Execute(sql);
	return objrs;
}

dbmetadata2.sp_helptrigger = function(object_name) {
	var sql = "exec sp_helptrigger '" + object_name + "'";
	var objrs = this.objdbConn.Execute(sql);
	return objrs;
}

dbmetadata2.sp_helptext = function(object_name) {
	var sql = "exec sp_helptext '" + object_name + "'";
	var objrs = this.objdbConn.Execute(sql);
	return objrs;
}

dbmetadata2.getRequiredDBTree = function(requiredb, dbname, quesid, postext, resultset, submit_callback){
    var request = {requestType: "requireddbtree", dbname:dbname, requiredb:JSON.stringify(requiredb)};
        chrome.runtime.sendMessage(this.jsqlesChromeExtensionId, request,
          function(dbtree) {
            submit_callback(quesid, dbtree, postext, resultset);
        });
}

/* 多行文本合并 */
dbmetadata2.process_sphelptext = function(o, objrs) {
	var text = "";
	while (!objrs.EOF) {
		text = text + " " + objrs.Fields(0).Value;
		objrs.moveNext();
	}

	o.text = text;
}
/* 单行的信息 */
dbmetadata2.process_sphelp_baseinfo = function(o, objrs) {
	o.name = objrs.Fields(0).Value;
	o.schema_name = objrs.Fields(1).Value;
	o.type = objrs.Fields(2).Value;

}
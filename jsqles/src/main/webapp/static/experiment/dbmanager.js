/*
主要功能：连接数据库，执行数据库脚本，建立数据库,数据库表等对象。

*/

var dbmanager = {};

dbmanager.isexpress = true;

/* */
dbmanager.initMasterConnection = function() {
	var objMasterConn = new ActiveXObject("ADODB.Connection");
	var strdsn = "";
	if (this.isexpress) {
		strdsn = "Integrated Security=SSPI;Provider=SQLOLEDB.1;Data Source=.\\sqlexpress;Initial Catalog=master";
	} else {
		strdsn = "Driver={SQL Server};SERVER=(local);DATABASE=master";
	}

	objMasterConn.Open(strdsn);
	this.objMasterConn = objMasterConn;
}

dbmanager.closeMasterConnection = function() {
	if (this.objMasterConn && this.objMasterConn.State != 0) {
		this.objMasterConn.close();
	}
}

/* 默认数据库怎么不起作用呢？在执行判别sql语句时提示无效的borrow对象*/
dbmanager.initConnection = function(dbname) {
	this.objdbConn = new ActiveXObject("ADODB.Connection");
	var strdsn = "";
	if (this.isexpress) {
		strdsn = "Integrated Security=SSPI;Provider=SQLOLEDB.1;Data Source=.\\sqlexpress;Initial Catalog=" + dbname;
	} else {
		strdsn = "Driver={SQL Server};SERVER=(local);DATABASE=" + dbname;
	}	

	this.objdbConn.Open(strdsn);
	return this.objdbConn && this.objdbConn.State != 0;
}

dbmanager.closeConnection = function() {
	if (this.objdbConn && this.objdbConn.State != 0) {
		this.objdbConn.close();
	}
}

dbmanager.dropDB = function(dbname) {
	var sql = "drop database " + dbname;
	var r = this.objMasterConn.Execute(sql);
}

dbmanager.createDB = function(dbname) {
	var sql = "create database " + dbname;
	var r = this.objMasterConn.Execute(sql);
}

dbmanager.existDB = function(dbname) {
	var sql = "select name from master.dbo.sysdatabases where [name]='"
			+ dbname + "'";
	var rs = this.objMasterConn.Execute(sql);
	if ((rs != null) && (!rs.EOF)) {
		return true
	}
	return false;
}

dbmanager.sp_helpdb = function(database_name) {
	var sql = "exec sp_helpdb '" + database_name + "'";
	var objrs = this.objMasterConn.Execute(sql);

	return objrs;
}

/* 支持sqlserver 2005版本的insert */
dbmanager.createInsertData = function(table) {
	if (!(table.data) || (table.data.length <= 0)) {
		return "";
	}
	var sql = "";
	var sqlpre = "insert into " + table.name + "(" + this.getColumnList(table)
			+ ") values ";

	for (var di = 0; di < table.data.length; di++) {
		var value = "(";
		var adata = table.data[di];
		var keyi = 0;
		for ( var key in adata) {
			value = value + this.getDataQuoted(table.columns[keyi], adata[key])
					+ ","
			keyi++;
		}
		value = value.substring(0, value.length - 1);
		value = value + ");";
		sql = sql + sqlpre + value;
	}
	/*
	 * sql = sql.substring(0, sql.length - 1); sql = sql + ";";
	 */
	return sql;
}

dbmanager.getColumnList = function(table) {
	var cl = "";
	for (var cli = 0; cli < table.columns.length - 1; cli++) {
		cl = cl + table.columns[cli].Column_name + ",";
	}
	cl = cl + table.columns[table.columns.length - 1].Column_name;
	return cl;
}

/* 检查存储过程killspid是否存在，若不存在则创建一个 */
dbmanager.notExistCreateKillspid = function() {
	var killspid = "killspid";
	var sql = "create proc "
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

	var existprocsql = "select object_id('" + killspid + "') as name;";
	var rs = this.objMasterConn.Execute(existprocsql);
	var ifExist = false;
	if (!rs.EOF) {
		var v = rs.Fields(0).Value;
		ifExist = (v != null);
	}
	if (!ifExist) {
		/* 该存储过程不存在，则创建 */
		var rs = this.objMasterConn.Execute(sql);
	}
}

dbmanager.killspid = function(dbname) {

	this.notExistCreateKillspid();

	var sql = "exec killspid '" + dbname + "'";
	var rs = this.objMasterConn.Execute(sql);
	return rs;
}

dbmanager.getDataQuoted = function(column, adata) {
	var dataquoted = "";
	switch (column.Type) {
	case "char":
	case "varchar":
	case "text":
	case "ntext":
	case "nchar":
	case "nvarchar":
	case "binary":
	case "varbinary":
	case "datetime":
	case "samlldatetime":
		dataquoted = "'" + adata + "'";
		break;

	default:
		dataquoted = adata;
		break;
	}
	return dataquoted;
}

dbmanager.createTable = function(table) {
	var sql = "create table " + table.name + "(";
	if (table.columns) {
		var columns = table.columns;
		for (var ci = 0; ci < columns.length - 1; ci++) {
			sql = sql + this.getColumnDef(columns[ci]) + ", ";
		}
		sql = sql + this.getColumnDef(columns[columns.length - 1]);
		sql = sql + " );"

		var rs = this.objdbConn.Execute(sql);
	}
}

dbmanager.createTableDDL = function(table) {

	if (table.columns) {
		var sql = "create table " + table.name + "(";
		var columns = table.columns;
		for (var ci = 0; ci < columns.length - 1; ci++) {
			sql = sql + this.getColumnDef(columns[ci]) + ", ";
		}
		sql = sql + this.getColumnDef(columns[columns.length - 1]);
		sql = sql + " );"

		return sql;
	} else {
		throw "数据表" + table.name + "至少要包含一个字段。";
	}
}
/* 获取default对象的ddl文本 */
dbmanager.createDefaultDDL = function(d) {
	var text = d.text.toLowerCase().trim();
	if (text.indexOf("create ")==0) {
		return d.text;
	}
}

/* 获取default对象的ddl文本 */
dbmanager.createRuleDDL = function(r) {
	var text = r.text.toLowerCase().trim();
	if (text.indexOf("create ")==0) {
		return r.text;
	}
}

dbmanager.createTableConstraintDDL = function(table) {
	if (table.constraints) {
		var cons = table.constraints;
		var tablename = table.name;
		var sql = {};
		sql.fkcons = [];
		sql.othercons = [];
		for (var ci = 0; ci < cons.length; ci++) {
			var con = cons[ci];
			var type = con.constraint_type.toLowerCase();
			switch (true) {
			case (type.indexOf("foreign key")==0):
				sql.fkcons.push(" alter table " + tablename
						+ " add constraint " + con.constraint_name
						+ " foreign key (" + con.constraint_keys + ") "
						+ con.references + ";");
				break;
			case (type.indexOf("primary key")==0):
				sql.othercons.push(" alter table " + tablename
						+ " add constraint " + con.constraint_name
						+ " primary key (" + con.constraint_keys + "); ");
				break;
			case (type.indexOf("unique")==0):
				sql.othercons.push(" alter table " + tablename
						+ " add constraint " + con.constraint_name
						+ " unique (" + con.constraint_keys + "); ");
				break;
			case (type.indexOf("check")==0):
				/* TODO */
				break;
			case (type.indexOf("default")==0):
				/* TODO */
				break;
			}
		}
		return sql;
	} else {
		return {};
	}
}

dbmanager.getColumnDef = function(column) {
	/* Column_name, Type, Length, Prec Scale, Nullable */
	var columntext = (column.Nullable == 'yes' ? '' : 'not') + " null "
	switch (column.Type) {
	case "char":
	case "varchar":
	case "text":
	case "ntext":
	case "nchar":
	case "nvarchar":
	case "binary":
	case "varbinary":
		columntext = column.Column_name + " " + column.Type + "("
				+ column.Length + ") " + columntext;
		break;
	case "int":
	case "bigint":
	case "bit":
	case "smallint":
	case "tinyint":
	case "datetime":
	case "samlldatetime":
	case "float":
	case "real":
		columntext = column.Column_name + " " + column.Type + " " + columntext;
		break;
	case "numeric":
	case "decimal":
		columntext = column.Column_name + " " + column.Type + "(" + column.Prec
				+ "," + column.Scale + ") " + columntext;
		break;
	default:
		columntext = column.Column_name + " " + column.Type + " " + columntext;
		break;
	}

	return columntext;

}

dbmanager.dropTable = function() {

}


dbmanager.createRoleDDL = function(role){
    return "create role " + role.role_name + ";";
}
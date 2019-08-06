/*
主要功能：连接数据库，执行数据库脚本，建立数据库,数据库表等对象。

*/

var dbmanager = {};


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

/*
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
*/

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
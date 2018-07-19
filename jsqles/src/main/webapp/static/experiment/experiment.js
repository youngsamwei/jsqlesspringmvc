/*
 * 
 * 实验操作控制
 * 包括学生用户开始、提交实验操作；
 * 教师用户设置实验的检验标准。
 * 
 * */

var experiment = {};
/*用于显示信息的div id，默认名称为info*/
experiment.info_div_id = 'info';

/*
 * 开始一个实验前需要： 1）初始化实验环境：比如创建所需数据库以及数据对象；
 * 2）提交至后台一条消息：(quesid,timestamp)开始实验操作，并计时。
 */
experiment.start = function( ) {

	this.disabledBtn("start", true);

    var html = $.ajax({
        type: 'POST',
        dataType : 'json',
        url:  '/experiment/start',
        data: {
            "quesid" : this.quesid,
            "eval" : 'not'
        },
        success: function (msg) {
//            console.info(msg);

            var respjson = [];
            try {
                if (!msg || msg.length == 0) {
                    respjson = [];
                } else if (typeof msg == "string") {
                    respjson = eval("(" + msg + ")");
                } else {
                    respjson = msg;
                }
            } catch (err) {
                respjson = msg;
            }

			if (respjson.success) {
                $('#start').val('  提交  ');
                $('#start').attr('onclick', '');
                showMsg('开始成功!');
				//info = "请初始化数据库环境。";
				/*点击开始后初始化数据库*/
                experiment.init();

                $('#start').click( function(){experiment.submit()});

                $('#btn_init').show();
                if (experiment.ifpostext){
                   experiment.showBtns();
                }

			} else {

				experiment.showInfo("开始实验发生异常，请重新尝试。");
			}
			experiment.disabledBtn("start", false);

		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
//		    console.error(textStatus);
			experiment.disabledBtn("start", false);
			experiment.showInfo("开始实验发生异常，请重新尝试。");
		}
    }).responseText;
//    console.info(html);
}

experiment.disabledBtn = function(btnid, disabled) {
	var submitBtn = $('#'+ btnid);
	submitBtn.attr('disabled',disabled);
}

experiment.hideBtns = function(){
    if ($('#btn_init')){
        $('#btn_init').hide();
    }
    if ($('#sql_tr_tips')){
        $('#sql_tr_tips').hide();
    }
    if ($('#sql_tr_textarea')){
        $('#sql_tr_textarea').hide();
    }
};

experiment.showBtns = function(){
    if ($('#btn_init')){
        $('#btn_init').show();
    }
    if ($('#sql_tr_tips')){
        $('#sql_tr_tips').show();
    }
    if ($('#sql_tr_textarea')){
        $('#sql_tr_textarea').show();
    }
}
/*ie版本不同，可信站点的描述不同 */
experiment.getTrustSiteDesc = function(){
    if (!$.support.leadingWhitespace) {
        return "可信站点";
    }else{
        return "受信任的站点";
    }
}

experiment.ieconfig = "解决方法：<BR>第一步：浏览器菜单-->工具-->Internet选项-->安全-->" + experiment.getTrustSiteDesc()
+ "-->站点,将" + top.location.protocol + "//" + top.location.hostname + " 加入受信任站点。"
+"<BR>第二步：浏览器菜单-->工具-->Internet选项-->安全-->" + experiment.getTrustSiteDesc()
+"-->自定义级别-->找到“对标记为可安全执行脚本的ActiveX控件初始化并执行脚本”-->选择“启用” "
+"<BR>第三步：找到“通过域访问数据源”-->选择“启用”-->确定。 ";

experiment.init = function( ) {

    progressLoad();

    var quesid = this.quesid;
    var quesPreq = this.quesPreq;
    var errorHandler = this.errorHandler;
    var successHandler = this.successHandler;

	var infotext = "请打开SSMS连接本机的SQLEXPRESS实例，按照实验问题要求完成实验，然后点击提交。<BR>"
			+ "在SSMS中编写sql代码完成实验问题后，在提交时会提示同时提交sql代码。 <BR><BR>"
			+ "注意：在实验操作过程中，请勿刷新此页面，否则将重新初始化数据库，清除您操作的结果。<BR>"
			+ "但如果想重新初始化数据库，则可以刷新页面。";
	if (!quesPreq) {
		this.showInfo(infotext)
		successHandler();
		progressClose();
		return;
	}

    experiment.showInfo("正在准备初始化数据库...");
	/* 产生创建数据表，增加数据，创建约束 */
	var sqls = this.createSQLText(quesPreq);
	var db = quesPreq ? quesPreq.database[0].name : null;
	var exeSql = function(v) {
		return function() {
			try {
				/* 再初始化数据表，数据，约束 */
				dbmetadata2.execute(sqls[v], db);

				if (v == (sqls.length - 1)) {
					experiment.showInfo("初始化数据库完成." + infotext)

					experiment.successHandler();

					dbmetadata2.closeConnection();
					progressClose();

				} else {
					setTimeout(exeSql(v + 1), 100);
				}
			} catch (e) {
				experiment.showInfo(e.name + " : " + e.message);
				dbmetadata2.closeConnection();

				experiment.errorHandler(e.name + " : " + e.message);
				progressClose();
			}
		}
	};

	setTimeout(function() {

        experiment.showInfo("正在初始化数据库...");
		try {
			/* 初始化数据库 */
			experiment.initDB(quesPreq);
			/* 再初始化数据表，数据，约束 */
			if (sqls.length > 0) {
				setTimeout(exeSql(0), 100);
			} else {
				experiment.showInfo(infotext);
				experiment.successHandler();
				progressClose();
			}

		} catch (e) {

			dbmanager.closeMasterConnection();

			experiment.errorHandler(e.name, e.message);
			progressClose();
		}
	}, 500);

}

experiment.createSQLText = function(quesPreq) {
	var sqls = [];
	var database = quesPreq.database[0];
	if (database && database.name) {
		var tables = database.tables;
		if (tables) {
			/* 数据表 */
			for (var ti = 0; ti < tables.length; ti++) {
				sqls.push(dbmanager.createTableDDL(tables[ti]));
			}
			/* 数据 */
			for (var ti = 0; ti < tables.length; ti++) {
				if (tables[ti].data) {
					sqls.push(dbmanager.createInsertData(tables[ti]));
				}
			}
			/* 约束 */
			var fkcons = [];
			var othercons = [];
			for (var ti = 0; ti < tables.length; ti++) {
				if (tables[ti].constraints) {
					var consddl = dbmanager
							.createTableConstraintDDL(tables[ti]);
					fkcons = fkcons.concat(consddl.fkcons);
					othercons = othercons.concat(consddl.othercons);
				}
			}
			/* 把外键放在最后. */
			sqls = sqls.concat(othercons).concat(fkcons);
		}
		var defaults = database.defaults;
		if (defaults) {
			for (var di = 0; di < defaults.length; di++) {
				sqls.push(dbmanager.createDefaultDDL(defaults[di]));
			}
		}
		var rules = database.rules;
		if (rules) {
			for (var ri = 0; ri < rules.length; ri++) {
				sqls.push(dbmanager.createRuleDDL(rules[ri]));
			}
		}
		var roles = database.roles;
		if (roles){
		    for (var ri = 0; ri < roles.length; ri++) {
        				sqls.push(dbmanager.createRoleDDL(roles[ri]));
        	}
		}
	}
	return sqls;
}

experiment.initDB = function(quesPreq) {
	dbmanager.initMasterConnection();
	var database = quesPreq.database[0];
	if (database && database.name) {
		if (dbmanager.existDB(database.name)) {
			var rs = dbmanager.killspid(database.name);
			dbmanager.dropDB(database.name);
		}
		dbmanager.createDB(database.name);
	}
	dbmanager.closeMasterConnection();

}

/*
 * 获得本地数据库中的学生实验操作的结果，并提交至服务器。 由服务器验证其正确性。
 *
 * 获取本地数据库中指定对象的详细信息。
 */
experiment.submitsql = "";
experiment.submit = function( ) {

    progressLoad();
    var quesid = this.quesid;
    var quesRequiredb = this.quesRequiredb;
    var ifpostext = this.ifpostext;
    var resultquery = this.resultquery;
//    var db = this.db;
    var db = this.quesPreq ? this.quesPreq.database[0].name : null;
	experiment.disabledBtn("submit", true);
	/*
	 * 验证的过程： 1）若需要进行结构验证，则将dbtree提交至服务器进行结构比较。
	 *  2）若需要进行结果验证，则先判断答案代码类型
	 * 2.1）若是select类型，则使用用户提交的代码获取数据提交至服务器进行数据验证；
	 * 2.2）若是其他类型，则使用教师提交的代码获取数据提交至服务器进行数据验证；
	 *
	 */

    try {
        /* 获取结构数据 */
        var dbtree = null;
         var resultset = null;

        if (ifpostext){
            var postext = $('#editSQLForm_textarea').val();
            if (postext == "") {
                  throw {name:"错误", message:"需要提交代码！"}
            }
        }
        if (quesRequiredb) {/* 结构验证*/
            dbtree = dbmetadata2.getRequiredDBTree(quesRequiredb);
        }else{/*结果验证*/
            /*用户提交的代码，但有些题目不需要用户提交代码*/
            var postext = $('#editSQLForm_textarea').val();

            /* 获取结果数据 */
            if (!resultquery) {
                resultquery = postext;
            }

            if (typeof (resultquery) == "string" && resultquery.length > 0) {
                resultset = dbmetadata2.query(resultquery, db);
            }
        }

        /*使用JSON.stringify会把汉字转换为unicode，所以使用eval再变为汉字
        http://blog.csdn.net/yefengmeander/article/details/45192565
        */

        /* 使用json2不对汉字转换为unicode，因此不必使用eval */
        var jsonstr_dbtree = JSON2.stringify(dbtree);
        var encodeAnswer = jsonstr_dbtree;
//        eval("var encodeAnswer = '"+ jsonstr_dbtree + "';");
/*20180714 使用eval时，在创建default的语句中包含单引号，因此提示缺少分号，现在已经不是用eval，因此已经没有这个错误*/

        var jsonstr_resultset = JSON2.stringify(resultset);
        var encodeResultset = jsonstr_resultset;
//        eval("var encodeResultset = '"+ jsonstr_resultset +"';");
        $.ajax({
            type: 'POST',
            dataType : 'json',
            url : '/experiment/submit',
            data : {
                "quesid" : quesid,
                "answer" : encodeAnswer, /* 用于结构验证的操作结果 */
                "postext" : postext, /* 用户提交的代码，有些题目不需要用户提交代码。结构验证将来可能也需要用户提交代码。 */
                "resultset" : encodeResultset
            },
            success : function(msg) {
                var respjson = [];
                try {
                    if (!msg || msg.length == 0) {
                        respjson = [];
                    } else if (typeof msg == "string") {
                        respjson = eval("(" + msg + ")");
                    } else {
                        respjson = msg;
                    }
                } catch (err) {
                    respjson = msg;
                }
                var info = "";
                if (respjson.success) {
                    info = "操作正确。";
                    experiment.disabledBtn("submit", true);
                    $('#btn_init').hide();
                    $('#submit').hide();
                    $('#start').hide();
                } else {
                    if (respjson.msg) {
                        info = "操作错误：" + respjson.msg
                                + " 请修改后再继续提交。";
                    } else {
                        info = "提交时发生异常，请重新尝试。"
                    }
                    experiment.disabledBtn("submit", false)
                }
                experiment.showInfo(info);
                progressClose();
            },
            failure : function(resp, opts) {
                experiment.showInfo('服务器端发生错误，请联系老师!');
                progressClose();
            }
        });
    } catch (e) {
        experiment.disabledBtn("submit", false);
        experiment.showInfo(e.name + ":" + e.message);
        progressClose();
    }

};

experiment.showInfo = function(info) {
	$("#info").html(info);
}

experiment.successHandler = function() {
    experiment.disabledBtn("submit", false)
}

/* TODO：在有的机器上无法捕获以下浏览器错误：
Error:Automation服务器不能创建对象
Error:此计算机上的安全设置禁止访问其它域的数据源。
*/
experiment.errorHandler = function(errorname, errormsg){
    experiment.disabledBtn("submit", false)
    var msg = (errorname?errorname:"")  + ":"+ (errormsg?errormsg:"")
    if (errormsg == "此计算机上的安全设置禁止访问其它域的数据源。"){
        experiment.showInfo(msg +  ". <BR>" + experiment.ieconfig);
    }else{
        experiment.showInfo("初始化数据库时发生错误，请刷新页面。如果多次刷新仍不能正确初始化数据库，请联系老师。<BR>"+ msg);
    }
}

/* 0 表示没有选， 1 select ,2 insert, 3 update, 4 delete, 5 create, 6 其他 */
experiment.getPostexttype = function() {
	var value = 0;
	var obj = document.all.postexttype;
	if (obj) {
		for (var i = 0; i < obj.length; i++) {// 适合length>=2时，当obj.length==null时，可以直接取obj.value值
			if (obj[i].checked) {
				value = (obj[i].value);
			}
		}
	}
	return value;
}

/* 设置结果检验 */
experiment.setResultcheck = function( quesid) {
	var ifpostextCmp = Ext.getDom("ifpostext");
	var ifpostext = ifpostextCmp.checked;
	var postexttype = this.getPostexttype();

	Ext.MessageBox.prompt("设定结果检验", "请输入完成题目的sql语句：", function(btnId, sql) {
		if (btnId == "ok") {
			var result = dbmetadata2.query(sql);
			Ext.Ajax.request({
				url : __ctxPath + '/sqles/saveResultcheckQuestion.do',
				params : {
					"quesid" : quesid,
					"question.resultcheck" : Ext.util.JSON.encode(result),
					"question.resultquery" : sql,
					"question.ifpostext" : ifpostext,
					"question.postexttype" : postexttype
				},
				method : 'post',
				success : function() {
					Ext.ux.Toast.msg('信息提示', '成功保存!');
					location.reload(true);
				},
				failure : function() {
					Ext.ux.Toast.msg('信息提示', '保存失败!');
				}
			});
		} else {
			/* 取消 */;
		}
	}, window, true);
}

/* 设置结果检验 */
experiment.setResultcheck = function( quesid, dbname) {
	var ifpostextCmp = Ext.getDom("ifpostext");
	var ifpostext = ifpostextCmp.checked;
	var postexttype = this.getPostexttype();

	Ext.MessageBox.prompt("设定结果检验", "请输入完成题目的sql语句：", function(btnId, sql) {
		if (btnId == "ok") {
			var result = dbmetadata2.query(sql, dbname);
			Ext.Ajax.request({
				url : __ctxPath + '/sqles/saveResultcheckQuestion.do',
				params : {
					"quesid" : quesid,
					"question.resultcheck" : Ext.util.JSON.encode(result),
					"question.resultquery" : sql,
					"question.ifpostext" : ifpostext,
					"question.postexttype" : postexttype
				},
				method : 'post',
				success : function() {
					Ext.ux.Toast.msg('信息提示', '成功保存!');
					location.reload(true);
				},
				failure : function() {
					Ext.ux.Toast.msg('信息提示', '保存失败!');
				}
			});
		} else {
			/* 取消 */;
		}
	}, window, true);
}
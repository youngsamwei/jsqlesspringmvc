var exputils = {};
exputils.keepintouch = function(interval) {
	if (!interval) {
		/* 间隔5分钟 */
		interval = 300000;
	}
	/* 每间隔interval访问一次服务器，保持session有效. */
	setInterval(function() {
		Ext.Ajax.request({
			url : __ctxPath + "/hi.do",
			method : "POST",
			success : function(f, g) {

			},
			failure : function(e, f) {
			},
			scope : this
		});
	}, interval);

}

/*
 * 异常1：计算机上的安全设置禁止访问其它域的数据源。
 * 
 * */
exputils.testado = function() {
	try {
		var objdbConn = new ActiveXObject("ADODB.Connection");
		//var strdsn = "Driver={SQL Server};SERVER=(local);DATABASE=master"  ; 
		var strdsn = "Integrated Security=SSPI;Provider=SQLOLEDB.1;Data Source=.\\sqlexpress;Initial Catalog=master";

		objdbConn.Open(strdsn);
		if (objdbConn && this.objdbConn.State != 0) {
			objdbConn.close();
		}
	} catch (e) {
		if (e.message == "此计算机上的安全设置禁止访问其它域的数据源。"){
			window.location.href="/jsqles/pages/ieconfig/ie.html"
		}else if (e.message == "[DBNETLIB][ConnectionOpen (Connect()).]SQL Server 不存在或拒绝访问。"){
			window.location.href="/jsqles/pages/ieconfig/sqlexpress.html"
		}else{
		/*非ie浏览器*/
			//alert(e.message);
		}
	}
}
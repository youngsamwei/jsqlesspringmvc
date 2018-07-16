/*
主要功能：为用户显示数据库数据以及元数据.

*/


    $.extend($.fn.tree.methods,{
        getLeafChildren:function(jq, params){
            var nodes = [];
            $(params).next().children().children("div.tree-node").each(function(){
                nodes.push($(jq[0]).tree('getNode',this));
            });
            return nodes;
        }
    });

var dbexplorer_jq = {};

 dbexplorer_jq.getDBJson =   function (dbname){
        var dbtree = dbmetadata2.getDBTree(dbname);
        var dbjson = this.dbtree2json('foolkey', dbtree);
        return dbjson;
    }

   dbexplorer_jq.dbtree2json  = function (key, dbtree){
        var dbjson  = {};
        dbjson.children = [];
        dbjson.text = dbtree.name ? dbtree.name : key;
        dbjson.jsonKey = key;
        dbjson.jsonValue = dbtree;
        dbjson.jsonType = this.jsontypeof(dbtree);

        for (var key in dbtree){
            var child = {};
            var value = dbtree[key];
            if (!value){
                continue;
            }
            var type = this.jsontypeof(value);
            if (type == "object"){
                child = this.dbtree2json(key, value);
            } else if (type == "array"){
                child = this.dbtree2json(key, value);
            } else if ((type == "string") || (type == "number")
                                       || (type == "boolean")) {
                child.jsonKey = key;
                child.jsonValue = value;
                child.jsonType = this.jsontypeof(value);
                child.text = key + ' : ' + value;
            }

            dbjson.children.push(child);
        }
        return dbjson;
    }

 dbexplorer_jq.jsontypeof =   function  (v) {
        var type = Object.prototype.toString.call(v).toLowerCase()
                .split(" ")[1];
        type = type.replace("]", "");
        return type;
    }

  dbexplorer_jq.getSolidTargets =  function (tree){
        var solid = tree.tree('getSolidExt');
        var solidtargets = [];
        for (var sd in solid){
            solidtargets.push(solid[sd].target);
        }
        return solidtargets;
    }

	dbexplorer_jq.getCheckedNodes = function		 (tree) {
        var r = tree.tree('getRoot');
        var solid = this.getSolidTargets(tree);
        var db = {};
        db.database = [];
        var childnodes = tree.tree('getLeafChildren',r.target);
        for (var ri = 0; ri < childnodes.length; ri++) {

            var n = childnodes[ri];
            var database = this.getCheckedNode(tree,n,solid);
            /*如果子节点没有被选中的节点，则不加入到结果中 2017-6-26*/
            if (!this.isEmptyObject(database)){
                db.database.push(database);
            }
        }

        return db;
    }

dbexplorer_jq.isEmptyObject =	function		 (e) {
        var t;
        for (t in e)
            return !1;
        return !0
    }

dbexplorer_jq.getCheckedNode =	function		 (tree, node, solid) {

        var obj = {};
        if (!node){
            return obj
        }
        var childnodes = tree.tree('getLeafChildren',node.target);
        for (var ci = 0; ci < childnodes.length; ci++) {
            var child = childnodes[ci];

            var checked = child.checked;
            var insolid = $.inArray(child.target, solid);
            if (!checked && (insolid < 0)) {
                continue;
            }
            var child = childnodes[ci];
            var jsonType = child.jsonType;
            var jsonKey = child.jsonKey;
            var jsonValue = child.jsonValue;

            if (jsonType == "object") {

                obj[jsonKey] = this.getCheckedNode(tree, child, solid);

            } else if (jsonType == "array") {
                if (!obj[jsonKey]) {
                    obj[jsonKey] = [];
                }
                var ele = this.getCheckedNode(tree, child, solid);
                for ( var k in ele) {
                    obj[jsonKey].push(ele[k]);
                }
            } else if ((jsonType == "number") || (jsonType == "string")
                    || (jsonType == "boolean")) {
                obj[jsonKey] = jsonValue;
            }
        }
        return obj;

    }

dbexplorer_jq.cloneArrayByObjName = function(jsonobj) {
    var objs = [];
    for (var ti = 0; ti < jsonobj.length; ti++) {
        var obj = {};
        for ( var key in dbmetadata2.getObjectPropertyKeys()) {
            obj[key] = jsonobj[ti][key];
        }
        objs.push(obj);
    }
    return objs;
}
/*根据结构验证内容，得到从客户端需要获取的最小对象集合，以提高效率。*/
dbexplorer_jq.dbStructureEvalRequiredClientObjects = function(db) {
    var requiredb = {};
    requiredb.database = [];
    for (var dbi = 0; dbi < db.database.length; dbi++) {
        var dbobj = db.database[dbi];
        var database = {};
        database.name = dbobj.name;

        var map = dbmetadata2.getObjectTypeMap();
        for ( var key in map) {
            var type = map[key];
            if (dbobj[type]) {
                database[type] = this
                        .cloneArrayByObjName(dbobj[type]);
            }
        }
        if (dbobj["files"]) {
            database["files"] = this
                    .cloneArrayByObjName(dbobj["files"]);
        }

        requiredb.database.push(database);
    }
    return requiredb;

}
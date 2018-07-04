/* 不依赖与dbtree的具体结构，仅根据json结构展示树形。
 * 数组作为可展开节点，属性作为叶节点。
 * 
 * */
dbexplorer3 = Ext.extend(Ext.Window,
		{
			treePanel : null,
			toolbar : null,
			testStore : null,
			constructor : function(a) {
				Ext.applyIf(this, a);
				this.initUIComponent();
				dbexplorer3.superclass.constructor.call(this, {
					layout : "border",
					id : "dbexplorer3Win",
					title : "选择数据库对象以及属性",
					iconCls : "menu-dbexplorer3",
					width : 800,
					minWidth : 800,
					height : 600,
					minHeight : 600,
					border : false,
					modal : true,
					maximizable : true,
					buttonAlign : "center",
					items : this.treePanel,
					buttons : [ this.buttons ],
					keys : {
						key : Ext.EventObject.ENTER,
						fn : this.save.createCallback(this.treePanel, this),
						scope : this
					}
				});
			},
			initUIComponent : function() {
				this.toolbar = new Ext.Toolbar({
					items : [ {
						xtype : "button",
						iconCls : "btn-refresh",
						text : "刷新",
						scope : this,
						handler : function() {
							this.treePanel.root.reload();
						}
					}, {
						xtype : "button",
						text : "展开",
						iconCls : "btn-expand",
						scope : this,
						handler : function() {
							this.treePanel.expandAll();
						}
					}, {
						xtype : "button",
						text : "收起",
						scope : this,
						iconCls : "btn-collapse",
						handler : function() {
							this.treePanel.collapseAll();
						}
					} , {
						xtype : "button",
						text : "增加数据库",
						scope : this,
						iconCls : "btn-add",
						handler : function() {
							this.addDB();
						}
					} ]
				});

				this.treePanel = new Ext.tree.TreePanel({
					region : "center",
					layout : "fit",
					id : "dbresource",
					authScroll : true,
					containerScroll : true,
					/*
					 * title : "数据库资源", collapsible : false, split : false,
					 */

					width : 160,
					height : 800,
					animate : false,

					tbar : this.toolbar,

					root : new Ext.tree.TreeNode({
						id : 'root-0',
						text : '数据库',
						expanded : true
					}),
					rootVisible : true,
					listeners : {
						scope : this,
						checkchange : function(node, state, a, b) {
							// 如果被勾选的节点有子节点，则将其子节点全部改为根节点状态
							var text = node.text;

							if (node.hasChildNodes()) {
								if ((text == "tables") || (text == "views")
										|| (text == "functions")
										|| (text == "procedures")
										|| (text == "defaults")
										|| (text == "rules")
										|| (text == "files")) {
									this.setCheckedNode(node, state);
									this.setCheckedParentNode(node, state);
								} else if (!state) {
									this.setCheckedNode(node, state);
								}
							}

						}
					}
				});

				this.initDBTreeJson(this.treePanel.getRootNode(),
						this.dbtree.database);

				this.buttons = [ {
					text : "保存",
					iconCls : "btn-save",
					handler : this.save.createCallback(this.treePanel, this)
				}, {
					text : "取消",
					iconCls : "btn-cancel",
					handler : this.cancel.createCallback(this)
				} ];
			},

			setCheckedParentNode : function(node, state) {
				if (node.parentNode != null) {
					// 选中子节点让相应的父节点选中
					var pNode = node.parentNode;
					if (state) {
						pNode.ui.toggleCheck(state);// 触发父节点被选中
						pNode.attributes.checked = state;
					}
				}
			},
			setCheckedNode : function(node, state) {
				for (var j = 0; j < node.childNodes.length; j++) {
					node.childNodes[j].attributes.checked = state;
					node.childNodes[j].ui.toggleCheck(state);

					this.setCheckedNode(node.childNodes[j], state);
				}
			},
			getColumnText : function(column) {
				/* Column_name, type, length, prec scale, nullable */
				var columntext = "";
				var prec = Ext.util.Format.trim(column.Prec);
				if (prec != '') {
					columntext = column.Column_name + " " + column.Type + "("
							+ column.Prec + "," + column.Scale + ") "
							+ (column.Nullable == 'yes' ? '' : 'not')
							+ " null ";
					return columntext;
				} else {
					columntext = column.Column_name + " " + column.Type + "("
							+ column.Length + ") "
							+ (column.Nullable == 'yes' ? '' : 'not')
							+ " null ";
					return columntext;
				}
			},
			initDBTreeJson : function(tnode, jsonObj) {

				for ( var key in jsonObj) {
					var keytype = typeof (key);
					var value = jsonObj[key];
					var type = this.jsontypeof(value);
					/* 针对null，{} 进行特殊处理 */
					if (this.isEmptyObject(value)) {
						value = "";
						type = this.jsontypeof(value);
					}
					if ((type == "object") || (type == "array")) {
						var text = (value && value.name ? value.name : key);
						var id = tnode.id + "-" + key;
						var anode = this.newCheckedTreeNode(tnode, text, id,
								key, null, type);
						this.initDBTreeJson(anode, value);
					} else if ((type == "string") || (type == "number")
							|| (type == "boolean")) {
						var text = key + " : " + value;
						var id = tnode.id + "-" + key;
						var anode = this.newCheckedTreeNode(tnode, text, id,
								key, value, type);
					}
				}
			},
			jsontypeof : function(v) {
				var type = Object.prototype.toString.call(v).toLowerCase()
						.split(" ")[1];
				type = type.replace("]", "");
				return type;
			},
			cloneBasicProps : function(value) {
				if (!value)
					return value;
				var obj = {};

				for ( var key in value) {
					var v = value[key];
					var type = typeof (v);
					if ((type == "string") || (type == "number")
							|| (type == "boolean")) {
						obj[key] = v;
					}
				}
				return obj;
			},

			getDataDesc : function(data) {
				var desc = "";
				for (key in data) {
					desc = desc + data[key] + ","
				}
				return desc;
			},
			newObjectNode : function(parent, typeName, objs, prefix, type) {
				if (objs) {
					var node = this.newTreeNode(parent, typeName, type);
					for (var i = 0; i < objs.length; i++) {
						this.newCheckedTreeNode(node, objs[i].full_name, prefix
								+ objs[i].object_id, objs[i]);
					}
				}
			},

			newTreeNode : function(parent, text, type) {
				var tnode = new Ext.tree.TreeNode({
					text : text,
					type : type
				});
				parent.appendChild(tnode);
				return tnode;
			},

			newCheckedTreeNode : function(parent, text, nodeid, key, value,
					type) {
				var tnode = new Ext.tree.TreeNode({
					text : text,
					id : nodeid,
					checked : false,
					jsonKey : key,
					jsonValue : value,
					jsonType : type
				});
				parent.appendChild(tnode);
				return tnode;
			},
			/*
			 * 仅被选择的节点，不隐含包含父节点以及子节点，除非被明确选择 被选择后的节点仍然保持层次关系，在验证时作为逐层验证的依据。
			 */
			getCheckedNodes : function(tree) {
				var r = tree.root;
				var db = {};
				db.database = [];
				for (var ri = 0; ri < r.childNodes.length; ri++) {

					var n = r.childNodes[ri];
					var database = this.getCheckedNode(n);
					/*如果子节点没有被选中的节点，则不加入到结果中 2017-6-26*/
					if (!this.isEmptyObject(database)){
						db.database.push(database);
					}
				}

				return db;
			},

			getCheckedNode : function(node) {

				var obj = {};
				for (var ci = 0; ci < node.childNodes.length; ci++) {
					var child = node.childNodes[ci];
					var checked = child.attributes.checked;
					if (!checked) {
						continue;
					}
					var child = node.childNodes[ci];
					var jsonType = child.attributes.jsonType;
					var jsonKey = child.attributes.jsonKey;
					var jsonValue = child.attributes.jsonValue;

					if (jsonType == "object") {

						obj[jsonKey] = this.getCheckedNode(child);

					} else if (jsonType == "array") {
						if (!obj[jsonKey]) {
							obj[jsonKey] = [];
						}
						var ele = this.getCheckedNode(child);
						for ( var k in ele) {
							obj[jsonKey].push(ele[k]);
						}
					} else if ((jsonType == "number") || (jsonType == "string")
							|| (jsonType == "boolean")) {
						obj[jsonKey] = jsonValue;
					}
				}
				return obj;

			},
			isEmptyObject : function(e) {
				var t;
				for (t in e)
					return !1;
				return !0
			},
			/* 从treenodes中获取选择的节点形成json */
			nodes2JsonArray : function(nodes) {
				var objs = [];
				for (var i = 0; i < nodes.length; i++) {
					var node = nodes[i];
					if (!node.attributes.checked)
						continue;
					var obj = node.attributes.oriObj;
					objs.push(obj);
				}
				return objs;
			},
			cloneArrayByObjName : function(jsonobj) {
				var objs = [];
				for (var ti = 0; ti < jsonobj.length; ti++) {
					var obj = {};
					for ( var key in dbmetadata2.getObjectPropertyKeys()) {
						obj[key] = jsonobj[ti][key];
					}
					objs.push(obj);
				}
				return objs;
			},
			db2required : function(db) {
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

			},
			save : function(a, b) {

				var dbtree = b.getCheckedNodes(a);
				var requiredb = b.db2required(dbtree);

				if (b.saveCallback) {
					b.saveCallback(b, b.quesid, dbtree, requiredb);
				}

			},
			cancel : function(a) {
				a.close();
			}
		});
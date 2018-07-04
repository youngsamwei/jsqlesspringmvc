/**
 * The Patch for jQuery EasyUI 1.4.4
 */

(function($){
	$.extend($.parser, {
		parse: function(context){
			var aa = [];
			for(var i=0; i<$.parser.plugins.length; i++){
				var name = $.parser.plugins[i];
				var r = $('.easyui-' + name, context);
				if (r.length){
					if (r[name]){
						r.each(function(){
							$(this)[name]($.data(this, 'options')||{});
						});
					} else {
						aa.push({name:name,jq:r});
					}
				}
			}
			if (aa.length && window.easyloader){
				var names = [];
				for(var i=0; i<aa.length; i++){
					names.push(aa[i].name);
				}
				easyloader.load(names, function(){
					for(var i=0; i<aa.length; i++){
						var name = aa[i].name;
						var jq = aa[i].jq;
						jq.each(function(){
							$(this)[name]($.data(this, 'options')||{});
						});
					}
					$.parser.onComplete.call($.parser, context);
				});
			} else {
				$.parser.onComplete.call($.parser, context);
			}
		}
	})
})(jQuery);

(function($){
	function indexOfArray(a,o){
		for(var i=0,len=a.length; i<len; i++){
			if (a[i] == o) return i;
		}
		return -1;
	}
	function getArguments(target, aa){
		return $.data(target, 'treegrid') ? aa.slice(1) : aa;
	}
	function endEdit(target, index, cancel){
		var state = $.data(target, 'datagrid');
		var opts = state.options;
		var updatedRows = state.updatedRows;
		var insertedRows = state.insertedRows;
		
		var tr = opts.finder.getTr(target, index);
		var row = opts.finder.getRow(target, index);
		if (!tr.hasClass('datagrid-row-editing')) {
			return;
		}
		
		if (!cancel){
			if (!$(target).datagrid('validateRow', index)) return;	// invalid row data
			
			var changed = false;
			var changes = {};
			tr.find('div.datagrid-editable').each(function(){
				var field = $(this).parent().attr('field');
				var ed = $.data(this, 'datagrid.editor');
				var t = $(ed.target);
				var input = t.data('textbox') ? t.textbox('textbox') : t;
				input.triggerHandler('blur');
				var value = ed.actions.getValue(ed.target);
				if (row[field] !== value){
					row[field] = value;
					changed = true;
					changes[field] = value;
				}
			});
			if (changed){
				if (indexOfArray(insertedRows, row) == -1){
					if (indexOfArray(updatedRows, row) == -1){
						updatedRows.push(row);
					}
				}
			}
			opts.onEndEdit.apply(target, getArguments(target, [index, row, changes]));
		}
		
		tr.removeClass('datagrid-row-editing');
		
		destroyEditor(target, index);
		$(target).datagrid('refreshRow', index);
		
		if (!cancel){
			opts.onAfterEdit.apply(target, getArguments(target, [index, row, changes]));
		} else {
			opts.onCancelEdit.apply(target, getArguments(target, [index, row]));
		}
	}
	function destroyEditor(target, index){
		var opts = $.data(target, 'datagrid').options;
		var tr = opts.finder.getTr(target, index);
		tr.children('td').each(function(){
			var cell = $(this).find('div.datagrid-editable');
			if (cell.length){
				var ed = $.data(cell[0], 'datagrid.editor');
				if (ed.actions.destroy) {
					ed.actions.destroy(ed.target);
				}
				cell.html(ed.oldHtml);
				$.removeData(cell[0], 'datagrid.editor');
				
				cell.removeClass('datagrid-editable');
				cell.css('width','');
			}
		});
	}

	var DATAGRID_SERNO=1;
	function createColumnHeader(target, container, columns, frozen){
		var state = $.data(target, 'datagrid');
		var opts = state.options;
		var dc = state.dc;
		var panel = state.panel;

		if (!columns) return;
		$(container).show();
		$(container).empty();
		var names = [];
		var orders = [];
		if (opts.sortName){
			names = opts.sortName.split(',');
			orders = opts.sortOrder.split(',');
		}
		var t = $('<table class="datagrid-htable" border="0" cellspacing="0" cellpadding="0"><tbody></tbody></table>').appendTo(container);
		for(var i=0; i<columns.length; i++) {
			var tr = $('<tr class="datagrid-header-row"></tr>').appendTo($('tbody', t));
			var cols = columns[i];
			for(var j=0; j<cols.length; j++){
				var col = cols[j];
				
				var attr = '';
				if (col.rowspan){
					attr += 'rowspan="' + col.rowspan + '" ';
				}
				if (col.colspan){
					attr += 'colspan="' + col.colspan + '" ';
					if (!col.id){
						col.id = ['datagrid-td-group' + DATAGRID_SERNO++, i, j].join('-');
					}
				}
				if (col.id){
					attr += 'id="' + col.id + '"';
				}
				var td = $('<td ' + attr + '></td>').appendTo(tr);
				
				if (col.checkbox){
					td.attr('field', col.field);
					$('<div class="datagrid-header-check"></div>').html('<input type="checkbox"/>').appendTo(td);
				} else if (col.field){
					td.attr('field', col.field);
					td.append('<div class="datagrid-cell"><span></span><span class="datagrid-sort-icon"></span></div>');
					td.find('span:first').html(col.title);
					var cell = td.find('div.datagrid-cell');
					var pos = indexOfArray(names, col.field);
					if (pos >= 0){
						cell.addClass('datagrid-sort-' + orders[pos]);
					}
					if (col.sortable){
						cell.addClass('datagrid-sort');
					}
					if (col.resizable == false){
						cell.attr('resizable', 'false');
					}
					if (col.width){
						var value = $.parser.parseValue('width',col.width,dc.view,opts.scrollbarSize);
						cell._outerWidth(value-1);
						col.boxWidth = parseInt(cell[0].style.width);
						col.deltaWidth = value - col.boxWidth;
					} else {
						col.auto = true;
					}
					cell.css('text-align', (col.halign || col.align || ''));
					
					// define the cell class
					col.cellClass = state.cellClassPrefix + '-' + col.field.replace(/[\.|\s]/g,'-');
					cell.addClass(col.cellClass).css('width', '');
				} else {
					$('<div class="datagrid-cell-group"></div>').html(col.title).appendTo(td);
				}
				
				if (col.hidden){
					td.hide();
				}
			}
			
		}
		if (frozen && opts.rownumbers){
			var td = $('<td rowspan="'+opts.frozenColumns.length+'"><div class="datagrid-header-rownumber"></div></td>');
			if ($('tr',t).length == 0){
				td.wrap('<tr class="datagrid-header-row"></tr>').parent().appendTo($('tbody',t));
			} else {
				td.prependTo($('tr:first', t));
			}
		}
	}
	function bindEvents(target) {
		var state = $.data(target, 'datagrid');
		var panel = state.panel;
		var opts = state.options;
		var dc = state.dc;
		
		var header = dc.header1.add(dc.header2);
		header.find('input[type=checkbox]').unbind('.datagrid').bind('click.datagrid', function(e){
			if (opts.singleSelect && opts.selectOnCheck) return false;
			if ($(this).is(':checked')){
				$(target).datagrid('checkAll');
			} else {
				$(target).datagrid('uncheckAll');
			}
			e.stopPropagation();
		});
		
		var cells = header.find('div.datagrid-cell');
		cells.closest('td').unbind('.datagrid').bind('mouseenter.datagrid', function(){
			if (state.resizing){return;}
			$(this).addClass('datagrid-header-over');
		}).bind('mouseleave.datagrid', function(){
			$(this).removeClass('datagrid-header-over');
		}).bind('contextmenu.datagrid', function(e){
			var field = $(this).attr('field');
			opts.onHeaderContextMenu.call(target, e, field);
		});
		
		cells.unbind('.datagrid').bind('click.datagrid', function(e){
			var p1 = $(this).offset().left + 5;
			var p2 = $(this).offset().left + $(this)._outerWidth() - 5;
			if (e.pageX < p2 && e.pageX > p1){
				$(target).datagrid('sort', $(this).parent().attr('field'));
			}
		}).bind('dblclick.datagrid', function(e){
			var p1 = $(this).offset().left + 5;
			var p2 = $(this).offset().left + $(this)._outerWidth() - 5;
			var cond = opts.resizeHandle == 'right' ? (e.pageX > p2) : (opts.resizeHandle == 'left' ? (e.pageX < p1) : (e.pageX < p1 || e.pageX > p2));
			if (cond){
				var field = $(this).parent().attr('field');
				var col = $(target).datagrid('getColumnOption', field);
				if (col.resizable == false) return;
				$(target).datagrid('autoSizeColumn', field);
				col.auto = false;
			}
		});
		
		var resizeHandle = opts.resizeHandle == 'right' ? 'e' : (opts.resizeHandle == 'left' ? 'w' : 'e,w');
		cells.each(function(){
			$(this).resizable({
				handles:resizeHandle,
				disabled:($(this).attr('resizable') ? $(this).attr('resizable')=='false' : false),
				minWidth:25,
				onStartResize: function(e){
					state.resizing = true;
//					header.css('cursor', 'e-resize');
					header.css('cursor', $('body').css('cursor'));
					if (!state.proxy){
						state.proxy = $('<div class="datagrid-resize-proxy"></div>').appendTo(dc.view);
					}
					state.proxy.css({
						left:e.pageX - $(panel).offset().left - 1,
						display:'none'
					});
					setTimeout(function(){
						if (state.proxy) state.proxy.show();
					}, 500);
				},
				onResize: function(e){
					state.proxy.css({
						left:e.pageX - $(panel).offset().left - 1,
						display:'block'
					});
					return false;
				},
				onStopResize: function(e){
					header.css('cursor', '');
					$(this).css('height','');
					var field = $(this).parent().attr('field');
					var col = $(target).datagrid('getColumnOption', field);
					col.width = $(this)._outerWidth();
					col.boxWidth = col.width - col.deltaWidth;
					col.auto = undefined;
					$(this).css('width', '');
					$(target).datagrid('fixColumnSize', field);
					state.proxy.remove();
					state.proxy = null;
					$(target).datagrid('fitColumns');
					opts.onResizeColumn.call(target, field, col.width);
					setTimeout(function(){
						state.resizing = false;
					}, 0);
				}
			});
		});
		
	}
	function setGrid(target){
		var state = $.data(target, 'datagrid');
		var opts = state.options;
		var dc = state.dc;
		createColumnHeader(target, dc.header1, opts.frozenColumns, true);
		createColumnHeader(target, dc.header2, opts.columns, false);
		bindEvents(target);
		$(target).datagrid('getPanel').find('.datagrid-header .datagrid-sort-icon').empty();
	}

	var plugin = $.fn.datagrid;
	$.fn.datagrid = function(options, param){
		if (typeof options == 'string'){
			return plugin.call(this, options, param);
		} else {
			return this.each(function(){
				plugin.call($(this), options, param);
				setGrid(this);
			})
		}
	};
	$.fn.datagrid.defaults = plugin.defaults;
	$.fn.datagrid.methods = plugin.methods;
	$.fn.datagrid.parseOptions = plugin.parseOptions;
	$.fn.datagrid.parseData = plugin.parseData;

	$.extend($.fn.datagrid.methods, {
		endEdit: function(jq, index){
			return jq.each(function(){
				endEdit(this, index, false);
			});
		},
		cancelEdit: function(jq, index){
			return jq.each(function(){
				endEdit(this, index, true);
			});
		},
		updateRow: function(jq, param){
			return jq.each(function(){
				var target = this;
				var state = $.data(target, 'datagrid');
				var opts = state.options;
				var row = opts.finder.getRow(target, param.index);
				var updated = false;
				for(var field in param.row){
					if (row[field] != param.row[field]){
						updated = true;
						break;
					}
				}
				if (updated){
					if ($.inArray(row, state.insertedRows) == -1){
						if ($.inArray(row, state.updatedRows) == -1){
							state.updatedRows.push(row);
						}
					}
					$.extend(row, param.row);
					opts.view.updateRow.call(opts.view, target, param.index, param.row);
				}
			});
		}
	})
})(jQuery);

(function($){
	var append = $.fn.treegrid.methods.append;
	var insert = $.fn.treegrid.methods.insert;
	$.extend($.fn.treegrid.methods, {
		append: function(jq, param){
			return jq.each(function(){
				var opts = $(this).treegrid('options');
				var onLoadSuccess = opts.onLoadSuccess;
				opts.onLoadSuccess = function(){};
				append($(this), param);
				opts.onLoadSuccess = onLoadSuccess;
			})
		},
		insert: function(jq, param){
			return jq.each(function(){
				var opts = $(this).treegrid('options');
				var onLoadSuccess = opts.onLoadSuccess;
				opts.onLoadSuccess = function(){};
				insert($(this), param);
				opts.onLoadSuccess = onLoadSuccess;
			})
		}
	})
})(jQuery);

(function($){
	var append = $.fn.tree.methods.append;
	var insert = $.fn.tree.methods.insert;
	$.extend($.fn.tree.methods, {
		append: function(jq, param){
			return jq.each(function(){
				var opts = $(this).tree('options');
				var onLoadSuccess = opts.onLoadSuccess;
				opts.onLoadSuccess = function(){};
				append($(this), param);
				opts.onLoadSuccess = onLoadSuccess;
			})
		},
		insert: function(jq, param){
			return jq.each(function(){
				var opts = $(this).tree('options');
				var onLoadSuccess = opts.onLoadSuccess;
				opts.onLoadSuccess = function(){};
				insert($(this), param);
				opts.onLoadSuccess = onLoadSuccess;
			})
		}
	})
})(jQuery);

(function($){
	$.fn.navpanel = function(options, param){
		if (typeof options == 'string'){
			var method = $.fn.navpanel.methods[options];
			return method ? method(this, param) : this.panel(options, param);
		} else {
			options = options || {};
			return this.each(function(){
				var state = $.data(this, 'navpanel');
				if (state){
					$.extend(state.options, options);
				} else {
					state = $.data(this, 'navpanel', {
						options: $.extend({}, $.fn.navpanel.defaults, $.fn.navpanel.parseOptions(this), options)
					});
				}
				$(this).panel(state.options);
			});
		}
	};
	$.fn.navpanel.methods = {
		options: function(jq){
			return $.data(jq[0], 'navpanel').options;
		}
	};
	$.fn.navpanel.parseOptions = function(target){
		return $.extend({}, $.fn.panel.parseOptions(target), $.parser.parseOptions(target, [
		]));
	};
	$.fn.navpanel.defaults = $.extend({}, $.fn.panel.defaults, {
		fit: true,
		border: false,
		cls: 'navpanel'
	});
})(jQuery);

(function($){
	function setValues(target, values){
		var state = $.data(target, 'combotree');
		var opts = state.options;
		var tree = state.tree;
		var topts = tree.tree('options');
		var onCheck = topts.onCheck;
		var onSelect = topts.onSelect;
		topts.onCheck = topts.onSelect = function(){};
		
		tree.find('span.tree-checkbox').addClass('tree-checkbox0').removeClass('tree-checkbox1 tree-checkbox2');
		if (!$.isArray(values)){values = values.split(opts.separator)}
		var vv = $.map(values, function(value){return String(value)});
		var ss = [];
		$.map(vv, function(v){
			var node = tree.tree('find', v);
			if (node){
				tree.tree('check', node.target).tree('select', node.target);
				ss.push(node.text);
			} else {
				// ss.push(v);
				ss.push(findText(v,opts.mappingRows) || v);
			}
		});
		if (opts.multiple){
			var nodes = tree.tree('getChecked');
			$.map(nodes, function(node){
				var id = String(node.id);
				if ($.inArray(id, vv) == -1){
					vv.push(id);
					ss.push(node.text);
				}
			});
		}
		topts.onCheck = onCheck;
		topts.onSelect = onSelect;
//		retrieveValues(target);
		$(target).combo('setText', ss.join(opts.separator)).combo('setValues', opts.multiple?vv:(vv.length?vv:['']));
		
		function findText(value, a){
			for(var i=0; i<a.length; i++){
				if (value == a[i].id){
					return a[i].text;
				}
			}
			return undefined;
		}
	}

	function setTree(target){
		var state = $.data(target, 'combotree');
		var opts = state.options;
		var tree = state.tree;
		tree.tree('options').onLoadSuccess = function(node,data){
			var values = $(target).combotree('getValues');
			if (opts.multiple){
				var nodes = tree.tree('getChecked');
				for(var i=0; i<nodes.length; i++){
					var id = nodes[i].id;
					(function(){
						for(var i=0; i<values.length; i++){
							if (id == values[i]) return;
						}
						values.push(id);
					})();
				}
			}
			// $(target).combotree('setValues', values);	
			setValues(target, values);	
			opts.onLoadSuccess.call(this, node, data);
		}
	}

	var plugin = $.fn.combotree;
	$.fn.combotree = function(options, param){
		if (typeof options == 'string'){
			return plugin.call(this, options, param);
		} else {
			return this.each(function(){
				plugin.call($(this), options, param);
				setTree(this);
			})
		}
	};
	$.fn.combotree.defaults = plugin.defaults;
	$.fn.combotree.methods = plugin.methods;
	$.fn.combotree.parseOptions = plugin.parseOptions;
	$.extend($.fn.combotree.defaults, {
		mappingRows: []
	});
	$.extend($.fn.combotree.methods, {
		setValues: function(jq, values){
			return jq.each(function(){
				var opts = $(this).combotree('options');
				if ($.isArray(values)){
					values = $.map(values, function(value){
						if (typeof value == 'object'){
							var id = value.id;
							var text = value.text;
							(function(){
								for(var i=0; i<opts.mappingRows.length; i++){
									if (id == opts.mappingRows[i].id){
										return;
									}
								}
								opts.mappingRows.push(value);
							})();
							return id;
						} else {
							return value;
						}
					})
				}
				setValues(this, values);
			})
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).combotree('setValues', [value]);
			});
		},
	});
})(jQuery);

(function($){
	function setValues(target, values, remainText){
		var state = $.data(target, 'combogrid');
		var opts = state.options;
		var grid = state.grid;
		
		var oldValues = $(target).combo('getValues');
		var cOpts = $(target).combo('options');
		var onChange = cOpts.onChange;
		cOpts.onChange = function(){};	// prevent from triggering onChange event
		var gOpts = grid.datagrid('options');
		var onSelect = gOpts.onSelect;
		var onUnselectAll = gOpts.onUnselectAll;
		gOpts.onSelect = gOpts.onUnselectAll = function(){};
		
		if (!$.isArray(values)){values = values.split(opts.separator)}
		var strValues = $.map(values, function(v){return String(v);});
		var selectedRows = [];
		$.map(grid.datagrid('getSelections'), function(row){
			if ($.inArray(String(row[opts.idField]), strValues) >= 0){
				selectedRows.push(row);
			}
		});
		grid.datagrid('clearSelections');
		grid.data('datagrid').selectedRows = selectedRows;

		var ss = [];
		for(var i=0; i<values.length; i++){
			var value = values[i];
			var index = grid.datagrid('getRowIndex', value);
			if (index >= 0){
				grid.datagrid('selectRow', index);
			}
			ss.push(findText(value, grid.datagrid('getRows')) ||
					findText(value, grid.datagrid('getSelections')) ||
					findText(value, opts.mappingRows) ||
					value
			);
		}

		opts.unselectedValues = [];
		var selectedValues = $.map(selectedRows, function(row){
			return String(row[opts.idField]);
		});
		$.map(values, function(value){
			if ($.inArray(String(value), selectedValues) == -1){
				opts.unselectedValues.push(value);
			}
		});

		$(target).combo('setValues', oldValues);
		cOpts.onChange = onChange;	// restore to trigger onChange event
		gOpts.onSelect = onSelect;
		gOpts.onUnselectAll = onUnselectAll;
		
		if (!remainText){
			var s = ss.join(opts.separator);
			if ($(target).combo('getText') != s){
				$(target).combo('setText', s);
			}
		}
		$(target).combo('setValues', values);
		
		function findText(value, a){
			for(var i=0; i<a.length; i++){
				if (value == a[i][opts.idField]){
					return a[i][opts.textField];
				}
			}
			return undefined;
		}
	}
	function setGrid(target){
		var state = $.data(target, 'combogrid');
		var opts = state.options;
		var grid = state.grid;
		$.extend(grid.datagrid('options'), {
			onLoadSuccess: function(data){
				var values = $(target).combo('getValues');
				// prevent from firing onSelect event.
				var oldOnSelect = opts.onSelect;
				opts.onSelect = function(){};
				setValues(target, values, state.remainText);
				opts.onSelect = oldOnSelect;
				
				opts.onLoadSuccess.apply(target, arguments);
			},
			onClickRow: function(index,row){
				state.remainText = false;
				retrieveValues();
				if (!opts.multiple){
					$(target).combo('hidePanel');
				}
				opts.onClickRow.call(this, index, row);			

			},
			onSelect: function(index, row){retrieveValues(); opts.onSelect.call(this, index, row);},
			onUnselect: function(index, row){retrieveValues(); opts.onUnselect.call(this, index, row);},
			onSelectAll: function(rows){retrieveValues(); opts.onSelectAll.call(this, rows);},
			onUnselectAll: function(rows){
				if (opts.multiple) retrieveValues(); 
				opts.onUnselectAll.call(this, rows);
			}
		});

		function retrieveValues(){
			var vv = $.map(grid.datagrid('getSelections'), function(row){
				return row[opts.idField];
			});
			vv = vv.concat(opts.unselectedValues);
			if (!opts.multiple){
				vv = vv.length ? [vv[0]] : [''];
			}
			setValues(target, vv, state.remainText);
		}
	}

	var plugin = $.fn.combogrid;
	$.fn.combogrid = function(options, param){
		if (typeof options == 'string'){
			return plugin.call(this, options, param);
		} else {
			return this.each(function(){
				plugin.call($(this), options, param);
				setGrid(this);
			})
		}
	};
	$.fn.combogrid.defaults = plugin.defaults;
	$.fn.combogrid.methods = plugin.methods;
	$.fn.combogrid.parseOptions = plugin.parseOptions;	

	$.extend($.fn.combogrid.methods, {
		setValues: function(jq, values){
			return jq.each(function(){
				var opts = $(this).combogrid('options');
				if ($.isArray(values)){
					values = $.map(values, function(value){
						if (typeof value == 'object'){
							var v = value[opts.idField];
							(function(){
								for(var i=0; i<opts.mappingRows.length; i++){
									if (v == opts.mappingRows[i][opts.idField]){
										opts.mappingRows[i] = value;
										return;
									}
								}
								opts.mappingRows.push(value);
							})();
							return v;
						} else {
							return value;
						}
					});
				}
				setValues(this, values);
			});
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).combogrid('setValues', [value]);
			});
		}
	});
})(jQuery);

(function($){
	$(
		'<style>' +
		'.combobox-icon{display:inline-block;width:16px;height:16px;vertical-align:top;}' +
		'.textbox .textbox-bgicon{background-position:3px center;padding-left:20px;}' +
		'.combobox-stick{position:absolute;top:1px;left:1px;right:1px;background:inherit;}' +
		'</style>'
	).appendTo('head');

	function select(target, value){
		var opts = $.data(target, 'combobox').options;
		var values = $(target).combo('getValues');
		if ($.inArray(value+'', values) == -1){
			if (opts.multiple){
				values.push(value);
			} else {
				values = [value];
			}
			setValues(target, values);
			opts.onSelect.call(target, opts.finder.getRow(target, value));
		}
	}
	function unselect(target, value){
		var opts = $.data(target, 'combobox').options;
		var values = $(target).combo('getValues');
		var index = $.inArray(value+'', values);
		if (index >= 0){
			values.splice(index, 1);
			setValues(target, values);
			opts.onUnselect.call(target, opts.finder.getRow(target, value));
		}
	}
	function setValues(target, values, remainText){
		var opts = $.data(target, 'combobox').options;
		var panel = $(target).combo('panel');
		
		if (!$.isArray(values)){values = values.split(opts.separator)}
		panel.find('div.combobox-item-selected').removeClass('combobox-item-selected');
		var vv = [], ss = [];
		var theRow;
		for(var i=0; i<values.length; i++){
			var v = values[i];
			var s = v;
			opts.finder.getEl(target, v).addClass('combobox-item-selected');
			var row = opts.finder.getRow(target, v);
			if (row){
				s = row[opts.textField];
				theRow = row;
			}
			vv.push(v);
			ss.push(s);
		}
		
		if (!remainText){
			$(target).combo('setText', ss.join(opts.separator));
			if (opts.showItemIcon){
		    	var tb = $(target).combobox('textbox');
		    	tb.attr('class', 'textbox-text validatebox-text');
				if (theRow && theRow.iconCls){
					tb.addClass('textbox-bgicon ' + theRow.iconCls);
				}
			}
		}
		$(target).combo('setValues', vv);
	}
	function setEvents(target){
		var state = $.data(target, 'combobox');
		var opts = state.options;
		$(target).combobox('panel').unbind('click').bind('click', function(e){
			var comboTarget = $(this).panel('options').comboTarget;
			var item = $(e.target).closest('div.combobox-item');
			if (!item.length || item.hasClass('combobox-item-disabled')){return}
			var row = opts.finder.getRow(comboTarget, item);
			if (!row){return}
			var value = row[opts.valueField];
			if (opts.multiple){
				if (item.hasClass('combobox-item-selected')){
					$(comboTarget).combobox('unselect', value);
				} else {
					$(comboTarget).combobox('select', value);
				}
			} else {
				$(comboTarget).combobox('select', value);
				$(comboTarget).combo('hidePanel');
			}
			e.stopPropagation();
		}).bind('scroll', function(){
			if (opts.groupPosition == 'sticky'){
				var comboTarget = $(this).panel('options').comboTarget;
				var stick = $(this).children('.combobox-stick');
				if (!stick.length){
					stick = $('<div class="combobox-group combobox-stick"></div>').appendTo(this);
				}
				stick.hide();
				$(this).children('.combobox-group:visible').each(function(){
					var g = $(this);
					var groupData = opts.finder.getGroup(comboTarget, g);
					var rowData = state.data[groupData.startIndex + groupData.count - 1];
					var last = opts.finder.getEl(comboTarget, rowData[opts.valueField]);
					if (g.position().top < 0 && last.position().top > 0){
						stick.show().html(g.html());
						return false;
					}
				});
			}
		});
		var copts = $(target).combo('options');
		var onShowPanel = copts.onShowPanel;
		copts.onShowPanel = function(){
			$(this).combobox('panel').triggerHandler('scroll');
			$(this).combobox('setValues', $(this).combobox('getValues'));
			onShowPanel.call(this);
		};
		var onLoadSuccess = opts.onLoadSuccess;
		opts.onLoadSuccess = function(data){
			if (opts.showItemIcon){
				for(var i=0; i<data.length; i++){
					var item = data[i];
					if (item.iconCls){
						var el = opts.finder.getEl(this, data[i][opts.valueField]);
						$(el).prepend('<span class="combobox-icon '+item.iconCls+'"></span>');
					}
				}
				$(this).combobox('setValues', $(this).combobox('getValues'));
			}
			state.groups = [];
			var group = undefined;
			for(var i=0; i<data.length; i++){
				var row = data[i];
				var g = row[opts.groupField];
				if (g){
					if (group != g){
						group = g;
						state.groups.push({
							value: g,
							startIndex: i,
							count: 1
						});
					} else {
						state.groups[state.groups.length-1].count++;
					}
				}
			}
			onLoadSuccess.call(this, data);
		}
		if (opts.data){
			$(target).combobox('loadData', opts.data);
		} else {
			var data = $.fn.combobox.parseData(target);
			if (data.length){
				$(target).combobox('loadData', data);
			}
		}
	}

	var plugin = $.fn.combobox;
	$.fn.combobox = function(options, param){
		if (typeof options == 'string'){
			return plugin.call(this, options, param);
		} else {
			return this.each(function(){
				plugin.call($(this), options, param);
				setEvents(this);
			});
		}
	};
	$.fn.combobox.defaults = plugin.defaults;
	$.fn.combobox.methods = plugin.methods;
	$.fn.combobox.parseOptions = plugin.parseOptions;	
	$.fn.combobox.parseData = plugin.parseData;
	var doQuery = $.fn.combobox.defaults.keyHandler.query;
	var keyUp = $.fn.combobox.defaults.keyHandler.up;
	var keyDown = $.fn.combobox.defaults.keyHandler.down;
	$.extend($.fn.combobox.defaults.keyHandler, {
		up: function(e){
			keyUp.call(this, e);
			$(this).combobox('setValues', $(this).combobox('getValues'));
		},
		down: function(e){
			keyDown.call(this, e);
			$(this).combobox('setValues', $(this).combobox('getValues'));
		},
		query: function(q,e){
			doQuery.call(this, q, e);
	    	var tb = $(this).combobox('textbox');
	    	tb.attr('class', 'textbox-text validatebox-text');			
		}
	});
	$.extend($.fn.combobox.defaults.finder, {
		getGroupEl:function(target, gvalue){
			var state = $.data(target, 'combobox');
			var index = $.easyui.indexOfArray(state.groups, 'value', gvalue);
			var id = state.groupIdPrefix + '_' + index;
			return $('#'+id);
		},
		getGroup:function(target, p){
			var state = $.data(target, 'combobox');
			var index = p.attr('id').substr(state.groupIdPrefix.length+1);
			return state.groups[parseInt(index)];
		}
	});
	$.extend($.fn.combobox.methods, {
		cloneFrom: function(jq, from){
			return jq.each(function(){
				$(this).combo('cloneFrom', from);
				$.data(this, 'combobox', $(from).data('combobox'));
				$(this).addClass('combobox-f').attr('comboboxName', $(this).attr('textboxName'));
			});
		},
		select: function(jq, value){
			return jq.each(function(){
				select(this, value);
			});
		},
		unselect: function(jq, value){
			return jq.each(function(){
				unselect(this, value);
			});
		},
		setValues: function(jq, values){
			return jq.each(function(){
				setValues(this, values);
			});
		},
		setValue: function(jq, value){
			return jq.each(function(){
				setValues(this, [value]);
			});
		}
	})
})(jQuery);

(function($){
	function setCls(target){
		var state = $.data(target, 'window');
		var opts = state.options;
		var win = $(target);
		state.window.css(opts.style).addClass(opts.cls);
		win.panel('header').addClass(opts.headerCls);
		win.panel('body').addClass(opts.bodyCls);
	}
	
	var plugin = $.fn.window;
	$.fn.window = function(options, param){
		if (typeof options == 'string'){
			return plugin.call(this, options, param);
		} else {
			return this.each(function(){
				plugin.call($(this), options, param);
				setCls(this);
			})
		}
	};
	$.fn.window.defaults = plugin.defaults;
	$.fn.window.methods = plugin.methods;
	$.fn.window.parseOptions = plugin.parseOptions;
	$.fn.window.getMaskSize = plugin.getMaskSize;

})(jQuery);

(function($){
	$(function(){
		$(document).bind('keydown.mymessager',function(e){
			var target = e.target;
			if (e.keyCode == 13){
				if ($(e.target).hasClass('messager-input')){
					$(e.target).closest('.messager-body').next().find('.l-btn:first').trigger('click')
				}
			}
		})
	})
})(jQuery);


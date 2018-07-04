<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var  schoolyeartermTreeGrid;
    $(function() {
         schoolyeartermTreeGrid = $('#schoolyeartermTreeGrid').treegrid({
            url : '${path }/schoolyearterm/treeGrid',
            idField : 'id',
            treeField : 'name',
            parentField : 'pid',
            fit : true,
            fitColumns : false,
            border : false,
            frozenColumns : [ [ {
                width : '20',
                title : '编号',
                field : 'id',
                sortable : true
            }, {
                width : '200',
                title : '名称',
                field : 'name',
                sortable : true
            } ,  {
                width : '160',
                title : '开始日期',
                field : 'startdate',
                sortable : true
            },  {
                 width : '160',
                 title : '结束日期',
                 field : 'enddate',
                 sortable : true
             },  {
                   width : '40',
                   title : '周数',
                   field : 'weeks',
                   sortable : true
               }, {
                field : 'action',
                title : '操作',
                width : 200,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/schoolyearterm/edit">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="schoolyear-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editSchoolyeartermFun(\'{0}\');" >编辑</a>', row.id);
                        </shiro:hasPermission>
                        <shiro:hasPermission name="/schoolyearterm/delete">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="schoolyearterm-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteschoolyeartermFun(\'{0}\');" >删除</a>', row.id);
                        </shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.schoolyearterm-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.schoolyearterm-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#schoolyeartermToolbar'
        });
    });

    function addSchoolyearFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 500,
            height : 300,
            href : '${path }/schoolyearterm/addSchoolyearPage',
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = schoolyeartermTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#schoolyearAddForm');
                    f.submit();
                }
            } ]
        });
    }

    function addTermFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 500,
            height : 300,
            href : '${path }/schoolyearterm/addTermPage',
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = schoolyeartermTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#termAddForm');
                    f.submit();
                }
            } ]
        });
    }

    function editSchoolyeartermFun(id) {
        if (id == undefined) {
            var rows = schoolyeartermTreeGrid.treegrid('getSelections');
            id = rows[0].id;
        } else {
            schoolyeartermTreeGrid.treegrid('unselectAll').treegrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '编辑',
            width : 500,
            height : 300,
            href : '${path }/schoolyearterm/editPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = schoolyeartermTreeGrid;//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#schoolyeartermEditForm');
                    f.submit();
                }
            } ]
        });
    }

    function deleteschoolyeartermFun(id) {
        var row ;
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = schoolyeartermTreeGrid.treegrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            schoolyeartermTreeGrid.treegrid('unselectAll').treegrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要删除当前学年或学期?', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/schoolyearterm/delete', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        schoolyeartermTreeGrid.treegrid('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',fit:true,border:false">
        <table id="schoolyeartermTreeGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="schoolyeartermToolbar" style="display: none;">
    <shiro:hasPermission name="/schoolyearterm/add">
        <a onclick="addSchoolyearFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加学年</a>
        <a onclick="addTermFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加学期</a>
    </shiro:hasPermission>
</div>
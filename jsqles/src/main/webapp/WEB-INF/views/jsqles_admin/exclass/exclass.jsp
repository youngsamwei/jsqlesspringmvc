<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var  exclassTreeGrid;
    $(function() {
         exclassTreeGrid = $('#exclassTreeGrid').treegrid({
            url : '${path }/exclass/tree',
            idField : 'id',
            treeField : 'text',
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
                title : '班级名称',
                field : 'text',
                sortable : true
            },  {
                   width : '40',
                   title : '学生数',
                   field : 'studentnum',
                   sortable : true
               }, {
                field : 'action',
                title : '操作',
                width : 500,
                formatter : function(value, row, index) {
                    var str = '';
                    /*如果是行政班级，则不显示编辑和删除按钮*/
                         if (row.pid < 0) {
                            <shiro:hasPermission name="/exclass/edit">
                                str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                                str += $.formatString('<a href="javascript:void(0)" class="exclass-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editExclassFun(\'{0}\');" >编辑</a>', row.id);
                            </shiro:hasPermission>
                             <shiro:hasPermission name="/exclass/delete">
                                 str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                                 str += $.formatString('<a href="javascript:void(0)" class="exclass-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteExclassFun(\'{0}\');" >删除</a>', row.id);
                             </shiro:hasPermission>
                                 str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                                 str += $.formatString('<a href="javascript:void(0)" class="exclass-easyui-linkbutton-addclass" data-options="plain:true,iconCls:\'fi-plus icon-blue\'" onclick="linkExclassFun(\'{0}\', 1);" >增加行政班级</a>', row.id);
                                 str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                                 str += $.formatString('<a href="javascript:void(0)" class="exclass-easyui-linkbutton-moveclass" data-options="plain:true,iconCls:\'fi-x icon-blue\'" onclick="linkExclassFun(\'{0}\', 0);" >移除行政班级</a>', row.id);
                        }

                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.exclass-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.exclass-easyui-linkbutton-del').linkbutton({text:'删除'});
                $('.exclass-easyui-linkbutton-addclass').linkbutton({text:'增加行政班级'});
                $('.exclass-easyui-linkbutton-moveclass').linkbutton({text:'移除行政班级'});
            },
            toolbar : '#exclassToolbar'
        });
    });

    /* flag=1 增加行政班级， flag=0 移除行政班级*/
    function linkExclassFun(exclassid, flag) {
        parent.$.modalDialog({
            title : flag == 1 ? '增加行政班级':'移除行政班级',
            width : 500,
            height : 300,
            href : '${path }/exclass/linkPage?exclassid=' + exclassid + '&flag=' + flag,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = exclassTreeGrid;//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#exclassLinkForm');
                    f.submit();
                }
            } ]
        });
    }

    function addExclassFun() {
        parent.$.modalDialog({
            title : '添加实验班级',
            width : 500,
            height : 300,
            href : '${path }/exclass/addPage',
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = exclassTreeGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#exclassAddForm');
                    f.submit();
                }
            } ]
        });
    }

    function editExclassFun(exclassid) {
        if (exclassid == undefined) {
            var rows = exclassTreeGrid.treegrid('getSelections');
            exclassid = rows[0].id;
        } else {
            exclassTreeGrid.treegrid('unselectAll').treegrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '编辑',
            width : 500,
            height : 300,
            href : '${path }/exclass/editPage?exclassid=' + exclassid,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = exclassTreeGrid;//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#exclassEditForm');
                    f.submit();
                }
            } ]
        });
    }

    function deleteExclassFun(exclassid) {
        var row ;
        if (exclassid == undefined) {//点击右键菜单才会触发这个
            var rows = exclassTreeGrid.treegrid('getSelections');
            exclassid = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            exclassTreeGrid.treegrid('unselectAll').treegrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要删除当前学年或学期?', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/exclass/delete', {
                    exclassid : exclassid
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        exclassTreeGrid.treegrid('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',fit:true,border:false">
        <table id="exclassTreeGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="exclassToolbar" style="display: none;">
    <shiro:hasPermission name="/exclass/add">
        <a onclick="addExclassFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加实验班级</a>
    </shiro:hasPermission>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var question_questionDataGrid;
    var question_examinationTree;

    $(function() {
        question_examinationTree = $('#question_examinationTree').tree({
            url : '${path }/examination/tree',
            parentField : 'pid',
            lines : true,
            onSelect : function(node) {
                question_questionDataGrid.datagrid({
                    url : '${path }/question/dataGrid',
                    queryParams:{
                            examid: node.id
                        }
                });

            },
            onLoadSuccess:function(node,data){
               $("#question_examinationTree li:eq(0)").find("div").addClass("tree-node-selected");   //设置第一个节点高亮
               var n = $("#question_examinationTree").tree("getSelected");
               if(n!=null){
                    $("#question_examinationTree").tree("select",n.target);    //相当于默认点击了一下第一个节点，执行onSelect方法
               }
            }
        });

        question_questionDataGrid = $('#question_questionDataGrid').datagrid({
            /*在加载数据时指定url*/
            //url : '${path }/question/dataGrid',
            fit : true,
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            idField : 'quesid',
            sortName : 'quesname',
	        sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            columns : [ [ {
                width : '250',
                title : '题目名称',
                field : 'quesname',
                sortable : true
            }, {
                width : '480',
                title : '题目内容',
                field : 'quescontent',
                sortable : true
            } , {
                 width : '80',
                 title : '实验前提',
                 field : 'quespreq',
                sortable : true,
                styler:cellStyler
             }, {
                 width : '80',
                 title : '结构验证',
                 field : 'queseval',
                 sortable : true,
                 styler:cellStyler
              }, {
                  width : '80',
                  title : '结果验证',
                  field : 'resultquery',
                  sortable : true,
                  styler:cellStyler
              }, {
                field : 'action',
                title : '操作',
                width : 200,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/question/edit">
                            str += $.formatString('<a href="javascript:void(0)" class="question-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editquestionFun(\'{0}\');" >编辑</a>', row.quesid);
                        </shiro:hasPermission>

                        <shiro:hasPermission name="/question/config">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="question-easyui-linkbutton-config" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="configquestionFun(\'{0}\');" >设置</a>', row.quesid);
                        </shiro:hasPermission>

                        <shiro:hasPermission name="/question/delete">
                            str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                            str += $.formatString('<a href="javascript:void(0)" class="question-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deletequestionFun(\'{0}\');" >删除</a>', row.quesid);
                        </shiro:hasPermission>
                    return str;
                }
            }] ],
            onLoadSuccess:function(data){
                $('.question-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.question-easyui-linkbutton-config').linkbutton({text:'设置'});
                var btn = $('.question-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#questionToolbar'
        });

    });

    function cellStyler(value,row,index){
    			if (value == '未设置'){
    				return 'color:red;';
    			}
    }

    function addquestionFun() {
        var sel = $('#question_examinationTree').tree('getSelected');

        parent.$.modalDialog({
            title : '添加',
            width : 700,
            height : 800,
            href : '${path }/question/addPage?id=' + sel.id,
            buttons : [ {
                text : '添加',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = question_questionDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#questionAddForm');
                    f.submit();
                }
            } ]
        });
    }

    function configquestionFun(id) {
        parent.$.modalDialog({
            title : '设置',
            width : 700,
            height : 800,
            resizable: true,
            href : '${path }/question/configPage?id=' + id,
            buttons : [ {
                text : '关闭',
                handler : function() {
                    question_questionDataGrid.datagrid('reload');
                    $.modalDialog.handler.dialog('close');
                }
            } ]
        });
    }

    function deletequestionFun(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = question_questionDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            question_questionDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要删除当前实验题目？', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/question/delete', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        question_questionDataGrid.datagrid('reload');
                    } else {
                        parent.$.messager.alert('错误', result.msg, 'error');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }
    
    function editquestionFun(id) {
        if (id == undefined) {
            var rows = question_questionDataGrid.datagrid('getSelections');
            id = rows[0].quesid;
        } else {
            question_questionDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '编辑',
            width : 1000,
            height : 800,
            href : '${path }/question/editPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = question_questionDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#questionEditForm');
                    f.submit();
                }
            } ]
        });
    }
    
    function searchquestionFun() {
        question_questionDataGrid.datagrid('load', $.serializeObject($('#searchquestionForm')));
    }
    function cleanquestionFun() {
        $('#searchquestionForm input').val('');
        question_questionDataGrid.datagrid('load', {});
    }
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff">
        <form id="searchquestionForm">
            <table>
                <tr>
                    <th>题目名称:</th>
                    <td><input name="quesname" placeholder="请输入题目名称"/></td>
                </tr>
            </table>
        </form>
    </div>
    <div data-options="region:'center',border:true,title:'题目列表'" >
        <table id="question_questionDataGrid" data-options="fit:true,border:false"></table>
    </div>
    <div data-options="region:'west',border:true,split:false,title:'实验列表'"  style="width:300px;overflow: hidden; ">
        <ul id="question_examinationTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
</div>

<div id="questionToolbar" style="display: none;">
    <shiro:hasPermission name="/question/add">
        <a onclick="addquestionFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加</a>
    </shiro:hasPermission>

</div>
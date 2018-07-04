<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var experiment_questionDataGrid;
    var experiment_examinationTree;

    $(function() {
        experiment_examinationTree = $('#experiment_examinationTree').tree({
            url : '${path }/experiment/examinationTree',
            parentField : 'pid',
            lines : true,
            onSelect : function(node) {
                experiment_questionDataGrid.datagrid({
                    url : '${path }/experiment/questionDataGrid',
                    queryParams:{
                            examid: node.id
                        }
                });

            },
            onLoadSuccess:function(node,data){
               $("#experiment_examinationTree li:eq(0)").find("div").addClass("tree-node-selected");   //设置第一个节点高亮
               var n = $("#experiment_examinationTree").tree("getSelected");
               if(n!=null){
                    $("#experiment_examinationTree").tree("select",n.target);    //相当于默认点击了一下第一个节点，执行onSelect方法
               }
            }
        });

        experiment_questionDataGrid = $('#experiment_questionDataGrid').datagrid({
            /*在加载数据时指定url*/
            //url : '${path }/experiment/questionDataGrid',
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
                field : 'action',
                title : '操作',
                width : 200,
                formatter : function(value, row, index) {
                    var str = '';
                        <shiro:hasPermission name="/experiment/do">
                            var status_tips = "回答问题";

                            if (row.eval =="solved"){
                                status_tips = "<font color='gray'>已完成</font>";
                            }else if (row.eval == "not"){
                                status_tips = "<font color='red'>已开始未完成</font>";
                            }

                            str += $.formatString('<a href="javascript:void(0)" class="question-easyui-linkbutton-experimentdo" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="experimentdoFun(\'{0}\');" >{1}</a>', row.quesid, status_tips);
                        </shiro:hasPermission>

                    return str;
                }
            }] ],
            onLoadSuccess:function(data){
                $('.question-easyui-linkbutton-experimentdo').linkbutton({});

            },
            toolbar : '#questionToolbar'
        });

    });

    function cellStyler(value,row,index){
    			if (value == '未设置'){
    				return 'color:red;';
    			}
    }

    function experimentdoFun(id) {
        if (id == undefined) {
            var rows = experiment_questionDataGrid.datagrid('getSelections');
            id = rows[0].quesid;
        } else {
            experiment_questionDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '回答问题',
            width : 700,
            height : 700,
            href : '${path }/experiment/doPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    experiment_questionDataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                }
            } ]
        });
    }
    
    function searchquestionFun() {
        experiment_questionDataGrid.datagrid('load', $.serializeObject($('#searchquestionForm')));
    }
    function cleanquestionFun() {
        $('#searchquestionForm input').val('');
        experiment_questionDataGrid.datagrid('load', {});
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
        <table id="experiment_questionDataGrid" data-options="fit:true,border:false"></table>
    </div>
    <div data-options="region:'west',border:true,split:false,title:'实验列表'"  style="width:300px;overflow: hidden; ">
        <ul id="experiment_examinationTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
</div>


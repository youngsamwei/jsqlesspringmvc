<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var  curriculaDataGridEditPage;
    $(function() {

         curriculaDataGridEditPage = $('#curriculaDataGridEditPage').datagrid({
            url : '${path }/curricula/dataGrid',
            queryParams:{
                section: ${curricula.section},
                weekday : ${curricula.weekday},
                termid : ${curricula.termid}
             },
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            idField : 'classno',
            sortName : 'classno',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            frozenColumns : [ [ {
                    width : '150',
                    title : '学期',
                    field : 'termname'
                },{
                    width : '100',
                    title : '班级',
                    field : 'classname'
                },{
                      width : '50',
                      title : '开始周',
                      field : 'startweek'
                },{
                    width : '50',
                    title : '结束周',
                    field : 'endweek'
                }, {
                     field : 'action',
                     title : '操作',
                     width : 200,
                     formatter : function(value, row, index) {
                         var str = '';
                         <shiro:hasPermission name="/curricula/edit">
                             str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                             str += $.formatString('<a href="javascript:void(0)" class="curricula-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editCurriculaFun(\'{0}\');" >编辑</a>', row.curriculaid);
                         </shiro:hasPermission>
                         <shiro:hasPermission name="/curricula/delete">
                             str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                             str += $.formatString('<a href="javascript:void(0)" class="curricula-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-x icon-red\'" onclick="deleteCurriculaFun(\'{0}\');" >删除</a>', row.curriculaid);
                         </shiro:hasPermission>
                         return str;
                     }
                 }
             ] ],
            onLoadSuccess:function(data){
                $('.curricula-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.curricula-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#curriculaEditSectionToolbar'
        });
    });

    function deleteCurriculaFun(id) {

        parent.$.messager.confirm('询问', '您是否要删除当前实验?', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/curricula/delete', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        curriculaDataGridEditPage.datagrid('reload');
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

    function editCurriculaFun(id) {
         $("#curriculaEditDialog").dialog({
            title : '编辑',
            width : 500,
            height : 400,
            closed: false,
            modal: true,
            href : '${path }/curricula/curriculaEditPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    $("#curriculaEditDialog").dialog.openner_dataGrid = curriculaDataGridEditPage;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = $('#curriculaEditForm');
                    f.submit();
                }
            } ]
        });
    }


</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',fit:true,border:false">
        <table id="curriculaDataGridEditPage" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="curriculaEditSectionToolbar" style="display: none;">

</div>
<div id='curriculaEditDialog'></div>

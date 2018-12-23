<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var  examinationDataGrid;
    $(function() {
         examinationDataGrid = $('#selectQuestionSolvedRatioDatagrid').datagrid({
            url : '${path }/exercisebook/selectQuestionSolvedRatioDatagrid',
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            idField : 'ratio',
            sortName : 'ratio',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            frozenColumns : [ [ {
                width : '100',
                title : '分类',
                field : 'ratio',
                sortable : true
            }, {
                width : '400',
                title : '学生人数',
                field : 'stucount',
                sortable : true
            } ,  {
                width : '60',
                title : '占比',
                field : 'sturatio'
            } ] ]
        });
    });

</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',fit:true,border:false">
        <table id="selectQuestionSolvedRatioDatagrid" data-options="fit:true,border:false"></table>
    </div>
</div>

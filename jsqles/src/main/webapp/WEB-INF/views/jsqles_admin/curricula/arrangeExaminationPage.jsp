<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var  curriculaDataGridEditPage;
    var examinationDataGrid;
    $(function() {

         curriculaDataGridEditPage = $('#arrangeExaminationCurriculaList').datagrid({
            url : '${path }/curricula/dataGrid',
            queryParams:{
                termid : ${curricula.termid}
             },
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            width : 700,
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
                },{
                     width : '50',
                     title : '周次',
                     field : 'weekday'
                 },{
                      width : '50',
                      title : '节次',
                      field : 'section'
                  }
             ] ],
             onClickRow: function (index, row){
                   loadCurrExaminationDataGrid(index,row);
            }

        });
         examinationDataGrid = $('#arrangeExaminationExaminationList').datagrid({
            url : '${path }/examination/dataGrid',
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            width : 200,
            idField : 'examid',
            sortName : 'examid',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            frozenColumns : [ [  {
                    width : '200',
                    title : '名称',
                    field : 'examname',
                    sortable : true
                } ,  {
                    width : '60',
                    title : '状态',
                    field : 'isOpen',
                    sortable : true,
                    formatter : function(value, row, index) {
                        switch (value) {
                        case 1:
                            return '正常';
                        case 0:
                            return '停用';
                        }
                    }
                }
            ]]
        });
        currExaminationDataGrid = $('#arrangeExaminationCurrExaminationDataGrid').datagrid({
              url : '${path }/examcurr/dataGrid',
              striped : false,
              rownumbers : true,
              pagination : true,
              singleSelect : true,
              width : 200,
              idField : 'EXAM_CURR_ID',
              sortName : 'WEEK',
              sortOrder : 'asc',
              pageSize : 20,
              pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
              frozenColumns : [ [  {
                      width : '30',
                      title : '周次',
                      field : 'WEEK',
                      sortable : true
                  } ,  {
                      width : '160',
                      title : '实验名称',
                      field : 'EXAMNAME'
                  }
              ]]
         });

         function loadCurrExaminationDataGrid(index,row){
           /* alert(index + ' : ' + JSON2.stringify(row) )*/
            currExaminationDataGrid.datagrid('getPanel').panel('setTitle', row.termname + ' ' + row.classname + ' 的实验安排');
            currExaminationDataGrid.datagrid('load', {curriculaid:row.curriculaid});
         }
    });

/*先获得选中的实验课表课时，再获得实验单元，再添加实验安排*/
    function arrangeExaminationCurriculaFun(id) {
        var rowCurr = curriculaDataGridEditPage.datagrid('getSelections');
        if (rowCurr.length<=0){
            alert('请选择实验课时。');
            return;
        }
        var currid = rowCurr[0].curriculaid;
        var rowExam = examinationDataGrid.datagrid('getSelections');
          if (rowExam.length<=0){
              alert('请选择实验。');
              return;
          }
        var examid = rowExam[0].examid;
        arrangeExaminationCurriculaSetWeekFun(currid, examid);
    }

    function arrangeExaminationCurriculaSetWeekFun(currid, examid){
         $("#arrangeExaminationCurriculaSetWeek").dialog({
            title: '设置周次',
            width : 700,
            height : 400,
            closed: false,
            modal: true,
            href : '${path }/curricula/arrangeExaminationCurriculaSetWeekPage?curriculaid=' + currid + '&examid=' + examid,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = currExaminationDataGrid;

                   //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = $('#arrangeExaminationCurriculaSetWeekForm');
                    f.submit();
                }
             } ]
        });
   }
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:true, height:100">
        <a onclick="arrangeExaminationCurriculaFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">安排实验</a>
        <P>请先选择一个实验时间，再选择一个或多个实验，再点击安排实验。注意:需要以节次为单位安排实验，每个节次需要对应一个独立实验。
        </P>
    </div>

    <div data-options="title:'实验时间', region:'west',border:true, width : 500">
        <table id="arrangeExaminationCurriculaList" data-options="fit:true,border:false"></table>
    </div>
    <div data-options="title:'实验', region:'center',fit:false,border:true, height: 400, width : 200">
        <table id="arrangeExaminationExaminationList" data-options="fit:true,border:false"></table>
    </div>
    <div data-options="title:'已经安排的实验', region:'south',fit:false,border:true, height : 300">
        <table id="arrangeExaminationCurrExaminationDataGrid" data-options="fit:true,border:false"></table>
    </div>

</div>

<div id="arrangeExaminationCurriculaSetWeek"></div>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var  curriculaDataGrid;
    var schoolyeartermTree;
    var termid;
    $(function() {
       schoolyeartermTree = $('#schoolyeartermTree').tree({
            url : '${path }/schoolyearterm/tree',
            parentField : 'pid',
            lines : true,
            onSelect : function(node) {
                /* 记录下当前选择的学期id */
                termid = node.id;
                curriculaDataGrid.datagrid({
                    url : '${path }/curricula/currGrid',
                    queryParams:{
                            termid: node.id
                        }
                });

            },
            onLoadSuccess:function(node,data){
                /*当前学期 currentTermid 在index.jsp中定义*/
                var node = $('#schoolyeartermTree').tree('find', currentTermid);
                if (node != null){
                    $('#schoolyeartermTree').tree('select', node.target);
                }else{
                   $("#schoolyeartermTree li:eq(0)").find("div").addClass("tree-node-selected");   //设置第一个节点高亮
                   var n = $("#schoolyeartermTree").tree("getSelected");
                   if(n!=null){
                        $("#schoolyeartermTree").tree("select",n.target);    //相当于默认点击了一下第一个节点，执行onSelect方法
                   }
               }
            }
        });

         curriculaDataGrid = $('#curriculaDataGrid').datagrid({
            url : '${path }/curricula/currGrid',
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            idField : 'id',
            sortName : 'id',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            frozenColumns : [ [ {
                width : '100',
                title : '节次',
                field : 'id',
                formatter : function(value, row, index) {
                    var str = '';
                    switch (value){
                        case 1 : str = '第1,2节';break;
                        case 2 : str = '第3,4节';break;
                        case 3 : str = '第5,6节';break;
                        case 4 : str = '第7,8节';break;
                        case 5 : str = '第9,10节';break;
                    }
                    return str;
                }
            }, {
                width : '200',
                title : '星期一',
                field : 'monday'
            }, {
                 width : '200',
                 title : '星期二',
                 field : 'tuesday'
             }, {
                  width : '200',
                  title : '星期三',
                  field : 'wednesday'
              }, {
                   width : '200',
                   title : '星期四',
                   field : 'thursday'
               }, {
                    width : '200',
                    title : '星期五',
                    field : 'friday'
                }, {
                     width : '200',
                     title : '星期六',
                     field : 'saturday'
                 }, {
                      width : '200',
                      title : '星期日 ',
                      field : 'sunday'
                  }
             ] ],
            toolbar : '#curriculaToolbar',
            onDblClickCell: function(index,field,value){
                editCurriculaSectionFun(index,field,value);
           }
        });
    });

    function getIndexOfWeekday(weekday){
        var wds = {'monday': '1', 'tuesday': '2', 'wednesday': '3', 'thursday': '4', 'friday': '5', 'saturday': '6', 'sunday': '7'};
        return wds[weekday];
    }

    function editCurriculaSectionFun(index,field,value) {
        var weekday = getIndexOfWeekday(field) ;
        if(!weekday){
           return;
        }
        parent.$.modalDialog({
            title : '编辑当前选中节次的实验班级',
            width : 700,
            height : 400,
            href : '${path }/curricula/editSectionPage?section=' + (index+1) + '&weekday=' + weekday + '&termid=' + termid,  /*termid是当前页面级别的局部变量*/
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = curriculaDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    curriculaDataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                }
            } ]
        });
    }


    function arrangeExaminationFun() {

        parent.$.modalDialog({
            title : '安排实验',
            width : 1000,
            height : 800,
            href : '${path }/curricula/arrangeExaminationPage?termid=' + termid,  /*termid是当前页面级别的局部变量*/
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = curriculaDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    curriculaDataGrid.datagrid('reload');
                    parent.$.modalDialog.handler.dialog('close');
                }
            } ]
        });
    }

    function addCurriculaFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 500,
            height : 300,
            href : '${path }/curricula/curriculaAddPage',
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = curriculaDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#curriculaAddForm');
                    f.submit();
                }
            } ]
        });
    }

</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'west',border:true,split:false,title:'学年学期'"  style="width:200px;overflow: hidden; ">
        <ul id="schoolyeartermTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="curriculaDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="curriculaToolbar" style="display: none;">
    <shiro:hasPermission name="/curricula/add">
        <a onclick="addCurriculaFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">安排实验班级</a>
    </shiro:hasPermission>
    <shiro:hasPermission name="/curricula/add">
        <a onclick="arrangeExaminationFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">安排实验内容</a>
    </shiro:hasPermission>
</div>

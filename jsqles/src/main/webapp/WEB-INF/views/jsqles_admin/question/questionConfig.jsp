<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

	<script type="text/javascript" src="${staticPath }/static/easyui/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${staticPath }/static/easyui/ckeditor/adapters/jquery.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbmanager.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/experiment.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbmetadata2.js"></script>

<script type="text/javascript">
    /*var quespreq = ${question.quespreq} */
    var configQuestionPreqForm;

    $(function() {
        configQuestionPreqForm = $('#configQuestionPreqForm').form({
            url : '',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success : function(result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    var form = $('#questionConfigForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }

        });

       var editor = CKEDITOR.instances["quescontent_config"];
       if (editor) { editor.destroy(true); }
       $("#quescontent_config").ckeditor( {
                    toolbarGroups: [],
                    language: 'zh',
                    readOnly : "true",
                    resize_enabled  : "false"
       });

        /*在windows 2008 r2 的ie11中linkbutton的onclick属性设置不起作用，因此需要手工绑定click事件
            解决方法：在windows 2008 中默认启用增强IE安全设置，可以设置禁止。再将信任网站设为低。就可以了。
        */
        /*
        $('#config_preq_linkbutton').bind('click', function onclick(e){configQuestionPreqFun(${question.quesid})});
        $('#init_preq_linkbutton').bind('click', function onclick(e){configInitPreqFun(${question.quesid})});
        $('#config_eval_linkbutton').bind('click', function onclick(e){configQuestionEvalFun(${question.quesid})});
        $('#config_result_query_linkbutton').bind('click', function onclick(e){configQuestionResultQueryFun(${question.quesid})});
*/
    });

    function configQuestionPreqFun(id){
        progressLoad();
        $("#configQuestionPreq").dialog({
            title: '设置实验前提',
            width : 700,
            height : 800,
            closed: false,
            modal: true,
            href : '${path }/question/preqConfigPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                   /* parent.$.modalDialog.openner_dataGrid = questionDataGrid;*/
                   //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = $('#questionConfigPreqForm');
                    f.submit();
                }
             } ]
        });
    }

    function configQuestionEvalFun(id){

        $("#configQuestionEval").dialog({
            title: '设置结构验证',
            width : 700,
            height : 800,
            closed: false,
            modal: true,
            href : '${path }/question/evalConfigPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                   /* parent.$.modalDialog.openner_dataGrid = questionDataGrid;*/
                   //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = $('#questionConfigEvalForm');
                    f.submit();
                }
             } ]
        });
    }

    function configQuestionResultQueryFun(id){
         $("#configQuestionResultQuery").dialog({
            title: '设置结果验证',
            width : 700,
            height : 700,
            closed: false,
            modal: true,
            href : '${path }/question/resultQueryConfigPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                   /* parent.$.modalDialog.openner_dataGrid = questionDataGrid;*/
                   //因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = $('#questionConfigEvalForm');
                    f.submit();
                }
             } ]
        });
   }

    function configInitPreqFun(id){

        var v = $('#quespreq').val();
        if (!v) {
            $.messager.confirm('提示', '当前实验前提为空，无法初始化!', function(r){
                if (r){

                }
            });
            return;
        }
        progressLoad();
        var quespreq=$.parseJSON(v);

        experiment.initDB(quespreq);
        var sqls = experiment.createSQLText(quespreq);
        var db = quespreq ? quespreq.database[0].name : null;

        for(var s in sqls){
            dbmetadata2.execute(sqls[s], db);
        }
        progressClose();
    }



</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">

        <form id="configQuestionPreqForm" method="post" autoscroll="true">
            <table class="grid">
                <tr>
                    <td width="120">实验名称</td>
                    <td><input name="quesid" type="hidden"  value="${question.quesid}">
                    ${question.quesname} </td>
                </tr>

                <tr>
                    <td>题目内容</td>
                    <td >
                        <textarea id="quescontent_config" name="quescontent">${question.quescontent}</textarea>

                    </td>
                </tr>
                <tr>
                    <td><a id="config_preq_linkbutton" onclick="configQuestionPreqFun(${question.quesid});" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">设置实验前提</a></td>
                    <td >
                    <a id="init_preq_linkbutton" onclick="configInitPreqFun(${question.quesid});" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">使用当前实验前提初始化数据库</a>
                    <textarea id="quespreq" name="quespreq"   style="width:100%;height:50px">${question.quespreq}</textarea>                    </td>
                </tr>
                <tr>
                    <td><a id="config_eval_linkbutton" onclick="configQuestionEvalFun(${question.quesid});" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">设置结构验证</a></td>
                    <td > <textarea id="queseval" name="queseval"   style="width:100%;height:50px">${question.queseval}</textarea>                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td >需要从客户端提取的数据库对象 (根据结构验证自动产生)<BR><textarea id="quesrequired" name="quesrequired"    style="width:100%;height:50px">${question.quesrequired}</textarea>                    </td>
                </tr>
                <tr>
                    <td><a id="config_result_query_linkbutton" onclick="configQuestionResultQueryFun(${question.quesid});" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">设置结果验证</a>
                    </td>
                    <td >
                            <label id="requireStudentSubmitSQLStatements"> ${question.ifpostext?"要求学生提交":"不要求学生提交"}
                                            ${question.postexttype==1?" SELECT ":""}
                                            ${question.postexttype==2?" INSERT ":""}
                                           ${question.postexttype==3?" UPDATE ":""}
                                             ${question.postexttype==4?" DELETE ":""}
                                            ${question.postexttype==5?" CREATE ":""}
                                            ${question.postexttype==0?" 其他 ":""}语句 </label>
                    <textarea id="resultquery" name="resultquery"    style="width:100%;height:50px">${question.resultquery}</textarea>                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td > 从客户端提交的语句需要满足下列结果(根据结果验证语句执行后产生)<BR><textarea id="resultcheck" name="resultcheck"   style="width:100%;height:50px">${question.resultcheck}</textarea>                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="configQuestionPreq"></div>
<div id="configQuestionEval"></div>
<div id="configQuestionResultQuery"></div>
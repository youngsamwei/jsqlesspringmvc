<%--

设置结构验证

--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

	<script type="text/javascript" src="${staticPath }/static/experiment/dbmanager.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbmetadata2.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbexplorer_jq.js"></script>

<script type="text/javascript">
    var quesid = ${question.quesid};

    var evalOrgTree;
    var questionConfigEvalForm;

    var evalstr = '';
    var evalrequiredstr = '';
    $(function() {

        questionConfigEvalForm = $('#questionConfigEvalForm').form({
            url : '${path }/question/edit',
            onSubmit : function(params) {
               progressLoad();
                var eval = getEval();
                var evalrequired = dbexplorer_jq.dbStructureEvalRequiredClientObjects(eval);
                evalstr = JSON2.stringify(eval);
                evalrequiredstr = JSON2.stringify(evalrequired);
                /* 通过params传递额外的参数 */
                params.queseval = evalstr;
                params.quesrequired = evalrequiredstr;

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
                    /* 修改 configQuestionEval 页面的textarea 的值.*/
                    $('#queseval').val(evalstr);
                    $('#quesrequired').val(evalrequiredstr);
                    $('#configQuestionEval').dialog('close');
                } else {
                    $.messager.alert('错误', '发生错误', 'error');
                }
            }
        });
        /* 在 quesiontConfig 页面中的变量 */
        var v = $('#quespreq').val();
        var quespreq=$.parseJSON(v);
        var db = quespreq ? quespreq.database[0].name : null;
        var dbjson = dbexplorer_jq.getDBJson(db);
        evalOrgTree  = $('#evalOrgTree').tree({
                url : '',
                checkbox:true,
                lines : true,
                data : dbjson.children
         });
         progressClose();
    });

    function addDBFun() {
        $.messager.prompt('提示', '请数据库名称', function(r){
				if (r){
                    var dbjson = dbexplorer_jq.getDBJson(r);
                    var db = dbjson.children[0].children;
                    var root  = evalOrgTree.tree('getRoot');
                    evalOrgTree.tree('append', {parent : root.target, data: db});
				}
		});
    }

   function getEval(){
        var ck = dbexplorer_jq.getCheckedNodes(evalOrgTree);
        return ck;
    }

</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div  data-options="region:'north',border:false" title="" >
        <a id="config_question_eval_add_db_linkbutton" onclick="addDBFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加数据库</a>
        <form id="questionConfigEvalForm" method="post" autoscroll="true">
            <table class="grid">
                <tr>
                    <td width="120">实验名称</td>
                    <td><input name="quesid" type="hidden"  value="${question.quesid}">
                    ${question.quesname} </td>
                </tr>

            </table>
        </form>
    </div>
    <div data-options="region:'center',border:false" title="" >
         <ul id="evalOrgTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
</div>

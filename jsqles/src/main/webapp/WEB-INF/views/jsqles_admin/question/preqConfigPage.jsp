<%--

设置实验前提

--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

	<script type="text/javascript" src="${staticPath }/static/experiment/dbmanager.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbmetadata2.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbexplorer_jq.js"></script>

<script type="text/javascript">
    var quesid = ${question.quesid};

    var organizationTree;
    var questionConfigPreqForm;

    var preqstr = '';
    $(function() {

        questionConfigPreqForm = $('#questionConfigPreqForm').form({
            url : '${path }/question/edit',
            onSubmit : function(params) {
               progressLoad();
                var preq = getPreq();
                preqstr = JSON2.stringify(preq);
                /* 通过params传递额外的参数 */
                params.quespreq = preqstr;

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
                    /* 修改configQuestionPreq 页面的textarea 的值.*/
                    $('#quespreq').val(preqstr);
                    $('#configQuestionPreq').dialog('close');
                } else {
                    $.messager.alert('错误', '发生错误', 'error');
                }
            }
        });

        var dbjson = dbexplorer_jq.getDBJson("exam");
        organizationTree  = $('#configPreqDBTree').tree({
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
                    var root  = organizationTree.tree('getRoot');
                    organizationTree.tree('append', {parent : root.target, data: db});
				}
		});
    }

   function getPreq(){
        var ck = dbexplorer_jq.getCheckedNodes(organizationTree);
        return ck;
    }

</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div  data-options="region:'north',border:false" title="" >
        <a id="add_db_linkbutton" onclick="addDBFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">添加数据库</a>
        <form id="questionConfigPreqForm" method="post" autoscroll="true">
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
         <ul id="configPreqDBTree" style="width:160px;margin: 10px 10px 10px 10px"></ul>
    </div>
</div>

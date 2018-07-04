<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

	<script type="text/javascript" src="${staticPath }/static/easyui/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${staticPath }/static/easyui/ckeditor/adapters/jquery.js"></script>

<script type="text/javascript">
    $(function() {
        $('#questionEditForm').form({
            url : '${path }/question/edit',
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
                    var form = $('#questionEditForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });

       var editor = CKEDITOR.instances["quescontent"];
       if (editor) { editor.destroy(true); }
        $("#quescontent").ckeditor({
                                               language: 'zh',
                                               height : '500'
                                           });

    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="questionEditForm" style="height:100%" method="post">
            <table style="height:100%" class="grid">
                <tr>
                    <td width="60">实验名称</td>
                    <td width="1000"><input name="quesid" type="hidden"  value="${question.quesid}">
                    <input name="quesname" type="text" placeholder="题目名称" class="easyui-validatebox" style="width:100%" data-options="width:600,height:29, required:true" value="${question.quesname}">
                    </td>
                </tr>

                <tr  style="height:90%">
                    <td>题目内容</td>
                    <td > <textarea id="quescontent" name="quescontent" style="height:100%" >${question.quescontent}</textarea>                    </td>
                </tr>

            </table>
        </form>
    </div>
</div>
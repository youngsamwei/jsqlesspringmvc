<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#schoolyeartermEditForm').form({
            url : '${path }/schoolyearterm/edit',
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
                    parent.$.modalDialog.openner_dataGrid.treegrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    var form = $('#schoolyeartermEditForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });
        
    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="schoolyeartermEditForm" method="post">
            <table class="grid">
                <tr>
                    <td>学年名称</td>
                    <td><input name="id" type="hidden"  value="${schoolyearterm.id}">
                    <input name="name" type="text" placeholder="请输入学年名称" class="easyui-validatebox" data-options="width:200,height:29, required:true" value="${schoolyearterm.name}"></td>
                </tr>

            </table>
        </form>
    </div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#userImportForm').form({
            url : '${path }/common/importuser',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate') ;
                if (!isValid) {
                    progressClose();
                } else {
                   isValid = checkData();
                    if (!isValid) {
                        progressClose();
                    }
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
                    var form = $('#userImportForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });

    });
            //JS校验form表单信息
             function checkData(){
                var fileDir = $("#upfile").val();
                var suffix = fileDir.substr(fileDir.lastIndexOf("."));
                if("" == fileDir){
                    parent.$.messager.alert('错误', "选择需要导入的Excel文件！", 'error');
                    return false;
                }
                if(".xls" != suffix && ".xlsx" != suffix ){
                    parent.$.messager.alert('错误', "选择Excel格式的文件导入！", 'error');
                    return false;
                }
                return true;
             }
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">为${organization.name}批量导入学生 </div></br>
        <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">格式要求：第一行是标题行，至少包含学号，姓名两列。 </div></br>
        <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">默认角色：学生, 默认密码:123456789 </div></br>
        <form method="POST"  enctype="multipart/form-data" id="userImportForm" action="/common/importuser">
            <table  class="grid">
             <tr>
                <td>请选择导入文件(.xls, .xlsx): </td>
                <td> <input id="upfile" type="file" name="upfile" class="easyui-validatebox" data-options="required:true" ></td>
                <input id="deptid" type="hidden" name="deptid" value="${organization.id}">
             </tr>
            </table>
        </form>

    </div>
</div>
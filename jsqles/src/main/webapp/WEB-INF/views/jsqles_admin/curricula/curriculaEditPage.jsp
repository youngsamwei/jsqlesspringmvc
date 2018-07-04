<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

<script type="text/javascript">
    $(function() {
        $('#curriculaEditClassno').combotree({
            url : '${path }/organization/tree',
            parentField : 'pid',
            lines : true,
            panelHeight : 'auto',
            value : '${curricula.classno}',
            readonly:true
        });

        $('#curriculaEditTermid').combotree({
            url : '${path }/schoolyearterm/tree',
            parentField : 'pid',
            lines : true,
            panelHeight : 'auto',
            value : '${curricula.termid}',
             readonly:true
        });

        $('#curriculaEditForm').form({
            url : '${path }/curricula/edit',
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
                    $("#curriculaEditDialog").dialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    $('#curriculaEditDialog').dialog('close');
                } else {
                    var form = $('#curriculaEditForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });

    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" title="" style="overflow: hidden;padding: 3px;">只能修改开始周和结束周.</div>
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">

        <form id="curriculaEditForm" style="height:100%" method="post">
            <input id="curriculaid" name="curriculaid" type="hidden" value="${curricula.curriculaid}">
            <table style="height:100%" class="grid">
                <tr>
                    <td width="60">班级</td>
                    <td>
                         <select id="curriculaEditClassno" style="width: 200px; height: 29px;"></select>
                         <a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#curriculaEditClassno').combotree('clear');" >清空</a>
                    </td>
                </tr>
                <tr>
                    <td width="60">学年学期</td>
                    <td>
                         <select id="curriculaEditTermid" style="width: 200px; height: 29px;"></select>
                         <a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#curriculaEditTermid').combotree('clear');" >清空</a>
                    </td>
                </tr>
                <tr>
                    <td width="60">开始周</td>
                    <td><input name="startweek" value="${curricula.startweek}"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false">     </td>
                </tr>
                <tr>
                    <td width="60">结束周</td>
                    <td><input name="endweek" value="${curricula.endweek}"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false">     </td>
                </tr>
                <tr>
                    <td width="60">周次</td>
                    <td>${curricula.weekday} </td>
                </tr>
                <tr>
                    <td width="60">节次</td>
                    <td>${curricula.section}</td>
                </tr>

            </table>
        </form>
    </div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

	<script type="text/javascript" src="${staticPath }/static/easyui/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${staticPath }/static/easyui/ckeditor/adapters/jquery.js"></script>

<script type="text/javascript">
    $(function() {
        $('#arrangeExaminationCurriculaSetWeekForm').form({
            url : '${path }/examcurr/add',
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
                    parent.$.modalDialog.openner_dataGrid.datagrid('reload');
                    $("#arrangeExaminationCurriculaSetWeek").dialog('close');
                } else {
                    var form = $('#arrangeExaminationCurriculaSetWeekForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });


    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="arrangeExaminationCurriculaSetWeekForm" style="height:100%" method="post">
            <table style="height:100%" class="grid">
                <tr>
                    <td width="60">实验时间与实验内容</td>
                    <td><input name="curriculaid" type="hidden"  value="${curricula.curriculaid}">
                    <input name="examid" type="hidden"  value="${examination.examid}">
                    <input type="text" placeholder="学期" style="width:100%" data-options="width:300,height:29" value="${schoolyearterm.name}">
                    <input  type="text" placeholder="班级" style="width:100%" data-options="width:300,height:29" value="${exclass.name}">
                    <input type="text" placeholder="开始周"  style="width:100%" data-options="width:300,height:29" value="${curricula.startweek}">
                    <input  type="text" placeholder="结束周" style="width:100%" data-options="width:300,height:29" value="${curricula.endweek}">
                    <input type="text" placeholder="实验名称" style="width:100%" data-options="width:300,height:29" value="${examination.examname}">
                    </td>
                </tr>

                <tr  style="height:90%">
                    <td>设置在第几周做这个实验</td>
                    <td >  <input name="week" type="text" placeholder="周次" class="easyui-validatebox" style="width:100%" data-options="width:300,height:29, required:true" value="1">
   </td>
                </tr>

            </table>
        </form>
    </div>
</div>
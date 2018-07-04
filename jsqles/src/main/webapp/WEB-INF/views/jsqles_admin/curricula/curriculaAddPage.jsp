<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

<script type="text/javascript">
    $(function() {
        $('#curriculaAddClassno').combotree({
        /*需要修改为实验班级*/
            url : '${path }/exclass/tree',
            parentField : 'pid',
            lines : true,
            panelHeight : 'auto',
            onLoadSuccess:function(node,data){
                if (data.length && data.length > 0 ){
                    $("#curriculaAddClassno").combotree('setValue',data[0].id);
                }
            }
        });

        $('#curriculaAddTermid').combotree({
            url : '${path }/schoolyearterm/tree',
            parentField : 'pid',
            lines : true,
            panelHeight : 'auto',
            onLoadSuccess:function(node,data){
                /*当前学期 currentTermid 在index.jsp中定义*/
                $("#curriculaAddTermid").combotree('setValue',currentTermid);
                /*
                if (data.length && data.length > 0 && data[0].children && data[0].children.length > 0){
                    $("#curriculaAddTermid").combotree('setValue',data[0].children[0].id);
                }*/
            }
        });

        $('#curriculaAddForm').form({
            url : '${path }/curricula/add',
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
                    var form = $('#curriculaAddForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });

    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="curriculaAddForm" style="height:100%" method="post">
            <table style="height:100%" class="grid">
                <tr>
                    <td width="60">班级</td>
                    <td>
                         <select id="curriculaAddClassno" name="classno" style="width: 200px; height: 29px;"></select>
                         <a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#exclassid').combotree('clear');" >清空</a>
                    </td>
                </tr>
                <tr>
                    <td width="60">学年学期</td>
                    <td>
                         <select id="curriculaAddTermid" name="termid" style="width: 200px; height: 29px;"></select>
                         <a class="easyui-linkbutton" href="javascript:void(0)" onclick="$('#termid').combotree('clear');" >清空</a>
                    </td>
                </tr>
                <tr>
                    <td width="60">开始周</td>
                    <td><input name="startweek" value="1"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false">     </td>
                </tr>
                <tr>
                    <td width="60">结束周</td>
                    <td><input name="endweek" value="10"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false">     </td>
                </tr>
                <tr>
                    <td width="60">周次</td>
                    <td><input name="weekday" value="1"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false">     </td>
                </tr>
                <tr>
                    <td width="60">节次</td>
                    <td><input name="section" value="1"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:false">      </td>
                </tr>

            </table>
        </form>
    </div>
</div>
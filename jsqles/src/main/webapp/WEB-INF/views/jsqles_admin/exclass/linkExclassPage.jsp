<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    /*flag=1增加行政班级，flag=0移除行政班级 */
    var flag=${flag };
    var exclassid=${exclassid };

    var linkExclassOrganizationTreeGrid;
    $(function() {
        linkExclassOrganizationTreeGrid = $('#linkExclassOrganizationTreeGrid').tree({
            url : '${path }/exclass/classtree?exclassid=' + exclassid + '&flag=' + flag,
            parentField : 'pid',
            lines : true,
            fit : true,
            checkbox : true,
            fitColumns : false,
            border : false,
            cascadeCheck : false
        });

        $('#exclassLinkForm').form({
            url : '${path }/exclass/link?flag=' + flag + '&exclassid=' + exclassid,
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                var checknodes = linkExclassOrganizationTreeGrid.tree('getChecked');
                                var ids = [];
                                if (checknodes && checknodes.length > 0) {
                                    for ( var i = 0; i < checknodes.length; i++) {
                                        ids.push(checknodes[i].id);
                                    }
                                }
                $('#classids').val(ids);
                return isValid;
            },
            success : function(result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    parent.$.modalDialog.openner_dataGrid.treegrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    var form = $('#exclassLinkForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });
    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false" >
    <div data-options="region:'center',border:false" style="overflow: hidden;padding: 3px;" >
        <table id="linkExclassOrganizationTreeGrid"></table>
        <form id="exclassLinkForm" method="post">
            <input id="classids" name="classids" type="hidden" />
        </form>
    </div>
</div>
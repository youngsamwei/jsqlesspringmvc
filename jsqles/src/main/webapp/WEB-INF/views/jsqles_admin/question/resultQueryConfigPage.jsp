<%--

设置结果验证

--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>

<script type="text/javascript">
    var quesid = ${question.quesid};


    var  resultquery = '';
    var  resultcheck = '';
    var  resulterror = '';
    var questionConfigEvalForm;

    $(function() {

        questionConfigEvalForm = $('#questionConfigEvalForm').form({
            url : '${path }/question/edit',
            onSubmit : function(params) {
               progressLoad();

                /*checkbox为未选中状态时，将不提交任何值，因此手动提交*/
                var ifpostext = $("input[name='ifpostext']:checked").val();
                if (ifpostext){
                    params.ifpostext = true;
                }else {
                    params.ifpostext = false;
                }

                resultquery = $('#resultquery_sql').val();
                resultcheck = $('#resultcheck_sql').val();
                params.resultcheck = resultcheck;
                params.resultquery = resultquery;

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
                    /* 修改 questionConfig 页面的textarea 的值.*/
                    $('#resultquery').val(resultquery);
                    $('#resultcheck').val(resultcheck);

                    var ifpostext = $("input[name='ifpostext']:checked").val();
                    var postexttype =  $("input[name='postexttype']:checked").val();

                    var info = getRequireInfo(ifpostext, postexttype);

                    $('#requireStudentSubmitSQLStatements').html(info);

                    $('#configQuestionResultQuery').dialog('close');
                } else {
                    $.messager.alert('错误', '发生错误', 'error');
                }
            }
        });


         progressClose();
    });

    /*获得是否要求学生提交语句的提示*/
    function getRequireInfo(ifpostext, postexttype){
        var sqltype = ['其他', 'SELECT', 'INSERT', 'UPDATE', 'DELETE', 'CREATE'];

        var info = '';
        if (ifpostext){
            info = '要求学生提交';
            var type = sqltype[postexttype];
            if (!type){
                type = '其他';
            }
            info = info + type + '语句';
        }else{
            info = '不要求学生提交语句.';
        }
        return info;
    }

    function runSQLFun(){
        var result= '';
        try{
            /* 从questionConfig.jsp页面中获取preq*/
            var v = $('#quespreq').val();
            var quespreq=$.parseJSON(v);
            var dbname = quespreq.database[0].name;
            var sql = $('#resultquery_sql').val();
            var jsonresult = dbmetadata2.query(sql, dbname);
            var result = JSON2.stringify(jsonresult);
            $('#runsqlinfo').val('执行成功');
            resulterror = '';
        }catch(e){
            $('#runsqlinfo').val('执行错误.\n' + e.message);
            resulterror = e.message;
        }
        $('#resultcheck_sql').val(result);
    }
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div  data-options="region:'north',border:false" title="" >
        <form id="questionConfigEvalForm" method="post" autoscroll="true">
            <table class="grid">
                <tr>
                    <td width="120">实验名称</td>
                    <td><input name="quesid" type="hidden"  value="${question.quesid}">
                    ${question.quesname} </td>
                </tr>
                <tr>
                    <td width="120">请在此输入SQL语句</td>
                    <td>
                    <a id="config_question_eval_add_db_linkbutton" onclick="runSQLFun();"
                                href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'fi-plus icon-green'">执行SQL语句</a>
                    <textarea id="resultquery_sql" style="width:100%;height:150px">${question.resultquery}</textarea>
                    </td>
                </tr>
                <tr>
                    <td width="120">查询结果(作为结果验证的依据)</td>
                    <td> <textarea id="resultcheck_sql"  style="width:100%;height:150px">${question.resultcheck}</textarea>
                    </td>
                </tr>
                <tr>
                    <td width="120">信息</td>
                    <td> <textarea  id="runsqlinfo"   style="width:100%;height:150px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td width="120">其他设置</td>
                    <td>
                            <label> <input type="checkbox" id="ifpostext" name="ifpostext" ${question.ifpostext?"checked='checked'":""}>是否需要学生提交语句</label>
                            <label><input id="postexttype" name="postexttype" type="radio" value="1" ${question.postexttype==1?"checked='checked'":""}/>SELECT </label>
                            <label><input id="postexttype"  name="postexttype" type="radio" value="2" ${question.postexttype==2?"checked='checked'":""}/>INSERT</label>
                            <label><input id="postexttype"  name="postexttype" type="radio" value="3" ${question.postexttype==3?"checked='checked'":""}/>UPDATE</label>
                            <label><input id="postexttype"  name="postexttype" type="radio" value="4" ${question.postexttype==4?"checked='checked'":""}/>DELETE</label>
                            <label><input id="postexttype"  name="postexttype" type="radio" value="5" ${question.postexttype==5?"checked='checked'":""}/>CREATE</label>
                            <label><input id="postexttype"  name="postexttype" type="radio" value="0" ${question.postexttype==0?"checked='checked'":""}/>其他</label><BR>
                    </td>
                </tr>
            </table>
        </form>
    </div>

</div>

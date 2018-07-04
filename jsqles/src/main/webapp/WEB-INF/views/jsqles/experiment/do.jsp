<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/commons/global.jsp" %>

	<script type="text/javascript" src="${staticPath }/static/experiment/experiment.js?2010"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbmanager.js"></script>
	<script type="text/javascript" src="${staticPath }/static/experiment/dbmetadata2.js?2"></script>

<script type="text/javascript">

	experiment.quesid = ${question.getQuesid() };

	<c:if test="${solved == false}">
		experiment.quesRequiredb =
		<c:choose>
			<c:when  test="${empty question.getQuesrequired()}">
				null;
			</c:when>
			<c:otherwise>
				${question.getQuesrequired()};
			</c:otherwise>
		</c:choose >
		experiment.quesPreq =
		<c:choose>
			<c:when  test="${empty question.getQuespreq()}">
				null;
			</c:when>
			<c:otherwise>
				${question.getQuespreq()};
			</c:otherwise>
		</c:choose >
		experiment.resultquery =
		<c:choose>
			<c:when  test="${question.getPostexttype()==1}">
				null;/*采用用户提交的代码获取结果*/
			</c:when>
			<c:otherwise>
				"${question.getResultquery()}"; /* 若不为空串，则用其作为获取结果的代码"*/
			</c:otherwise>
		</c:choose >

		experiment.ifpostext =
				<c:choose>
			<c:when  test="${empty question.getIfpostext()}">
				null;
			</c:when>
			<c:otherwise>
				${question.getIfpostext()};
			</c:otherwise>
		</c:choose >
	</c:if>

    $(function() {
        $('#questionAnswerForm').form({
            url : '${path }/question/answer',
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
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    var form = $('#questionAnswerForm');
                    parent.$.messager.alert('错误', eval(result.msg), 'error');
                }
            }
        });

        <c:choose>
             <c:when test="${(started == false)||(empty question.getIfpostext())}">
                    experiment.hideBtns();
            </c:when>
        </c:choose>

        /*decode题目内容*/
        $('quescontent').innerHTML = $('quescontent').innerText || $('quescontent').textContent;

        <c:if test="${(started == true) && (solved == false)}">
            /*只要没解决，每次打开问题都重新初始化数据库*/
            experiment.init();
            $('#btn_init').show();
        </c:if>
    });
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false" title="" style="overflow: hidden;padding: 3px;">
        <form id="questionAnswerForm" style="height:100%" method="post">
            <table style="height:100%" class="grid">
                <tr>
                    <th width="60">实验名称</th>
                    <th><input name="quesid" type="hidden"  value="${question.quesid}">
                    ${question.quesname}
                    </th>
                </tr>
                <c:choose>
                    <c:when  test="${empty question.getIfpostext()}">
                        <tr >
                             <th>实验内容</td>
                                <td style="height:70%"> <div style="overflow-y:scroll;height:100%" id="quescontent">${question.quescontent}</div>                 </td>
                         </tr>
                    </c:when>
                    <c:otherwise>
                        <tr >
                             <th>实验内容</td>
                                <td style="height:40%"> <div style="overflow-y:scroll;height:100%" id="quescontent">${question.quescontent}</div>                 </td>
                         </tr>
                        <tr >
                                <th  colspan = "2">
                                <div id="sql_tr_tips">请输入完成此实验的sql语句：(请先在SSMS中执行成功，再复制粘贴到这里):</div>
                                </td>
                        </tr>
                        <tr >
                            <th></th>
                            <td style="height:30%;">
                            <div id="sql_tr_textarea" >
                                <textarea  id="editSQLForm_textarea"  style="height:100%;width:100%" value="">${postext}</textarea>
                                </div>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${started == false}">
                                <tr>
                                    <th ></th>
                                    <td><input id="start" name="start" type="button" value="开始实验"
                                        onclick="experiment.start()">
                                        <input id="btn_init" type="button" value="初始化数据库"
                                                                                onclick="experiment.init()">
                                        </td>
                                </tr>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${solved == false}">

                                <tr>
                                    <th ></th>
                                    <td><input id="submit" name="submit" disabled type="button" value="提交"
                                        onclick="experiment.submit( )">
                                        <input id="btn_init" type="button" value="初始化数据库"
                                                                                onclick="experiment.init()">
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>

                                <tr>
                                    <th></th>
                                    <td>已经完成。
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
                <tr style="height:30%">
                    <th ></th>
                    <td style="height:100%"><font size="4px" color="red"><strong>
                            <div style="overflow-y:scroll;height:100%"
                                    id="info">&nbsp;</div></strong> </font>

                    </td>
                </tr>

            </table>
        </form>
    </div>
</div>


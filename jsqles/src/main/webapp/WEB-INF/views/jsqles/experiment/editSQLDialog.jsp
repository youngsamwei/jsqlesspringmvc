<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/commons/global.jsp" %>


<div id="editSQLDialog" style="height:200;width:300">
        <table style="height:100%;width:100%" class="grid">
                <tr>
                    <th>请输入完成此实验的sql语句：\n (请先在SSMS中执行成功，再复制粘贴到这里):
                    </th>
                </tr>
                <tr>
                    <td>
                    <textarea  rows="8" type="height:90%;width:100%"
                        id="editSQLForm_textarea" value=""></textarea>

                    </td>
                </tr>
        </table>

</div>
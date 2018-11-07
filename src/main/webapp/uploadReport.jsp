<%@ page isELIgnored="false" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
<title>Upload reports Page</title>

</head>
<body onload='document.loginForm.username.focus();'>
 
	<div id="login-box">
 
		<h3>Upload a passenger report here</h3>
 
		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>
 
		<form name='uploadReportForm' action="/uploadReport" method='POST'>
 
		    <table>
			<tr>
				<td>Journey Date</td>
				<td><input type='text' name='username' value=''></td>
			</tr>
			<tr>
				<td>Service Name</td>
				<td><input type='text' name='serviceName' /></td>
			</tr>
			<tr>
                <td colspan='2'>
                    <input name="submit" type="submit" value="submit" />
                </td>
			</tr>
		   </table>
		</form>
	</div>
 
</body>
</html>
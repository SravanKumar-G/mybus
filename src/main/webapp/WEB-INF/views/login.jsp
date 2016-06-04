<%@ page isELIgnored="false" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
<title>Login Page</title>
<style>
.error {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
}
 
.msg {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
	
}
 
#login-box {
	width: 350px;
	padding: 20px;
	margin: 120px auto;
	background: #fff;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	
	box-shadow:10px 5px 20px #333;
	border-radius:10px;
}
.btn{
	border-radius:5px;
	color:white;height:25px;
	background-color:#8d8d8d;
}
</style>
</head>
<body style="background-color:#ccc;" onload='document.loginForm.username.focus();'>
 
	<div id="login-box">
 
		<h3>Login with Username and Password here</h3>
 
		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>
 
		<form name='loginForm' action="<c:url value='/login' />" method='POST'>
 
		    <table align=center>
			<tr>
				<td>User this is:</td>
				<td><input type='text' name='username' value='' ></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
			        <td colspan='2' align='right'>
                                <input name="submit" type="submit" value="submit" class="btn" />
                                </td>
			</tr>
		   </table>
 
		   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</form>
	</div>
 
</body>
</html>
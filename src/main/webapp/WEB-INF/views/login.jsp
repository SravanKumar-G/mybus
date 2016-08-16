<%@ page isELIgnored="false" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
<title>Login Page</title>
<link rel="stylesheet" href="css/login-page.css">

</head>
<body onload='document.loginForm.username.focus();'>
<link href='http://fonts.googleapis.com/css?family=Ubuntu:500' rel='stylesheet' type='text/css'>

<div class="login-header">
	<h1>Login</h1>
</div>

	<div id="login-box">
 

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>
 
		<form name='loginForm' class="login-form" action="<c:url value='/login' />" method='POST'>
 
		    <table align=center>
			<tr>
				<td>Username:</td>
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
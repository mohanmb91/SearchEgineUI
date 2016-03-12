<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Engine</title>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body class="container-fluid">

<div class="container">
<div class="jumbotron">
<h1>DBZ Search Engine</h1>      
</div>
<div class="panel panel-default">
<!-- Default panel contents -->
<div class="panel-heading">Search here</div>
<div class="panel-body">
<form class="form" method="post" action="SeachUI">
		<div class="form-group">
			<label for="SearchText">Search Here!!</label> <input type="text"
				class="form-control" name="SearchText"
				placeholder="Search Here" required="required">
		</div>
	<button class="btn-primary btn-block" type="submit">Search</button>
</form>
</div>
</div>
<c:if test="${not empty Results}">
	<div class="panel panel-default">
	<!-- Default panel contents -->
	<div class="panel-heading">Search Results</div>
	<div class="panel-body">
	<c:forEach items="${ Results}" var="eachResult">
		<a href = "${eachResult.key }">${eachResult.value }</a></br>
	</c:forEach>
	  <c:set var="Results" value="" scope="session"></c:set> 
	</div>
	</div>
</c:if> 
</body>
</html>

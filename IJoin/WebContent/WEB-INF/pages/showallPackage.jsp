<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js' /></script>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'></script>

<title>Package Details</title>
</head>
<body>

	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-12">
				<form class="" style="padding-top: 1em;" action="getpackage" method="post">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Pack Information</h3>
						</div>

						<div class="panel-body">
							<select name="code">
								<option>select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["PACKAGE_CODE"]}</option>
								</c:forEach>
							</select> <select name="category">
									<option>select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["PACKAGE_CATEGORY"]}</option>
								</c:forEach>
							</select> <select name="group">
								<option>select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["PACKAGE_GROUP"]}</option>
								</c:forEach>
							</select> <input class="btn btn-default" type="Submit" name="action" value="edit">
							 <input class="btn btn-default"	type="Submit" name="action" value="show">

						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

</body>
</html>
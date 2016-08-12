<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>

<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js'
	type="text/javascript"> </script>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'
	type="text/javascript"> 
	</script>
	
<style type="text/css">     
    select {
        width:200px;
    }
    label{
    	font-weight:100;
    }
</style>	
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Order Management</title>
</head>
<body>
	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
	
		<div class="row">
			<div class="col-md-12">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Order Management</h3>
						</div>

						<div class="panel-body">
					<form class="" style="padding-top: 5em;" action="getpackage" method="post">
				  		<div>
				  		<c:out value="${msg}" escapeXml="false" />
				   			</div>
				
								<div class="row">
								<div class="col-md-10 ">

									# Offer information<br> &nbsp&nbsp&nbsp <a href='order'>1. Order Staus</a><br>
															&nbsp&nbsp&nbsp <a href='showallPackage1'>2. Package CATEGORY </a><br>
														    &nbsp&nbsp&nbsp <a href='showallPackage2'>3. Package INFORMATION </a><br>
														    &nbsp&nbsp&nbsp <a href='showallStore'>4. Store Information</a><br>
														    &nbsp&nbsp&nbsp <a href='showallOffer'>5. show all Offer</a><br> 
														    &nbsp&nbsp&nbsp <a href='showallOfferSSP'>6. Update SSP Package Code</a>
														    
														    

								</div>
							</div>


						</form>
						</div>
					</div>
				
			</div>
		</div>
	</div>

</body>
</html>
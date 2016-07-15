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
	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js'>
	
</script>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'>
	
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Register</title>
</head>
<body>
	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-6">
				<form class="" style="padding-top: 10em;" action="registerUser">
					<c:out value="${msg}" escapeXml="false" />
					<div class="form-group">
						<label for="userid">Nama</label> <input type="text"
							class="form-control" id="userid" placeholder="Nama" name="name" />
					</div>
					<div class="form-group">
						<label for="userid">Nomor ID</label> <input type="text"
							class="form-control" id="userid" placeholder="Nomor ID"
							name="userid" required="true" />
					</div>
					<div class="form-group">
						<label for="dofbirth">Tanggal Lahir(dd-mm-yyyy)</label> <input
							type="text" class="form-control" id="dofbirth"
							placeholder="Tanggal Lahir(dd-mm-yyyy)" name="dofbirth" />
					</div>
					<div class="form-group">
						<label for="pofbirth">Tempat Lahir</label> <input type="text"
							class="form-control" id="pofbirth" placeholder="Tempat Lahir"
							name="pofbirth" />
					</div>
					<div class="form-group">
						<label for="addr">Alamat</label> <input type="text"
							class="form-control" id="addr" placeholder="Alamat" name="addr" />
					</div>
					<div class="form-group">
						<label for="email">Email address</label> <input type="email"
							class="form-control" id="email" placeholder="Email" name="email" />
					</div>
					<div class="form-group">
						<input type="submit" value="Register" class="btn btn-primary" />
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
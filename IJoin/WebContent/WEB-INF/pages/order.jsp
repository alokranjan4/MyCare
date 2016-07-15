<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet"	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet" href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>
<link rel="stylesheet"	href='<c:out value="${pageContext.request.contextPath}"/>/css/datatables.css'>
<script	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js'/></script>
<script	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'></script>
<script	src='<c:out value="${pageContext.request.contextPath}"/>/js/datatables.js'></script>
<script>



$(document).ready(function() {
    $('#example').DataTable( {
        "processing": true,
        "serverSide": true,
        "ordering": false,
        "ajax": {
            "type":"POST",
            "url": "orderdetails"
        	},
            "columnDefs": [ {
                "targets": -1,
                "data": null,
                "defaultContent": "<button>Details</button>"
            } ]
	});

    $('#example tbody').on( 'click', 'button', function () {
     //   alert("Dynamic Button Click");
     var id = $(this).parent().parent().children().eq(0).text();
            location.href = "/IJoin/getOrderDetails?id="+id;
    } );
        
   /*  $(".dataTables_filter input")
    .unbind()
    .bind('keyup change', function(e) {
        if (e.keyCode == 13 || this.value == "") {
            table
                .search(this.value)
                .draw();
        }
    }); */
});


</script>

<title>Package Details</title>
</head>
<body>
<div class="container">
<c:import url='/jsp/nav.jsp'></c:import>
<div class="row">
<div class='col-md-12'>
		
		<div class="panel panel-info">
			<div class="panel-body">
			<table>
			<tr>
				<td>
					<select name="" id="" >
						<option>select</option>
					</select>
		   		</td>
			</tr>	
			
			</table>
			</div>
		</div>
			
	
		<table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
        <thead>
            <tr>
				<th>ORDER ID</th>
                <th>INVOICE</th>
                <th>MSISDN</th>
                 <th>ORDER STATUS</th>
                <th>ACTIVATION STATUS</th>
                <th>DELIVERY STATUS</th>
                <th>ACTION</th>            	  
            </tr>
        </thead>
    </table></div>
 </div>		
</div>	
</body>
</html>

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
	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js' type="text/javascript">
</script>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js' type="text/javascript">
</script>
<script>
var request;  
function sendInfo(){   
	var v=document.Stores.ID.value;  
	var url="getStoreAjaxID?action="+v;  
	if(window.XMLHttpRequest){  
		request=new XMLHttpRequest();  
		}  
		else if(window.ActiveXObject){  
		request=new ActiveXObject("Microsoft.XMLHTTP");  
		}  
		try  
		{  
		request.onreadystatechange=getInfo;  
		request.open("GET",url,true);  
		request.send();  
		}  
		catch(e)  
		{  
		alert("Unable to connect to server");  
		}  
		}  
		  
		function getInfo(){  
		if(request.readyState==4){  
		var val=request.responseText;  
		document.getElementById('NAME').innerHTML=val;  
		} 
}  
</script>
<style type="text/css">     
    select {
        width:200px;
    }
</style>
<script>

$(document).ready(function(){
      $("#submit").on('click',function(){
    	  
    	  var v_StoreID=$("#StoreID").val();
    	  var v_StoreName=$("#StoreName").val();
    	  
     		if(v_StoreID==""||v_StoreID==null){
  		    document.getElementById("StoreID").style.borderColor = "red";
  			return false;
  		 }else if(v_StoreName==""||v_StoreName==null){
  			 document.getElementById("StoreID").style.borderColor = "";
  			 document.getElementById("StoreName").style.borderColor = "red";
  			 return false;
  		 }
    	  
    		$.ajax({
  				url : "newStoreInfo",
  				data : {
					"StoreID" : $("#StoreID").val(),
					"StoreName" : $("#StoreName").val(),
					"City" : $("#City").val(),
					"Address" : $("#Address").val(),
					"Longitude" : $("#Longitude").val(),
					"LattiTude" : $("#LattiTude").val(),
					"StoreDescription" : $("#StoreDescription").val(),
				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					 document.getElementById("StoreID").style.borderColor = "";
  		  			 document.getElementById("StoreName").style.borderColor = "";
  		  			$("#ID").append('<option value='+$("#StoreID").val()+'>'+$("#StoreID").val()+'</option>');
  	  		  	    $("#NAME").append('<option value='+$("#StoreName").val()+'>'+$("#StoreName").val()+'</option>');
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Inserted Successfully.");
					$("#StoreID").val('');
					$("#StoreName").val('');
					$("#City").val('');
					$("#Address").val('');
					$("#Longitude").val('');
					$("#LattiTude").val('');
					$("#StoreDescription").val('');
  				} else {
  					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Insertion fail. Please try again.");
	
  				}
		    });
     	 }); 
      
      $("#new").on('click',function(){
    	  $("#editStoreForm").hide();
    	  $("#showStoreForm").hide();
    	  $("#newStoreForm").show();
    		$.ajax({
  				url : "getStore?action=new",
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("New Form "); 
   				  } else {
  					console.log(data.Status);
  				}
		    });
     	 }); 
  	  
  		$("#edit").on('click',function(){
  			var id=$("#ID").val();
  			var name=$("#NAME").val();
  			
  	 		if(id=="select"||id==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("ID").style.borderColor = "red";
  	 			return false;
  			}else if(name=="select"||name==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("ID").style.borderColor = "";
  		 	    document.getElementById("NAME").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("ID").style.borderColor = "";
  		 	    document.getElementById("NAME").style.borderColor = "";
  			}
  			
  			$("#newStoreForm").hide();
	   	    $("#showStoreForm").hide();
		 	$("#editStoreForm").show();
			$.ajax({
  				url : "getStore?action=edit",
  				data :{
  					"ID" : $("#ID").val(),
					"NAME" : $("#NAME").val(),
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retreived Successfully."); 
					$("#edit_Store_ID").val(data.list[0]["ID"]);
  					$("#edit_Store_Name").val(data.list[0]["NAME"]);
  					$("#edit_CITY").val(data.list[0]["CITY"]);
  					$("#edit_Address").val(data.list[0]["ADDRESS"]);
  					$("#edit_Longitude").val(data.list[0]["LONGITUDE"]);
  					$("#edit_LattiTude").val(data.list[0]["LATTITUDE"]);
  					$("#edit_Store_Description").val(data.list[0]["STORE_DESC"]);
					}
  					else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Not Found .");
				}
		    });
 		 
		
	    }); 

  		$("#update").on('click',function(){	
 	 		$.ajax({
  				url : "EditStoreInfo",
  				data : {
					"StoreID" : $("#edit_Store_ID").val(),
					"StoreName" : $("#edit_Store_Name").val(),
					"CITY" : $("#edit_CITY").val(),
					"Address" : $("#edit_Address").val(),
					"Longitude" : $("#edit_Longitude").val(),
					"LattiTude" : $("#edit_LattiTude").val(),
					"StoreDescription" : $("#edit_Store_Description").val(),
				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Updated Successfully."); 
					$("#edit_Store_ID").val('');
  					$("#edit_Store_Name").val('');
  					$("#edit_CITY").val('');
  					$("#edit_Address").val('');
  					$("#edit_Longitude").val('');
  					$("#edit_LattiTude").val('');
  					$("#edit_Store_Description").val('');
		
  				} else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Updation fail. Please try again.");
				}
		    });
 		
 	 		
	    });
  		

  		
  		$("#delete").on('click',function(){	
  			var id=$("#ID").val();
  			var name=$("#NAME").val();
  			
  	 		if(id=="select"||id==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("ID").style.borderColor = "red";
  	 			return false;
  			}else if(name=="select"||name==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("ID").style.borderColor = "";
  		 	    document.getElementById("NAME").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("ID").style.borderColor = "";
  		 	    document.getElementById("NAME").style.borderColor = "";
  			}

  		 	 $("#editStoreForm").hide();
	   	     $("#showStoreForm").hide();
   	  		 $("#newStoreForm").hide();

  			$.ajax({
  				url : "getStore?action=delete",
  				data :{
  					"ID" : $("#ID").val(),
					"NAME" : $("#NAME").val(),
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#ID option:selected").remove();
  					$("#NAME option:selected").remove();
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Deleted Successfully."); 
				} else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Deletion fail. Please try again.");
				}
		    });
 		
 	 		
	    });
  		
  		$("#show").on('click',function(){	
  			var id=$("#ID").val();
  			var name=$("#NAME").val();
  			
  	 		if(id=="select"||id==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("ID").style.borderColor = "red";
  	 			return false;
  			}else if(name=="select"||name==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("ID").style.borderColor = "";
  		 	    document.getElementById("NAME").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("ID").style.borderColor = "";
  		 	    document.getElementById("NAME").style.borderColor = "";
  			}
  			
  			$("#newStoreForm").hide();
  			 $("#editStoreForm").hide();
  			 $("#showStoreForm").show();
  			 $.ajax({
  				url : "getStore?action=show",
  				data :{
  					"ID" : $("#ID").val(),
					"NAME" : $("#NAME").val(),
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retreived Successfully."); 
					$("#show_Store_ID").val(data.list[0]["ID"]);
  					$("#show_Store_Name").val(data.list[0]["NAME"]);
  					$("#show_CITY").val(data.list[0]["CITY"]);
  					$("#show_Address").val(data.list[0]["ADDRESS"]);
  					$("#show_Longitude").val(data.list[0]["LONGITUDE"]);
  					$("#show_LattiTude").val(data.list[0]["LATTITUDE"]);
  					$("#show_Store_Description").val(data.list[0]["STORE_DESC"]);
					}
  					else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Not Found .");
				}
			    });
 		
 	 		
	    });
  		$("li").each(function(){
  			$(this).removeClass("active");
  		});
		$("#showallStore").addClass("active");
    	  
});
</script>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Show All Store</title>
</head>
<body>
	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-12">
			  <div>
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Store Information</h3>
						</div>
						<div class="panel-body">
<!-- Select_Form -->						
							<form class="" style="padding-top: 1em;" action="getStore" method="post" name="Stores">
							   <div class="row">
									<div>
									&nbsp&nbsp <font size="4"> Status: <span id="msg"></span></p> </font>
								</div>
						     
							<select name="ID" id="ID"  onchange="sendInfo()">
								<option>select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["ID"]}</option>
								</c:forEach>
							</select> 
							<select name="NAME" id="NAME">
						
								<c:forEach items="${Ajexlist}" var="entry">
									<option>${entry["NAME"]}</option>
								</c:forEach>
							</select> 
							<input class="btn btn-default" type="button" name="action" value="New" id="new">
							 <input class="btn btn-default" type="button" name="action" value="Edit" id="edit">
							 <input class="btn btn-default" type="button" name="action" value="delete" id="delete">
							 <input class="btn btn-default" type="button" name="action" value="show" id="show">
						</div>	
						</form>
						</div>
						<div>
						</div>
					</div>
				
				</div>
				<div id="newStoreForm" style="display:none">
<!-- New_Form -->				
					<form style="padding-top: 2em; class="form-horizontal" action="newStoreInfo" method="post">
							<div class="row">
								<div class="col-md-8 ">
									<div class="form-group">
										<label for="StoreID" class="col-sm-3 control-label"> ID:</label>
										<div class="col-sm-8">
											<input type="text" id="StoreID" class="form-control" name="StoreID" placeholder="StoreID">
										</div>
									</div>
									<div class="form-group">
										<label for="StoreName" class="col-sm-3 control-label">Name:</label>
										<div class="col-sm-8">
											<input type="text" id="StoreName" class="form-control" name="StoreName" placeholder="StoreName">
										</div>
									</div>
									<div class="form-group">
										<label for="City" class="col-sm-3 control-label">City:</label>
										<div class="col-sm-8">
											<input type="text" id="City" class="form-control" name="City" placeholder="City">
										</div>
									</div>
									<div class="form-group">
										<label for="Address" class="col-sm-3 control-label">Address:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id='Address' name="Address" placeholder="Address">
										</div>
									</div>
									<div class="form-group">
										<label for="Longitude" class="col-sm-3 control-label">Longitude:</label>
										<div class="col-sm-8">
											<input type="text" id="Longitude" class="form-control" name="Longitude" placeholder="Longitude">
										</div>
									</div>
									<div class="form-group">
										<label for="LattiTude" class="col-sm-3 control-label">LattiTude:</label>
										<div class="col-sm-8">
											<input type="text" id="LattiTude" class="form-control" name="LattiTude" placeholder="LattiTude">
										</div>
									</div>
									<div class="form-group">
										<label for="StoreDescription" class="col-sm-3 control-label">Store Description:</label>
										<div class="col-sm-8">
											<input type="text" id="StoreDescription" class="form-control" name="StoreDescription" placeholder="StoreDescription">
										</div>
									</div>
									<br>
								</div>
								<div class="col-md-4 "></div>
								<br> <br> <br>
								<div class="form-group">
									<div class="col-sm-8" align="center">
										<input type="button" class="btn btn-default" class="form-control" value="submit" id="submit" /> &nbsp &nbsp &nbsp
									    <input type="reset" class="btn btn-default" class="form-control" value="cancel" />
									</div>
								</div>
							</div>
					</form>
				</div>
<!-- Edit_Form -->
				 <div id="editStoreForm"  style="display:none">
				 				<form style="padding-top: 2em; class="form-horizontal" action="EditStoreInfo" method="post">
							<div class="row">
								<div class="col-md-8 ">		
																	
									<div class="form-group">
										<label for="StoreID" class="col-sm-3 control-label">ID:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_Store_ID" name="StoreID" placeholder="ID" value='' disabled>
										</div>
									</div>
	 								<div class="form-group">
										<label for="StoreName" class="col-sm-3 control-label">NAME:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_Store_Name" name="StoreName" placeholder="StoreName" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="CITY" class="col-sm-3 control-label">CITY:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_CITY" name="CITY" placeholder="CITY" value='' >
										</div>
									</div>
									<div class="form-group">
										<label for="Address" class="col-sm-3 control-label">Address:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_Address" name="Address" placeholder="Address" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="Longitude" class="col-sm-3 control-label">Longitude:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_Longitude" name="Longitude" placeholder="Longitude" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="LattiTude" class="col-sm-3 control-label">LattiTude:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_LattiTude" name="LattiTude" placeholder="LattiTude"  value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="StoreDescription" class="col-sm-3 control-label">Store Description:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_Store_Description" name="StoreDescription" placeholder="StoreDescription"  value=''>
										</div>
									</div>
									<br>
								</div>
								<div class="col-md-4 "></div>
								<br> <br> <br>
								<div class="form-group">
									<div class="col-sm-8" align="center">
										<input type="button" class="btn btn-default" class="form-control" value="update" id="update"/> &nbsp &nbsp &nbsp
									    <input type="reset" class="btn btn-default" class="form-control" value="cancel" />
									</div>
								</div>
							</div>
					</form>
			 </div>
<!-- show_Form -->
			 <div id="showStoreForm" style="display:none">
			 						<form style="padding-top: 2em; class="form-horizontal" action="EditStoreInfo" method="post">
								<div>
								<c:out value="${msg}" escapeXml="false"/>
								</div>
							<div class="row">
								<div class="col-md-8 ">		
								<div class="form-group">
										<label for="StoreID" class="col-sm-3 control-label">StoreID:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="StoreID" placeholder="StoreID" id="show_Store_ID" value='' disabled>
										</div>
									</div>
								<div class="form-group">
										<label for="StoreNAME" class="col-sm-3 control-label">StoreNAME:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="StoreNAME" placeholder="StoreNAME" id="show_Store_Name" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="CITY" class="col-sm-3 control-label">CITY:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="CITY"placeholder="CITY" value='' id="show_CITY" disabled>
										</div>
									</div>

									
									
									<div class="form-group">
										<label for="Address" class="col-sm-3 control-label">Address:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="Address" id="show_Address" placeholder="Address" value='' disabled>
										</div>
									</div>


									<div class="form-group">
										<label for="Longitude" class="col-sm-3 control-label">Longitude:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="Longitude" placeholder="Longitude" id="show_Longitude" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="LattiTude" class="col-sm-3 control-label">LattiTude:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="LattiTude" placeholder="LattiTude" id="show_LattiTude" value='' disabled>
											
										</div>
									</div>
									<div class="form-group">
										<label for="StoreDescription" class="col-sm-3 control-label">Store Description:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="StoreDescription" id="show_Store_Description"placeholder="StoreDescription"  value='' disabled>
											 
										</div>
									</div>
									<br> 
								</div>
							</div>
					</form>
			 </div>
			 
			</div>
		</div>
	</div>
</body>
</html>
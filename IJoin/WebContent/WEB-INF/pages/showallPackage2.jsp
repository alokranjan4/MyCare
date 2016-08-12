
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
	type="text/javascript">
	
</script>
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
<script>
$(document).ready(function(){
	
	$("#PACKAGE_NAME1").on('change',function(){
		$.ajax({
			url : "getPackaGroup?action="+$("#PACKAGE_NAME1").val(),
			method : "GET"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				console.log(data);
				$('#PACKAGE_GROUP1').find('option').remove().end().append('<option value="s_null">Select</option>').val('s_null');
				for( i=0;i<=data.list.length-1;i++){
	 				 $('#PACKAGE_GROUP1').append($("<option></option>").attr("value",data.list[i]).text(data.list[i])); 
	 				 }
			} else {
				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Not  Found. Please try again.");
			}
	    });
		}); 
	
			$("#new_back").on('click',function(){ 
				 $("#editPackageInformation").hide();
				 $("#newPackageInformation").hide();
				 $("#showPackageInformation").hide()
			     $("#operatinPage").show();
				 $("#PACKAGE_NAME1").val('Select');
				 $("#PACKAGE_GROUP1").val('s_null');  
			});
		
			$("#edit_back").on('click',function(){ 
			$("#editPackageInformation").hide();
		 	$("#newPackageInformation").hide();
		 	$("#showPackageInformation").hide()
      		$("#operatinPage").show();
	  		$("#PACKAGE_NAME1").val('Select');
	 		$("#PACKAGE_GROUP1").val('s_null');  
			});
	
			$("#show_back").on('click',function(){ 
				$("#editPackageInformation").hide();
			 	$("#newPackageInformation").hide();
			 	$("#showPackageInformation").hide()
	      		$("#operatinPage").show();
		  		$("#PACKAGE_NAME1").val('Select');
		 		$("#PACKAGE_GROUP1").val('s_null');  
			});
		
	
	
	      $("#submit").on('click',function(){
	    	  var v_PACKAGE_NAME=$("#PACKAGE_NAME").val();
	    	  var v_PACKAGE_GROUP=$("#PACKAGE_GROUP").val();
	     		if(v_PACKAGE_NAME==""||v_PACKAGE_NAME==null){
	  		    document.getElementById("PACKAGE_NAME").style.borderColor = "red";
	  			return false;
	  		 }else if(v_PACKAGE_GROUP=="s_null"||v_PACKAGE_GROUP==null){
	  			 document.getElementById("PACKAGE_NAME").style.borderColor = "";
	  			 document.getElementById("PACKAGE_GROUP").style.borderColor = "red";
	  			 return false;
	  		 }
    		$.ajax({
  				url : "newPackInfor",
  				data : {
					"PACKAGE_NAME" : $("#PACKAGE_NAME").val(),
					"PACKAGE_GROUP" : $("#PACKAGE_GROUP").val(),
					"TARIFF" : $("#TARIFF").val(),
					"QUOTA" : $("#QUOTA").val(),
					"DESCRIPTION" : $("#DESCRIPTION").val(),
					"GIFT_FLAG" : $("#GIFT_FLAG").val(),
					"BUY_FLAG" : $("#BUY_FLAG").val(),
					"BUY_EXTRA_FLAG" : $("#BUY_EXTRA_FLAG").val(),
					"PARAM" : $("#PARAM").val(),
					"PACKAGE_CATEGORY" : $("#PACKAGE_CATEGORY").val(),
					"UNREG_KEYWORD" : $("#UNREG_KEYWORD").val(),
					"UNREG_PARAM" : $("#UNREG_PARAM").val(),
					"KEYWORD" : $("#KEYWORD").val(),
					"SERVICECLASS" : $("#SERVICECLASS").val(),
					
				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					 document.getElementById("PACKAGE_NAME").style.borderColor = "";
  		  			 document.getElementById("PACKAGE_GROUP").style.borderColor = "";
  		  			$("#PACKAGE_NAME1").append('<option value='+$("#PACKAGE_NAME").val()+'>'+$("#PACKAGE_NAME").val()+'</option>');
  	  		  	    $("#PACKAGE_GROUP1").append('<option value='+$("#PACKAGE_GROUP").val()+'>'+$("#PACKAGE_GROUP").val()+'</option>');
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Inserted Successfully."); 
					$("#PACKAGE_NAME").val('');
					$("#PACKAGE_GROUP").val('');
					$("#TARIFF").val('');
					$("#QUOTA").val('');
					$("#DESCRIPTION").val('');
					$("#GIFT_FLAG").val('');
					$("#BUY_FLAG").val('');
					$("#BUY_EXTRA_FLAG").val('');
					$("#PARAM").val('');
					$("#COMMENTS").val('');
					$("#PACKAGE_CATEGORY").val('');
					$("#UNREG_KEYWORD").val('');
					$("#UNREG_PARAM").val('');
					$("#KEYWORD").val('');
					$("#SERVICECLASS").val('');
  				}else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Insertion fail. Please try again.");
				}
		    });
     	 }); 
      
      $("#new").on('click',function(){
    	  $("#showPackageInformation").hide();
    	  $("#editPackageInformation").hide();
    	  $("#newPackageInformation").show();
     }); 
  	
      
  		$("#edit").on('click',function(){
  			
  			var packageGroup=$("#PACKAGE_GROUP1").val();
  			var packageName=$("#PACKAGE_NAME1").val();
  			
  			var packageGroup=$("#PACKAGE_GROUP1").val();
  			var packageName=$("#PACKAGE_NAME1").val();
  			
  	 		if(packageName=="Select"||packageName==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("PACKAGE_NAME1").style.borderColor = "red";
  	 			return false;
  			}else if(packageGroup=="s_null"||packageGroup==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("PACKAGE_NAME1").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_GROUP1").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("PACKAGE_NAME1").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_GROUP1").style.borderColor = "";
  			}
  			
  				
	   	 $("#showPackageInformation").hide();
	   	 $("#newPackageInformation").hide();
		 $("#editPackageInformation").show();
			$.ajax({
  				url : "getPackInfor?action=edit",
  				data :{
  					"PACKAGE_GROUP1": $("#PACKAGE_GROUP1").val(),
  					"PACKAGE_NAME1": $("#PACKAGE_NAME1").val()
  					
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					console.log(data);
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retrived Successfully."); 
  					$("#edit_PACKAGE_NAME").val(data.list[0]["PACKAGE_NAME"]);
  					$("#edit_PACKAGE_GROUP").val(data.list[0]["PACKAGE_GROUP"]);
  					$("#edit_PARAM").val(data.list[0]["PARAM"]);
  					$("#edit_QUOTA").val(data.list[0]["QUOTA"]);
  					$("#edit_SERVICECLASS").val(data.list[0]["SERVICECLASS"]);
  					$("#edit_TARIFF").val(data.list[0]["TARIFF"]);
  					$("#edit_UNREG_KEYWORD").val(data.list[0]["UNREG_KEYWORD"]);
  					$("#edit_UNREG_PARAM").val(data.list[0]["UNREG_PARAM"]);
  					$("#edit_BUY_EXTRA_FLAG").val(data.list[0]["BUY_EXTRA_FLAG"]);
  					$("#edit_BUY_FLAG").val(data.list[0]["BUY_FLAG"]);
  					$("#edit_COMMENTS").val(data.list[0]["COMMENTS"]);
  					$("#edit_DESCRIPTION").val(data.list[0]["DESCRIPTION"]);
  					$("#edit_GIFT_FLAG").val(data.list[0]["GIFT_FLAG"]);
  					$("#edit_KEYWORD").val(data.list[0]["KEYWORD"]);  
  					$("#edit_PACKAGE_CATEGORY").val(data.list[0]["PACKAGE_CATEGORY"]);  
  					
  					} else{
  				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Not Found. Please try again.");
				}
		    });
	    });
  
  		$("#update").on('click',function(){
     		$.ajax({
     			url : "editPackInfor",
   				data : {
					"PACK_NAME" : $("#edit_PACKAGE_NAME").val(),
					"PACK_GROUP" : $("#edit_PACKAGE_GROUP").val(),
					"TARIFF2" : $("#edit_TARIFF").val(),
					"QUOTA2" : $("#edit_QUOTA").val(),
					"DESCRIPTION2" : $("#edit_DESCRIPTION").val(),
					"GIFT_FLAG2" : $("#edit_GIFT_FLAG").val(),
					"BUY_FLAG2" : $("#edit_BUY_FLAG").val(),
					"BUY_EXTRA_FLAG2" : $("#edit_BUY_EXTRA_FLAG").val(),
					"PARAM2" : $("#edit_PARAM").val(),
					"COMMENTS2" : $("#edit_COMMENTS").val(),
					"PACKAGE_CATEGORY2" : $("#edit_PACKAGE_CATEGORY").val(),
					"UNREG_KEYWORD2" : $("#edit_UNREG_KEYWORD").val(),
					"UNREG_PARAM2" : $("#edit_UNREG_PARAM").val(),
					"KEYWORD2" : $("#edit_KEYWORD").val(),
					"SERVICECLASS2" : $("#edit_SERVICECLASS").val(),
				},
   				method : "POST"
   			}).done(function(data) {
   				if (null != data.Status && data.Status === "SUCCESS") {
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Updated Successfully."); 
					$("#edit_PACKAGE_NAME").val('');
  					$("#edit_PACKAGE_GROUP").val('');
  					$("#edit_PARAM").val('');
  					$("#edit_QUOTA").val('');
  					$("#edit_SERVICECLASS").val('');
  					$("#edit_TARIFF").val('');
  					$("#edit_UNREG_KEYWORD").val('');
  					$("#edit_UNREG_PARAM").val('');
  					$("#edit_BUY_EXTRA_FLAG").val('');
  					$("#edit_BUY_FLAG").val('');
  					$("#edit_COMMENTS").val('');
  					$("#edit_DESCRIPTION").val('');
  					$("#edit_GIFT_FLAG").val('');
  					$("#edit_KEYWORD").val('');  
  					$("#edit_PACKAGE_CATEGORY").val('');  
  					
   				} else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Updation fail. Please try again.");
				}
				});
      	 }); 
       
  		$("#delete").on('click',function(){	
 	 		
  			var packageGroup=$("#PACKAGE_GROUP1").val();
  			var packageName=$("#PACKAGE_NAME1").val();
  			
  	 		if(packageName=="Select"||packageName==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("PACKAGE_NAME1").style.borderColor = "red";
  	 			return false;
  			}else if(packageGroup=="s_null"||packageGroup==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("PACKAGE_NAME1").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_GROUP1").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("PACKAGE_NAME1").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_GROUP1").style.borderColor = "";
  			}
  	 		
  		  $("#showPackageInformation").hide();
    	  $("#editPackageInformation").hide();
    	  $("#newPackageInformation").hide();
  			
  			$.ajax({
  				url : "getPackInfor?action=delete",
  				data :{
  					"PACKAGE_GROUP1": $("#PACKAGE_GROUP1").val(),
  					"PACKAGE_NAME1": $("#PACKAGE_NAME1").val()
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#PACKAGE_GROUP1 option:selected").remove();
  					$("#PACKAGE_NAME1 option:selected").remove();
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Deleted Successfully."); 
				} else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Deletion Fail. Please try again.");
				}
	
		    });
 		
 	 		
	    });
  		
  		$("#show").on('click',function(){	
  			var packageGroup=$("#PACKAGE_GROUP1").val();
  			var packageName=$("#PACKAGE_NAME1").val();
  			
  	 		if(packageName=="Select"||packageName==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("PACKAGE_NAME1").style.borderColor = "red";
  	 			return false;
  			}else if(packageGroup=="s_null"||packageGroup==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("PACKAGE_NAME1").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_GROUP1").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("PACKAGE_NAME1").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_GROUP1").style.borderColor = "";
  			}
  			
  			$("#editPackageInformation").hide();
  			 $("#newPackageInformation").hide();
  			 $("#showPackageInformation").show();
  			 $.ajax({
  				url : "getPackInfor?action=show",
  				data :{
  					"PACKAGE_GROUP1": $("#PACKAGE_GROUP1").val(),
  					"PACKAGE_NAME1": $("#PACKAGE_NAME1").val()
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					console.log(data);
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retrived Successfully."); 
  					$("#show_PACKAGE_NAME").val(data.list[0]["PACKAGE_NAME"]);
  					$("#show_PACKAGE_GROUP").val(data.list[0]["PACKAGE_GROUP"]);
  					$("#show_PARAM").val(data.list[0]["PARAM"]);
  					$("#show_QUOTA").val(data.list[0]["QUOTA"]);
  					$("#show_SERVICECLASS").val(data.list[0]["SERVICECLASS"]);
  					$("#show_TARIFF").val(data.list[0]["TARIFF"]);
  					$("#show_UNREG_KEYWORD").val(data.list[0]["UNREG_KEYWORD"]);
  					$("#show_UNREG_PARAM").val(data.list[0]["UNREG_PARAM"]);
  					$("#show_BUY_EXTRA_FLAG").val(data.list[0]["BUY_EXTRA_FLAG"]);
  					$("#show_BUY_FLAG").val(data.list[0]["BUY_FLAG"]);
  					$("#show_COMMENTS").val(data.list[0]["COMMENTS"]);
  					$("#show_DESCRIPTION").val(data.list[0]["DESCRIPTION"]);
  					$("#show_GIFT_FLAG").val(data.list[0]["GIFT_FLAG"]);
  					$("#show_KEYWORD").val(data.list[0]["KEYWORD"]);  
  					$("#show_PACKAGE_CATEGORY").val(data.list[0]["PACKAGE_CATEGORY"]);  
  					
  					} else{
  				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Not Found. Please try again.");
				}
		
		    });
 		
	    });
  		$("li").each(function(){
  			$(this).removeClass("active");
  		});
		$("#showallPackage2").addClass("active");
});
</script>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Show Package Information</title>
</head>
<body>

	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-12">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Package Information</h3>
						</div>

						<div class="panel-body">
					<div id="operatinPage">	
<!-- Select_Form -->		
					<form class="" style="padding-top: 1em;" action="getPackage2" method="post" name="packageInformation">
							<div class="row">
								<div>
								&nbsp&nbsp  Status: <span id="msg"></span>
								</div>
							 	<br>
							
								<select name="PACKAGE_NAME1" id="PACKAGE_NAME1" onchange="">
								<option>Select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["PACKAGE_NAME"]}</option>
								</c:forEach>
							</select> 
							<select name="PACKAGE_GROUP1" id="PACKAGE_GROUP1">
							<option value="s_null">Select</option>
							</select>
							<input class="btn btn-default" type="button" name="action" value="New" id="new"/>
							 <input class="btn btn-default" type="button" name="action" value="Edit" id="edit"/>
							 <input class="btn btn-default" type="button" name="action" value="Delete" id="delete"/>
							 <input class="btn btn-default" type="button" name="action" value="Show" id="show"/>
							</div>
							</form>
							</div>
							
<!-- New_Form -->
							<div id="newPackageInformation" style="display:none">
							<form class="form-horizontal" style="padding-top: 2em;" action="newPackage2" method="post" >
                               <div class="row">
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="OfferID" class="col-sm-4 control-label">PACKAGE_NAME
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_NAME" id="PACKAGE_NAME" placeholder="OfferID">
										</div>
									</div>

									<div class="form-group">
										<label for="PACKAGE_GROUP" class="col-sm-4 control-label">PACKAGE_GROUP
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_GROUP" id="PACKAGE_GROUP" placeholder="PACKAGE_GROUP">
										</div>
									</div>



									<div class="form-group">
										<label for="TARIFF" class="col-sm-4 control-label">TARIFF:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="TARIFF" name="TARIFF"
												placeholder="TARIFF">
										</div>
									</div>


									<div class="form-group">
										<label for="QUOTA" class="col-sm-4 control-label">QUOTA:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="QUOTA" name="QUOTA"
												placeholder="QUOTA">
										</div>
									</div>

									<div class="form-group">
										<label for="DESCRIPTION" class="col-sm-4 control-label">DESCRIPTION:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="DESCRIPTION" name="DESCRIPTION"
												placeholder="DESCRIPTION">
										</div>
									</div>


									<div class="form-group">
										<label for="GIFT_FLAG" class="col-sm-4 control-label">GIFT_FLAG:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="GIFT_FLAG" name="GIFT_FLAG"
												placeholder="GIFT_FLAG">
										</div>
									</div>


									<div class="form-group">
										<label for="BUY_FLAG" class="col-sm-4 control-label">BUY_FLAG
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="BUY_FLAG" name="BUY_FLAG"
												placeholder="BUY_FLAG">
										</div>
									</div>



									<div class="form-group">
										<label for="BUY_EXTRA_FLAG" class="col-sm-4 control-label">BUY_EXTRA_FLAG:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control"  id="BUY_EXTRA_FLAG" name="BUY_EXTRA_FLAG"
												id="BUY_EXTRA_FLAG" placeholder="BUY_EXTRA_FLAG">
										</div>
									</div>

								</div>
								<div class="col-md-6 ">

									<div class="form-group">
										<label for="PARAM" class="col-sm-4 control-label">PARAM
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="PARAM" name="PARAM"
												placeholder="PARAM">
										</div>
									</div>



									<div class="form-group">
										<label for="COMMENTS" class="col-sm-4 control-label">COMMENTS
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="COMMENTS" name="COMMENTS"
												placeholder="COMMENTS">
										</div>
									</div>



									<div class="form-group">
										<label for="PACKAGE_CATEGORY" class="col-sm-4 control-label">PACKAGE_CATEGORY
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="PACKAGE_CATEGORY" name="PACKAGE_CATEGORY"
												placeholder="PACKAGE_CATEGORY">
										</div>
									</div>


									<div class="form-group">
										<label for="UNREG_KEYWORD" class="col-sm-4 control-label">UNREG_KEYWORD
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="UNREG_KEYWORD" name="UNREG_KEYWORD"
												placeholder="UNREG_KEYWORD">
										</div>
									</div>


									<div class="form-group">
										<label for="UNREG_PARAM" class="col-sm-4 control-label">UNREG_PARAM
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="UNREG_PARAM" name="UNREG_PARAM"
												placeholder="UNREG_PARAM">
										</div>
									</div>
						
									<div class="form-group">
										<label for="KEYWORD" class="col-sm-4 control-label">KEYWORD:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="KEYWORD" name="KEYWORD"
												placeholder="KEYWORD">
										</div>
									</div>

									<div class="form-group">
										<label for="SERVICECLASS" class="col-sm-4 control-label">SERVICECLASS
											Type:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="SERVICECLASS" name="SERVICECLASS"
												placeholder="SERVICECLASS">
										</div>
									</div>
									
									</div>
									<br> 
									<div class="form-group">
										<div class="col-sm-10" align="right">
											<input type="button" id="submit" class="btn btn-default" class="form-control" value="Submit" /> &nbsp &nbsp &nbsp 
											<input type="button" class="btn btn-default" id="new_back" name="new_back"  value='Back'>  &nbsp &nbsp &nbsp
											<input type="reset" class="btn btn-default" class="form-control" value="Cancel" />
										</div>
									</div>
									</div>
								</form>
							</div>
<!-- Edit_Form -->				
							<div id="editPackageInformation" style="display:none">
								
							<form style="padding-top: 2em;" class="form-horizontal" action="editPackage2" method="post"  >
                               
							<div class="row">
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="OfferID" class="col-sm-4 control-label">PACKAGE_NAME
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_NAME" id="edit_PACKAGE_NAME" placeholder="PACKAGE_NAME" value='' disabled>
										</div>
									</div>

									<div class="form-group">
										<label for="PACKAGE_GROUP" class="col-sm-4 control-label">PACKAGE_GROUP
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_GROUP" id="edit_PACKAGE_GROUP" placeholder="PACKAGE_GROUP" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="TARIFF" class="col-sm-4 control-label">TARIFF:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="TARIFF" id="edit_TARIFF" placeholder="TARIFF" value=''>
										</div>
									</div>


									<div class="form-group">
										<label for="QUOTA" class="col-sm-4 control-label">QUOTA:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="QUOTA" id="edit_QUOTA" placeholder="QUOTA" value=''>
										</div>
									</div>

									<div class="form-group">
										<label for="DESCRIPTION" class="col-sm-4 control-label">DESCRIPTION:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="DESCRIPTION" id="edit_DESCRIPTION" placeholder="DESCRIPTION" value=''>
										</div>
									</div>


									<div class="form-group">
										<label for="GIFT_FLAG" class="col-sm-4 control-label">GIFT_FLAG:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="GIFT_FLAG" id="edit_GIFT_FLAG" placeholder="GIFT_FLAG" value='${list[0]["GIFT_FLAG"]}'>
										</div>
									</div>


									<div class="form-group">
										<label for="BUY_FLAG" class="col-sm-4 control-label">BUY_FLAG
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="BUY_FLAG" id="edit_BUY_FLAG" placeholder="BUY_FLAG" value='${list[0]["BUY_FLAG"]}'>
										</div>
									</div>



									<div class="form-group">
										<label for="BUY_EXTRA_FLAG" class="col-sm-4 control-label">BUY_EXTRA_FLAG:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="BUY_EXTRA_FLAG" id="edit_BUY_EXTRA_FLAG" placeholder="BUY_EXTRA_FLAG" value='${list[0]["BUY_EXTRA_FLAG"]}'>
										</div>
									</div>

								</div>
								<div class="col-md-6 ">

									<div class="form-group">
										<label for="PARAM" class="col-sm-4 control-label">PARAM
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PARAM" id="edit_PARAM" placeholder="PARAM" value='${list[0]["PARAM"]}'>
										</div>
									</div>



									<div class="form-group">
										<label for="COMMENTS" class="col-sm-4 control-label">COMMENTS
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="COMMENTS" id="edit_COMMENTS" placeholder="COMMENTS" value='${list[0]["COMMENTS"]}'>
										</div>
									</div>



									<div class="form-group">
										<label for="PACKAGE_CATEGORY" class="col-sm-4 control-label">PACKAGE_CATEGORY
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_CATEGORY" id="edit_PACKAGE_CATEGORY" placeholder="PACKAGE_CATEGORY" value='${list[0]["PACKAGE_CATEGORY"]}'>
										</div>
									</div>


									<div class="form-group">
										<label for="UNREG_KEYWORD" class="col-sm-4 control-label">UNREG_KEYWORD
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="edit_UNREG_KEYWORD" name="UNREG_KEYWORD" placeholder="UNREG_KEYWORD" value='${list[0]["UNREG_KEYWORD"]}'>
										</div>
									</div>


									<div class="form-group">
										<label for="UNREG_PARAM" class="col-sm-4 control-label">UNREG_PARAM
											:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_UNREG_PARAM" class="form-control" name="UNREG_PARAM" placeholder="UNREG_PARAM" value='${list[0]["UNREG_PARAM"]}'>
										</div>
									</div>
						
									<div class="form-group">
										<label for="KEYWORD" class="col-sm-4 control-label">KEYWORD:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="KEYWORD" id="edit_KEYWORD" placeholder="KEYWORD" value='${list[0]["KEYWORD"]}'>
										</div>
									</div>

									<div class="form-group">
										<label for="SERVICECLASS" class="col-sm-4 control-label">SERVICECLASS
											Type:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="SERVICECLASS" id="edit_SERVICECLASS" placeholder="SERVICECLASS" value='${list[0]["SERVICECLASS"]}'>
										</div>
									</div>
									
									</div>
									<br> 
									<div class="form-group">
										<div class="col-sm-10" align="right">
											<input type="button" class="btn btn-default" id="update" class="form-control" value="Update" /> &nbsp &nbsp &nbsp 
 											<input type="button" class="btn btn-default" id="edit_back" name="edit_back"  value='Back'>  &nbsp &nbsp &nbsp 
											<input type="reset" class="btn btn-default" class="form-control" value="Cancel" />
										</div>
									</div>
									</div>
								</form >
							
							</div>
<!-- Show_Form -->
							<div id="showPackageInformation" style="display:none">
											<form style="padding-top: 2em;" class="form-horizontal" action="editPackage2" method="post" >
                              
							<div class="row">
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="OfferID" class="col-sm-4 control-label">PACKAGE_NAME
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_NAME" id="show_PACKAGE_NAME" placeholder="PACKAGE_NAME" value='' disabled>
										</div>
									</div>

									<div class="form-group">
										<label for="PACKAGE_GROUP" class="col-sm-4 control-label">PACKAGE_GROUP
											:</label>
										<div class="col-sm-6"> 
											<input type="text" class="form-control" name="PACKAGE_GROUP" id="show_PACKAGE_GROUP"
												placeholder="PACKAGE_GROUP" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="TARIFF" class="col-sm-4 control-label">TARIFF:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="TARIFF"
												placeholder="TARIFF" value='' id="show_TARIFF" disabled>
										</div>
									</div>


									<div class="form-group">
										<label for="QUOTA" class="col-sm-4 control-label">QUOTA:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="QUOTA"
												placeholder="QUOTA" value='' id="show_QUOTA" disabled>
										</div>
									</div>

									<div class="form-group">
										<label for="DESCRIPTION" class="col-sm-4 control-label">DESCRIPTION:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="DESCRIPTION"
												placeholder="DESCRIPTION" value='' id="show_DESCRIPTION" disabled>
										</div>
									</div>


									<div class="form-group">
										<label for="GIFT_FLAG" class="col-sm-4 control-label">GIFT_FLAG:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="GIFT_FLAG"
												placeholder="GIFT_FLAG" value='' id="show_GIFT_FLAG" disabled>
										</div>
									</div>


									<div class="form-group">
										<label for="BUY_FLAG" class="col-sm-4 control-label">BUY_FLAG
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="BUY_FLAG"
												placeholder="BUY_FLAG" value='' id="show_BUY_FLAG" disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="BUY_EXTRA_FLAG" class="col-sm-4 control-label">BUY_EXTRA_FLAG:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="BUY_EXTRA_FLAG"
												id="show_BUY_EXTRA_FLAG" placeholder="BUY_EXTRA_FLAG" value='' disabled>
										</div>
									</div>

								</div>
								<div class="col-md-6 ">

									<div class="form-group">
										<label for="PARAM" class="col-sm-4 control-label">PARAM
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PARAM"
												placeholder="PARAM" id="show_PARAM" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="COMMENTS" class="col-sm-4 control-label">COMMENTS
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="COMMENTS"
												placeholder="COMMENTS" id="show_COMMENTS" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="PACKAGE_CATEGORY" class="col-sm-4 control-label">PACKAGE_CATEGORY
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="PACKAGE_CATEGORY"
												placeholder="PACKAGE_CATEGORY" id="show_PACKAGE_CATEGORY" value='' disabled>
										</div>
									</div>


									<div class="form-group">
										<label for="UNREG_KEYWORD" class="col-sm-4 control-label">UNREG_KEYWORD
											:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="UNREG_KEYWORD"
												placeholder="UNREG_KEYWORD" id="show_UNREG_KEYWORD" value='' disabled>
										</div>
									</div>


									<div class="form-group">
										<label for="UNREG_PARAM" class="col-sm-4 control-label">UNREG_PARAM
											:</label>
										<div class="col-sm-6" value='${list[0]["BUY_FLAG"]}'>
											<input type="text" class="form-control" name="UNREG_PARAM"
												placeholder="UNREG_PARAM" id="show_UNREG_PARAM" value='' disabled>
										</div>
									</div>
						
									<div class="form-group">
										<label for="KEYWORD" class="col-sm-4 control-label">KEYWORD:</label>
										<div class="col-sm-6" value='${list[0]["BUY_FLAG"]}'>
											<input type="text" class="form-control" name="KEYWORD"
												placeholder="KEYWORD" id="show_KEYWORD" value='' disabled>
										</div>
									</div>

									<div class="form-group">
										<label for="SERVICECLASS" class="col-sm-4 control-label">SERVICECLASS
											Type:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="SERVICECLASS"
												placeholder="SERVICECLASS" id="show_SERVICECLASS" value='' disabled>
										</div>
									</div>
									
									</div>
									<div align="center">
									 	<input type="button" class="btn btn-default" id="show_back" name="show_back"  value='Back'>
									</div>
									</div>
									
								</form>
							
							</div>							
						</div>
					</div>
			</div>
		</div>
	</div>

</body>
</html>
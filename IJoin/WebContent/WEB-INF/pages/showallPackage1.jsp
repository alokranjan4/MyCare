
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
var request;  
function sendInfo(){   
	var v=document.packageCategory.PACKAGE_TYPE.value;  
	var url="getPackCategory?action="+v;  
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
		$('#PACKAGE_CATEGORY').prop('selectedIndex',0);
		document.getElementById('PACKAGE_CATEGORY').innerHTML=val;  
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
	
					$("#PACKAGE_TYPE").on('change',function(){
						$.ajax({
							url : "getPackCategory?action="+$("#PACKAGE_TYPE").val(),
							method : "GET"
						}).done(function(data) {
							if (null != data.Status && data.Status === "SUCCESS") {
								console.log(data);
								$('#PACKAGE_CATEGORY').find('option').remove().end().append('<option value="s_null">Select</option>').val('s_null');
								for( i=0;i<=data.list.length-1;i++){
					 				 $('#PACKAGE_CATEGORY').append($("<option></option>").attr("value",data.list[i]).text(data.list[i])); 
					 				 }
							} else {
								$("#msg").removeClass("text-success");
								$("#msg").addClass("text-danger");
								$("#msg").html("Record Not  Found. Please try again.");
							}
					    });
						}); 

	
			$("#new_back").on('click',function(){
		    	  $("#showPackageCategory").hide();
		    	  $("#editPackageCategory").hide();
		    	  $("#newPackageCategory").hide();
				  $("#operatinPage").show();
				  $("#PACKAGE_TYPE").val('Select');
				  $("#PACKAGE_CATEGORY").val('s_null');
			 }); 

			$("#edit_back").on('click',function(){
		    	  $("#showPackageCategory").hide();
		    	  $("#editPackageCategory").hide();
		    	  $("#newPackageCategory").hide();
				  $("#operatinPage").show();
				  $("#PACKAGE_TYPE").val('Select');
				  $("#PACKAGE_CATEGORY").val('s_null');
			 }); 

			$("#show_back").on('click',function(){
		    	  $("#showPackageCategory").hide();
		    	  $("#editPackageCategory").hide();
		    	  $("#newPackageCategory").hide();
				  $("#operatinPage").show();
				  $("#PACKAGE_TYPE").val('Select');
				  $("#PACKAGE_CATEGORY").val('s_null');
			 }); 

	      $("#submit").on('click',function(){
	    	  var v_PackageType=$("#PackageType").val();
	    	  var v_PackageCategory=$("#PackageCategory").val();
	    	  var v_catSeq=$("#catSeq").val();
	    	  if(v_PackageType==""||v_PackageType==null){
	  		    document.getElementById("PackageType").style.borderColor = "red";
	  			return false;
	  		 }else if(v_PackageCategory==""||v_PackageCategory==null){
	  			 document.getElementById("PackageType").style.borderColor = "";
	  			 document.getElementById("PackageCategory").style.borderColor = "red";
	  			 return false;
	  		 }else if(isNaN(v_catSeq)){
	  			 document.getElementById("PackageType").style.borderColor = "";
	  			 document.getElementById("PackageCategory").style.borderColor = "";
	  			 document.getElementById("catSeq").style.borderColor = "red";
	  			  return false;
	  		 }
	    	  
	    	  console.log($("#BannerImage").val());
	    	  var form =$("#newPackCat").get(0); 
	    	  var fd = new FormData(form);
	    	  	fd.append("PackageType", $("#PackageType").val());
	    	  	fd.append("PackageCategory",$("#PackageCategory").val());
	    	  	fd.append("description",$("#description").val());
				fd.append("packageCategoryID",$("#packageCategoryID").val());
				fd.append("catSeq",$("#catSeq").val());
				fd.append("BannerImage",$("#BannerImage").val());
    		$.ajax({
  				url : "newPackCategory",
  				data :fd,
  				processData: false,  // tell jQuery not to process the data
                contentType: false,   // tell jQuery not to set contentType
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					 document.getElementById("PackageType").style.borderColor = "";
  		  			 document.getElementById("PackageCategory").style.borderColor = "";
  		  		 document.getElementById("catSeq").style.borderColor = "";
  		  		$("#PACKAGE_TYPE").append('<option value='+$("#PackageType").val()+'>'+$("#PackageType").val()+'</option>');
  		  	    $("#PACKAGE_CATEGORY").append('<option value='+$("#PackageCategory").val()+'>'+$("#PackageCategory").val()+'</option>');
				$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Inserted Successfully."); 
					$("#PackageCategory").val('');
					$("#description").val('');
					$("#packageCategoryID").val('');
					$("#catSeq").val('');
					$("#BannerImage").val('');
					$("#PackageType").val('');
				} else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Insertion fail. Please try again.");
				}
  		    });
    		
     	 }); 
      
      $("#new").on('click',function(){
    	  $("#showPackageCategory").hide();
    	  $("#editPackageCategory").hide();
    	  $("#newPackageCategory").show();
     	 }); 
  	
      
  		$("#edit").on('click',function(){
  			var packageType=$("#PACKAGE_TYPE").val();
  			var packageCategory=$("#PACKAGE_CATEGORY").val();
  	 		if(packageType=="s_null"||packageType==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("PACKAGE_TYPE").style.borderColor = "red";
  	 			return false;
  			}else if(packageCategory=="s_null"||packageCategory==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("PACKAGE_TYPE").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_CATEGORY").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("PACKAGE_TYPE").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_CATEGORY").style.borderColor = "";
  			}
  			
	   	 $("#showPackageCategory").hide();
	   	 $("#newPackageCategory").hide();
		 $("#editPackageCategory").show();
			$.ajax({
  				url : "getPackCategory?action=edit",
  				data :{
  					"PACKAGE_TYPE": $("#PACKAGE_TYPE").val(),
  					"PACKAGE_CATEGORY": $("#PACKAGE_CATEGORY").val()
  					
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retrived Successfully."); 
  					$("#edit_Package_Type").val(data.list[0]["PACKAGE_TYPE"]);
  					$("#edit_Package_Category").val(data.list[0]["PACKAGE_CATEGORY"]);
  					$("#edit_description").val(data.list[0]["DESCRIPTION"]);
  				  //$("#edit_Banner_Image").val(data.list[0]["BANNER_IMAGE"]);
  					$("#edit_package_CategoryID").val(data.list[0]["PACKAGE_CATEGORY_ID"]);
  					$("#edit_cat_Seq").val(data.list[0]["CAT_SEQ"]);
  				}else{
  					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Not Found. Please try again.");
  				}

		    });
	    });
  
  		
  		$("#update").on('click',function(){
  			  console.log($("#edit_Banner_Image").val());
	    	  var form =$("#editPackCat").get(0); 
	    	  var fd = new FormData(form);
	    	  	fd.append("edit_Package_Type", $("#edit_Package_Type").val());
	    	  	fd.append("edit_Package_Category",$("#edit_Package_Category").val());
	    	  	fd.append("edit_description",$("#edit_description").val());
				fd.append("edit_Banner_Image",$("#edit_Banner_Image").val());
				fd.append("edit_package_CategoryID",$("#edit_package_CategoryID").val());
				fd.append("edit_cat_Seq",$("#edit_cat_Seq").val());
    		$.ajax({
  				url : "EditPackCategory",
  				data :fd,
  				processData: false,  // tell jQuery not to process the data
                contentType: false,   // tell jQuery not to set contentType
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record updated Successfully."); 
					$("#edit_Package_Type").val('');
					$("#edit_Package_Category").val('');
					$("#edit_description").val('');
					//$("#edit_Banner_Image").val('');
					$("#edit_cat_Seq").val('');
					$("#edit_package_CategoryID").val('');
				} else {
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record updation fail. Please try again.");
				}
  		    });
  	   	 }); 
  	    	
  		
  		$("#delete").on('click',function(){	
  			$("#showPackageCategory").hide();
      	    $("#editPackageCategory").hide();
      	    $("#newPackageCategory").hide();
  			
  			var packageType=$("#PACKAGE_TYPE").val();
  			var packageCategory=$("#PACKAGE_CATEGORY").val();
  			
  	 		if(packageType=="Select"||packageType==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("PACKAGE_TYPE").style.borderColor = "red";
  	 			return false;
  			}else if(packageCategory=="s_null"||packageCategory==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("PACKAGE_TYPE").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_CATEGORY").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("PACKAGE_TYPE").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_CATEGORY").style.borderColor = "";
  			}
  			
  			$.ajax({
  				url : "getPackCategory?action=delete",
  				data :{
  					"PACKAGE_TYPE": $("#PACKAGE_TYPE").val(),
  					"PACKAGE_CATEGORY": $("#PACKAGE_CATEGORY").val()
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#PACKAGE_TYPE option:selected").remove();
  					$("#PACKAGE_CATEGORY option:selected").remove();
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
	
  			var packageType=$("#PACKAGE_TYPE").val();
  			var packageCategory=$("#PACKAGE_CATEGORY").val();
  			
  	 		if(packageType=="Select"||packageType==null){
  	 			$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  	 		  document.getElementById("PACKAGE_TYPE").style.borderColor = "red";
  	 			return false;
  			}else if(packageCategory=="s_null"||packageCategory==null){
  				$("#msg").addClass("text-danger");
  	 			$("#msg").html("Please select an option.");
  		 	    document.getElementById("PACKAGE_TYPE").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_CATEGORY").style.borderColor = "red";
  		 	    return false;
  			}else{
  				$("#msg").html("");
  		 	    document.getElementById("PACKAGE_TYPE").style.borderColor = "";
  		 	    document.getElementById("PACKAGE_CATEGORY").style.borderColor = "";
  			}
  			
  			 $("#editPackageCategory").hide();
  			 $("#newPackageCategory").hide();
  			 $("#showPackageCategory").show();
  			 $.ajax({
  				url : "getPackCategory?action=show",
  				data :{
  					"PACKAGE_TYPE": $("#PACKAGE_TYPE").val(),
  					"PACKAGE_CATEGORY": $("#PACKAGE_CATEGORY").val()
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					console.log(data);
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retrived Successfully."); 
  					$("#show_Package_Type").val(data.list[0]["PACKAGE_TYPE"]);
  					$("#show_Package_Category").val(data.list[0]["PACKAGE_CATEGORY"]);
  					$("#show_description").val(data.list[0]["DESCRIPTION"]);
  					$("#show_package_CategoryID").val(data.list[0]["PACKAGE_CATEGORY_ID"]);
  					$("#show_cat_seq").val(data.list[0]["CAT_SEQ"]);
  					$("#show_BANNER_IMAGE").attr('src', 'data:image/jpeg;base64,'+data.list[0]["BANNER_IMAGE"]);
  					$("#show_BANNER_IMAGE").attr("width","250");
  					$("#show_BANNER_IMAGE").attr("height","200");
  		
  					 }
  				else{
  					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Not Found. Please try again.");
  				}
		    });
 		
 	 		
	    });
  		$("li").each(function(){
  			$(this).removeClass("active");
  		});
  		$("#showallPackage1").addClass("active");
});
</script>




<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>show Package Category</title>
</head>
<body>
	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-12">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">show Package Category</h3>
						</div>
						<div class="panel-body">
						<div id="operatinPage">
<!-- Select_Form -->
						<form class="" style="padding-top: 1em;" action="getPackage1" method="post" name="packageCategory">
                          <div class="row">
                              <div>
								&nbsp&nbsp  Status: <span id="msg"></span></p> 
							  </div>
							  
							<select name="PACKAGE_TYPE" id="PACKAGE_TYPE" onchange="">
								<option>Select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["PACKAGE_TYPE"]}</option>
								</c:forEach>
							</select>
							 <select name="PACKAGE_CATEGORY" id="PACKAGE_CATEGORY">
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
						<div id="newPackageCategory" style="display:none">
						<form class="form-horizontal" id="newPackCat" style="padding-top: 2em;"  action="newPackage1" method="post" enctype="multipart/form-data" >
				 			<div class="row">
								<div class="col-md-8 ">
									<div class="form-group">
										<label for="PackageType" class="col-sm-3 control-label">PackageType
											:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="PackageType" id="PackageType" placeholder="PackageType">
										</div>
									</div>



									<div class="form-group">
										<label for="PackageCategory" class="col-sm-3 control-label">PackageCategory:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="PackageCategory" id="PackageCategory" placeholder="PackageCategory">
										</div>
									</div>



									<div class="form-group">
										<label for="description" class="col-sm-3 control-label">description:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="description" id="description" placeholder="description">
										</div>
									</div>


									<div class="form-group">
										<label for="packageCategoryID" class="col-sm-3 control-label">packageCategoryID:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="packageCategoryID" id="packageCategoryID" placeholder="packageCategoryID">
										</div>
									</div>
									
									
									<div class="form-group">
										<label for="catSeq" class="col-sm-3 control-label">catSeq:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="catSeq" id="catSeq" placeholder="Enter seq number">
										</div>
									</div>

									
									<div class="form-group">
										<label for="BannerImage" class="col-sm-3 control-label">BannerImage:</label>
										<div class="col-sm-8">
											<input type="file" class="form-control" name="BannerImage" id="BannerImage" placeholder="BannerImage">
										</div>
									</div>

									<br> <br>
								</div>
								
								<div class="col-md-4 "></div>
								<br> 
								<div class="form-group">
									<div class="col-sm-8" align="center">
										<input type="button" class="btn btn-default" id="submit" class="form-control"  value="Submit" /> &nbsp&nbsp &nbsp
										<input type="button" class="btn btn-default" id="new_back" name="new_back"  value='Back'>		&nbsp&nbsp &nbsp
										 <input type="reset" class="btn btn-default" class="form-control"  value="Cancel" />
									</div>
								</div>
							</div>
						</form>
						</div>
						<div id="editPackageCategory" style="display:none">
<!-- Edit_Form -->			
					 <form class="form-horizontal" id="editPackCat" style="padding-top: 2em;" action="EditPackage1" method="post" enctype="multipart/form-data" >

							<div class="row">
								<div class="col-md-8 ">
									<div class="form-group">
										<label for="PackageType" class="col-sm-3 control-label">PackageType:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="PackageType" id="edit_Package_Type" placeholder="PackageType" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="PackageCategory" class="col-sm-3 control-label">PackageCategory:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="PackageCategory" id="edit_Package_Category" placeholder="PackageCategory" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="description" class="col-sm-3 control-label">description:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="description" id="edit_description" placeholder="description" value=''>
										</div>
									</div>


									<div class="form-group">
										<label for="packageCategoryID" class="col-sm-3 control-label">packageCategoryID:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_package_CategoryID" name="packageCategoryID" placeholder="packageCategoryID" value=''>
										</div>
									</div>
									
									
									<div class="form-group">
										<label for="catSeq" class="col-sm-3 control-label">catSeq:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="catSeq" id="edit_cat_Seq" placeholder="Enter seq number" value=''>
										</div>
									</div>

									
									<div class="form-group">
										<label for="BannerImage" class="col-sm-3 control-label">BannerImage:</label>
										<div class="col-sm-8">
											<input type="file" class="form-control" id="edit_Banner_Image" name="edit_Banner_Image" placeholder="BannerImage">
										</div>
									</div>

									<br> <br>
								</div>
								
								<div class="col-md-4 "></div>
								<br> <br> <br>
								<div class="form-group">
									<div class="col-sm-8" align="center">
										<input type="button" class="btn btn-default" class="form-control" id="update" value="Update" /> &nbsp&nbsp &nbsp
										<input type="button" class="btn btn-default" id="edit_back" name="edit_back"  value='Back'> &nbsp&nbsp &nbsp
										 <input type="reset" class="btn btn-default" class="form-control"  value="Cancel" />
									</div>
								</div>
							</div>
							</form>
						</div>
						<div id="showPackageCategory" style="display:none">
<!-- show_Form -->						
						<form class="form-horizontal" style="padding-top: 2em;" action="EditPackage1" method="post" enctype="multipart/form-data">
							<div class="row">
								<div class="col-md-8 ">
									<div class="form-group">
										<label for="PackageType" class="col-sm-3 control-label">PackageType
											:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_Package_Type" name="PackageType" placeholder="PackageType" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="PackageCategory" class="col-sm-3 control-label">PackageCategory:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_Package_Category" name="PackageCategory" placeholder="PackageCategory" value='' disabled>
										</div>
									</div>



									<div class="form-group">
										<label for="description" class="col-sm-3 control-label">description:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_description" name="description" placeholder="description" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="packageCategoryID" class="col-sm-3 control-label">packageCategoryID:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_package_CategoryID" name="packageCategoryID" placeholder="packageCategoryID" value='' disabled>
										</div>
									</div>
									
									
									<div class="form-group">
										<label for="catSeq" class="col-sm-3 control-label">catSeq:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_cat_seq" name="catSeq" placeholder="catSeq" value='${list[0]["CAT_SEQ"]}' disabled>
										</div>
									</div>

									
									<div class="form-group" class="loader">
										<label for="BannerImage" class="col-sm-3 control-label">BannerImage:</label>
										<div class="col-sm-8">
											<img class="img-rounded" id="show_BANNER_IMAGE"  src='images/loader.gif' height='48' width='48' border='5'>
										</div>
									</div>
									<br>
									<div class="form-group" class="loader">
										<label for="show_back" class="col-sm-3 control-label"></label>
										<div class="col-sm-8" align="center">
					 					<input type="button" class="btn btn-default" id="show_back" name="show_back"  value='Back'/>										</div>
									</div>
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